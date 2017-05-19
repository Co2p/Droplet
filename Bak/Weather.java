package co2p.droplet;

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

    public Weather(MainActivity context){
        mainActivity = context;
    }


    public void getWeather(double lat, double lon){
        ID = "TEMP";
        lat = Double.valueOf(String.valueOf(lat).substring(0, 5));
        lon = Double.valueOf(String.valueOf(lon).substring(0, 5));

        //TODO units= can be metric or imperial (C or F), none means Kelvin
        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&units=metric&appid=key";
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


                InputStream is = new URL(params[0]).openStream();

                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String raw = readAll(rd);

                is.close();
                return raw;
            }
            catch (IOException e){
                e.printStackTrace();
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

        protected void onPostExecute(String result){
            if(ID.equals("TEMP")){
                try {
                    mainActivity.updateTemperature(new JSONObject(result));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
