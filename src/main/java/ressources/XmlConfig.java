package ressources;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XmlConfig
{

    private static final String XML_FILE_PATH = "./ressources/config.xml";

    public static String getPath(String colorName)
    {
        try
        {
            Document doc = loadXmlDocument();

            NodeList nodeList = doc.getElementsByTagName("path");
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

    public static List<String> lireAgendasDepuisXml() {
        List<String> agendas = new ArrayList<>();
        try {
            Document document = loadXmlDocument();

            // Récupère tous les <agenda>
            NodeList nodes = document.getElementsByTagName("agenda");
            for (int i = 0; i < nodes.getLength(); i++) {
                agendas.add(nodes.item(i).getTextContent().trim());
            }

        } catch (Exception e) {
            Message.showErrorMessage("Chargement des noms des agendas", "Impossible de lire les noms des agendas : " + e.getMessage());
        }
        return agendas;
    }

    private static Document loadXmlDocument() throws Exception
    {
        File xmlFile = new File(XML_FILE_PATH);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();
        return doc;
    }
}