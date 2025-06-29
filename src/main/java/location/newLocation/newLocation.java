package location.newLocation;

import ressources.Message;
import ressources.dataBase.Requete;
import ressources.Style;
import options.ColorXml;
import agenda.google.GoogleCalendarService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

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
        JPanel infoPanel = new JPanel(new GridLayout(11, 2, 20, 10));
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
            StringBuilder sb = new StringBuilder();
            sb.append("Informations Générales :\n");
            for (int i = 0; i < labels.length; i++) {
                sb.append(labels[i]).append(" ").append(fields[i].getText()).append("\n");
            }

            sb.append("\nPersonne Responsable :\n");
            if (isOrganisateur.isSelected()) {
                sb.append("Même que l'organisateur.\n");
            } else {
                for (int i = 0; i < respLabels.length; i++) {
                    sb.append(respLabels[i]).append(" ").append(respFields[i].getText()).append("\n");
                }
            }

            // Type de lieu
            boolean salleSelected = salle.isSelected();
            boolean cafetSelected = cafet.isSelected();
            String typeAgenda = "";

            if (salleSelected || cafetSelected) {
                sb.append("\nType : ");
                if (salleSelected && cafetSelected) {
                    sb.append("Salle + Cafétéria\n");
                    typeAgenda = "BAC - Salle/Cafétéria";
                } else if (salleSelected) {
                    sb.append("Salle\n");
                    typeAgenda = "BAC - Salle";
                } else {
                    sb.append("Cafétéria\n");
                    typeAgenda = "BAC - Cafétéria";
                }
            } else {
                sb.append("\nAucun espace sélectionné.\n");
            }

            // Affichage des options selon sélection
            if (salleSelected) {
                sb.append("\nOptions Salle :\n");
                appendOptions(salleOptions, sb);
            }

            if (cafetSelected) {
                sb.append("\nOptions Cafétéria :\n");
                appendOptions(cafetOptions, sb);
            }

            LocalDate date = DateParser.parseDate(fields[8].getText());

            try {
                calendarService.set(new GoogleCalendarService());
                calendarService.get().ajouterEvenementPlageJours(typeAgenda,
                        fields[0].getText().toUpperCase() + " " + fields[0].getText(),
                        sb.toString(), date, date.plusDays(1));

                Message.showValidMessage("Validation de la location",
                        "La location a été ajoutée à l'agenda");
            } catch (GeneralSecurityException | IOException ex) {
                throw new RuntimeException(ex);
            }
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

    private void appendOptions(JPanel optionsPanel, StringBuilder sb) {
        for (Component c : optionsPanel.getComponents()) {
            if (c instanceof JPanel) {
                Component[] row = ((JPanel) c).getComponents();
                if (row.length >= 3 && row[0] instanceof JLabel && row[1] instanceof JRadioButton) {
                    String question = ((JLabel) row[0]).getText();
                    boolean selected = ((JRadioButton) row[1]).isSelected(); // bouton "Oui"
                    sb.append("- ").append(question).append(" : ").append(selected ? "Oui" : "Non").append("\n");
                }
            }
        }
    }

}
