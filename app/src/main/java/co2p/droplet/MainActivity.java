package co2p.droplet;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class MainActivity extends Activity {

    private Calendar calendar = Calendar.getInstance();
    private Weather weather = new Weather(this);
    private long updated = 25;
    protected LocationManager locationManager;
    private Resources res;
    private View background;
    private MaterialProgressBar loading;
    private Location loc;
    private boolean first = true;
    private TextView temperature;
    private TextView location_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.cold);
        setContentView(R.layout.activity_main);

        temperature = (TextView) findViewById(R.id.temperature);
        location_text = (TextView) findViewById(R.id.location);
        background = (View) findViewById(R.id.background);

        loading = (MaterialProgressBar) findViewById(R.id.progressBar);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                loc = location;
                System.out.println("updated");
                if (first){
                    onRefresh();
                    first = false;
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);



    }

    public void onRefresh(View view){
        onRefresh();
    }

    public void onRefresh() {
        loading.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("refresh called");
                requestWeather(loc);
            }
        }, 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void requestWeather(Location location){
        if (updated >= calendar.get(Calendar.HOUR_OF_DAY) + 1 || updated == 25){
            System.out.println("getting weather");
            loading.setVisibility(View.VISIBLE);
            weather.getTemp(location.getLatitude(), location.getLongitude());
        }
        else
            loading.setVisibility(View.INVISIBLE);
    }

    public void updateTemperature(JSONObject object) {
        String simpleWeather[] = new String[1];
        updated = calendar.get(Calendar.HOUR_OF_DAY);
        try {
            System.out.println(object.getJSONArray("timeseries").get(0));
            weather.getLocation((double) object.get("lat"), (double) object.get("lon"));
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            simpleWeather = simplifyWeather(object.getJSONArray("timeseries"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //background.setBackgroundColor(res.getColor(R.color.color_primary_sunny));
/*
        if(Float.valueOf(simpleWeather[0]) <= -10) {
            getApplicationContext().setTheme(R.style.freezing);
            background.setBackgroundColor(res.getColor(R.color.color_primary_freezing));
        }
        if(Float.valueOf(simpleWeather[0]) <= 0) {
            getApplicationContext().setTheme(R.style.cold);
            background.setBackgroundColor(res.getColor(R.color.color_primary_cold));
        }
        if(Float.valueOf(simpleWeather[0]) <= 10) {
            getApplicationContext().setTheme(R.style.warm);
            background.setBackgroundColor(res.getColor(R.color.color_primary_warm));
        }
        if(Float.valueOf(simpleWeather[0]) <= 20) {
            getApplicationContext().setTheme(R.style.hot);
            background.setBackgroundColor(res.getColor(R.color.color_primary_hot));
        }
*/

        temperature.setText(simpleWeather[0].substring(0, simpleWeather[0].length()-2) + "°C");
        loading.setVisibility(View.INVISIBLE);
    }

    public void updateLocation(JSONObject object){
        String locationS = "";
        try {
            locationS = String.valueOf(object.getJSONArray("results").getJSONObject(2).getJSONArray("address_components").getJSONObject(0).get("short_name"));
            System.out.println(locationS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        location_text.setText(locationS);

    }

    private String[] simplifyWeather(JSONArray w) throws JSONException {
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        int date = calendar.get(Calendar.DATE);
        String simplified[] = new String[1];

        boolean not_found = true;

        int i = 0;
        JSONObject currentJson;
        while (not_found){
            currentJson = w.getJSONObject(i);
            if(date == Integer.parseInt(String.valueOf(currentJson.get("validTime")).substring(8, 10))) {
                if (time == Integer.parseInt(String.valueOf(currentJson.get("validTime")).substring(11, 13))) {
                    simplified[0] = String.valueOf(currentJson.get("t"));
                    not_found = false;
                }
            }
            i++;
        }
        return simplified;
    }

}
