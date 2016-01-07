package co2p.droplet;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Calendar;

public class MainActivity extends Activity implements LocationListener {

    private Calendar calendar = Calendar.getInstance();
    private WeatherTask weather = new WeatherTask(this);
    private long updated = 25;
    protected LocationManager locationManager;
    private Resources res;
    private Location loc;
    private boolean first = true;
    private TextView temperature;
    private TextView location_text;
    private View background;
    private float[] currentBackgroundColor = new float[] {198, 0, 0};
    private String provider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperature = (TextView) findViewById(R.id.temperature);
        location_text = (TextView) findViewById(R.id.location);
// Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            loc = null;
        }

    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        float distance = 600;
        if (loc != null) {
            distance = location.distanceTo(loc);
        }
        if (distance > 500) {
            loc = location;
            onRefresh();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void onRefresh(View view){
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    public void onRefresh() {
        if (updated < calendar.get(Calendar.HOUR_OF_DAY) || updated == 25) {
            requestWeather(loc);
        } else {
            locationManager.removeUpdates(this);
        }
    }

    public void requestWeather(Location location){
        //TODO fix update time to 10 minutes
        if (updated >= calendar.get(Calendar.HOUR_OF_DAY) + 1 || updated == 25){
            System.out.println("getting weather");
            weather.getWeather(location.getLatitude(), location.getLongitude());
        }
    }

    public void updateTemperature(Weather object) {
        locationManager.removeUpdates(this);

        res = getResources();

        final float[] from = currentBackgroundColor;
        float temp = Float.parseFloat(object.temperature);
        final float[] to = co2p.droplet.Color.backgroundColor(temp, res);

        String location = "";
        background = (View) findViewById(R.id.background);

        //String simpleWeather[] = new String[1];
        updated = calendar.get(Calendar.HOUR_OF_DAY);

        Context context = getApplicationContext();
        View view = getWindow().getDecorView().findViewById(android.R.id.content);

        temperature.setText(temp + "°C");

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

}
