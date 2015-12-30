package co2p.droplet;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
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
    private Location loc;
    private boolean first = true;
    private TextView temperature;
    private TextView location_text;
    private View background;
    private float[] currentBackgroundColor = new float[] {198, 0, 0};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperature = (TextView) findViewById(R.id.temperature);
        location_text = (TextView) findViewById(R.id.location);

        onRefresh();
    }

    public void onRefresh(View view){
        onRefresh();
    }

    public void onRefresh() {
        final Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.NO_REQUIREMENT); // Choose your accuracy requirement.
        criteria.setSpeedRequired(true); // Choose if speed for first location fix is required.
        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(false);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                loc = location;
                System.out.println("updated location");
                System.out.println("long" + loc.getLongitude());
                System.out.println("lat" + loc.getLatitude());
                System.out.println("accuracy" + loc.getAccuracy());
                System.out.println("provider" + loc.getProvider());
                locationManager.requestLocationUpdates(locationManager.getBestProvider(criteria, true), 0, 0, this);

                locationManager.removeUpdates(this);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (loc == null) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println("refresh called");
                requestWeather(loc);
            }
        }).start();

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
            weather.getWeather(location.getLatitude(), location.getLongitude());
        }

    }

    public void updateTemperature(JSONObject object) {
        final float[] from = currentBackgroundColor;
        final float[] to =   new float[3];
        background = (View) findViewById(R.id.background);


        res = getResources();



        String simpleWeather[] = new String[1];
        updated = calendar.get(Calendar.HOUR_OF_DAY);
        try {
            System.out.println("Timeseries " + object.getJSONArray("timeseries").get(0));
            weather.getLocation((double) object.get("lat"), (double) object.get("lon"));
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            simpleWeather = simplifyWeather(object.getJSONArray("timeseries"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(simpleWeather);

        Context context = getApplicationContext();
        View view = getWindow().getDecorView().findViewById(android.R.id.content);
        System.out.println("Setting colour");
        if(Float.valueOf(simpleWeather[0]) <= -20) {
            Color.colorToHSV(res.getColor(R.color.cold5), to);
        }
        else if(Float.valueOf(simpleWeather[0]) <= -15) {
            Color.colorToHSV(res.getColor(R.color.cold4), to);
        }
        else if(Float.valueOf(simpleWeather[0]) <= -10) {
            Color.colorToHSV(res.getColor(R.color.cold3), to);
        }
        else if(Float.valueOf(simpleWeather[0]) <= -5) {
            Color.colorToHSV(res.getColor(R.color.cold4), to);
        }
        else if(Float.valueOf(simpleWeather[0]) <= 0) {
            Color.colorToHSV(res.getColor(R.color.cold1), to);
        }
        else if(Float.valueOf(simpleWeather[0]) <= 5) {
            Color.colorToHSV(res.getColor(R.color.warm1), to);
        }
        else if(Float.valueOf(simpleWeather[0]) <= 10) {
            Color.colorToHSV(res.getColor(R.color.warm2), to);
        }
        else if(Float.valueOf(simpleWeather[0]) <= 15) {
            Color.colorToHSV(res.getColor(R.color.warm3), to);
        }
        else if(Float.valueOf(simpleWeather[0]) <= 20) {
            Color.colorToHSV(res.getColor(R.color.warm4), to);
        }
        else if(Float.valueOf(simpleWeather[0]) <= 25) {
            Color.colorToHSV(res.getColor(R.color.warm5), to);
        }

        temperature.setText(simpleWeather[0].substring(0, simpleWeather[0].length() - 2) + "°C");

        currentBackgroundColor = to;

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);   // animate from 0 to 1
        anim.setDuration(2000);                              // for 300 ms

        final float[] hsv  = new float[3];                  // transition color
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Transition along each axis of HSV (hue, saturation, value)
                hsv[0] = from[0] + (to[0] - from[0]) * animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1]) * animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2]) * animation.getAnimatedFraction();

                background.setBackgroundColor(Color.HSVToColor(hsv));
                //System.out.println(hsv[0] + "," +  hsv[1] + "," +  hsv[2]);
            }
        });

        anim.start();
    }

    public void updateLocation(JSONObject object){
        String locationS = "";
        try {
            locationS = String.valueOf(object.getJSONArray("results").getJSONObject(2).getJSONArray("address_components").getJSONObject(0).get("short_name"));
            System.out.println(locationS);
            location_text.setText(locationS);
        } catch (JSONException e) {
            location_text.setText("Error finding location");
            System.out.println(object);
            e.printStackTrace();
        }


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
