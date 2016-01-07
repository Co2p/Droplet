package co2p.droplet;

/**
 * Created by gordon on 06/01/16.
 */
public class Weather {
    public final String temperature;
    public final String wind;
    public final String summary;

    public Weather(String temperature, String summary, String wind) {
        this.temperature = temperature;
        this.summary = summary;
        this.wind = wind;
    }
}
