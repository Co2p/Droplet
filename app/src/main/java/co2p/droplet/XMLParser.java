package co2p.droplet;

import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Calendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by gordon on 06/01/16.
 */
public class XMLParser {

    public Weather parse(String xml) {
        String temperature;
        String windspeed;
        String winddir;

        Document doc = getDOM(xml);

        Calendar c = Calendar.getInstance();
        String current_time = c.get(Calendar.YEAR) + "-" + pad(c.get(Calendar.MONTH) + 1) + "-" +
                pad(c.get(Calendar.DAY_OF_MONTH)) + "T" + pad(c.get(Calendar.HOUR_OF_DAY)) + ":00:00Z";

        NodeList times = doc.getElementsByTagName("time");

        Element e = getNodebySubData(times, "from", current_time);

        e = getNodebyName(e.getChildNodes(), "location");
        temperature = getNodebyName(e.getChildNodes(), "temperature").getAttribute("value");
        windspeed = getNodebyName(e.getChildNodes(), "windSpeed").getAttribute("mps");
        winddir = getNodebyName(e.getChildNodes(), "windDirection").getAttribute("name");

        return new Weather(temperature, windspeed, winddir, current_time, "");
    }

    private Document getDOM(String xml) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();

            is.setCharacterStream(new StringReader(xml));

            doc = db.parse(is);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return doc;
    }

    private Element getNodebyName(NodeList nodes, String nodename) {
        Node node;
        for (int i = 0; i<nodes.getLength(); i++) {
            node = nodes.item(i);
            if(node.getNodeName().equalsIgnoreCase(nodename)) {
                return (Element) node;
            }
        }
        return null;
    }

    private Element getNodebySubData(NodeList nodes, String subtag, String subdata) {
        for (int i = 0; i<nodes.getLength(); i++) {
            Element e = (Element) nodes.item(i);

            if (e.getAttribute(subtag).equals(subdata)) {
                return e;
            }
        }
        return null;
    }

    private String pad(int num) {
        if(num < 10) {
            return "0" + num;
        }
        return String.valueOf(num);
    }
}
