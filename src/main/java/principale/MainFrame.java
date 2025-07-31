package principale;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

import agenda.app.GoogleAgendaStyleCalendar;
import boisson.Boisson;
import location.Location;
import ressources.*;
import options.Options;
import options.ColorXml;

public class MainFrame extends JFrame {
    public MainFrame() {
        ColorXml colorXml = new ColorXml();

        setTitle("Gesti'Bac");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        setSize(800, 600);

        // === Menu de gauche ===
        JPanel panelGauche = new JPanel(new BorderLayout());
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(Color.LIGHT_GRAY);
        menuPanel.setPreferredSize(new Dimension(250, getHeight()));

        JButton btnAgenda = new JButton("Agenda");
        JButton btnCompta = new JButton("Gestion Locations");
        JButton btnBoissons = new JButton("Boissons");
        JButton btnOption = new JButton("Options");
        JButton btnQuitter = new JButton("Quitter");

        JButton[] boutons = { btnAgenda, btnCompta, btnBoissons, btnOption, btnQuitter };
        for (JButton btn : boutons) {
            Style.applyButtonStyle(btn);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(200, 50));
            btn.setFocusable(false);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            menuPanel.add(btn);
        }

        int nbMailNonLu = 0, nbMailEnvoyer = 0, nbMailRecu = 0;

        try {
            int list[] = GmailStats.getNbMail();
            nbMailNonLu = list[0];
            nbMailEnvoyer = list[1];
            nbMailRecu = list[2] - nbMailEnvoyer;

            JPanel mailPanel = new JPanel();
            mailPanel.setBackground(Color.LIGHT_GRAY);
            mailPanel.setPreferredSize(new Dimension(250, 170));
            mailPanel.setMaximumSize(new Dimension(250, 170));
            mailPanel.setMinimumSize(new Dimension(250, 170));

            Date dt = new Date();
            int year = dt.getYear();
            int current_Year = year + 1900;

            JLabel LabelMailNonLu = createStyledLabel("Nombre de mail non lu : " + nbMailNonLu);
            JLabel LabelMailEnvoyer = createStyledLabel("Nombre de mail envoyé (" + current_Year + ") : " + nbMailEnvoyer);
            JLabel LabelMailRecu = createStyledLabel("Nombre de mail reçu (" + current_Year + ") : " + nbMailRecu);

            if(nbMailNonLu > 0)
            {
                LabelMailNonLu.setBackground(Color.RED);
            }
            else
            {
                LabelMailNonLu.setBackground(Color.GREEN);
            }
            LabelMailNonLu.setOpaque(true);

            LabelMailNonLu.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    try {
                        Desktop desktop = Desktop.getDesktop();
                        if (desktop.isSupported(Desktop.Action.BROWSE)) {
                            desktop.browse(new java.net.URI("https://mail.google.com"));
                        } else {
                            Message.showErrorMessage("Ouverture de Gmail", "Impossible d'ouvrir le navigateur.");
                        }
                    } catch (Exception ex) {
                        Message.showErrorMessage("Ouverture de Gmail", "Impossible d'ouvrir Gmail.");
                    }
                }

                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    LabelMailNonLu.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            });

            mailPanel.add(LabelMailNonLu);
            mailPanel.add(LabelMailEnvoyer);
            mailPanel.add(LabelMailRecu);

            panelGauche.add(mailPanel, BorderLayout.SOUTH);
        } catch (Exception ignored) {}

        btnBoissons.addActionListener(e -> {
            Boisson boisson = new Boisson();
            boisson.setVisible(true);
            this.dispose();
        });

        btnCompta.addActionListener(e -> {
            Location loacation = new Location(null);
            loacation.setVisible(true);
            this.dispose();
        });

        btnAgenda.addActionListener(e -> {
            new Thread(() -> {
                GoogleAgendaStyleCalendar agenda = new GoogleAgendaStyleCalendar(this);

                if (agenda.isLoaded) {
                    SwingUtilities.invokeLater(() -> {
                        agenda.setVisible(true);
                        this.dispose();
                    });
                }
            }).start();
        });

        btnOption.addActionListener(e -> {
            Options options = new Options();
            options.setVisible(true);
            this.dispose();
        });
        btnQuitter.addActionListener(e -> System.exit(0));

        // === Panneau qui contient titre + image ===
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.decode(colorXml.xmlReader("background")));

        JLabel titleLabel = new JLabel("Gesti'Bac");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon icon = new ImageIcon(XmlConfig.getPath("logo"));
        JLabel imageLabel = new JLabel(icon);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(imageLabel);

        JPanel imagePanel = new JPanel(new GridBagLayout());
        imagePanel.setBackground(Color.decode(colorXml.xmlReader("background")));
        imagePanel.add(contentPanel);

        panelGauche.add(menuPanel, BorderLayout.CENTER);
        add(panelGauche, BorderLayout.WEST);
        add(imagePanel, BorderLayout.CENTER);

        setIconImage(icon.getImage());
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setPreferredSize(new Dimension(250, 50));
        label.setMaximumSize(new Dimension(250, 50));
        label.setMinimumSize(new Dimension(250, 50));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 15));

        return label;
    }

}