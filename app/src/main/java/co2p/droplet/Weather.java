package co2p.droplet;

/**
 * Created by gordon on 06/01/16.
 */
public class Weather {
    public final String temperature;
    public final String windspeed;
    public final String winddirection;
    public final String time;
    public final String area;

    public Weather(String temperature, String windspeed, String winddirection, String time, String area) {
        this.temperature = temperature;
        this.windspeed = windspeed;
        this.winddirection = winddirection;
        this.time = time;
        this.area = area;
    }
}
