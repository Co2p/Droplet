package co2p.droplet;

import android.content.res.Resources;

/**
 * Created by gordon on 07/01/16.
 */
public class Color {

    public static float[] backgroundColor(float temp, Resources res) {
        float[] color = new float[3];

        if(temp <= -20) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.cold5), color);
        }
        else if(temp <= -15) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.cold4), color);
        }
        else if(temp <= -10) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.cold3), color);
        }
        else if(temp <= -5) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.cold4), color);
        }
        else if(temp <= 0) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.cold1), color);
        }
        else if(temp <= 5) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.warm1), color);
        }
        else if(temp <= 10) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.warm2), color);
        }
        else if(temp <= 15) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.warm3), color);
        }
        else if(temp <= 20) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.warm4), color);
        }
        else if(temp <= 25) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.warm5), color);
        }

        return color;
    }
}
