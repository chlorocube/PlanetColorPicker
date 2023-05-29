package jp.co.chlorocube.planetcolorpicker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.*

class ColorPickerView : View {
    interface ColorChangeListener {
        fun onColorChanged(hsv: FloatArray)
    }

    private var mHue = 0f // 0~359
    private var mSaturationRatio = 1f // 0~1
    private var mBrightRatio = 1f // 0~1
    private var mWheelPaint: Paint? = null
    private var mBrightPaint: Paint? = null
    private var mSaturationPaint: Paint? = null
    private var mCenterPaint: Paint? = null
    private var mCenterOldPaint: Paint? = null
    private var mWheelThumbPaint: Paint? = null
    private var mBrightThumbPaint: Paint? = null
    private var mSaturationThumbPaint: Paint? = null
    private var mBackgroundPaint: Paint? = null
    private lateinit var mBrightColors: IntArray
    private lateinit var mSaturationColors: IntArray
    private lateinit var mBackgroundColors: IntArray
    private var mWheelThumbPoint: FloatPoint? = null
    private var mBrightThumbPoint: FloatPoint? = null
    private var mSaturationThumbPoint: FloatPoint? = null
    private var mIsTrackingWheel = false
    private var mIsTrackingBright = false
    private var mIsTrackingSaturation = false
    private val mRect = RectF()
    private var mListener: ColorChangeListener? = null
    private var mNeedsComplementaryColorBackgroundDraw: Boolean = false
    private var mNeedsOldColorDraw: Boolean = true
    private var mOuterRadiusDip = OUTER_RADIUS_DIP
    private var mInnerRadiusDip = INNER_RADIUS_DIP

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = ColorPickerUtils.toPx(context, mOuterRadiusDip + PADDING_DIP) * 2
        setMeasuredDimension(width, width)
    }

    override fun onDraw(canvas: Canvas) {
        var r = ColorPickerUtils.toPx(context, mOuterRadiusDip).toFloat()
        mRect[-r, -r, r] = r
        val padding = ColorPickerUtils.toPx(context, PADDING_DIP).toFloat()
        canvas.translate(r + padding, r + padding)
        if (mNeedsComplementaryColorBackgroundDraw)
            canvas.drawCircle(0f, 0f, r, mBackgroundPaint!!)
        canvas.drawOval(mRect, mWheelPaint!!)
        r = ColorPickerUtils.toPx(context, mInnerRadiusDip).toFloat()
        mRect[-r, -r, r] = r
        canvas.drawArc(mRect, 30f, 120f, false, mBrightPaint!!)
        canvas.drawArc(mRect, 210f, 120f, false, mSaturationPaint!!)
        if (mWheelThumbPoint != null) {
            canvas.drawCircle(
                mWheelThumbPoint!!.x,
                mWheelThumbPoint!!.y,
                ColorPickerUtils.toPx(context, THUMB_RADIUS_DIP).toFloat(),
                mWheelThumbPaint!!
            )
        }
        if (mBrightThumbPoint != null) {
            canvas.drawCircle(
                mBrightThumbPoint!!.x,
                mBrightThumbPoint!!.y,
                ColorPickerUtils.toPx(context, THUMB_RADIUS_DIP).toFloat(),
                mBrightThumbPaint!!
            )
        }
        if (mSaturationThumbPoint != null) {
            canvas.drawCircle(
                mSaturationThumbPoint!!.x,
                mSaturationThumbPoint!!.y,
                ColorPickerUtils.toPx(context, THUMB_RADIUS_DIP).toFloat(),
                mSaturationThumbPaint!!
            )
        }
        r = ColorPickerUtils.toPx(context, CENTER_RADIUS_DIP).toFloat()
        canvas.drawCircle(0f, -padding / 7, r, mCenterPaint!!)
        if (mNeedsOldColorDraw) {
            r = ColorPickerUtils.toPx(context, CENTER_OLD_RADIUS_DIP).toFloat()
            canvas.drawCircle(padding * 2 / 3, padding / 2, r, mCenterOldPaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val r = ColorPickerUtils.toPx(context, mOuterRadiusDip).toFloat()
        val x = event.x - r - ColorPickerUtils.toPx(context, PADDING_DIP)
        val y = event.y - r - ColorPickerUtils.toPx(context, PADDING_DIP)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (hypot(
                    x.toDouble(),
                    y.toDouble()
                ) > ColorPickerUtils.toPx(
                    context,
                    (mOuterRadiusDip - mInnerRadiusDip) / 2 + mInnerRadiusDip
                )
            ) {
                mIsTrackingWheel = true
                redrawByTrackingWheel(x, y)
            } else if (hypot(x.toDouble(), y.toDouble()) > ColorPickerUtils.toPx(
                    context,
                    (mInnerRadiusDip - CENTER_RADIUS_DIP) / 2 + CENTER_RADIUS_DIP
                )
            ) {
                if (y < 0) {
                    mIsTrackingSaturation = true
                    redrawByTrackingSaturation(x, y)
                } else if (y > 0) {
                    mIsTrackingBright = true
                    redrawByTrackingBright(x, y)
                }
            }
            MotionEvent.ACTION_MOVE -> if (mIsTrackingWheel) {
                redrawByTrackingWheel(x, y)
            } else if (mIsTrackingBright) {
                redrawByTrackingBright(x, y)
            } else if (mIsTrackingSaturation) {
                redrawByTrackingSaturation(x, y)
            }
            MotionEvent.ACTION_UP -> {
                mIsTrackingWheel = false
                mIsTrackingBright = false
                mIsTrackingSaturation = false
            }
            else -> {}
        }
        return true
    }

    /**
     * Returns the currently selected HSV values.
     * @return the currently selected HSV values
     */
    val currentHsv: FloatArray
        get() = floatArrayOf(mHue, mSaturationRatio, mBrightRatio)

    /**
     * Returns the currently selected color value in the form 0xAARRGGBB.
     * @return the currently selected color value in the form 0xAARRGGBB
     */
    val currentColor: Int
        get() = ColorPickerUtils.getHSVColor(mHue, mSaturationRatio, mBrightRatio)

    /**
     * Used to do a one-time initialization of the ColorPickerView.
     * @param listener ColorChangeListener
     * @param needsComplementaryColorBackgroundDraw if true, draw complementary-color-background
     * @param needsOldColorDraw if true, draw previous-color
     * @param outerRadiusDip hue circle radius (dp)
     */
    fun initializePicker(
        listener: ColorChangeListener,
        needsComplementaryColorBackgroundDraw: Boolean = false,
        needsOldColorDraw: Boolean = true,
        outerRadiusDip: Int = OUTER_RADIUS_DIP,
    ) {
        mNeedsComplementaryColorBackgroundDraw = needsComplementaryColorBackgroundDraw
        mNeedsOldColorDraw = needsOldColorDraw
        mOuterRadiusDip = outerRadiusDip
        mInnerRadiusDip = mOuterRadiusDip - 30
        initializePicker(
            DEFAULT_HUE.toFloat(),
            DEFAULT_SATURATION.toFloat(),
            DEFAULT_BRIGHT.toFloat(),
            listener
        )
    }

    /**
     * Used to do a one-time initialization of the ColorPickerView.
     * @param hsv initial HSV values
     * @param listener ColorChangeListener
     * @param needsComplementaryColorBackgroundDraw if true, draw complementary-color-background
     * @param needsOldColorDraw if true, draw previous-color
     * @param outerRadiusDip hue circle radius (dp)
     */
    fun initializePicker(
        hsv: FloatArray,
        listener: ColorChangeListener,
        needsComplementaryColorBackgroundDraw: Boolean = false,
        needsOldColorDraw: Boolean = true,
        outerRadiusDip: Int = OUTER_RADIUS_DIP,
    ) {
        mNeedsComplementaryColorBackgroundDraw = needsComplementaryColorBackgroundDraw
        mNeedsOldColorDraw = needsOldColorDraw
        mOuterRadiusDip = outerRadiusDip
        mInnerRadiusDip = mOuterRadiusDip - 30
        initializePicker(hsv[0], hsv[1], hsv[2], listener)
    }

    private fun initializePicker(
        hue: Float,
        saturation: Float,
        bright: Float,
        listener: ColorChangeListener
    ) {
        mListener = listener
        mBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBackgroundPaint!!.style = Paint.Style.FILL
        mWheelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mWheelPaint!!.style = Paint.Style.STROKE
        mWheelPaint!!.strokeWidth =
            ColorPickerUtils.toPx(context, STROKE_WIDTH_DIP).toFloat()
        val outerShader: Shader = SweepGradient(0f, 0f, WHEEL_COLORS, null)
        mWheelPaint!!.shader = outerShader
        mBrightPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBrightPaint!!.style = Paint.Style.STROKE
        mBrightPaint!!.strokeWidth =
            ColorPickerUtils.toPx(context, STROKE_WIDTH_DIP).toFloat()
        mSaturationPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mSaturationPaint!!.style = Paint.Style.STROKE
        mSaturationPaint!!.strokeWidth =
            ColorPickerUtils.toPx(context, STROKE_WIDTH_DIP).toFloat()
        mCenterPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mCenterPaint!!.style = Paint.Style.FILL
        mCenterOldPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mCenterOldPaint!!.style = Paint.Style.FILL
        mWheelThumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mWheelThumbPaint!!.style = Paint.Style.FILL
        mBrightThumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBrightThumbPaint!!.style = Paint.Style.FILL
        mSaturationThumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mSaturationThumbPaint!!.style = Paint.Style.FILL
        mHue = hue
        mSaturationRatio = saturation
        mBrightRatio = bright
        val currentColor = ColorPickerUtils.getHSVColor(mHue, mSaturationRatio, mBrightRatio)
        mCenterOldPaint!!.color = currentColor
        mCenterPaint!!.color = currentColor
        val wheelColor = ColorPickerUtils.getHSVColor(mHue, 1f, 1f)
        mWheelThumbPaint!!.color = wheelColor
        mSaturationColors = intArrayOf(
            wheelColor, -0x1, -0x1
        )
        mBrightColors = intArrayOf(
            -0x1000000, wheelColor, wheelColor
        )
        val brightShader: Shader = SweepGradient(0f, 0f, mBrightColors, BRIGHT_POS)
        mBrightPaint!!.shader = brightShader
        val saturationShader: Shader = SweepGradient(0f, 0f, mSaturationColors, SATURATION_POS)
        mSaturationPaint!!.shader = saturationShader
        mBrightThumbPaint!!.color = ColorPickerUtils.getHSVColor(mHue, 1f, mBrightRatio)
        mSaturationThumbPaint!!.color = ColorPickerUtils.getHSVColor(mHue, mSaturationRatio, 1f)
        var r = ColorPickerUtils.toPx(context, mOuterRadiusDip).toFloat()
        var x = r * cos((mHue * 2 * PI / 360).toDouble())
        var y = -r * sin((mHue * 2 * PI / 360).toDouble())
        mWheelThumbPoint = ColorPickerUtils.getFixedPoint(x.toFloat(), y.toFloat(), r)
        r = ColorPickerUtils.toPx(context, mInnerRadiusDip).toFloat()
        var arg = ((mSaturationRatio + 0.25) * 2 * PI / 3).toFloat()
        x = r * cos(arg.toDouble())
        y = -r * sin(arg.toDouble())
        mSaturationThumbPoint = ColorPickerUtils.getFixedPoint(x.toFloat(), y.toFloat(), r)
        arg = ((mBrightRatio + 0.25) * 2 * PI / 3).toFloat()
        x = r * cos(arg.toDouble())
        y = r * sin(arg.toDouble())
        mBrightThumbPoint = ColorPickerUtils.getFixedPoint(x.toFloat(), y.toFloat(), r)
        val backgroundColor = ColorPickerUtils.getHSVColor((hue + 180) % 360, 0.05f, 0.95f)
        mBackgroundColors = intArrayOf(
            backgroundColor, backgroundColor, -0x1
        )
        val backgroundShader =
            RadialGradient(0f, 0f, 2 * r, mBackgroundColors, BACKGROUND_POS, Shader.TileMode.CLAMP)
        mBackgroundPaint!!.shader = backgroundShader
    }

    private fun redrawByTrackingWheel(x: Float, y: Float) {
        var r = ColorPickerUtils.toPx(context, mOuterRadiusDip).toFloat()
        mWheelThumbPoint = ColorPickerUtils.getFixedPoint(x, y, r)
        val angle = atan2(y.toDouble(), x.toDouble()).toFloat()
        var unit = angle / (2 * PI)
        if (unit < 0) {
            unit += 1f
        }
        val wheelColor = ColorPickerUtils.interpretColor(WHEEL_COLORS, unit)
        mWheelThumbPaint!!.color = wheelColor
        mHue = ColorPickerUtils.getHue(wheelColor)
        val currentColor = ColorPickerUtils.getHSVColor(mHue, mSaturationRatio, mBrightRatio)
        mBrightThumbPaint!!.color = ColorPickerUtils.getHSVColor(mHue, 1f, mBrightRatio)
        mSaturationThumbPaint!!.color = ColorPickerUtils.getHSVColor(mHue, mSaturationRatio, 1f)
        mCenterPaint!!.color = currentColor
        mSaturationColors = intArrayOf(
            wheelColor, -0x1, -0x1
        )
        mBrightColors = intArrayOf(
            -0x1000000, wheelColor, wheelColor
        )
        val brightShader: Shader = SweepGradient(0f, 0f, mBrightColors, BRIGHT_POS)
        mBrightPaint!!.shader = brightShader
        val saturationShader: Shader = SweepGradient(0f, 0f, mSaturationColors, SATURATION_POS)
        mSaturationPaint!!.shader = saturationShader
        r = ColorPickerUtils.toPx(context, mInnerRadiusDip).toFloat()
        val backgroundColor = ColorPickerUtils.getHSVColor((mHue + 180) % 360, 0.05f, 0.95f)
        mBackgroundColors = intArrayOf(
            backgroundColor, backgroundColor, -0x1
        )
        val rg =
            RadialGradient(0f, 0f, 2 * r, mBackgroundColors, BACKGROUND_POS, Shader.TileMode.CLAMP)
        mBackgroundPaint!!.shader = rg
        if (mListener != null) mListener!!.onColorChanged(
            floatArrayOf(
                mHue,
                mSaturationRatio,
                mBrightRatio
            )
        )
        invalidate()
    }

    private fun redrawByTrackingSaturation(x: Float, y: Float) {
        val r = ColorPickerUtils.toPx(context, mInnerRadiusDip).toFloat()
        val tempThumbPoint = ColorPickerUtils.getFixedPoint(x, y, r)
        if (tempThumbPoint.y > 0) {
            return
        }
        if (tempThumbPoint.y > -r * sin((PI / 6).toDouble()) * 7 / 8) {
            tempThumbPoint.y = (-r * sin((PI / 6).toDouble())).toFloat()
            if (x > 0) tempThumbPoint.x =
                (r * cos((PI / 6).toDouble())).toFloat() else tempThumbPoint.x =
                (-r * cos((PI / 6).toDouble())).toFloat()
        }
        mSaturationThumbPoint = tempThumbPoint
        val arg = acos((mSaturationThumbPoint!!.x / r).toDouble())
        mSaturationRatio = (3 * arg / (2 * PI) - 0.25).toFloat()
        val currentColor = ColorPickerUtils.getHSVColor(mHue, mSaturationRatio, mBrightRatio)
        mSaturationThumbPaint!!.color = ColorPickerUtils.getHSVColor(mHue, mSaturationRatio, 1f)
        mCenterPaint!!.color = currentColor
        if (mListener != null) mListener!!.onColorChanged(
            floatArrayOf(
                mHue,
                mSaturationRatio,
                mBrightRatio
            )
        )
        invalidate()
    }

    private fun redrawByTrackingBright(x: Float, y: Float) {
        val r = ColorPickerUtils.toPx(context, mInnerRadiusDip).toFloat()
        val tempThumbPoint = ColorPickerUtils.getFixedPoint(x, y, r)
        if (tempThumbPoint.y < 0) {
            return
        }
        if (tempThumbPoint.y < r * sin((PI / 6).toDouble()) * 7 / 8) {
            tempThumbPoint.y = (r * sin((PI / 6).toDouble())).toFloat()
            if (x > 0) tempThumbPoint.x =
                (r * cos((PI / 6).toDouble())).toFloat() else tempThumbPoint.x =
                (-r * cos((PI / 6).toDouble())).toFloat()
        }
        mBrightThumbPoint = tempThumbPoint
        val arg = acos((mBrightThumbPoint!!.x / r).toDouble())
        mBrightRatio = (3 * arg / (2 * PI) - 0.25).toFloat()
        val currentColor = ColorPickerUtils.getHSVColor(mHue, mSaturationRatio, mBrightRatio)
        mBrightThumbPaint!!.color = ColorPickerUtils.getHSVColor(mHue, 1f, mBrightRatio)
        mCenterPaint!!.color = currentColor
        if (mListener != null) mListener!!.onColorChanged(
            floatArrayOf(
                mHue,
                mSaturationRatio,
                mBrightRatio
            )
        )
        invalidate()
    }

    companion object {
        private const val DEFAULT_HUE = 0
        private const val DEFAULT_SATURATION = 0
        private const val DEFAULT_BRIGHT = 1
        private const val PI = 3.1415926f
        private const val STROKE_WIDTH_DIP = 3
        private const val OUTER_RADIUS_DIP = 90
        private const val INNER_RADIUS_DIP = OUTER_RADIUS_DIP - 30
        private const val CENTER_RADIUS_DIP = 22
        private const val CENTER_OLD_RADIUS_DIP = 12
        private const val THUMB_RADIUS_DIP = 6
        private const val PADDING_DIP = 30
        private val WHEEL_COLORS = intArrayOf(
            -0x10000, -0xff01, -0xffff01, -0xff0001, -0xff0100,
            -0x100, -0x10000
        )
        private val BRIGHT_POS = floatArrayOf(0.08333f, 0.41667f, 1f)
        private val SATURATION_POS = floatArrayOf(0.58333f, 0.91667f, 1f)
        private val BACKGROUND_POS = floatArrayOf(0.0f, 0.5f, 1f)
    }
}