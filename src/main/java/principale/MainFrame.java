package principale;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.security.GeneralSecurityException;

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
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(Color.LIGHT_GRAY);
        menuPanel.setPreferredSize(new Dimension(250, getHeight()));

        JButton btnAgenda = new JButton("Agenda");
        JButton btnCompta = new JButton("Gestion Locations");
        JButton btnBoissons = new JButton("Boissons");
        JButton btnOption = new JButton("Option");
        JButton btnQuitter = new JButton("Enregistrer et Quitter");

        JButton[] boutons = { btnAgenda, btnCompta, btnBoissons, btnOption, btnQuitter };
        for (JButton btn : boutons) {
            Style.applyButtonStyle(btn);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(200, 50));
            btn.setFocusable(false);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            menuPanel.add(btn);
        }

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

        add(menuPanel, BorderLayout.WEST);
        add(imagePanel, BorderLayout.CENTER);

        setIconImage(icon.getImage());
    }
}