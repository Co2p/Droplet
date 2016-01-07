package co2p.droplet;

import android.content.Context;
import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by gordon on 03/01/16.
 */
public class WeatherTask {

    MainActivity mainActivity;


    public WeatherTask(MainActivity context) {
        mainActivity = context;
    }

    public void getWeather(double lat, double lon) {
        String url = "http://api.yr.no/weatherapi/locationforecast/1.9/?lat=" + lat + ";lon=" + lon;
        p.rint("starting async");
        AsyncTask task = new DownloadFilesTask().execute(url);
    }

    class DownloadFilesTask extends AsyncTask<String, Integer, Weather> {

        /**
         * A thread that downloads data from the specified url.
         * @param params Contains a url and a ID
         * @return A JsonObject with the downloaded data
         */
        protected Weather doInBackground(String... params) {
            Weather weather = null;

            try {
                p.rint(params[0]);
                InputStream stream = new URL(params[0]).openStream();

                XMLParser parser = new XMLParser();
                try {
                    weather = parser.parse(stream);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                stream.close();
                return weather;
            } catch (ConnectException | UnknownHostException e){
                p.rint("===========================Internet error===========================");
            } catch (IOException e){
                e.printStackTrace();
            }

            /*see return in IO try statement*/
            return null;
        }

        protected void onPostExecute(Weather result){
            mainActivity.updateTemperature(result);
        }
    }

}
