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
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Calendar;

public class MainActivity extends Activity {

    private Calendar calendar = Calendar.getInstance();
    private WeatherTask weather = new WeatherTask(this);
    private long updated = 25;
    protected LocationManager locationManager;
    private Resources res;
    private Location loc = new Location("YEY");
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
        loc.setLongitude(17.5790669);
        loc.setLatitude(60.2930692);
        requestWeather(loc);
    }

    public void requestWeather(Location location){
        //TODO fix update time to 10 minutes
        if (updated >= calendar.get(Calendar.HOUR_OF_DAY) + 1 || updated == 25){
            System.out.println("getting weather");
            weather.getWeather(location.getLatitude(), location.getLongitude());
        }

    }

    public void updateTemperature(JSONObject object) {
        final float[] from = currentBackgroundColor;
        final float[] to =   new float[3];
        long temp = 0;
        String location = "";
        background = (View) findViewById(R.id.background);
        res = getResources();

        //String simpleWeather[] = new String[1];
        updated = calendar.get(Calendar.HOUR_OF_DAY);
        try {
            //System.out.println("Timeseries " + object.getJSONArray("timeseries").get(0));
            //TODO fix a kelvin conversion class
            temp = object.getJSONObject("main").getLong("temp");
            location = object.getString("name");
            p.rint("temp " + temp);
        }catch (Exception e){
            p.rint(object);
            e.printStackTrace();
        }

        location_text.setText(location.split(" ")[0]);

        Context context = getApplicationContext();
        View view = getWindow().getDecorView().findViewById(android.R.id.content);
        System.out.println("Setting colour");
        if(temp <= -20) {
            Color.colorToHSV(res.getColor(R.color.cold5), to);
        }
        else if(temp <= -15) {
            Color.colorToHSV(res.getColor(R.color.cold4), to);
        }
        else if(temp <= -10) {
            Color.colorToHSV(res.getColor(R.color.cold3), to);
        }
        else if(temp <= -5) {
            Color.colorToHSV(res.getColor(R.color.cold4), to);
        }
        else if(temp <= 0) {
            Color.colorToHSV(res.getColor(R.color.cold1), to);
        }
        else if(temp <= 5) {
            Color.colorToHSV(res.getColor(R.color.warm1), to);
        }
        else if(temp <= 10) {
            Color.colorToHSV(res.getColor(R.color.warm2), to);
        }
        else if(temp <= 15) {
            Color.colorToHSV(res.getColor(R.color.warm3), to);
        }
        else if(temp <= 20) {
            Color.colorToHSV(res.getColor(R.color.warm4), to);
        }
        else if(temp <= 25) {
            Color.colorToHSV(res.getColor(R.color.warm5), to);
        }

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
