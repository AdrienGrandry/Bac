package location.manageLocation;

import agenda.google.GoogleCalendarService;
import agenda.model.EventModel;
import location.Location;
import options.ColorXml;
import ressources.*;
import ressources.dataBase.QueryResult;
import ressources.dataBase.Requete;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class detailLocation extends JPanel {
    boolean isOption = false;
    boolean isCloture = false;
    JLabel lblDonneesA, lblDonneesB, lblDonneesC, lblDonneesD;
    String date, nomPrenom;
    int id, idSalle, idCafet;
    JFrame parentframe;
    JPanel tablePanel;

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
        scrollPaneInfo.setPreferredSize(new Dimension(400, hauteurParent / 4));
        scrollPaneInfo.setBorder(new LineBorder(Color.BLACK, 2));

        gbc.gridy = 0;
        gbc.weighty = 0;  // 25% hauteur pour le JScrollPane
        contenu.add(scrollPaneInfo, gbc);


        // Espace vertical
        gbc.gridy = 1;
        gbc.weighty = 0;
        contenu.add(Box.createRigidArea(new Dimension(0, 10)), gbc);

        JPanel panelBoisons = new JPanel();
        panelBoisons.setBackground(getBackground());
        panelBoisons.setLayout(new BorderLayout()); // très important pour BorderLayout

        JButton buttonBoisson = new JButton("Boissons");
        Style.applyButtonStyle(buttonBoisson);
        panelBoisons.add(buttonBoisson, BorderLayout.NORTH);
        buttonBoisson.addActionListener(e -> {
            if(!isCloture)
            {
                try {
                    manageBoisson fenetreBoissons = new manageBoisson(parentFrame, idSalle, idCafet);

                    // brancher le listener
                    fenetreBoissons.getValidationListener(() ->
                    {
                        tablePanel = Requete.executeQueryAndReturnPanel("SELECT libelle Boisson, Fournie, CASE WHEN Reprise == -1 THEN '' ELSE Reprise END Reprise, CASE WHEN Reprise == -1 THEN '' ELSE Fournie-Reprise END AS Consomé FROM MouvementLocation INNER JOIN produit ON produit.id = MouvementLocation.IdBoisson WHERE IdSalle = " + idSalle + " AND IdCafeteria = " + idCafet, 0, 0, "pair_impair");
                        panelBoisons.removeAll();
                        panelBoisons.add(buttonBoisson, BorderLayout.NORTH);
                        panelBoisons.add(tablePanel, BorderLayout.CENTER);
                        panelBoisons.revalidate();
                        panelBoisons.repaint();
                    });

                    fenetreBoissons.setVisible(true);

                } catch (SQLException ex) {
                    Message.showErrorMessage("Erreur", "Erreur de la base de données");
                }
            }
        });

        tablePanel = Requete.executeQueryAndReturnPanel("SELECT libelle Boisson, Fournie, CASE WHEN Reprise == -1 THEN '' ELSE Reprise END Reprise, CASE WHEN Reprise == -1 THEN '' ELSE Fournie-Reprise END AS Consomé FROM MouvementLocation INNER JOIN produit ON produit.id = MouvementLocation.IdBoisson WHERE IdSalle = " + idSalle + " AND IdCafeteria = " + idCafet, 0, 0, "pair_impair");
        panelBoisons.add(tablePanel, BorderLayout.CENTER);
        panelBoisons.revalidate();
        panelBoisons.repaint();

        JPanel panelCasse = new JPanel();

        JPanel panelDocuments = new JPanel();

        JPanel panelDonnees = new JPanel(new GridBagLayout());
        panelDonnees.setBackground(backgroundColor);
        addDataPanel(panelDonnees, 0, panelBoisons);
        addSpace(panelDonnees, 1, backgroundColor);
        addDataPanel(panelDonnees, 2, panelCasse);
        addSpace(panelDonnees, 3, backgroundColor);
        addDataPanel(panelDonnees, 4, panelDocuments);

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
        QueryResult queryResult = null;
        try {
            // === Requête pour Location uniquement ===
            queryResult = Requete.executeQuery("SELECT * FROM Location WHERE idLocation = " + id);
            if (queryResult.getResultSet().next()) {
                isOption = queryResult.getResultSet().getBoolean("Option");
                isCloture = queryResult.getResultSet().getBoolean("Cloturee");
                date = queryResult.getResultSet().getString("Date");
                nomPrenom = queryResult.getResultSet().getString("nom").toUpperCase() + " "
                        + queryResult.getResultSet().getString("prenom");

                idSalle = queryResult.getResultSet().getInt("IdSalle");
                idCafet = queryResult.getResultSet().getInt("IdCafeteria");

                // Label A et D → uniquement Location
                lblDonneesA.setText(createLabelA(queryResult.getResultSet()));
                lblDonneesD.setText(createLabelD(queryResult.getResultSet()));
            }
            queryResult.close();

            // === Requête pour Location + Cafeteria ===
            if (idCafet > 0) {
                queryResult = Requete.executeQuery(
                        "SELECT * FROM Location l " +
                                "JOIN Cafeteria c ON l.IdCafeteria = c.IdCafeteria " +
                                "WHERE l.idLocation = " + id
                );
                if (queryResult.getResultSet().next()) {
                    lblDonneesB.setText(createLabelB(queryResult.getResultSet()));
                }
                queryResult.close();
            }

            // === Requête pour Location + Salle ===
            if (idSalle > 0) {
                queryResult = Requete.executeQuery(
                        "SELECT * FROM Location l " +
                                "JOIN Salle s ON l.IdSalle = s.IdSalle " +
                                "WHERE l.idLocation = " + id
                );
                if (queryResult.getResultSet().next()) {
                    lblDonneesC.setText(createLabelC(queryResult.getResultSet()));
                }
                queryResult.close();
            }

        } catch (Exception ex) {
            Message.showErrorMessage("Erreur de la base de données", ex.getMessage());
        } finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }
    }

    private String createLabelA(ResultSet rs) throws SQLException {
        return String.format(
                "<html>%s %s<br>%s<br>%s %s<br>%s %s<br>%s<br><br>%s<br></html>",
                rs.getString("Nom"), rs.getString("Prenom"),
                rs.getString("Adresse"),
                rs.getString("CodePostal"), rs.getString("Localite"),
                rs.getString("Gsm"), rs.getString("Tel"),
                rs.getString("Email"),
                rs.getString("TypeEvenement")
        );
    }

    private String createLabelB(ResultSet rs) throws SQLException {
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

    private String createLabelC(ResultSet rs) throws SQLException {
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

    private String createLabelD(ResultSet rs) throws SQLException {
        String nom = rs.getString("NomResponsable");
        String prenom = rs.getString("PrenomResponsable");
        String gsm = rs.getString("GsmResponsable");
        String tel = rs.getString("TelResponsable");

        if (nom == null && prenom == null && gsm == null && tel == null) {
            return "<html>Responsable :<br>Responsable location</html>";
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

    private void addDataPanel(JPanel parent, int gridx, JPanel content) {
        content.setBorder(new LineBorder(Color.BLACK, 2));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        parent.add(content, gbc);
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

    private JPanel createButtonPanel(Color bg, boolean isOption){

        JButton optionValid = new JButton("Confirmer la location");
        Style.applyButtonStyle(optionValid);
        optionValid.addActionListener(e -> updateGoogleCalendar());

        JButton option = new JButton("Annuler l'option");
        Style.applyButtonStyle(option);
        option.addActionListener(e -> deleteLocation());

        JButton annuler = new JButton("Annuler la location");//Demander si facture
        Style.applyButtonStyle(annuler);
        annuler.addActionListener(e ->
        {
            boolean reponse = Message.askYesNoQuestion("Générer une facture ?", "Annulation de la location");

            if(reponse)
            {
                Message.showValidMessage("Facture", "Réalisation future de la facture...");
            }
            deleteLocation();
        });

        JButton facture = new JButton("Cloturer la location");//Demander si facture
        Style.applyButtonStyle(facture);
        facture.addActionListener(e -> clotureLocation());

        JPanel panelBtn = new JPanel();
        panelBtn.setBackground(bg);
        if (isOption) {
            panelBtn.add(optionValid);
            panelBtn.add(option);
        }
        else if(!isCloture)
        {
            panelBtn.add(annuler);
            panelBtn.add(facture);
        }
        return panelBtn;
    }

    private void clotureLocation() {
        if (verifIsNullBoisson()) {
            Message.showErrorMessage("Cloture de la location", "Toutes les boissons n'ont pas été contrôlées");
            return;
        }

        String dbPath = "jdbc:sqlite:" + XmlConfig.getPath("database");

        try (Connection conn = DriverManager.getConnection(dbPath)) {
            conn.setAutoCommit(false); // ⚡ démarre une transaction

            try (Statement stmt = conn.createStatement()) {
                // 1. Insert du mouvement
                stmt.executeUpdate("INSERT INTO mouvement (description, type) VALUES ('Location de " + nomPrenom + "', 'Location');");

                // 2. Récupérer l'id du mouvement inséré
                int idMouvement = 0;
                try (ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        idMouvement = rs.getInt(1);
                    }
                }

                // 3. Update MouvementLocation
                stmt.executeUpdate("UPDATE MouvementLocation SET idMouvement = " + idMouvement +
                        " WHERE IdSalle = " + idSalle + " AND IdCafeteria = " + idCafet);

                // 4. Récupérer les consommations et mettre à jour produit
                try (Statement selectStmt = conn.createStatement();
                     ResultSet rs = selectStmt.executeQuery(
                             "SELECT IdBoisson, (fournie - Reprise) AS consome " +
                                     "FROM MouvementLocation WHERE IdCafeteria = " + idCafet +
                                     " AND IdSalle = " + idSalle);
                     PreparedStatement updateStmt = conn.prepareStatement(
                             "UPDATE produit SET stock = stock - ? WHERE id = ?")) {

                    while (rs.next()) {
                        int idBoisson = rs.getInt("IdBoisson");
                        int consome = rs.getInt("consome");

                        updateStmt.setInt(1, consome);
                        updateStmt.setInt(2, idBoisson);
                        updateStmt.executeUpdate();
                    }
                }

                // 5. Clôturer la location
                stmt.executeUpdate("UPDATE location SET Cloturee = 1 WHERE idLocation = " + id);

                conn.commit(); // ✅ valide tout
            } catch (Exception e) {
                conn.rollback(); // ❌ annule tout si erreur
                throw e;
            }
        } catch (Exception e) {
            Message.showErrorMessage("Erreur", "Erreur : " + e.getMessage());
        }

        Message.showErrorMessage("Amélioration", "Facture disponible plus tard");

        showLocation showLocation = new showLocation(parentframe);
        Location.panel.removeAll();
        Location.panel.add(showLocation, BorderLayout.CENTER);
        parentframe.revalidate();
        parentframe.repaint();
    }

    private boolean verifIsNullBoisson()
    {
        QueryResult queryResult = null;
        boolean ret = false;
        try
        {
            queryResult = Requete.executeQuery("SELECT Reprise FROM MouvementLocation WHERE IdSalle = " + idSalle + " AND IdCafeteria = " + idCafet);

            while (queryResult.getResultSet().next() && !ret)
            {
                if(queryResult.getResultSet().getInt(1) == -1)
                {
                    ret = true;
                }
            }
        }
        catch(Exception e)
        {
            Message.showErrorMessage("Erreur", "Erreur : " + e.getMessage());
        }
        finally {
            if(queryResult != null)
            {
                queryResult.close();
            }
        }

        return ret;
    }

    private void updateGoogleCalendar() {
        LocalDate localdate = DateParser.parseStringToLocalDate(date);

        LoadingDialog loadingDialog = new LoadingDialog(parentframe, "Validation de l'option...");
        SwingUtilities.invokeLater(() -> loadingDialog.setVisible(true));

        new Thread(() -> {
            try {
                GoogleCalendarService googleCalendarService = new GoogleCalendarService();
                List<EventModel> evenements = googleCalendarService.findEventsByNameAndDate("(Option) " + nomPrenom, localdate);

                if (evenements.isEmpty()) {
                    Message.showErrorMessage("Google Calendar", "Aucun événement trouvé pour " + nomPrenom + " à la date " + localdate + ". La location est confirmée uniquement en base de données.");
                } else {
                    // On a trouvé au moins un événement → on modifie dans Google Calendar
                    EventModel eventToUpdate = evenements.get(0);
                    eventToUpdate.setTitle(nomPrenom);

                    String calendarId = eventToUpdate.getCalendarId();

                    if (calendarId == null || calendarId.isEmpty()) {
                        SwingUtilities.invokeLater(() -> {
                            loadingDialog.setVisible(false);
                            loadingDialog.dispose();
                            Message.showErrorMessage("Validation de l'option", "Impossible de retrouver le calendarId dans l'événement. La location est confirmée uniquement en base de données.");
                        });
                    } else {
                        googleCalendarService.updateEvent(calendarId, eventToUpdate);
                        Message.showValidMessage("Confirmation de la location", "L'événement a été confirmé dans Google Calendar et la location a été confirmée en base de données.");
                    }
                }
                Requete.executeUpdate("update location set Option = 0 where idLocation = " + id);

                // Rafraîchir l'écran dans les deux cas
                detailLocation panel = new detailLocation(parentframe, id);
                Location.panel.removeAll();
                Location.panel.add(panel, BorderLayout.CENTER);
                parentframe.revalidate();
                parentframe.repaint();

            } catch (GeneralSecurityException | IOException ex) {
                SwingUtilities.invokeLater(() -> {
                    loadingDialog.setVisible(false);
                    loadingDialog.dispose();
                    Message.showErrorMessage("Erreur Google Calendar", ex.getMessage());
                });
            }

            SwingUtilities.invokeLater(() -> {
                loadingDialog.setVisible(false);
                loadingDialog.dispose();
            });
        }).start();
    }

    private void deleteLocation() {
        LoadingDialog loadingDialog = new LoadingDialog(parentframe, "Annulation de l'option...");
        SwingUtilities.invokeLater(() -> loadingDialog.setVisible(true));

        new Thread(() -> {
            if (idSalle != 0) {
                Requete.executeUpdate("delete from Salle where IdSalle = " + idSalle);
            }
            if (idCafet != 0) {
                Requete.executeUpdate("delete from Cafeteria where IdCafeteria = " + idCafet);
            }
            Requete.executeUpdate("delete from Location where IdLocation = " + id);

            SwingUtilities.invokeLater(() -> {
                loadingDialog.setTitle("Supression de l'option dans l'agenda...");
            });

            if(isOption)
            {
                nomPrenom = "(Option) " + nomPrenom;
            }

            try {
                GoogleCalendarService googleCalendarService = new GoogleCalendarService();
                List<EventModel> evenements = googleCalendarService.findEventsByNameAndDate(nomPrenom, DateParser.parseStringToLocalDate(date));

                if (evenements.isEmpty()) {
                    return;
                }

                String eventId= evenements.get(0).getEventId();

                String calendarId = evenements.get(0).getCalendarId();

                if (calendarId == null || eventId == null) {
                    Message.showErrorMessage("Validation de l'option", "Impossible de retrouver le calendarId dans l'événement.");
                    return;
                }

                googleCalendarService.deleteEvent(calendarId, eventId);

                SwingUtilities.invokeLater(() -> {
                    loadingDialog.setVisible(false);
                    loadingDialog.dispose();
                });

                showLocation panel = new showLocation(parentframe);
                Location.panel.removeAll();
                Location.panel.add(panel, BorderLayout.CENTER);
                parentframe.revalidate();
                parentframe.repaint();

                Message.showValidMessage("Annulation de la location", "La location a été annulée !");
            } catch (GeneralSecurityException | IOException ex) {
                Message.showErrorMessage("Erreur Google Calendar", ex.getMessage());
            }

            SwingUtilities.invokeLater(() -> {
                loadingDialog.setVisible(false);
                loadingDialog.dispose();
            });
        }).start();
    }
}