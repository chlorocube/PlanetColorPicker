package jp.co.chlorocube.planetcolorpicker

import android.content.Context
import android.graphics.Color
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

object ColorPickerUtils {
    fun toPx(context: Context, dip: Int): Int {
        val metrics = context.resources.displayMetrics
        return (metrics.density * dip + 0.5).toInt()
    }

    fun getHue(color: Int): Float {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        return hsv[0]
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

    fun getFixedPoint(x: Float, y: Float, radius: Float): FloatPoint {
        val temp = radius.toDouble().pow(2.0) / (1 + (y / x).toDouble().pow(2.0))
        val fixedX = sqrt(temp).toFloat()
        val fixedY = sqrt(radius.toDouble().pow(2.0) - fixedX.toDouble().pow(2.0))
            .toFloat()
        return if (x >= 0 && y >= 0) {
            FloatPoint(fixedX, fixedY)
        } else if (x >= 0 && y < 0) {
            FloatPoint(fixedX, -fixedY)
        } else if (x < 0 && y >= 0) {
            FloatPoint(-fixedX, fixedY)
        } else {
            FloatPoint(-fixedX, -fixedY)
        }
    }

    fun interpretColor(colors: IntArray, unit: Float): Int {
        if (unit <= 0) {
            return colors[0]
        }
        if (unit >= 1) {
            return colors[colors.size - 1]
        }
        var p = unit * (colors.size - 1)
        val i = p.toInt()
        p -= i.toFloat()
        val c0 = colors[i]
        val c1 = colors[i + 1]
        val a = ave(Color.alpha(c0), Color.alpha(c1), p)
        val r = ave(Color.red(c0), Color.red(c1), p)
        val g = ave(Color.green(c0), Color.green(c1), p)
        val b = ave(Color.blue(c0), Color.blue(c1), p)
        return Color.argb(a, r, g, b)
    }

    private fun ave(s: Int, d: Int, p: Float): Int {
        return s + (p * (d - s)).roundToInt()
    }
}