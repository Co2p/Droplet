package co2p.droplet

import android.Manifest
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView

import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import java.util.Arrays
import java.util.Calendar

import co2p.droplet.Color.backgroundColor
import java.lang.Float

class MainActivity : Activity() {

    private val calendar = Calendar.getInstance()
    private var updated = 25
    private var temperature_text: TextView? = null
    private var location_text: TextView? = null
    private var state_text: TextView? = null
    private var spinner: ProgressBar? = null
    private var background: View? = null
    private var currentBackgroundColor = floatArrayOf(0f, 0f, 0f)
    private var animating = false
    private var searching = false
    private var location: Location? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        temperature_text = findViewById<TextView>(R.id.temperature)
        location_text = findViewById<TextView>(R.id.location)
        state_text = findViewById<TextView>(R.id.state)
        spinner = findViewById<ProgressBar>(R.id.progress)
    }

    fun setLocation(l: Location) {
        location = l
    }

    fun getLocation() {
        if (!searching) {
            searching = true
            spinner!!.visibility = View.VISIBLE
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION)
            if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                searching = false
                getLocation()
            }


            // Acquire a reference to the system Location Manager
            val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            try {
                requestWeather(location)
            } catch (e: NullPointerException) {
                location_text!!.setText(R.string.locationerror)
                temperature_text!!.text = "???"
                animateBackground(currentBackgroundColor, backgroundColor(50f, baseContext), 2000)
            }

            // Define a listener that responds to location updates
            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    // Called when a new location is found by the network location provider.
                    //locationManager.removeUpdates(this);
                    searching = false
                    setLocation(location)

                    requestWeather(location)
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

                override fun onProviderEnabled(provider: String) {}

                override fun onProviderDisabled(provider: String) {}
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)
        }
    }

    /* Request updates at startup */
    override fun onResume() {
        super.onResume()
        requestWeather(location)
        getLocation()
    }

    /* Remove the locationlistener updates when Activity is paused */
    override fun onPause() {
        super.onPause()
    }

    fun onRefresh(view: View) {
        requestWeather(location)
        getLocation()
    }

    fun requestWeather(location: Location?) {
        //TODO fix update time to 10 minutes
        location_text!!.text = getString(R.string.loading_weather)

        if (location != null && (updated >= calendar.get(Calendar.HOUR_OF_DAY) + 1 || updated == 25)) {
            spinner!!.visibility = View.VISIBLE

            //weather.getWeather(location.getLatitude(), location.getLongitude());


            val request = Volley.newRequestQueue(this)

            val url = "https://api.met.no/weatherapi/locationforecast/1.9/?lat=" + location.latitude + ";lon=" + location.longitude

            val stringRequest = StringRequest(Request.Method.GET, url, { response ->
                val parser = XMLParser()
                val weather = parser.parse(response)
                updateTemperature(weather)
            }) { error ->
                var statusCode = 0
                try {
                    statusCode = error.networkResponse.statusCode
                    println("StatusCode: " + statusCode)
                } catch (e: NullPointerException) {
                    location_text!!.setText(R.string.airplanemode)
                }
                when (statusCode) {
                    404 -> location_text!!.setText(R.string.interneterror)
                    429 -> location_text!!.setText("Error 429\n Too Many Requests")
                    else -> error.printStackTrace()
                }
                spinner!!.visibility = View.INVISIBLE
            }

            request.add(stringRequest)
            println(url)
        } else {
            spinner!!.visibility = View.INVISIBLE
        }
    }

    fun updateTemperature(weather: Weather) {
        val from = currentBackgroundColor
        val temp = Float.parseFloat(weather.temperature)
        val to = backgroundColor(temp, baseContext)

        val location = weather.area
        println("Loaction: " + location)
        background = findViewById(R.id.background)

        //String simpleWeather[] = new String[1];
        updated = calendar.get(Calendar.HOUR_OF_DAY)

        temperature_text!!.text = Math.round(temp).toString() + getString(R.string.celcius)
        location_text!!.text = location
        state_text!!.text = weather.winddirection + ", " + weather.windspeed + getString(R.string.speed_meters_per_sec)

        animateBackground(from, to, 2000)

        spinner!!.visibility = View.INVISIBLE

    }

    private fun animateBackground(from: FloatArray, to: FloatArray, time: Int) {
        animating = true
        val anim = ValueAnimator.ofFloat(0F, 1F)   // animate from 0 to 1
        anim.duration = time.toLong()                              // for 2000 ms

        val hsv = FloatArray(3)                  // transition color
        anim.addUpdateListener { animation ->
            // Transition along each axis of HSV (hue, saturation, value)
            hsv[0] = from[0] + (to[0] - from[0]) * animation.animatedFraction
            hsv[1] = from[1] + (to[1] - from[1]) * animation.animatedFraction
            hsv[2] = from[2] + (to[2] - from[2]) * animation.animatedFraction

            background!!.setBackgroundColor(Color.HSVToColor(hsv))
            if (Arrays.equals(from, to)) {
                animating = false
            }
        }

        anim.start()
        currentBackgroundColor = to
    }

    companion object {
        const val MY_PERMISSIONS_REQUEST_LOCATION = 57
    }
}
