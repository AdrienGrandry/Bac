package agenda.app;

import agenda.model.CalendarModel;
import agenda.model.EventModel;
import agenda.google.GoogleCalendarService;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.ColorDefinition;
import com.google.api.services.calendar.model.Colors;
import principale.MainFrame;
import ressources.LoadingDialog;
import ressources.Style;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

public class GoogleAgendaStyleCalendar extends JFrame {

    // Date affichée : premier jour du mois courant affiché
    private LocalDate currentMonth;

    // Composants UI principaux
    private JLabel monthYearLabel;
    private JPanel calendarGrid;
    private JComboBox<String> agendaSelector;

    private CalendarModel calendarModel;
    private GoogleCalendarService calendarService;

    // Liste des agendas filtrés (Google Calendar API)
    private List<CalendarListEntry> filteredAgendas;

    // Couleurs associées aux agendas
    private Map<String, Color> agendaColors;

    public GoogleAgendaStyleCalendar() throws GeneralSecurityException, IOException {
        currentMonth = LocalDate.now().withDayOfMonth(1);
        calendarModel = new CalendarModel();
        calendarService = new GoogleCalendarService();

        // Récupérer uniquement les agendas filtrés
        filteredAgendas = calendarService.getFilteredCalendarNames();

        // Charger les couleurs des agendas via l'API Google Calendar
        agendaColors = loadAgendaColors();

        initUI();
        refreshCalendar();
    }

    /**
     * Initialise l'interface graphique, layout et listeners.
     */
    private void initUI() {
        setTitle("Gesti'Bac - Agenda");
        setSize(900, 700);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header avec bouton retour et titre
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        JButton btnRetour = new JButton("Retour");
        Style.applyButtonStyle(btnRetour);
        btnRetour.setFocusable(false);
        header.add(btnRetour, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Gesti'Bac - Agenda", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.add(titleLabel, BorderLayout.CENTER);

        // Panel pour navigation mois + sélection agenda
        JPanel monthPanel = new JPanel(new BorderLayout());
        monthPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JButton prev = new JButton("Mois précédent");
        Style.applyButtonStyle(prev);
        JButton next = new JButton("Mois suivant");
        Style.applyButtonStyle(next);

        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));

        // ComboBox agenda : "Tous" + agendas filtrés
        List<String> agendaNames = new ArrayList<>();
        agendaNames.add("Tous");
        for (CalendarListEntry cle : filteredAgendas) {
            agendaNames.add(cle.getSummary());
        }

        agendaSelector = new JComboBox<>(agendaNames.toArray(new String[0]));
        Style.applyBoxStyle(agendaSelector);
        agendaSelector.setFocusable(false);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        centerPanel.setOpaque(false);
        centerPanel.add(monthYearLabel);
        centerPanel.add(agendaSelector);

        monthPanel.add(prev, BorderLayout.WEST);
        monthPanel.add(centerPanel, BorderLayout.CENTER);
        monthPanel.add(next, BorderLayout.EAST);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(header);
        topPanel.add(monthPanel);

        add(topPanel, BorderLayout.NORTH);

        // Listener bouton Retour
        btnRetour.addActionListener(e -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
            dispose();
        });

        // Navigation mois
        prev.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            safeRefresh();
        });

        next.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            safeRefresh();
        });

        // Changement d'agenda
        agendaSelector.addActionListener(e -> safeRefresh());

        // Grid pour affichage calendrier
        calendarGrid = new JPanel(new GridLayout(0, 7));
        calendarGrid.setBackground(Color.WHITE);
        add(calendarGrid, BorderLayout.CENTER);
    }

    /**
     * Chargement des couleurs officielles Google Calendar pour les agendas filtrés.
     */
    private Map<String, Color> loadAgendaColors() throws IOException {
        Map<String, Color> map = new HashMap<>();
        Colors colors = calendarService.getService().colors().get().execute();
        Map<String, ColorDefinition> calendarColors = colors.getCalendar();

        for (CalendarListEntry cal : filteredAgendas) {
            String colorId = cal.getColorId();
            ColorDefinition def = calendarColors.get(colorId);
            if (def != null) {
                String hex = def.getBackground(); // Exemple: "#2952A3"
                map.put(cal.getSummary(), Color.decode(hex));
            } else {
                map.put(cal.getSummary(), Color.GRAY);
            }
        }

        return map;
    }

    /**
     * Rafraîchit l'affichage du calendrier pour le mois et agenda sélectionnés.
     */
    private void refreshCalendar() throws IOException {
        calendarGrid.removeAll();

        // Mise à jour label mois/année
        monthYearLabel.setText(currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE) + " " + currentMonth.getYear());

        // Affichage noms jours semaine
        String[] jours = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};
        for (String jour : jours) {
            JLabel labelJour = new JLabel(jour, SwingConstants.CENTER);
            labelJour.setFont(new Font("Segoe UI", Font.BOLD, 14));
            labelJour.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
            calendarGrid.add(labelJour);
        }

        // Récupération des événements filtrés
        List<EventModel> allEvents = new ArrayList<>();
        String selectedAgenda = (String) agendaSelector.getSelectedItem();

        if ("Tous".equals(selectedAgenda)) {
            for (CalendarListEntry cle : filteredAgendas) {
                List<EventModel> events = calendarService.getEventsForMonth(currentMonth, cle.getId());

                // Assigner le nom du calendrier à chaque événement
                for (EventModel ev : events) {
                    ev.setCalendarName(cle.getSummary());
                }

                allEvents.addAll(events);
            }
        } else {
            CalendarListEntry selectedCal = filteredAgendas.stream()
                    .filter(c -> c.getSummary().equals(selectedAgenda))
                    .findFirst()
                    .orElse(null);

            if (selectedCal != null) {
                allEvents = calendarService.getEventsForMonth(currentMonth, selectedCal.getId());
                for (EventModel ev : allEvents) {
                    ev.setCalendarName(selectedCal.getSummary());
                }
            }
        }

        calendarModel.setEvents(allEvents);

        // Décalage du premier jour du mois pour aligner le jour de la semaine
        int offset = currentMonth.getDayOfWeek().getValue() - 1; // Lundi=1

        // Ajout des cases vides avant le premier jour du mois
        for (int i = 0; i < offset; i++) {
            calendarGrid.add(new JPanel());
        }

        // Ajout des jours du mois
        int daysInMonth = currentMonth.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.withDayOfMonth(day);
            List<EventModel> eventsForDay = calendarModel.getEventsForDate(date);

            // Utilisation de DayPanel personnalisé (possiblement modifié pour EventLabel)
            agenda.app.DayPanel panel = new agenda.app.DayPanel(date, eventsForDay, agendaColors);
            calendarGrid.add(panel);
        }

        // Compléter la grille pour arriver à 6 semaines affichées (42 cellules)
        int totalCells = 7 * 6;
        int filledCells = offset + daysInMonth;
        for (int i = 0; i < totalCells - filledCells; i++) {
            calendarGrid.add(new JPanel());
        }

        calendarGrid.revalidate();
        calendarGrid.repaint();

        /*LocalDate date = LocalDate.parse("2025-06-30");;
        calendarService.ajouterEvenementPlageJours("BAC - Salle", "GRANDRY Adrien (exemple encodage egenda", date, date.plusDays(1));

        date = LocalDate.parse("2025-07-01");;
        calendarService.ajouterEvenementPlageJours("BAC - Cafétéria", "Événement journée complète", date, date.plusDays(1));

        date = LocalDate.parse("2025-07-02");;
        calendarService.ajouterEvenementPlageJours("BAC - Salle/Cafétéria", "Événement journée complète", date, date.plusDays(1));
        */
    }

    /**
     * Exécute refreshCalendar en gérant les exceptions.
     */
    private void safeRefresh() {
        LoadingDialog loadingDialog = new LoadingDialog(this, "Chargement...");

        // Affiche le dialog dans l'EDT
        SwingUtilities.invokeLater(() -> loadingDialog.setVisible(true));

        // Lancer le chargement dans un thread à part
        new Thread(() -> {
            try {
                // Appel bloquant
                refreshCalendar();

                // Une fois terminé, revenir dans l'EDT pour cacher la popup
                SwingUtilities.invokeLater(() -> loadingDialog.setVisible(false));

            } catch (IOException ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    loadingDialog.setVisible(false);
                    JOptionPane.showMessageDialog(this, "Erreur lors du chargement du calendrier : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }
}
