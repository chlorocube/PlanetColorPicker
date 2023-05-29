package jp.co.chlorocube.planetcolorpickersample

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import jp.co.chlorocube.planetcolorpicker.ColorPickerView
import jp.co.chlorocube.planetcolorpicker.ColorPickerView.ColorChangeListener

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        drawCircle01() // Default
        drawCircle02() // Draw complementary-color-background
        drawCircle03() // No previous-color & Custom radius
    }

    private fun drawCircle01() {
        val textView = findViewById<TextView>(R.id.color_picker_text_view_01)
        val view = findViewById<ColorPickerView>(R.id.color_picker_main_view_01)
        view.initializePicker(MainPreferenceManager.getColor(this)!!, object : ColorChangeListener {
            override fun onColorChanged(hsv: FloatArray) {
                textView.text = getColorCode(Color.HSVToColor(hsv))
                MainPreferenceManager.setColor(this@MainActivity, hsv)
            }
        })
        val currentColor = view.currentColor
        textView.text = getColorCode(currentColor)
    }

    private fun drawCircle02() {
        val textView = findViewById<TextView>(R.id.color_picker_text_view_02)
        val view = findViewById<ColorPickerView>(R.id.color_picker_main_view_02)
        view.initializePicker(MainPreferenceManager.getColor(this)!!, object : ColorChangeListener {
            override fun onColorChanged(hsv: FloatArray) {
                textView.text = getColorCode(Color.HSVToColor(hsv))
                //MainPreferenceManager.setColor(this@MainActivity, hsv)
            }
        }, true)
        val currentColor = view.currentColor
        textView.text = getColorCode(currentColor)
    }

    private fun drawCircle03() {
        val textView = findViewById<TextView>(R.id.color_picker_text_view_03)
        val view = findViewById<ColorPickerView>(R.id.color_picker_main_view_03)
        view.initializePicker(MainPreferenceManager.getColor(this)!!, object : ColorChangeListener {
            override fun onColorChanged(hsv: FloatArray) {
                textView.text = getColorCode(Color.HSVToColor(hsv))
                //MainPreferenceManager.setColor(this@MainActivity, hsv)
            }
        }, false, false, 120)
        val currentColor = view.currentColor
        textView.text = getColorCode(currentColor)
    }

    private fun getColorCode(color: Int): String {
        return "#" + Integer.toHexString(color).substring(2)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_github -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(getString(R.string.url_github))
                intent.addCategory(Intent.CATEGORY_BROWSABLE)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}