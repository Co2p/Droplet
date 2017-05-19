package co2p.droplet;

import android.content.res.Resources;

/**
 * Created by gordon on 07/01/16.
 */
public class Color {

    public static float[] backgroundColor(float temp, Resources res) {
        float[] color = new float[3];

        if(temp <= -26) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.cold14), color);
        }
        else if(temp <= -24) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.cold14), color);
        }
        else if(temp <= -22) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.cold12), color);
        }
        else if(temp <= -20) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.cold11), color);
        }
        else if(temp <= -18) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.cold10), color);
        }
        else if(temp <= -16) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.cold9), color);
        }
        else if(temp <= -14) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.cold8), color);
        }
        else if(temp <= -12) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.cold7), color);
        }
        else if(temp <= -10) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.cold6), color);
        }
        else if(temp <= -8) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.cold5), color);
        }
        else if(temp <= -6) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.cold4), color);
        }
        else if(temp <= -4) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.cold3), color);
        }
        else if(temp <= -2) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.cold4), color);
        }
        else if(temp <= 0) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.cold1), color);
        }
        else if(temp <= 2) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.warm1), color);
        }
        else if(temp <= 6) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.warm2), color);
        }
        else if(temp <= 8) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.warm3), color);
        }
        else if(temp <= 12) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.warm4), color);
        }
        else if(temp <= 16) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.warm5), color);
        }
        else if(temp <= 20) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.warm6), color);
        }
        else if(temp <= 22) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.warm7), color);
        }
        else if(temp <= 24) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.warm8), color);
        }
        else if(temp <= 26) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.warm9), color);
        }
        else if(temp <= 28) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.warm10), color);
        }
        else if(temp <= 30) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.warm11), color);
        }
        else if(temp <= 32) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.warm12), color);
        }
        else if(temp <= 34) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.warm13), color);
        }
        else if(temp <= 36) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.warm14), color);
        }
        else if(temp <= 38) {
            android.graphics.Color.colorToHSV(res.getColor(R.color.warm15), color);
        }

        return color;
    }
}
