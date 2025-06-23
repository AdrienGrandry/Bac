package boisson;

import ressources.Style;

import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.border.EmptyBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import options.ColorXml;

/**
* @author Grandry Adrien
*/
public class CreateMenuLocation extends JPanel
{
	private static final long serialVersionUID = 1L;
	/**
	 * Variable du tableau des items du menu.
	 */
	/* default */private final List<String> menuItems = new ArrayList<>();

	/**
	 * Variable de la classe style.
	 */
	private Style style = new Style();
	/**
	 * Variable de la classe ColorXml.
	 */
	final private static ColorXml color = new ColorXml();

	public CreateMenuLocation(ActionListener actionListener)
	{
		// Initialisation des éléments du menu
		initMenuItems();
		// Ajout conditionnel de l'élément "Base de Données" si le mode debug est activé
		if (addDebugMenu())
		{
			menuItems.add("Base de Données");
		}

		// Layout et panneau
		setLayout(new GridLayout(1, 1, 0, 0));
		final JPanel panel = new JPanel();
		panel.setBackground(Color.decode(color.xmlReader("background_menu")));
		updateButtonLayout(panel);
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Création des boutons pour chaque élément du menu
		for (String menuItem : menuItems)
		{
			JButton button = new JButton(menuItem);
			Style.applyButtonStyle(button);
			button.addActionListener(actionListener);
			panel.add(button);
		}

		add(panel);

		// Ajustement des boutons lors du redimensionnement de la fenêtre
		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				updateButtonLayout(panel);
			}
		});
	}

	// Initialisation des éléments du menu
	private void initMenuItems()
	{
		/*
		*AFFICHER PDF INCLUS
		 */
		menuItems.add("Enregistrer et retour");
		menuItems.add("Enregister Location");//AJout dans ajenda + db de la loc avec options + choix 1 ou deux salles
		menuItems.add("Gérer Location");
		//liste boisson(affichée ou enregistrer) + document location + info choix loc future ou cloturée
		//pouvoir annuler location --> remboursement
		menuItems.add("Facture");//Généré facture : location  ou caution
		menuItems.add("Tarifs");//Changer le prix
	}

	// Mise à jour de la disposition des boutons selon la taille de la fenêtre
	private void updateButtonLayout(JPanel panel)
	{
		int columns = (getWidth() / 145) > 1 ? (getWidth() / 145) : 1;
		columns = Math.min(columns, menuItems.size());

		panel.setLayout(new GridLayout(0, columns, 20, 25));
		revalidate();
	}

	// Lecture du fichier XML et vérification de l'option debug
	public boolean addDebugMenu()
	{
		try
		{
			File xmlFile = new File("ressources/config.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();

			NodeList debugNodeList = doc.getElementsByTagName("debug");
			if (debugNodeList.getLength() > 0)
			{
				String debugValue = debugNodeList.item(0).getTextContent().trim();

				return debugValue.equals("true");
			} else
			{
				return false;
			}
		} catch (Exception e)
		{
			e.getMessage();
			return false;
		}
	}
}
