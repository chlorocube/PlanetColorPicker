PlanetColorPicker
====

PlanetColorPicker is a color-picker library for Android.  
It's a simple, cool, cute and material-like design.

<a href='https://play.google.com/store/apps/details?id=jp.co.chlorocube.planetcolorpickersample&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/ja/badges/images/generic/en_badge_web_generic.png' width='150'/></a>

Screenshot
----------
![screenshot01.png](https://github.com/chlorocube/PlanetColorPicker/blob/master/screenshot/screenshot01.png)
![screenshot02.png](https://github.com/chlorocube/PlanetColorPicker/blob/master/screenshot/screenshot02.png)

Download
--------

grab via Gradle:
```
repositories {
    maven {
        url 'https://chlorocube.github.io/PlanetColorPicker/repository'
    }
}

dependencies {
    implementation 'jp.co.chlorocube:planetcolorpicker:1.0.5'
}
```

Usage
-----

1. First, include the following code in your layout file.
```xml
    <jp.co.chlorocube.planetcolorpicker.ColorPickerView
        android:id="@+id/color_picker_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
```

2. Then, call initialization method in your kotlin file.
```kotlin
    val initColor = FloatArray(3)
    initColor[0] = 0f // hue (0-359)
    initColor[1] = 0f // saturation (0-1)
    initColor[2] = 1f // value (0-1)
      
    val view = findViewById<ColorPickerView>(R.id.color_picker_view)
    view.initializePicker(initColor, object : ColorChangeListener {
        override fun onColorChanged(hsv: FloatArray) {
            // ...
        }
    }, null)
      
    // You can also call the follow methods.
    val currentColor = view.currentColor
    val currentHSV = view.currentHsv
```

Example
-------
![exm01.png](https://github.com/chlorocube/PlanetColorPicker/blob/master/screenshot/exm01.png)
```kotlin
    view.initializePicker(initColor, object : ColorChangeListener {
        override fun onColorChanged(hsv: FloatArray) {
            // ...
        }
    }, null)
```
![exm02.png](https://github.com/chlorocube/PlanetColorPicker/blob/master/screenshot/exm02.png)
```kotlin
    view.initializePicker(initColor, object : ColorChangeListener {
        override fun onColorChanged(hsv: FloatArray) {
            // ...
        }
    }, null, true) // needsComplementaryColorBackgroundDraw = true
```
![exm03.png](https://github.com/chlorocube/PlanetColorPicker/blob/master/screenshot/exm03.png)
```kotlin
    view.initializePicker(initColor, object : ColorChangeListener {
        override fun onColorChanged(hsv: FloatArray) {
            // ...
        }
    }, null, false, false, 120)
    // needsComplementaryColorBackgroundDraw = false
    // needsOldColorDraw = false
    // outerRadiusDip = 120
```

License
-------

```txt
Copyright 2023 chlorocube

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```