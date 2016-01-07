package co2p.droplet;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * Created by gordon on 06/01/16.
 */
public class XMLParser {

    // We don't use namespaces
    private static final String ns = null;

    public Weather parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private Weather readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        String name;
        Calendar c = Calendar.getInstance();
        String current_time = c.get(Calendar.YEAR) + "-" + pad(c.get(Calendar.MONTH) + 1) + "-" +
                pad(c.get(Calendar.DAY_OF_MONTH)) + "T" + pad(c.get(Calendar.HOUR_OF_DAY)) + ":00:00Z";


        parser.require(XmlPullParser.START_TAG, ns, "weatherdata");
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            name = parser.getName();
            if (name != null && name.equals("time")) {
                String time = parser.getAttributeValue(null, "from");

                if (time != null && time.contains(current_time)) {
                    p.rint("time is: " + current_time);
                    p.rint("testing time: " + time);
                    while (parser.next() != XmlPullParser.END_TAG) {
                        name = parser.getName();
                        if (name != null && name.equals("temperature")) {
                            String temperature = parser.getAttributeValue(null, "value");
                            //p.rint("temp: " +  temperature);
                            return new Weather(temperature, null, null);
                        }
                    }
                }
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
