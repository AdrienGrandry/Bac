package agenda.app;

import agenda.model.EventModel;
import location.Location;
import ressources.DateParser;
import ressources.Message;
import ressources.XmlConfig;
import ressources.dataBase.QueryResult;
import ressources.dataBase.Requete;

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
    public DayPanel(LocalDate date, List<EventModel> events, Map<String, Color> agendaColors, JFrame frame) {
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
            eventLabel.setOpaque(true);
            eventLabel.setFont(EVENT_FONT);
            eventLabel.setForeground(Color.WHITE);

            // Couleur selon l'agenda
            Color bg = agendaColors.getOrDefault(event.getCalendarName(), Color.GRAY);
            eventLabel.setBackground(bg);
            eventLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

            // Curseur main pour indiquer que c'est cliquable
            eventLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // ✅ Listener pour récupérer infos
            eventLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    String titre = event.getTitle();
                    String agenda = XmlConfig.recupNomDepuisValue(event.getCalendarName());
                    LocalDate dateEvent = date; // date du DayPanel

                    // Séparer sur le premier espace
                    titre = titre.replaceFirst("^\\([^)]*\\)\\s*", "");
                    // Maintenant tu peux séparer NOM et Prénom
                    String[] parts = titre.split(" ", 2);

                    String nom = parts[0].toLowerCase();
                    String prenom = (parts.length > 1) ? parts[1].toLowerCase() : "";
                    String IdSalle = "NULL";
                    String IdCafet = "NULL";

                    switch (agenda)
                    {
                        case "salle":
                            IdSalle = "NOT NULL";
                            IdCafet = "NULL";
                            break;
                        case "cafet":
                            IdSalle = "NULL";
                            IdCafet = "NOT NULL";
                            break;
                        case "deux":
                            IdSalle = "NOT NULL";
                            IdCafet = "NOT NULL";
                            break;
                    }

                    //select * from location where lower(Nom) = "grandry" and lower(Prenom) = "adrien" and Date = "2025-08-20"
                    QueryResult queryResult = null;
                    try
                    {
                        queryResult = Requete.executeQuery("select IdLocation from location where lower(Nom) ='" + nom
                                + "' and lower(Prenom) = '" + prenom + "' and Date = '" + dateEvent + "' and IdSalle IS " + IdSalle + " and IdCafeteria IS " + IdCafet);
                        int id = 0;
                        if(queryResult.getResultSet().next())
                        {

                            id = queryResult.getResultSet().getInt("IdLocation");
                        }
                        if(id == 0)
                        {
                            Message.showErrorMessage("Récupération de la location", "L'évenement n'est pas encodé dans le programme !");
                        }
                        else
                        {
                            Location location = new Location("affichelocation", id);
                            location.setVisible(true);
                            frame.dispose();
                        }
                    } catch (Exception ex) {
                        Message.showErrorMessage("Erreur de la base de données", ex.getMessage());
                    }
                    finally
                    {
                        if (queryResult != null)
                            queryResult.close();
                    }
                }
            });

            eventsPanel.add(eventLabel);
            eventsPanel.add(Box.createVerticalStrut(2));
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