package main;

import start.Start;
import ressources.ColorXml;
import ressources.Message;

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
	static JPanel panel = new JPanel(new BorderLayout());
	static Start start = new Start(null);
	static ColorXml color = new ColorXml();

	public static void main(String[] args)
	{
		// Initialisation de la fenêtre
		final JFrame frame = createMainFrame();

		// Définition du listener pour les actions des boutons du menu
		ActionListener actionListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				loadPanel(e.getActionCommand(), frame);
			}
		};

		// Création et ajout du menu
		Menu menu = new Menu(actionListener);
		frame.add(menu, BorderLayout.NORTH);

		// Panneau central
		panel.setBorder(new EmptyBorder(0, 50, 10, 50));
		panel.setBackground(Color.decode(color.xmlReader("background")));
		panel.add(start, BorderLayout.CENTER);
		start.setBackground(Color.decode(color.xmlReader("background")));
		frame.add(panel, BorderLayout.CENTER);

		// Panneau de copyright
		addCopyrightPanel(frame);

		// Affichage de la fenêtre
		frame.setVisible(true);
	}

	private static JFrame createMainFrame()
	{
		JFrame frame = new JFrame("Gesti'Bac");
		frame.setBackground(Color.decode(color.xmlReader("background")));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setMinimumSize(new Dimension(800, 800));

		ImageIcon icon = new ImageIcon("ressources/img/logo.png");
		frame.setIconImage(icon.getImage());

		return frame;
	}

	private static void addCopyrightPanel(JFrame frame)
	{
		JPanel panelCopyright = new JPanel();
		panelCopyright.setBackground(Color.decode(color.xmlReader("background")));
		JLabel copyright = new JLabel("Gesti'Bac by Adrien GRANDRY", SwingConstants.CENTER);
		copyright.setFont(new Font("Brush Script MT", Font.PLAIN, 35));
		copyright.setBackground(Color.RED);
		panelCopyright.add(copyright);
		frame.add(panelCopyright, BorderLayout.SOUTH);
	}

	private static void loadPanel(String panelName, JFrame parentFrame)
	{
		try
		{
			switch (panelName)
			{
			case "Accueil":
				loadSpecificPanel("start.Start", parentFrame);
				break;
			case "Stock":
				loadSpecificPanel("stock.Stock", parentFrame);
				break;
			case "Produits":
				loadSpecificPanel("produit.Produit", parentFrame);
				break;
			case "Mouvements":
				loadSpecificPanel("mouvement.Mouvement", parentFrame);
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
				Message.showErrorMessage("Erreur de pannel", "Impossible d'ouvrir l'onglet de ce nom");
			}
		} catch (Exception ex)
		{
			Message.showErrorMessage("Erreur de pannel", "Erreur lors du chargement de l'onglet");
		}
	}

	private static void loadSpecificPanel(String className, JFrame parentFrame) throws Exception
	{
		// Charger la classe dynamiquement
		Class<?> panelClass = Class.forName(className);

		// Utilisation de la réflexion pour instancier le panel
		Constructor<?> constructor = panelClass.getConstructor(JFrame.class);
		Object panelObject = constructor.newInstance(parentFrame);

		// Vérification et ajout du panel au conteneur principal
		if (panelObject instanceof JPanel)
		{
			JPanel loadedPanel = (JPanel) panelObject;

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