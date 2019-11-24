package jp.co.chlorocube.planetcolorpicker;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;

public class ColorPickerUtils {

    public static int toPx(Context context, int dip) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (metrics.density * dip + 0.5);
    }

    public static float getHue(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return hsv[0];
    }

    public static int getHSVColor(float hue, float saturation, float value) {
        float[] hsv = new float[3];
        if(hue >= 360)
            hue = 359;
        else if(hue < 0)
            hue = 0;

        if(saturation > 1)
            saturation = 1;
        else if(saturation < 0)
            saturation = 0;

        if(value > 1)
            value = 1;
        else if(value < 0)
            value = 0;

        hsv[0] = hue;
        hsv[1] = saturation;
        hsv[2] = value;

        return Color.HSVToColor(hsv);
    }

    public static FloatPoint getFixedPoint(float x, float y, float radius) {

        double temp = Math.pow(radius, 2) / (1 + Math.pow(y / x, 2));
        float fixedX = (float)Math.sqrt(temp);
        float fixedY = (float)(Math.sqrt(Math.pow(radius, 2) - Math.pow(fixedX, 2)));

        if (x >= 0 && y >= 0) {
            return new FloatPoint(fixedX, fixedY);
        } else if (x >= 0 && y < 0) {
            return new FloatPoint(fixedX, -fixedY);
        } else if (x < 0 && y >= 0) {
            return new FloatPoint(-fixedX, fixedY);
        } else {
            return new FloatPoint(-fixedX, -fixedY);
        }
    }

    public static int interpretColor(int[] colors, float unit) {

        if (unit <= 0) {
            return colors[0];
        }
        if (unit >= 1) {
            return colors[colors.length - 1];
        }

        float p = unit * (colors.length - 1);
        int i = (int)p;
        p -= i;

        int c0 = colors[i];
        int c1 = colors[i+1];
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);

        return Color.argb(a, r, g, b);
    }

    private static int ave(int s, int d, float p) {
        return s + java.lang.Math.round(p * (d - s));
    }
}
