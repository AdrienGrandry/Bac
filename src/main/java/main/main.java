package main;

import start.Start;
import ressources.ColorXml;
import ressources.Variables;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class Main {
    static Variables variables = new Variables();
    static JPanel panel = new JPanel(new BorderLayout());
    static Start start = new Start(null);
    static ColorXml color = new ColorXml();

    public static void main(String[] args) {
        final JFrame frame = new JFrame("Gesti'Bac");
        frame.setBackground(Color.decode(color.xmlReader("background")));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setMinimumSize(new Dimension(800, 800));
        
        // Création d'un ActionListener pour les boutons du menu
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPanel(e.getActionCommand(), frame); // Passage de `frame` à la méthode loadPanel
            }
        };

        // Création du menu et ajout à la fenêtre
        Menu menu = new Menu(actionListener);
        frame.add(menu, BorderLayout.NORTH);
        
        // Création du panneau principal avec la référence à `frame`
        panel.setBorder(new EmptyBorder(50, 50, 10, 50));
        panel.setBackground(Color.decode(color.xmlReader("background")));
        panel.add(start, BorderLayout.CENTER);
        start.setBackground(Color.decode(color.xmlReader("background")));
        frame.add(panel, BorderLayout.CENTER);
        
        // Panneau de copyright
        JPanel panelCopyright = new JPanel();
        panelCopyright.setBackground(Color.decode(color.xmlReader("background")));
        JLabel copyright = new JLabel("Gesti'Bac by Adrien GRANDRY", SwingConstants.CENTER);
        copyright.setFont(new Font("Brush Script MT", Font.PLAIN, 35));
        copyright.setBackground(Color.RED);
        panelCopyright.add(copyright);
        frame.add(panelCopyright, BorderLayout.SOUTH);

        // Affichage de la fenêtre
        frame.setVisible(true);
    }
    
    // Méthode pour charger dynamiquement un panneau
    private static void loadPanel(String panelName, JFrame parentFrame) {
        try {
            switch (panelName) {
                case "Accueil":
                    loadSpecificPanel("start.Start", parentFrame);
                    break;
                case "Stock":
                    loadSpecificPanel("stock.Stock", parentFrame);
                    break;
                case "Produits":
                    loadSpecificPanel("produit.Produit", parentFrame);
                    break;
                case "Entrée / Sortie":
                    loadSpecificPanel("inputouput.inputouput", parentFrame);
                    break;
                case "Commande":
                    loadSpecificPanel("inputouput.inputouput", parentFrame);
                    break;
                case "Options":
                    loadSpecificPanel("options.Options", parentFrame);
                    break;
                case "Base de Données":
                    loadSpecificPanel("database.Database", parentFrame);
                    break;
                default:
                    System.out.println("Panneau non reconnu: " + panelName);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Méthode pour charger un panneau spécifique via réflexion
    private static void loadSpecificPanel(String className, JFrame parentFrame) throws Exception {
        // Charger la classe du panneau dynamiquement
        Class<?> panelClass = Class.forName(className);

        // Récupérer le constructeur qui prend un JFrame en paramètre
        Constructor<?> constructor = panelClass.getConstructor(JFrame.class);

        // Créer une instance du panneau
        Object panelObject = constructor.newInstance(parentFrame);

        // Vérifier si l'objet est bien une instance de JPanel
        if (panelObject instanceof JPanel) {
            JPanel loadedPanel = (JPanel) panelObject;

            // Remplacer uniquement le panneau central sans affecter le reste de la fenêtre
            panel.removeAll(); // Supprimer l'ancien panneau central
            panel.add(loadedPanel, BorderLayout.CENTER); // Ajouter le nouveau panneau au centre
            parentFrame.revalidate();  // Revalider la fenêtre pour qu'elle s'ajuste
            parentFrame.repaint();     // Rafraîchir la fenêtre
        } else {
            throw new IllegalArgumentException("La classe " + className + " n'est pas un JPanel.");
        }
    }
}