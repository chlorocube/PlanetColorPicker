package jp.co.chlorocube.planetcolorpicker

import android.graphics.Color

object ColorPickerUtils {

    fun get(color: Int): FloatArray {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        return hsv
    }

    fun getHue(color: Int): Float {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        return hsv[0]
    }

    fun getSaturation(color: Int): Float {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        return hsv[1]
    }

    fun getValue(color: Int): Float {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        return hsv[2]
    }

    fun getHSVColor(hue: Float, saturation: Float, value: Float): Int {
        var hue = hue
        var saturation = saturation
        var value = value

        val hsv = FloatArray(3)
        if (hue >= 360) hue = 359f else if (hue < 0) hue = 0f
        if (saturation > 1) saturation = 1f else if (saturation < 0) saturation = 0f
        if (value > 1) value = 1f else if (value < 0) value = 0f
        hsv[0] = hue
        hsv[1] = saturation
        hsv[2] = value
        return Color.HSVToColor(hsv)
    }

    fun getHSVColor(hsv: FloatArray): Int {
        var hue = hsv[0]
        var saturation = hsv[1]
        var value = hsv[2]

        val hsv = FloatArray(3)
        if (hue >= 360) hue = 359f else if (hue < 0) hue = 0f
        if (saturation > 1) saturation = 1f else if (saturation < 0) saturation = 0f
        if (value > 1) value = 1f else if (value < 0) value = 0f
        hsv[0] = hue
        hsv[1] = saturation
        hsv[2] = value
        return Color.HSVToColor(hsv)
    }
}