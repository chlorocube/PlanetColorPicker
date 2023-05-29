package jp.co.chlorocube.planetcolorpickersample

import android.content.Context
import androidx.preference.PreferenceManager

internal object MainPreferenceManager {
    private const val KEY_HUE = "HUE"
    private const val KEY_SATURATION = "SATURATION"
    private const val KEY_BRIGHT = "BRIGHT"
    fun setColor(context: Context?, color: FloatArray) {
        if (context == null) return
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sp.edit()
        editor.putFloat(KEY_HUE, color[0])
        editor.putFloat(KEY_SATURATION, color[1])
        editor.putFloat(KEY_BRIGHT, color[2])
        editor.apply()
    }

    fun getColor(context: Context?): FloatArray? {
        if (context == null) return null
        val color = FloatArray(3)
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        color[0] = sp.getFloat(KEY_HUE, 0f)
        color[1] = sp.getFloat(KEY_SATURATION, 0f)
        color[2] = sp.getFloat(KEY_BRIGHT, 1f)
        return color
    }
}