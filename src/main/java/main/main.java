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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class Main
{
    static Variables variables = new Variables();
    static JPanel panel = new JPanel(new BorderLayout());
    static Start start = new Start(null);
    static ColorXml color = new ColorXml();

    public static void main(String[] args)
    {
        final JFrame frame = new JFrame("Gesti'Bac");
        frame.setBackground(Color.decode(color.xmlReader("background")));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setMinimumSize(new Dimension(800, 800));
        
        ImageIcon icon = new ImageIcon("./logo.png");
        frame.setIconImage(icon.getImage());
        
        ActionListener actionListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                loadPanel(e.getActionCommand(), frame); 
            }
        };

        Menu menu = new Menu(actionListener);
        frame.add(menu, BorderLayout.NORTH);
        
        panel.setBorder(new EmptyBorder(50, 50, 10, 50));
        panel.setBackground(Color.decode(color.xmlReader("background")));
        panel.add(start, BorderLayout.CENTER);
        start.setBackground(Color.decode(color.xmlReader("background")));
        frame.add(panel, BorderLayout.CENTER);
        
        JPanel panelCopyright = new JPanel();
        panelCopyright.setBackground(Color.decode(color.xmlReader("background")));
        JLabel copyright = new JLabel("Gesti'Bac by Adrien GRANDRY", SwingConstants.CENTER);
        copyright.setFont(new Font("Brush Script MT", Font.PLAIN, 35));
        copyright.setBackground(Color.RED);
        panelCopyright.add(copyright);
        frame.add(panelCopyright, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
    
    private static void loadPanel(String panelName, JFrame parentFrame)
    {
        try
        {
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
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private static void loadSpecificPanel(String className, JFrame parentFrame) throws Exception
    {
        Class<?> panelClass = Class.forName(className);

        Constructor<?> constructor = panelClass.getConstructor(JFrame.class);

        Object panelObject = constructor.newInstance(parentFrame);

        if (panelObject instanceof JPanel) {
            JPanel loadedPanel = (JPanel) panelObject;

            panel.removeAll();
            panel.add(loadedPanel, BorderLayout.CENTER);
            parentFrame.revalidate();
            parentFrame.repaint();
        }
        else
        {
            throw new IllegalArgumentException("La classe " + className + " n'est pas un JPanel.");
        }
    }
}