package jp.co.chlorocube.planetcolorpickersample;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import jp.co.chlorocube.planetcolorpicker.ColorPickerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final TextView textView = findViewById(R.id.color_picker_text_view);

        ColorPickerView view = findViewById(R.id.color_picker_main_view);
        view.initializePicker(MainPreferenceManager.getColor(this), new ColorPickerView.ColorChangeListener(){
                    @Override
                    public void onColorChanged(float[] hsv) {
                        textView.setText(getColorCode(Color.HSVToColor(hsv)));
                        MainPreferenceManager.setColor(MainActivity.this, hsv);
                    }
                });

        int currentColor = view.getCurrentColor();
        textView.setText(getColorCode(currentColor));
    }

    private String getColorCode(int color) {
        return "#" + Integer.toHexString(color).substring(2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_github:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.url_github)));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
