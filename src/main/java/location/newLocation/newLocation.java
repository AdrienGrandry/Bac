package location.newLocation;

import ressources.DateParser;
import ressources.LoadingDialog;
import ressources.Message;
import ressources.dataBase.QueryResult;
import ressources.dataBase.Requete;
import ressources.Style;
import options.ColorXml;
import agenda.google.GoogleCalendarService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

import static ressources.DateParser.parseDateToIsoString;
import static ressources.DateParser.parseStringToLocalDate;

public class newLocation extends JPanel {

    public newLocation(final JFrame parentFrame) {
        final ColorXml color = new ColorXml();
        final Requete requete = new Requete();
        AtomicReference<GoogleCalendarService> calendarService = new AtomicReference<>();

        setBackground(Color.decode(color.xmlReader("background")));
        setLayout(new BorderLayout(10, 10));

        JLabel titre = new JLabel("Nouvelle location", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 40));
        titre.setForeground(Color.decode(color.xmlReader("foreground")));
        titre.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(titre, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.decode(color.xmlReader("background")));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        Font baseFont = new Font("Arial", Font.PLAIN, 16);

        // SECTION 1 : Informations Générales
        JPanel infoPanel = new JPanel(new GridLayout(12, 2, 20, 10));
        infoPanel.setOpaque(false);

        String[] labels = {"Nom :", "Prénom :", "Adresse :", "Code Postal :", "Localité :", "N° GSM :",
                "N° Téléphone :", "Email :", "Date :", "N° TVA :", "Type événement :"};
        JTextField[] fields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setFont(baseFont);
            fields[i] = new JTextField();
            infoPanel.add(label);
            infoPanel.add(fields[i]);
        }

        JLabel labelOption = new JLabel("Option : ");
        labelOption.setFont(baseFont);
        infoPanel.add(labelOption);

        JCheckBox boxOption = new JCheckBox();
        Style.applyCheckBoxStyle(boxOption);
        infoPanel.add(boxOption);



        contentPanel.add(sectionWrapper("Informations Générales", infoPanel, color));
        contentPanel.add(Box.createVerticalStrut(50));

        // SECTION 2 : Personne Responsable
        JPanel respPanel = new JPanel();
        respPanel.setLayout(new BoxLayout(respPanel, BoxLayout.Y_AXIS));
        respPanel.setOpaque(false);

        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkboxPanel.setOpaque(false);
        JCheckBox isOrganisateur = new JCheckBox("La personne responsable est l’organisateur");
        Style.applyCheckBoxStyle(isOrganisateur);
        isOrganisateur.setOpaque(false);
        isOrganisateur.setSelected(true);
        isOrganisateur.setFont(baseFont);
        checkboxPanel.add(isOrganisateur);
        respPanel.add(checkboxPanel);

        JPanel otherPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        otherPanel.setOpaque(false);

        String[] respLabels = {"Nom :", "Prénom :", "GSM :", "Téléphone :"};
        JTextField[] respFields = new JTextField[respLabels.length];

        for (int i = 0; i < respLabels.length; i++) {
            JLabel label = new JLabel(respLabels[i]);
            label.setFont(baseFont);
            respFields[i] = new JTextField();
            otherPanel.add(label);
            otherPanel.add(respFields[i]);
        }

        otherPanel.setVisible(false);
        respPanel.add(otherPanel);

        isOrganisateur.addActionListener(e -> otherPanel.setVisible(!isOrganisateur.isSelected()));
        contentPanel.add(sectionWrapper("Personne Responsable", respPanel, color));
        contentPanel.add(Box.createVerticalStrut(25));

        // SECTION 3 : Choix d’espace
        JPanel espacePanel = new JPanel();
        espacePanel.setLayout(new BoxLayout(espacePanel, BoxLayout.Y_AXIS));
        espacePanel.setOpaque(false);

        JCheckBox salle = new JCheckBox("Salle");
        JCheckBox cafet = new JCheckBox("Cafétéria");

        JCheckBox[] espaceChecks = {salle, cafet};
        for (JCheckBox cb : espaceChecks) {
            cb.setOpaque(false);
            Style.applyCheckBoxStyle(cb);
            cb.setFont(baseFont);
        }

        JPanel choixPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        choixPanel.setOpaque(false);
        choixPanel.add(salle);
        choixPanel.add(cafet);
        espacePanel.add(choixPanel);

        JPanel salleOptions = createYesNoPanel(
                baseFont,
                "Utilisation de la salle, de la cuisine avec la vaisselle pour 80 personnes",
                "Utilisation de la cuisine avec la vaisselle pour 160 personnes",
                "Utilisation de la cuisine avec la vaisselle pour 240 personnes",
                "Utilisation du bar avec boissons et fûts fournis par l'ASBL",
                "Utilisation du bar sans boissons fournies par l'ASBL",
                "Utilisation du bar et des pompes à bière sans boissons et fûts fournis par l'ASBL"
        );

        JPanel cafetOptions = createYesNoPanel(
                baseFont,
                "Utilisation de la cafétéria seule",
                "Utilisation de la cuisine et de la vaisselle pour 80 personnes",
                "Réunion en semaine (du lundi au jeudi) excepté les veille et jours fériés",
                "Utilisation du bar avec boissons fournies par l'asbl",
                "Utilisation du bar sans boissons fournies par l'asbl",
                "Utilisation du projecteur PC et de l'écran"
        );

        salleOptions.setVisible(false);
        cafetOptions.setVisible(false);

        espacePanel.add(salleOptions);
        espacePanel.add(Box.createVerticalStrut(10));
        espacePanel.add(Box.createVerticalStrut(10));
        espacePanel.add(cafetOptions);

        contentPanel.add(sectionWrapper("Choix d’espace à réserver", espacePanel, color));
        contentPanel.add(Box.createVerticalStrut(50));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        JButton enregistrer = new JButton("Enregistrer");
        Style.applyButtonStyle(enregistrer);
        enregistrer.setFont(baseFont);
        buttonPanel.add(enregistrer);
        buttonPanel.setVisible(false);

        enregistrer.addActionListener(e -> {
            StringBuilder requeteSalle = new StringBuilder();
            StringBuilder requeteCafet = new StringBuilder();
            StringBuilder requeteLocation = new StringBuilder();

            StringBuilder sb = new StringBuilder();
            sb.append("Informations Générales :\n");
            requeteLocation.append("INSERT INTO Location (Nom, Prenom, Adresse, CodePostal, Localite, Gsm, Tel, Email, Date, NumTVA, TypeEvenement,NomResponsable, PrenomResponsable, GsmResponsable, TelResponsable, IdSalle, IdCafeteria, Option) VALUES (");
            for (int i = 0; i < labels.length; i++) {
                sb.append(labels[i]).append(" ").append(fields[i].getText()).append("\n");
                if(i == 8)
                {
                    requeteLocation.append("'").append(parseDateToIsoString(fields[i].getText())).append("', ");
                }
                else
                {
                    requeteLocation.append("'").append(fields[i].getText()).append("', ");
                }
            }

            sb.append("\nPersonne Responsable :\n");
            if (isOrganisateur.isSelected()) {
                sb.append("Même que l'organisateur.\n");
                requeteLocation.append("null, null, null, null, ");
            } else {
                for (int i = 0; i < respLabels.length; i++) {
                    sb.append(respLabels[i]).append(" ").append(respFields[i].getText()).append("\n");
                }
                requeteLocation.append("'" + respFields[0].getText() + "', '" + respFields[1].getText() + "', '" + respFields[2].getText() + "', '" + respFields[3].getText() + "', ");
            }

            // Type de lieu
            boolean salleSelected = salle.isSelected();
            boolean cafetSelected = cafet.isSelected();
            String typeAgenda = "";

            if (salleSelected) {
                typeAgenda = "BAC - Salle";
                requeteSalle.append("INSERT INTO salle (SalleSeul, Cuisine160, Cuissine240, BarAsbl, BarVide, PompeVide) values (");
                requeteSalle.append(appendOptions(salleOptions));
                requeteSalle.append(");");
                if (cafetSelected)
                {
                    typeAgenda = "BAC - Salle/Cafétéria";
                    requeteCafet.append("INSERT INTO cafeteria (CafeteriaSeule, Cuisine, Reunion, BarAsbl, BarVide, Projecteur) values (");
                    requeteCafet.append(appendOptions(cafetOptions));
                    requeteCafet.append(");");
                }
            } else {
                typeAgenda = "BAC - Cafétéria";
                requeteCafet.append("INSERT INTO cafeteria (CafeteriaSeule, Cuisine, Reunion, BarAsbl, BarVide, Projecteur) values (");
                requeteCafet.append(appendOptions(cafetOptions));
                requeteCafet.append(");");
            }

            String date = DateParser.parseDateToIsoString(fields[8].getText());
            if(date == null)
            {
                Message.showErrorMessage("Erreur date", "Le format de la date est incorrect !");
                return;
            }

            LoadingDialog loadingDialog = new LoadingDialog(parentFrame, "Ajout dans l'agenda...");

            // Affiche la fenêtre de chargement dans l'EDT
            SwingUtilities.invokeLater(() -> loadingDialog.setVisible(true));

            // Traitement long dans un thread à part
            String finalTypeAgenda = typeAgenda;
            new Thread(() -> {
                try {
                    QueryResult queryResult = null;

                    try
                    {
                        queryResult = Requete.executeQuery("select count(*) from location where Date = '" + DateParser.parseDateToIsoString(fields[8].getText()) + "'");

                        if(queryResult.getResultSet().next())
                        {
                            if(queryResult.getResultSet().getInt(1) != 0)
                            {
                                int choix = JOptionPane.showConfirmDialog(
                                        parentFrame,
                                        "Une location existe déjà pour cette date. Continuer ?",
                                        "Confirmation",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE
                                );

                                if (choix == JOptionPane.NO_OPTION) {
                                    SwingUtilities.invokeLater(() ->{
                                        loadingDialog.setVisible(false);
                                    });
                                    return;
                                }
                            }
                        }
                    } catch (Exception ex) {
                        Message.showErrorMessage("Erreur de la base de données", ex.getMessage());
                    }
                    finally
                    {
                        if (queryResult != null)
                            queryResult.close();
                    }

                    String titreEvenement = "";
                    if(boxOption.isSelected())
                    {
                        titreEvenement += "(Option) ";
                    }
                    titreEvenement += fields[0].getText().toUpperCase() + " " + fields[0].getText();

                    calendarService.set(new GoogleCalendarService());
                    calendarService.get().safeAjouterEvenementPlageJours(parentFrame, finalTypeAgenda, titreEvenement,
                            sb.toString(), parseStringToLocalDate(date), parseStringToLocalDate(date).plusDays(1));

                    int idSalle = -1;
                    int idCafet = -1;
                    queryResult = null;

                    if(requeteSalle.length() != 0)
                    {
                        try
                        {
                            queryResult = Requete.executeQuery(requeteSalle.toString());
                        } catch (Exception ex) {
                            Message.showErrorMessage("Erreur de la base de données", ex.getMessage());
                        }
                        finally
                        {
                            if (queryResult != null)
                                queryResult.close();
                        }

                        queryResult = null;

                        try
                        {
                            queryResult = Requete.executeQuery("SELECT MAX(IdSalle) FROM salle");

                            if(queryResult.getResultSet().next())
                            {
                                idSalle = queryResult.getResultSet().getInt(1);
                            }
                        } catch (Exception ex) {
                            Message.showErrorMessage("Erreur de la base de données", ex.getMessage());
                        }
                        finally
                        {
                            if (queryResult != null)
                                queryResult.close();
                        }
                        requeteLocation.append(idSalle + ", ");
                    }
                    else
                    {
                        requeteLocation.append("null, ");
                    }

                    if(requeteCafet.length() != 0)
                    {
                        queryResult = null;
                        try
                        {
                            queryResult = Requete.executeQuery(requeteCafet.toString());
                        } catch (Exception ex) {
                            Message.showErrorMessage("Erreur de la base de données", ex.getMessage());
                        }
                        finally
                        {
                            if (queryResult != null)
                                queryResult.close();
                        }

                        queryResult = null;

                        try
                        {
                            queryResult = Requete.executeQuery("SELECT MAX(IdCafeteria) FROM Cafeteria");

                            if(queryResult.getResultSet().next())
                            {
                                idCafet = queryResult.getResultSet().getInt(1);
                            }
                        } catch (Exception ex) {
                            Message.showErrorMessage("Erreur de la base de données", ex.getMessage());
                        }
                        finally
                        {
                            if (queryResult != null)
                                queryResult.close();
                        }
                        requeteLocation.append(idCafet + ", ");
                    }
                    else
                    {
                        requeteLocation.append("null, ");
                    }

                    if(boxOption.isSelected())
                    {
                        requeteLocation.append("true);");
                    }
                    else
                    {
                        requeteLocation.append("false);");
                    }

                    queryResult = null;
                    try
                    {
                        queryResult = Requete.executeQuery(requeteLocation.toString());
                    } catch (Exception ex) {
                        Message.showErrorMessage("Erreur de la base de données", ex.getMessage());
                    }
                    finally
                    {
                        if (queryResult != null)
                            queryResult.close();
                    }

                    SwingUtilities.invokeLater(() -> {
                        loadingDialog.setVisible(false);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        loadingDialog.setVisible(false);
                        Message.showErrorMessage("Erreur", ex.getMessage());
                    });
                }
            }).start();
        });

        contentPanel.add(buttonPanel);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Gestion exclusive
        salle.addActionListener(e -> {
            if (salle.isSelected()) {
                salleOptions.setVisible(true);
                buttonPanel.setVisible(true);
            } else {
                salleOptions.setVisible(false);
                if(!cafet.isSelected()) {
                    buttonPanel.setVisible(false);
                }
            }
        });

        cafet.addActionListener(e -> {
            if (cafet.isSelected()) {
                cafetOptions.setVisible(true);
                buttonPanel.setVisible(true);
            } else {
                cafetOptions.setVisible(false);
                if(!salle.isSelected()) {
                    buttonPanel.setVisible(false);
                }
            }
        });
    }

    private JPanel sectionWrapper(String title, JPanel innerPanel, ColorXml color) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        JLabel sectionTitle = new JLabel(title);
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 20));
        sectionTitle.setForeground(Color.decode(color.xmlReader("foreground")));
        sectionTitle.setBorder(new EmptyBorder(10, 0, 10, 0));
        wrapper.add(sectionTitle, BorderLayout.NORTH);
        wrapper.add(innerPanel, BorderLayout.CENTER);
        wrapper.setBorder(new EmptyBorder(10, 0, 10, 0));
        return wrapper;
    }

    private JPanel createYesNoPanel(Font font, String... questions) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        for (String question : questions) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            row.setOpaque(false);

            JLabel label = new JLabel(question);
            label.setFont(font);

            JRadioButton oui = new JRadioButton("Oui");
            JRadioButton non = new JRadioButton("Non", true);

            oui.setFont(font);
            non.setFont(font);

            Style.applyRadioStyle(oui);
            Style.applyRadioStyle(non);

            oui.setOpaque(false);
            non.setOpaque(false);

            ButtonGroup group = new ButtonGroup();
            group.add(oui);
            group.add(non);

            row.add(label);
            row.add(oui);
            row.add(non);

            panel.add(row);
        }

        return panel;
    }

    private StringBuilder appendOptions(JPanel optionsPanel) {
        StringBuilder sb = new StringBuilder();
        for (Component c : optionsPanel.getComponents()) {
            if (c instanceof JPanel) {
                Component[] row = ((JPanel) c).getComponents();
                if (row.length >= 3 && row[0] instanceof JLabel && row[1] instanceof JRadioButton) {
                    String question = ((JLabel) row[0]).getText();
                    boolean selected = ((JRadioButton) row[1]).isSelected(); // bouton "Oui"
                    sb.append(selected ? "true" : "false").append(",");
                }
            }
        }
        sb.delete(sb.length() - 1 , sb.length());
        return sb;
    }
}