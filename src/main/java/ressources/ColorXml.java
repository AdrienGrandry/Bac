package ressources;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ColorXml
{

    private static final String XML_FILE_PATH = "config.xml";

    public String xmlReader(String colorName)
    {
        try
        {
            Document doc = loadXmlDocument();

            NodeList nodeList = doc.getElementsByTagName("color");
            for (int i = 0; i < nodeList.getLength(); i++)
            {
                Element colorElement = (Element) nodeList.item(i);
                String name = colorElement.getElementsByTagName("name").item(0).getTextContent();
                
                if (name.equals(colorName))
                {
                    return colorElement.getElementsByTagName("value").item(0).getTextContent();
                }
            }

            return "#FFFFFF";  
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "#FFFFFF";
        }
    }

    public void updateColorValue(String colorName, String newColorValue)
    {
        try
        {
            Document doc = loadXmlDocument();
            NodeList nodeList = doc.getElementsByTagName("color");
            boolean colorFound = false;

            for (int i = 0; i < nodeList.getLength(); i++)
            {
                Element colorElement = (Element) nodeList.item(i);
                String name = colorElement.getElementsByTagName("name").item(0).getTextContent();
                if (name.equals(colorName))
                {
                    colorElement.getElementsByTagName("value").item(0).setTextContent(newColorValue);
                    colorFound = true;
                    break;
                }
            }

            if (colorFound)
            {
                saveXmlDocument(doc);
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Color not found in XML file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to modify the XML file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<ColorInfo> getAllColors()
    {
        List<ColorInfo> colors = new ArrayList<>();
        try
        {
            File xmlFile = new File(XML_FILE_PATH);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("color");
            for (int i = 0; i < nodeList.getLength(); i++)
            {
                Element colorElement = (Element) nodeList.item(i);

                String name = colorElement.getElementsByTagName("name").item(0).getTextContent();
                String description = colorElement.getElementsByTagName("description").item(0).getTextContent();
                String value = colorElement.getElementsByTagName("value").item(0).getTextContent();
                
                colors.add(new ColorInfo(name, description, value));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return colors;
    }
    
    public String xmlLocated()
    {
        try
        {
            Document doc = loadXmlDocument();

            NodeList nodeList = doc.getElementsByTagName("located");
            
	        return (nodeList.item(0).getTextContent().trim());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }
    
    public void updateLocated(String path) {
        try {
            Document doc = loadXmlDocument();
            NodeList nodeList = doc.getElementsByTagName("located");

            if (nodeList.getLength() > 0) {
                nodeList.item(0).setTextContent(path);
                saveXmlDocument(doc); // Sauvegarde du document après modification
            } else {
                JOptionPane.showMessageDialog(null, "Balise <located> non trouvée dans le fichier XML.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Échec de la modification du fichier XML.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Document loadXmlDocument() throws Exception
    {
        File xmlFile = new File(XML_FILE_PATH);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();
        return doc;
    }

    private void saveXmlDocument(Document doc) throws Exception
    {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "0");
        
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(XML_FILE_PATH));
        transformer.transform(source, result);
    }
}