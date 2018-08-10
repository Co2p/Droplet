package co2p.droplet

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import java.io.IOException
import java.io.StringReader
import java.util.Calendar
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

/**
 * Created by gordon on 06/01/16.
 */
class XMLParser {

    fun parse(xml: String): Weather {
        val temperature: String
        val windspeed: String
        val winddir: String

        val doc = getDOM(xml)

        val c = Calendar.getInstance()
        val current_time = c.get(Calendar.YEAR).toString() + "-" + pad(c.get(Calendar.MONTH) + 1) + "-" +
                pad(c.get(Calendar.DAY_OF_MONTH)) + "T" + pad(c.get(Calendar.HOUR_OF_DAY)) + ":00:00Z"

        val times = doc.getElementsByTagName("time")

        var e: Element = getNodebySubData(times, "from", current_time) as Element

        e = getNodebyName(e.childNodes, "location") as Element
        temperature = getNodebyName(e.childNodes, "temperature")!!.getAttribute("value")
        windspeed = getNodebyName(e.childNodes, "windSpeed")!!.getAttribute("mps")
        winddir = getNodebyName(e.childNodes, "windDirection")!!.getAttribute("name")

        return Weather(temperature, windspeed, winddir, current_time, "")
    }

    private fun getDOM(xml: String): Document {
        var doc: Document? = null
        val dbf = DocumentBuilderFactory.newInstance()

        try {
            val db = dbf.newDocumentBuilder()
            val `is` = InputSource()

            `is`.characterStream = StringReader(xml)

            doc = db.parse(`is`)

        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        } catch (e: SAXException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return doc!!
    }

    private fun getNodebyName(nodes: NodeList, nodename: String): Element? {
        var node: Node
        for (i in 0..nodes.length - 1) {
            node = nodes.item(i)
            if (node.nodeName.equals(nodename, ignoreCase = true)) {
                return node as Element
            }
        }
        return null
    }

    private fun getNodebySubData(nodes: NodeList, subtag: String, subdata: String): Element? {
        for (i in 0..nodes.length - 1) {
            val e = nodes.item(i) as Element

            if (e.getAttribute(subtag) == subdata) {
                return e
            }
        }
        return null
    }

    private fun pad(num: Int): String {
        if (num < 10) {
            return "0" + num
        }
        return num.toString()
    }
}
