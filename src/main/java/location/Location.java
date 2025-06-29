package location;

import location.start.StartLocation;
import options.ColorXml;
import ressources.Message;
import principale.MainFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public final class Location extends JFrame
{
    final private static JPanel panel = new JPanel(new BorderLayout());
    final private static StartLocation start = new StartLocation(null);
    final private static ColorXml color = new ColorXml();

    public Location() {
        createMainFrame();

        // Définition du listener pour les actions des boutons du menu
        final ActionListener actionListener = new ActionListener()
        {
            @Override
            public void actionPerformed(final ActionEvent actionEvent)
            {
                loadPanel(actionEvent.getActionCommand(), Location.this);
            }
        };

        // Création et ajout du menu
        final CreateMenuLocation menu = new CreateMenuLocation(actionListener);
        add(menu, BorderLayout.NORTH);

        // Panneau central
        panel.setBorder(new EmptyBorder(0, 50, 10, 50));
        panel.setBackground(Color.decode(color.xmlReader("background")));
        panel.add(start, BorderLayout.CENTER);
        start.setBackground(Color.decode(color.xmlReader("background")));
        add(panel, BorderLayout.CENTER);

        // Panneau de copyright
        addCopyrightPanel(this);

        // Affichage de la fenêtre
        setVisible(true);
    }

    private void createMainFrame() {
        setTitle("Gesti'Bac");
        setBackground(Color.decode(color.xmlReader("background")));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setSize(new Dimension(800, 800));

        final ImageIcon icon = new ImageIcon("ressources/img/logo.png");
        setIconImage(icon.getImage());
    }

    private static void addCopyrightPanel(final JFrame frame)
    {
        final JPanel panelCopyright = new JPanel();
        panelCopyright.setBackground(Color.decode(color.xmlReader("background")));
        final JLabel copyright = new JLabel("Gesti'Bac by Adrien GRANDRY", SwingConstants.CENTER);
        copyright.setFont(new Font("Brush Script MT", Font.PLAIN, 35));
        copyright.setBackground(Color.RED);
        panelCopyright.add(copyright);
        frame.add(panelCopyright, BorderLayout.SOUTH);
    }

    private void loadPanel(final String panelName, final JFrame parentFrame)
    {
        try
        {
            switch (panelName)
            {
                case "Enregistrer et retour":
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                    dispose(); // ferme Boisson
                    break;
                case "Enregister Location":
                    loadSpecificPanel("location.newLocation.newLocation", parentFrame);
                    break;
                case "Gérer Location":
                    loadSpecificPanel("location.produit.Produit", parentFrame);
                    break;
                case "Facture":
                    loadSpecificPanel("location.mouvement.Mouvement", parentFrame);
                    break;
                case "Tarifs":
                    loadSpecificPanel("location.inputouput.inputouput", parentFrame);
                    break;
                case "Base de Données":
                    loadSpecificPanel("location.database.Database", parentFrame);
                    break;
                default:
                    Message.showErrorMessage("Erreur de pannel", "Impossible d'ouvrir l'onglet de ce nom");
            }
        } catch (Exception Exception)
        {
            Message.showErrorMessage("Erreur de pannel", "Erreur lors du chargement de l'onglet");
        }
    }

    private static void loadSpecificPanel(final String className, final JFrame parentFrame) throws Exception
    {
        // Charger la classe dynamiquement
        final Class<?> panelClass = Class.forName(className);

        // Utilisation de la réflexion pour instancier le panel
        final Constructor<?> constructor = panelClass.getConstructor(JFrame.class);
        final Object panelObject = constructor.newInstance(parentFrame);

        // Vérification et ajout du panel au conteneur principal
        if (panelObject instanceof JPanel)
        {
            final JPanel loadedPanel = (JPanel) panelObject;

            panel.removeAll();
            panel.add(loadedPanel, BorderLayout.CENTER);
            parentFrame.revalidate();
            parentFrame.repaint();
        } else
        {
            throw new IllegalArgumentException("La classe " + className + " n'est pas un JPanel.");
        }
    }
}