package co2p.droplet;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Arrays;
import java.util.Calendar;

import static co2p.droplet.Color.backgroundColor;

public class MainActivity extends Activity {

    private Calendar calendar = Calendar.getInstance();
    private long updated = 25;
    private TextView temperature_text;
    private TextView location_text;
    private TextView state_text;
    private ProgressBar spinner;
    private View background;
    private float[] currentBackgroundColor = new float[]{0, 0, 0};
    private boolean animating = false;
    private boolean searching = false;
    private Location location;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 57;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperature_text = (TextView) findViewById(R.id.temperature);
        location_text = (TextView) findViewById(R.id.location);
        state_text = (TextView) findViewById(R.id.state);
        spinner = (ProgressBar) findViewById(R.id.progress);

    }

    public void setLocation(Location l) {
        location = l;
    }

    public void getLocation() {
        if (!searching) {
            searching = true;
            spinner.setVisibility(View.VISIBLE);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                searching =false;
                getLocation();
            }


            // Acquire a reference to the system Location Manager
            final LocationManager locationManager =
                    (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            try {
                requestWeather(location);
            } catch (NullPointerException e) {
                location_text.setText(R.string.locationerror);
                temperature_text.setText("???");
                animateBackground(currentBackgroundColor, backgroundColor(50, getBaseContext()), 2000);
            }

            // Define a listener that responds to location updates
            final LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    //locationManager.removeUpdates(this);
                    searching = false;
                    setLocation(location);

                    requestWeather(location);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            //Refresh animation
        }

    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        requestWeather(location);
        getLocation();
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onRefresh(View view) {
        requestWeather(location);
        getLocation();
    }

    public void requestWeather(Location location) {
        //TODO fix update time to 10 minutes
        if (location != null && (updated >= calendar.get(Calendar.HOUR_OF_DAY) + 1 || updated == 25)) {
            spinner.setVisibility(View.VISIBLE);

            //weather.getWeather(location.getLatitude(), location.getLongitude());

            RequestQueue request = Volley.newRequestQueue(this);

            String url = "http://api.yr.no/weatherapi/locationforecast/1.9/?lat=" + location.getLatitude() + ";lon=" + location.getLongitude();

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
                XMLParser parser = new XMLParser();
                Weather weather = parser.parse(response);
                updateTemperature(weather);
            }, error -> location_text.setText(R.string.interneterror));

            request.add(stringRequest);

        } else {
            spinner.setVisibility(View.INVISIBLE);
        }

    }

    public void updateTemperature(Weather object) {
        final float[] from = currentBackgroundColor;
        float temp = Float.parseFloat(object.temperature);
        final float[] to = backgroundColor(temp, getBaseContext());

        String location = "";
        background = findViewById(R.id.background);

        //String simpleWeather[] = new String[1];
        updated = calendar.get(Calendar.HOUR_OF_DAY);


        temperature_text.setText(Math.round(temp) + getString(R.string.celcius));
        location_text.setText(location);
        state_text.setText(object.winddirection + ", " + object.windspeed + "m/s");

        animateBackground(from, to, 2000);

        spinner.setVisibility(View.INVISIBLE);

    }

    private void animateBackground(final float[] from, final float[] to, int time) {
        animating = true;
        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);   // animate from 0 to 1
        anim.setDuration(time);                              // for 2000 ms

        final float[] hsv = new float[3];                  // transition color
        anim.addUpdateListener(animation -> {
            // Transition along each axis of HSV (hue, saturation, value)
            hsv[0] = from[0] + (to[0] - from[0]) * animation.getAnimatedFraction();
            hsv[1] = from[1] + (to[1] - from[1]) * animation.getAnimatedFraction();
            hsv[2] = from[2] + (to[2] - from[2]) * animation.getAnimatedFraction();

            background.setBackgroundColor(Color.HSVToColor(hsv));
            if (Arrays.equals(from, to)) {
                animating = false;
            }
        });

        anim.start();
        currentBackgroundColor = to;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}

