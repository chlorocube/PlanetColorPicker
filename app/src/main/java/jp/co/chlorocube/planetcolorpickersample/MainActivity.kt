package jp.co.chlorocube.planetcolorpickersample

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import jp.co.chlorocube.planetcolorpicker.ColorPickerView
import jp.co.chlorocube.planetcolorpicker.ColorPickerView.ColorChangeListener
import androidx.core.net.toUri

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scroll)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            controller.isAppearanceLightStatusBars = true
        }

        drawCircle01() // Default
        drawCircle02() // Draw complementary-color-background
        drawCircle03() // No previous-color & Custom radius
    }

    private fun drawCircle01() {
        val scrollView = findViewById<OperableScrollView>(R.id.scroll)
        val textView = findViewById<TextView>(R.id.color_picker_text_view_01)
        val view = findViewById<ColorPickerView>(R.id.color_picker_main_view_01)
        view.initializePicker(MainPreferenceManager.getColor(this)!!, object : ColorChangeListener {
            override fun onColorChanged(hsv: FloatArray) {
                textView.text = getColorCode(Color.HSVToColor(hsv))
                MainPreferenceManager.setColor(this@MainActivity, hsv)
            }
        }, object : ColorPickerView.TrackListener {
            override fun onStartTrack() {
                scrollView.disableScroll()
            }

            override fun onStopTrack() {
                scrollView.enableScroll()
            }
        })
        val currentColor = view.currentColor
        textView.text = getColorCode(currentColor)
    }

    private fun drawCircle02() {
        val scrollView = findViewById<OperableScrollView>(R.id.scroll)
        val textView = findViewById<TextView>(R.id.color_picker_text_view_02)
        val view = findViewById<ColorPickerView>(R.id.color_picker_main_view_02)
        view.initializePicker(MainPreferenceManager.getColor(this)!!, object : ColorChangeListener {
            override fun onColorChanged(hsv: FloatArray) {
                textView.text = getColorCode(Color.HSVToColor(hsv))
                //MainPreferenceManager.setColor(this@MainActivity, hsv)
            }
        }, object : ColorPickerView.TrackListener {
            override fun onStartTrack() {
                scrollView.disableScroll()
            }

            override fun onStopTrack() {
                scrollView.enableScroll()
            }
        }, true)
        val currentColor = view.currentColor
        textView.text = getColorCode(currentColor)
    }

    private fun drawCircle03() {
        val scrollView = findViewById<OperableScrollView>(R.id.scroll)
        val textView = findViewById<TextView>(R.id.color_picker_text_view_03)
        val view = findViewById<ColorPickerView>(R.id.color_picker_main_view_03)
        view.initializePicker(
            MainPreferenceManager.getColor(this)!!, object : ColorChangeListener {
                override fun onColorChanged(hsv: FloatArray) {
                    textView.text = getColorCode(Color.HSVToColor(hsv))
                    //MainPreferenceManager.setColor(this@MainActivity, hsv)
                }
            }, object : ColorPickerView.TrackListener {
                override fun onStartTrack() {
                    scrollView.disableScroll()
                }

                override fun onStopTrack() {
                    scrollView.enableScroll()
                }
            },
            needsComplementaryColorBackgroundDraw = false,
            needsOldColorDraw = false,
            outerRadiusDip = 120
        )
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
                intent.data = getString(R.string.url_github).toUri()
                intent.addCategory(Intent.CATEGORY_BROWSABLE)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}