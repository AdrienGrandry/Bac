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