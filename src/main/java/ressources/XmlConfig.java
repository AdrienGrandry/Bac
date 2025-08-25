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
            for (int i = 0; i < nodes.getLength(); i++)
            {
                Element agendaElement = (Element) nodes.item(i);
                String name = agendaElement.getElementsByTagName("value").item(0).getTextContent();
                agendas.add(name);
            }

        } catch (Exception e) {
            Message.showErrorMessage("Chargement des noms des agendas", "Impossible de lire les noms des agendas : " + e.getMessage());
        }
        return agendas;
    }

    public static String recupNomAgenda(String nom) {
        String value = "";
        try {
            Document document = loadXmlDocument();

            // Récupère tous les <agenda>
            NodeList nodes = document.getElementsByTagName("agenda");
            for (int i = 0; i < nodes.getLength(); i++)
            {
                Element agendaElement = (Element) nodes.item(i);
                String name = agendaElement.getElementsByTagName("name").item(0).getTextContent();
                if(name.equals(nom))
                {
                    value = agendaElement.getElementsByTagName("value").item(0).getTextContent();
                }
            }

        } catch (Exception e) {
            Message.showErrorMessage("Chargement des noms des agendas", "Impossible de lire les noms des agendas : " + e.getMessage());
        }
        return value;
    }

    public static String recupNomDepuisValue(String valueRecherche) {
        String name = "";
        try {
            Document document = loadXmlDocument();

            // Récupère tous les <agenda>
            NodeList nodes = document.getElementsByTagName("agenda");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element agendaElement = (Element) nodes.item(i);
                String value = agendaElement.getElementsByTagName("value").item(0).getTextContent();

                if (value.equals(valueRecherche)) {
                    name = agendaElement.getElementsByTagName("name").item(0).getTextContent();
                    break; // trouvé, on peut sortir de la boucle
                }
            }

        } catch (Exception e) {
            Message.showErrorMessage(
                    "Chargement des noms des agendas",
                    "Impossible de lire les noms des agendas : " + e.getMessage()
            );
        }
        return name;
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