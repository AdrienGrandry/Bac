package agenda.google;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Colors;
import com.google.api.services.calendar.model.ColorDefinition;

import agenda.model.EventModel;
import principale.GoogleAuthorizeUtil;
import ressources.LoadingDialog;
import ressources.Message;
import ressources.XmlConfig;
import ressources.dataBase.Requete;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.*;
import java.util.*;
import java.util.List;

/**
 * Service Google Calendar complet.
 * Permet d'interagir avec plusieurs calendriers, filtrés,
 * et de gérer tous types d'événements.
 */
public class GoogleCalendarService {

    private final Calendar service;

    // Les noms des agendas affichés uniquement
    private static final List<String> NOMS_AGENDAS_AFFICHER = XmlConfig.lireAgendasDepuisXml();

    public GoogleCalendarService() throws GeneralSecurityException, IOException {
        var httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        var jsonFactory = JacksonFactory.getDefaultInstance();
        var credentials = GoogleAuthorizeUtil.authorize();

        service = new Calendar.Builder(httpTransport, jsonFactory, credentials)
                .setApplicationName("GestiBac")
                .build();
    }

    /**
     * Récupère la liste complète des calendriers de l'utilisateur.
     */
    public List<CalendarListEntry> getCalendars() throws IOException {
        CalendarList calendarList = service.calendarList().list().execute();
        return calendarList.getItems();
    }

    /**
     * Récupère uniquement les calendriers dont le nom est dans la liste NOMS_AGENDAS_AFFICHER.
     */
    public List<CalendarListEntry> getFilteredCalendarNames() throws IOException {
        List<CalendarListEntry> allCalendars = getCalendars();
        List<CalendarListEntry> filtered = new ArrayList<>();
        for (CalendarListEntry cal : allCalendars) {
            if (NOMS_AGENDAS_AFFICHER.contains(cal.getSummary())) {
                filtered.add(cal);
            }
        }
        return filtered;
    }

    public Calendar getService() {
        return service;
    }

    public Map<String, Color> getGoogleCalendarColors() throws IOException {
        Map<String, Color> colorMap = new HashMap<>();

        Colors colors = service.colors().get().execute(); // <-- CORRECTION ICI
        Map<String, ColorDefinition> calendarColors = colors.getCalendar();

        for (Map.Entry<String, ColorDefinition> entry : calendarColors.entrySet()) {
            String colorId = entry.getKey();
            String hexColor = entry.getValue().getBackground(); // Exemple: "#2952A3"
            colorMap.put(colorId, Color.decode(hexColor));
        }

        return colorMap;
    }

    /**
     * Retourne une map des noms d'agendas et leurs IDs associés.
     * Utile pour retrouver dynamiquement les agendas.
     */
    public Map<String, String> getCalendarNameIdMap() throws IOException {
        Map<String, String> calendarMap = new HashMap<>();
        String pageToken = null;

        do {
            CalendarList calendarList = service.calendarList().list()
                    .setPageToken(pageToken)
                    .execute();

            for (CalendarListEntry entry : calendarList.getItems()) {
                if (NOMS_AGENDAS_AFFICHER.contains(entry.getSummary())) {
                    calendarMap.put(entry.getSummary(), entry.getId());
                }
            }

            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);

        return calendarMap;
    }

    /**
     * Étale un événement multi-jours en plusieurs événements, un par jour.
     */
    public static List<EventModel> expandMultiDayEvent(EventModel event, LocalDate startDate, LocalDate endDateExclusive) {
        List<EventModel> expandedEvents = new ArrayList<>();
        for (LocalDate date = startDate; date.isBefore(endDateExclusive); date = date.plusDays(1)) {
            EventModel copy = new EventModel(date, event.getTitle());
            copy.setEventId(event.getEventId());
            expandedEvents.add(copy);
        }
        return expandedEvents;
    }

    /**
     * Récupère les événements d'un calendrier donné pour un mois donné.
     * @param firstDayOfMonth Premier jour du mois ciblé.
     * @param calendarId ID du calendrier Google.
     * @return Liste d'événements dans ce calendrier sur ce mois.
     */
    public List<EventModel> getEventsForMonth(LocalDate firstDayOfMonth, String calendarId) throws IOException {
        LocalDate start = firstDayOfMonth.withDayOfMonth(1);
        LocalDate end = start.plusMonths(1);

        var timeMin = new com.google.api.client.util.DateTime(
                start.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        var timeMax = new com.google.api.client.util.DateTime(
                end.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());

        Events events = service.events().list(calendarId)
                .setTimeMin(timeMin)
                .setTimeMax(timeMax)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        List<EventModel> results = new ArrayList<>();
        for (Event event : events.getItems()) {
            EventModel model = EventMapper.fromGoogleEvent(event);
            if (model != null) {
                model.setEventId(event.getId());

                LocalDate startDate = null;
                LocalDate endDate = null;

                if (event.getStart().getDate() != null) {
                    // Événement journée complète
                    startDate = LocalDate.parse(event.getStart().getDate().toStringRfc3339());
                    endDate = LocalDate.parse(event.getEnd().getDate().toStringRfc3339());
                } else if (event.getStart().getDateTime() != null) {
                    // Événement avec heure
                    startDate = Instant.ofEpochMilli(event.getStart().getDateTime().getValue()).atZone(ZoneId.systemDefault()).toLocalDate();
                    endDate = Instant.ofEpochMilli(event.getEnd().getDateTime().getValue()).atZone(ZoneId.systemDefault()).toLocalDate();
                }

                if (startDate != null && endDate != null) {
                    // Étale l'événement sur tous les jours s'il dure plusieurs jours
                    if (endDate.isAfter(startDate.plusDays(1))) {
                        results.addAll(expandMultiDayEvent(model, startDate, endDate));
                    } else {
                        results.add(model);
                    }
                } else {
                    results.add(model);
                }
            }
        }
        return results;
    }

    /**
     * Ajoute un événement sur plusieurs jours (journée complète).
     *
     * @param finExclu Date de fin exclusive (comme demandé par l'API Google Calendar)
     */
    public void ajouterEvenementPlageJours(String nomCalendrier, String titre, String description, LocalDate debut, LocalDate finExclu) throws IOException {
        Map<String, String> calendrierIds = getCalendarNameIdMap();
        String calendarId = calendrierIds.get(nomCalendrier);
        if (calendarId == null) {
            throw new IllegalArgumentException("Calendrier introuvable : " + nomCalendrier);
        }

        Event event = new Event().setSummary(titre).setDescription(description);;

        event.setStart(new EventDateTime().setDate(new com.google.api.client.util.DateTime(debut.toString())));
        event.setEnd(new EventDateTime().setDate(new com.google.api.client.util.DateTime(finExclu.toString())));

        Event createdEvent = service.events().insert(calendarId, event).execute();

        EventModel created = new EventModel(debut, titre);
        created.setEventId(createdEvent.getId());
    }

    public void safeAjouterEvenementPlageJours(JFrame parent, String nomCalendrier, String titre, String description, LocalDate debut, LocalDate finExclu) {
        LoadingDialog loadingDialog = new LoadingDialog(parent, "Ajout de l'événement...");

        SwingUtilities.invokeLater(() -> loadingDialog.setVisible(true));

        new Thread(() -> {
            try {
                ajouterEvenementPlageJours(nomCalendrier, titre, description, debut, finExclu);

                SwingUtilities.invokeLater(() -> {
                    loadingDialog.setVisible(false);
                    Message.showValidMessage("Validation de la location", "La location a bien été enregistrée et ajoutée dans l'agenda");
                });

            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> {
                    loadingDialog.setVisible(false);

                    String message;
                    if (ex instanceof java.net.UnknownHostException || ex instanceof java.net.SocketTimeoutException) {
                        message = "La location à été enregistrée et sera ajoutée à l'agenda lors de votre prochaine connexion à internet !";

                        String newTitre = titre.replace("'", "''");
                        String newDescription = description.replace("'", "''");

                        Requete.executeUpdate("INSERT INTO AjoutAgenda(Agenda, Titre, Description, Date) VALUES ('" + nomCalendrier + "', '" + newTitre + "', '" + newDescription + "', '" + debut.toString() + "')");
                    } else {
                        message = "Impossible de se connecter à Google Agenda.";
                    }

                    Message.showErrorMessage("Erreur ajout location", message);
                });
            } catch (IllegalArgumentException ex) {
                SwingUtilities.invokeLater(() -> {
                    loadingDialog.setVisible(false);
                    Message.showErrorMessage("Erreur ajout location", ex.getMessage());
                });
            }
        }).start();
    }

    /**
     * Met à jour un événement existant (journée complète).
     * @param calendarId ID du calendrier
     * @param eventModel modèle avec eventId existant
     */
    public void updateEvent(String calendarId, EventModel eventModel) throws IOException {
        Event event = service.events().get(calendarId, eventModel.getEventId()).execute();
        event.setSummary(eventModel.getTitle());

        LocalDate startDate = eventModel.getDate();
        LocalDate endDate = startDate.plusDays(1);

        event.setStart(new EventDateTime()
                .setDate(new com.google.api.client.util.DateTime(startDate.toString())));
        event.setEnd(new EventDateTime()
                .setDate(new com.google.api.client.util.DateTime(endDate.toString())));

        service.events().update(calendarId, event.getId(), event).execute();
    }

    /**
     * Supprime un événement donné dans un calendrier.
     */
    public void deleteEvent(String calendarId, String eventId) throws IOException {
        service.events().delete(calendarId, eventId).execute();
    }

    /**
     * Récupère tous les événements des 3 calendriers filtrés pour un mois donné.
     * Retourne une map: clé = nom calendrier, valeur = liste des events.
     */
    public Map<String, List<EventModel>> getEventsForMonthAllCalendars(LocalDate month) throws IOException {
        Map<String, String> calendrierIds = getCalendarNameIdMap();
        Map<String, List<EventModel>> result = new HashMap<>();

        for (String nomCalendrier : NOMS_AGENDAS_AFFICHER) {
            String calendarId = calendrierIds.get(nomCalendrier);
            if (calendarId != null) {
                List<EventModel> events = getEventsForMonth(month, calendarId);
                result.put(nomCalendrier, events);
            } else {
                result.put(nomCalendrier, Collections.emptyList());
            }
        }

        return result;
    }

    /**
     * Recherche un événement à partir du nom complet et d'une date dans les calendriers filtrés.
     *
     * @param nomPrenom Nom et prénom (ex: "GRANDRY Grandry")
     * @param date Date de l'événement à rechercher
     * @return EventModel trouvé ou null si aucun événement correspondant
     */
    public List<EventModel> findEventsByNameAndDate(String nomPrenom, LocalDate date) throws IOException {
        Map<String, String> calendarMap = getCalendarNameIdMap();
        List<EventModel> matches = new ArrayList<>();

        for (Map.Entry<String, String> entry : calendarMap.entrySet()) {
            String calendarId = entry.getValue();

            // Charger les événements du mois pour ce calendrier
            List<EventModel> events = getEventsForMonth(date.withDayOfMonth(1), calendarId);

            for (EventModel event : events) {
                if (event.getDate() != null && event.getTitle() != null
                        && event.getDate().equals(date)
                        && event.getTitle().equals("(Option) " + nomPrenom)) {
                    // Assigner le calendarId à l'événement avant de l'ajouter
                    event.setCalendarId(calendarId);  // <-- ici on conserve le calendarId
                    matches.add(event);
                }
            }
        }

        return matches;
    }

}