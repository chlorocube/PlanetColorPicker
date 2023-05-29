package jp.co.chlorocube.planetcolorpickersample

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

class OperableScrollView : ScrollView {

    private var mEnableScroll: Boolean = true

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return if (mEnableScroll)
            super.onTouchEvent(ev)
        else
            true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return if (mEnableScroll)
            super.onInterceptTouchEvent(ev)
        else
            false
    }

    fun enableScroll() {
        mEnableScroll = true
    }

    fun disableScroll() {
        mEnableScroll = false
    }
}