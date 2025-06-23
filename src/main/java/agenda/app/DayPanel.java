package agenda.app;

import agenda.model.EventModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DayPanel extends JPanel {
    private final LocalDate date;

    // Polices réutilisées pour éviter recréation à chaque label (optimisation mineure)
    private static final Font DAY_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font EVENT_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    /**
     * Panneau représentant un jour du calendrier avec ses événements.
     * @param date la date du jour
     * @param events la liste des événements pour ce jour
     * @param agendaColors map des noms d'agendas vers leur couleur associée
     */
    public DayPanel(LocalDate date, List<EventModel> events, Map<String, Color> agendaColors) {
        this.date = date;

        // Configuration du layout principal et style du panneau
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        setBackground(Color.WHITE);

        // Label du numéro du jour en haut à droite
        JLabel dayLabel = new JLabel(String.valueOf(date.getDayOfMonth()), SwingConstants.RIGHT);
        dayLabel.setFont(DAY_FONT);
        dayLabel.setBorder(new EmptyBorder(2, 5, 2, 5));
        add(dayLabel, BorderLayout.NORTH);

        // Panel vertical pour la liste des événements
        JPanel eventsPanel = new JPanel();
        eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
        eventsPanel.setBackground(Color.WHITE);
        eventsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Ajout d'un label par événement avec la couleur correspondante
        for (EventModel event : events) {
            JLabel eventLabel = new JLabel(event.getTitle());
            eventLabel.setOpaque(true);  // pour afficher la couleur de fond
            eventLabel.setFont(EVENT_FONT);
            eventLabel.setForeground(Color.WHITE);

            // Récupérer la couleur par nom d'agenda, gris par défaut si absent ou null
            Color bg = agendaColors.getOrDefault(event.getCalendarName(), Color.GRAY);
            eventLabel.setBackground(bg);

            eventLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

            eventsPanel.add(eventLabel);
            eventsPanel.add(Box.createVerticalStrut(2)); // espacement entre événements
        }

        add(eventsPanel, BorderLayout.CENTER);
    }

    /**
     * @return la date associée à ce panneau
     */
    public LocalDate getDate() {
        return date;
    }
}