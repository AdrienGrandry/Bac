package location.manageLocation;

import agenda.google.GoogleCalendarService;
import agenda.model.EventModel;
import location.Location;
import options.ColorXml;
import ressources.DateParser;
import ressources.LoadingDialog;
import ressources.Message;
import ressources.Style;
import ressources.dataBase.QueryResult;
import ressources.dataBase.Requete;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class detailLocation extends JPanel {
    boolean isOption = false;
    JLabel lblDonneesA, lblDonneesB, lblDonneesC, lblDonneesD;
    String date, nomPrenom;
    int id;
    JFrame parentframe;

    public detailLocation(final JFrame parentFrame, int id) {
        Color backgroundColor = Color.decode(ColorXml.xmlReader("background"));
        Color foregroundColor = Color.decode(ColorXml.xmlReader("foreground"));
        this.id = id;
        this.parentframe = parentFrame;
        setBackground(backgroundColor);
        setLayout(new BorderLayout(10, 10));

        lblDonneesA = createInfoLabel();
        lblDonneesB = createInfoLabel();
        lblDonneesC = createInfoLabel();
        lblDonneesD = createInfoLabel();

        findDBData(id);

        // En-tête
        add(createHeader("Gérer Location", foregroundColor), BorderLayout.NORTH);

        // Corps principal
        JPanel contenu = new JPanel(new GridBagLayout());
        contenu.setBackground(backgroundColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;

        // Panels info
        // Panels info
        JPanel panelInfo = new JPanel(new GridBagLayout());
        panelInfo.setBackground(backgroundColor);

        addInfoPanel(panelInfo, 0, lblDonneesA);
        addInfoPanel(panelInfo, 1, lblDonneesB);
        addInfoPanel(panelInfo, 2, lblDonneesC);
        addInfoPanel(panelInfo, 3, lblDonneesD);

        // Wrap panelInfo in JScrollPane for vertical scrollbar
        JScrollPane scrollPaneInfo = new JScrollPane(panelInfo);
        scrollPaneInfo.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPaneInfo.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        int hauteurParent = parentFrame.getHeight();
        scrollPaneInfo.setPreferredSize(new Dimension(400, hauteurParent / 7));
        scrollPaneInfo.setBorder(new LineBorder(Color.BLACK, 2));

        gbc.gridy = 0;
        gbc.weighty = 0;  // 25% hauteur pour le JScrollPane
        contenu.add(scrollPaneInfo, gbc);


        // Espace vertical
        gbc.gridy = 1;
        gbc.weighty = 0;
        contenu.add(Box.createRigidArea(new Dimension(0, 10)), gbc);

        // Panels données (exemple simplifié avec 3 couleurs)
        JPanel panelDonnees = new JPanel(new GridBagLayout());
        panelDonnees.setBackground(backgroundColor);
        addDataPanel(panelDonnees, 0, Color.RED);
        addSpace(panelDonnees, 1, backgroundColor);
        addDataPanel(panelDonnees, 2, Color.GREEN);
        addSpace(panelDonnees, 3, backgroundColor);
        addDataPanel(panelDonnees, 4, Color.BLUE);

        JPanel panelDonneesWrapper = new JPanel(new BorderLayout());
        panelDonneesWrapper.setBackground(backgroundColor);
        panelDonneesWrapper.add(panelDonnees, BorderLayout.CENTER);

        gbc.gridy = 2;
        gbc.weighty = 0.75; // 75% hauteur pour panelDonneesWrapper
        contenu.add(panelDonneesWrapper, gbc);

        add(contenu, BorderLayout.CENTER);

        // Boutons
        add(createButtonPanel(backgroundColor, isOption), BorderLayout.SOUTH);
    }

    private JLabel createInfoLabel() {
        JLabel label = new JLabel();
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        label.setBorder(new EmptyBorder(10, 10, 10, 10));
        label.setVerticalAlignment(SwingConstants.TOP);  // Texte aligné en haut
        label.setHorizontalAlignment(SwingConstants.LEFT);
        return label;
    }

    private void findDBData(int id) {
        try (QueryResult queryResult = Requete.executeQuery("SELECT * FROM location LEFT JOIN Salle ON Location.IdSalle = Salle.IdSalle LEFT JOIN Cafeteria ON Location.IdCafeteria = Cafeteria.IdCafeteria WHERE idLocation = " + id)) {
            if (queryResult.getResultSet().next()) {
                isOption = queryResult.getResultSet().getBoolean("Option");
                date = queryResult.getResultSet().getString("Date");
                nomPrenom = queryResult.getResultSet().getString("nom").toUpperCase() + " " + queryResult.getResultSet().getString("prenom");

                lblDonneesA.setText(createLabelA(queryResult.getResultSet()));
                if (queryResult.getResultSet().getBoolean("IdSalle")) {
                    lblDonneesB.setText(createLabelB(queryResult.getResultSet()));
                }
                if (queryResult.getResultSet().getBoolean("IdCafeteria")) {
                    lblDonneesC.setText(createLabelC(queryResult.getResultSet()));
                }
                lblDonneesD.setText(createLabelD(queryResult.getResultSet()));
            }
        } catch (Exception ex) {
            Message.showErrorMessage("Erreur de la base de données", ex.getMessage());
        }
    }

    private String createLabelA(ResultSet rs) throws SQLException {
        return String.format(
                "<html>%s %s<br>%s<br>%s %s<br>%s %s<br>%s<br></html>",
                rs.getString("Nom"), rs.getString("Prenom"),
                rs.getString("Adresse"),
                rs.getString("CodePostal"), rs.getString("Localite"),
                rs.getString("Gsm"), rs.getString("Tel"),
                rs.getString("Email")
        );
    }

    private String createLabelB(ResultSet rs) throws SQLException {
        StringBuilder sb = new StringBuilder("<html>Salle : <br><br>");

        if ("1".equals(rs.getString("SalleSeul"))) {
            sb.append("Salle Seule : oui<br>");
        }
        if ("1".equals(rs.getString("Cuisine160"))) {
            sb.append("Cuisine pour 160 personnes : oui<br>");
        }
        if ("1".equals(rs.getString("Cuisine240"))) {
            sb.append("Cuisine pour 240 personnes : oui<br>");
        }
        if ("1".equals(rs.getString("BarAsbl"))) {
            sb.append("Bar avec boissons : oui<br>");
        }
        if ("1".equals(rs.getString("Barvide"))) {
            sb.append("Bar Sans boissons : oui<br>");
        }
        if ("1".equals(rs.getString("PompeVide"))) {
            sb.append("Bar avec pompes à bières : oui<br>");
        }

        sb.append("</html>");
        return sb.toString();
    }

    private String createLabelC(ResultSet rs) throws SQLException {
        StringBuilder sb = new StringBuilder("<html>Cafétéria : <br><br>");

        if ("1".equals(rs.getString("CafeteriaSeule"))) {
            sb.append("Caféria Seule : oui<br>");
        }
        if ("1".equals(rs.getString("Cuisine"))) {
            sb.append("Cuisine : oui<br>");
        }
        if ("1".equals(rs.getString("Reunion"))) {
            sb.append("Réunion : oui<br>");
        }
        if ("1".equals(rs.getString("BarAsbl"))) {
            sb.append("Bar avec boissons : oui<br>");
        }
        if ("1".equals(rs.getString("Barvide"))) {
            sb.append("Bar Sans boissons : oui<br>");
        }
        if ("1".equals(rs.getString("Projecteur"))) {
            sb.append("Projecteur : oui<br>");
        }

        sb.append("</html>");
        return sb.toString();
    }

    private String createLabelD(ResultSet rs) throws SQLException {
        String nom = rs.getString("NomResponsable");
        String prenom = rs.getString("PrenomResponsable");
        String gsm = rs.getString("GsmResponsable");
        String tel = rs.getString("TelResponsable");

        if (nom == null && prenom == null && gsm == null && tel == null) {
            return "<html>Responsable :<br>Organisateur</html>";
        }
        return String.format(
                "<html>Responsable :<br>%s %s<br>%s %s<br></html>",
                nom != null ? nom : "",
                prenom != null ? prenom : "",
                gsm != null ? gsm : "",
                tel != null ? tel : ""
        );
    }

    private JPanel createHeader(String title, Color fg) {
        JPanel entete = new JPanel(new BorderLayout());
        entete.setBackground(getBackground());
        JLabel titre = new JLabel(title, SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 40));
        titre.setForeground(fg);
        titre.setBorder(new EmptyBorder(10, 10, 10, 10));
        entete.add(titre, BorderLayout.CENTER);
        return entete;
    }

    private void addInfoPanel(JPanel parent, int gridx, JLabel content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(getBackground());
        panel.add(content, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.25;
        gbc.weighty = 0;
        parent.add(panel, gbc);
    }

    private void addDataPanel(JPanel parent, int gridx, Color bg) {
        JPanel panel = new JPanel();
        panel.setBackground(bg);
        panel.setBorder(new LineBorder(Color.BLACK, 1));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1; // Étire aussi verticalement
        parent.add(panel, gbc);
    }

    private void addSpace(JPanel parent, int gridx, Color bg) {
        JPanel espace = new JPanel();
        espace.setBackground(bg);
        espace.setPreferredSize(new Dimension(10, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 1; // pour que l'espace prenne toute la hauteur possible (ou au moins ne gêne pas)
        gbc.fill = GridBagConstraints.BOTH;
        parent.add(espace, gbc);
    }

    private JPanel createButtonPanel(Color bg, boolean isOption) {

        JButton optionValid = new JButton("Confirmer la location");
        Style.applyButtonStyle(optionValid);
        optionValid.addActionListener(e -> updateGoogleCalendar());

        JButton option = new JButton("Annuler l'option");
        Style.applyButtonStyle(option);

        JButton annuler = new JButton("Annuler la location");//Demander si facture
        Style.applyButtonStyle(annuler);

        JPanel panelBtn = new JPanel();
        panelBtn.setBackground(bg);
        if (isOption) {
            panelBtn.add(optionValid);
            panelBtn.add(option);
        }
        else
        {
            panelBtn.add(annuler);
        }
        return panelBtn;
    }

    private void updateGoogleCalendar() {
        LocalDate localdate = DateParser.parseStringToLocalDate(date);

        LoadingDialog loadingDialog = new LoadingDialog(parentframe, "Chargement du nombre de mail...");
        SwingUtilities.invokeLater(() -> loadingDialog.setVisible(true));

        new Thread(() -> {
            try {
                GoogleCalendarService googleCalendarService = new GoogleCalendarService();
                List<EventModel> evenements = googleCalendarService.findEventsByNameAndDate(nomPrenom, localdate);

                if (evenements.isEmpty()) {
                    return;
                }

                EventModel eventToUpdate = evenements.get(0);
                eventToUpdate.setTitle(nomPrenom);

                String calendarId = eventToUpdate.getCalendarId();

                if (calendarId == null || calendarId.isEmpty()) {
                    Message.showErrorMessage("Validation de l'option", "Impossible de retrouver le calendarId dans l'événement.");
                    return;
                }

                googleCalendarService.updateEvent(calendarId, eventToUpdate);

                Requete.executeUpdate("update location set Option = 0 where idLocation = " + id);

                SwingUtilities.invokeLater(() -> {
                    loadingDialog.setVisible(false);
                    loadingDialog.dispose();
                });

                Message.showValidMessage("Confirmation de la location", "La location a été confirmée.");

                detailLocation panel = new detailLocation(parentframe, id);
                Location.panel.removeAll();
                Location.panel.add(panel, BorderLayout.CENTER);
                parentframe.revalidate();
                parentframe.repaint();
            } catch (GeneralSecurityException | IOException ex) {
                Message.showErrorMessage("Erreur Google Calendar", ex.getMessage());
            }
        }).start();
    }
}