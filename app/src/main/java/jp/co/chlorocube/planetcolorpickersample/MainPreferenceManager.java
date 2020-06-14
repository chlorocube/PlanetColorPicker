package jp.co.chlorocube.planetcolorpickersample;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

class MainPreferenceManager {

    private static final String KEY_HUE = "HUE";
    private static final String KEY_SATURATION = "SATURATION";
    private static final String KEY_BRIGHT = "BRIGHT";

    static void setColor(Context context, float[] color) {
        if (context == null)
            return;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(KEY_HUE, color[0]);
        editor.putFloat(KEY_SATURATION, color[1]);
        editor.putFloat(KEY_BRIGHT, color[2]);
        editor.apply();
    }

    static float[] getColor(Context context) {
        if (context == null)
            return null;

        float[] color = new float[3];
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        color[0] = sp.getFloat(KEY_HUE, 0);
        color[1] = sp.getFloat(KEY_SATURATION, 0);
        color[2] = sp.getFloat(KEY_BRIGHT, 1);
        return color;
    }
}
