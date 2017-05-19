package co2p.droplet;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Arrays;
import java.util.Calendar;

import static co2p.droplet.Color.backgroundColor;

public class MainActivity extends Activity {

    private Calendar calendar = Calendar.getInstance();
    private long updated = 25;
    private Resources res;
    private boolean gettinglocation = false;
    private TextView temperature_text;
    private TextView location_text;
    private TextView state_text;
    private View background;
    private float[] currentBackgroundColor = new float[]{0, 0, 0};
    private boolean animating = false;
    private boolean searching = false;
    private Activity thisActivity;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 57;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        thisActivity = this;
        temperature_text = (TextView) findViewById(R.id.temperature);
        location_text = (TextView) findViewById(R.id.location);
        state_text = (TextView) findViewById(R.id.state);
    }

    public void getLocation() {
        if (!searching) {
            searching = true;
            p.rint("Location requested");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            // Acquire a reference to the system Location Manager
            final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            // Define a listener that responds to location updates
            final LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    locationManager.removeUpdates(this);
                    searching = false;

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
        getLocation();
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onRefresh(View view) {
        getLocation();
    }

    public void onRefresh() {
        getLocation();
    }

    public void requestWeather(Location location) {
        //TODO fix update time to 10 minutes
        if (updated >= calendar.get(Calendar.HOUR_OF_DAY) + 1 || updated == 25) {
            System.out.println("getting weather");
            //weather.getWeather(location.getLatitude(), location.getLongitude());

            RequestQueue request = Volley.newRequestQueue(this);

            String url = "http://api.yr.no/weatherapi/locationforecast/1.9/?lat=" + location.getLatitude() + ";lon=" + location.getLongitude();

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    XMLParser parser = new XMLParser();
                    Weather weather = parser.parse(response);
                    updateTemperature(weather);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    p.rint("Internet error");
                    location_text.setText("Having trouble with the internet");
                }
            });

            request.add(stringRequest);
        }
    }

    public void updateTemperature(Weather object) {
        res = getResources();

        final float[] from = currentBackgroundColor;
        float temp = Float.parseFloat(object.temperature);
        final float[] to = backgroundColor(temp, res);

        String location = "";
        background = findViewById(R.id.background);

        //String simpleWeather[] = new String[1];
        updated = calendar.get(Calendar.HOUR_OF_DAY);


        temperature_text.setText((int) Math.ceil(temp) + "°C");
        location_text.setText(location);
        state_text.setText(object.winddirection + ", " + object.windspeed + "m/s");

        animateBackground(from, to, 2000);
    }

    private void animateBackground(final float[] from, final float[] to, int time) {
        animating = true;
        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);   // animate from 0 to 1
        anim.setDuration(time);                              // for 2000 ms

        final float[] hsv = new float[3];                  // transition color
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Transition along each axis of HSV (hue, saturation, value)
                hsv[0] = from[0] + (to[0] - from[0]) * animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1]) * animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2]) * animation.getAnimatedFraction();

                background.setBackgroundColor(Color.HSVToColor(hsv));
                if (Arrays.equals(from, to)) {
                    animating = false;
                }
            }
        });

        anim.start();
        currentBackgroundColor = to;
        p.rint("background has been set to " + currentBackgroundColor[0] + "°");
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

