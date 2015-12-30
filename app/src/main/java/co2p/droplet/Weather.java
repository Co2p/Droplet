package co2p.droplet;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

public class Weather {

    MainActivity mainActivity;
    String ID;
    private float temp;

    public Weather(MainActivity context){
        mainActivity = context;
    }

    public void getWeather(double lat, double lon){
        ID = "TEMP";
        lat = Double.valueOf(String.valueOf(lat).substring(0, 5));
        lon = Double.valueOf(String.valueOf(lon).substring(0, 5));

        String url = "http://opendata-download-metfcst.smhi.se/api/category/pmp1.5g/version/1/geopoint/lat/" + lat + "/lon/" + lon + "/data.json";
        AsyncTask task = new DownloadFilesTask().execute(url);
    }

    public void getWeatherNew(double lat, double lon){
        ID = "TEMP";
        lat = Double.valueOf(String.valueOf(lat).substring(0, 5));
        lon = Double.valueOf(String.valueOf(lon).substring(0, 5));

        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=60a2ddd82f61b00813d4526099b05a71";
        AsyncTask task = new DownloadFilesTask().execute(url);
    }

    public float gettmp() {
        return temp;
    }

    public void getLocation(double lat, double lon){
        ID = "LOCATION";
        String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lon + "&sensor=true";
        AsyncTask task = new DownloadFilesTask().execute(url);
    }

    class DownloadFilesTask extends AsyncTask<String, Integer, JSONObject> {

        /**
         * A thread that downloads data from the specified url.
         * @param params Contains a url and a ID
         * @return A JsonObject with the downloaded data
         */
        protected JSONObject doInBackground(String... params) {
            try {
                InputStream is = new URL(params[0]).openStream();
                try {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                    String jsonText = readAll(rd);

                    JSONObject json = new JSONObject(jsonText);

                    return json;

                } catch (JSONException e) {

                }
                finally {
                    is.close();
                }
            }
            catch (IOException e){
                System.out.println(e);
            }
            /*see return in Json catch statement*/
            return null;
        }

        public String readAll(Reader reader) throws IOException {
            StringBuilder sb = new StringBuilder();
            int copy;
            while ((copy = reader.read()) != -1) {
                sb.append((char) copy);
            }
            return sb.toString();
        }

        protected void onPostExecute(JSONObject result){
            if(ID.equals("TEMP")){
                mainActivity.updateTemperature(result);
            }
            if(ID.equals("LOCATION")){
                mainActivity.updateLocation(result);
            }
            if(ID.equals("NEW-TEMP")){
                try {
                    temp = result.getJSONObject("main").getLong("temp");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
