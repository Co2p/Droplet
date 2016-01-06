package co2p.droplet;

import android.content.Context;
import android.os.AsyncTask;

import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gordon on 03/01/16.
 */
public class WeatherTask {

    public WeatherTask(Context context) {

    }

    public void getWeather(double lat, double lon) {
        String url = "http://api.yr.no/weatherapi/locationforecast/1.9/?lat=" + lat + ";lon=" + lon;
        p.rint("starting async");
        AsyncTask task = new DownloadFilesTask().execute(url);
    }

    class DownloadFilesTask extends AsyncTask<String, Integer, String> {

        /**
         * A thread that downloads data from the specified url.
         * @param params Contains a url and a ID
         * @return A JsonObject with the downloaded data
         */
        protected String doInBackground(String... params) {
            try {
                p.rint(params[0]);
                List entries = new ArrayList<>();
                InputStream stream = new URL(params[0]).openStream();

                XMLParser parser = new XMLParser();
                try {
                    entries = parser.parse(stream);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                stream.close();
                p.rint(entries.get(0).toString());
                return null;
            }
            catch (IOException e){
                e.printStackTrace();
            }

            /*see return in IO try statement*/
            return null;
        }

        protected void onPostExecute(String result){

        }
    }

}
