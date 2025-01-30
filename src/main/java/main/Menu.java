package main;
import style.Style;

import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.border.EmptyBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import ressources.ColorXml;

public class Menu extends JPanel {
    private static final long serialVersionUID = 1L;
    private final ArrayList<String> menuItems = new ArrayList<>();
    
    Style style = new Style();
	static ColorXml color = new ColorXml();

    public Menu(ActionListener actionListener)
    {
    	menuItems.add("Accueil");
    	menuItems.add("Entrée / Sortie");
    	menuItems.add("Stock");
    	menuItems.add("Produits");
    	menuItems.add("Commande");
    	menuItems.add("Options");

    	if (addDebugMenu())
    	{
    	    menuItems.add("Base de Données");
    	}
        
        setLayout(new GridLayout(1, 1, 0, 0));
        
        final JPanel panel = new JPanel();
        panel.setBackground(Color.decode(color.xmlReader("background_menu")));
        updateButtonLayout(panel);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        for (String menuItem : menuItems)
        {
            JButton button = new JButton(menuItem);
            Style.applyButtonStyle(button);
            button.addActionListener(actionListener);
            panel.add(button);
        }
        
        add(panel);
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateButtonLayout(panel);
            }
        });
    }
    
    private void updateButtonLayout(JPanel panel)
    {
        int columns = (getWidth() / 185) > 1 ? (getWidth() / 185) : 1;
        columns = Math.min(columns, menuItems.size());
        
        panel.setLayout(new GridLayout(0, columns, 20, 25));
        revalidate();
    }
    
    public boolean addDebugMenu()
    {
    	try
    	{
    	    File xmlFile = new File("config.xml");
    	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    	    Document doc = dBuilder.parse(xmlFile);
    	    doc.getDocumentElement().normalize();
    	    
    	    NodeList debugNodeList = doc.getElementsByTagName("debug");
    	    if (debugNodeList.getLength() > 0)
    	    {
    	        String debugValue = debugNodeList.item(0).getTextContent().trim();
    	        
    	        if(debugValue.equals("true"))
    	        {
    	        	return true;
    	        }
    	        else
    	        {
    	        	return false;
    	        }
    	    }
    	    else
    	    {
    	        return false;
    	    }
    	}
    	catch (Exception e)
    	{
    	    return false;
    	}
    }
}