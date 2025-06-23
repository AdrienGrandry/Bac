package agenda.google;

import com.google.api.services.calendar.model.Event;
import agenda.model.EventModel;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class EventMapper {

    public static EventModel fromGoogleEvent(Event event) {
        if (event == null) return null;

        LocalDate date;

        // Priorité à l'événement "journée entière" (start.date)
        if (event.getStart().getDate() != null) {
            // Conversion de la date Google en LocalDate (UTC)
            long millis = event.getStart().getDate().getValue();
            date = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate();
        } else if (event.getStart().getDateTime() != null) {
            // Conversion date+heure en LocalDate (UTC)
            long millis = event.getStart().getDateTime().getValue();
            date = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate();
        } else {
            // Pas de date trouvée, on ne peut pas créer d'événement valide
            return null;
        }

        // Récupération du titre, ou valeur par défaut si null
        String summary = event.getSummary() != null ? event.getSummary() : "(Sans titre)";

        // Création de l'objet métier avec la date et le titre
        return new EventModel(date, summary);
    }
}