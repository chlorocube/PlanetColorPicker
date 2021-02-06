package jp.co.chlorocube.planetcolorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ColorPickerView extends View {

    public interface ColorChangeListener {
        void onColorChanged(float[] hsv);
    }

    private static final int DEFAULT_HUE = 0;
    private static final int DEFAULT_SATURATION = 0;
    private static final int DEFAULT_BRIGHT = 1;

    private static final float PI = 3.1415926f;

    private static final int STROKE_WIDTH_DIP = 3;
    private static final int OUTER_RADIUS_DIP = 90;
    private static final int INNER_RADIUS_DIP = 60;
    private static final int CENTER_RADIUS_DIP = 22;
    private static final int CENTER_OLD_RADIUS_DIP = 12;
    private static final int THUMB_RADIUS_DIP = 6;
    private static final int PADDING_DIP = 30;

    private static final int[] WHEEL_COLORS = new int[] {
        0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00,
                0xFFFFFF00, 0xFFFF0000
    };
    private static final float[] BRIGHT_POS = new float[]{0.08333f, 0.41667f, 1f};
    private static final float[] SATURATION_POS = new float[]{0.58333f, 0.91667f, 1f};
    private static final float[] BACKGROUND_POS = new float[]{0.0f, 0.5f, 1f};

    private float mHue = 0; // 0~359
    private float mSaturationRatio = 1; // 0~1
    private float mBrightRatio = 1; // 0~1

    private Paint mWheelPaint;
    private Paint mBrightPaint;
    private Paint mSaturationPaint;
    private Paint mCenterPaint;
    private Paint mCenterOldPaint;
    private Paint mWheelThumbPaint;
    private Paint mBrightThumbPaint;
    private Paint mSaturationThumbPaint;
    private Paint mBackgroundPaint;

    private int[] mBrightColors;
    private int[] mSaturationColors;
    private int[] mBackgroundColors;

    private FloatPoint mWheelThumbPoint;
    private FloatPoint mBrightThumbPoint;
    private FloatPoint mSaturationThumbPoint;
    private boolean mIsTrackingWheel = false;
    private boolean mIsTrackingBright = false;
    private boolean mIsTrackingSaturation = false;

    private final RectF mRect = new RectF();

    private ColorChangeListener mListener = null;

    public ColorPickerView(Context context) {
        super(context);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = ColorPickerUtils.toPx(getContext(), OUTER_RADIUS_DIP + PADDING_DIP) * 2;
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        float r = ColorPickerUtils.toPx(getContext(), OUTER_RADIUS_DIP);
        mRect.set(-r, -r, r, r);
        float padding = ColorPickerUtils.toPx(getContext(), PADDING_DIP);
        canvas.translate(r + padding, r + padding);
        canvas.drawCircle(0, 0, r, mBackgroundPaint);
        canvas.drawOval(mRect, mWheelPaint);

        r = ColorPickerUtils.toPx(getContext(), INNER_RADIUS_DIP);
        mRect.set(-r, -r, r, r);
        canvas.drawArc(mRect, 30f, 120f, false, mBrightPaint);
        canvas.drawArc(mRect, 210f, 120f, false, mSaturationPaint);

        if (mWheelThumbPoint != null) {
            canvas.drawCircle(mWheelThumbPoint.x,
                    mWheelThumbPoint.y , ColorPickerUtils.toPx(getContext(), THUMB_RADIUS_DIP), mWheelThumbPaint);
        }
        if (mBrightThumbPoint != null) {
            canvas.drawCircle(mBrightThumbPoint.x,
                    mBrightThumbPoint.y , ColorPickerUtils.toPx(getContext(), THUMB_RADIUS_DIP), mBrightThumbPaint);
        }
        if (mSaturationThumbPoint != null) {
            canvas.drawCircle(mSaturationThumbPoint.x,
                    mSaturationThumbPoint.y , ColorPickerUtils.toPx(getContext(), THUMB_RADIUS_DIP), mSaturationThumbPaint);
        }

        r = ColorPickerUtils.toPx(getContext(), CENTER_RADIUS_DIP);
        canvas.drawCircle(0, -padding / 7, r, mCenterPaint);
        r = ColorPickerUtils.toPx(getContext(), CENTER_OLD_RADIUS_DIP);
        canvas.drawCircle(padding * 2 / 3, padding / 2, r, mCenterOldPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float r = ColorPickerUtils.toPx(getContext(), OUTER_RADIUS_DIP);
        float x = event.getX() - r - ColorPickerUtils.toPx(getContext(), PADDING_DIP);
        float y = event.getY() - r - ColorPickerUtils.toPx(getContext(), PADDING_DIP);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (Math.hypot(x, y) > ColorPickerUtils.toPx(getContext(),
                        (OUTER_RADIUS_DIP - INNER_RADIUS_DIP) / 2 + INNER_RADIUS_DIP)) {
                    mIsTrackingWheel = true;
                    redrawByTrackingWheel(x, y);
                } else if (Math.hypot(x, y) > ColorPickerUtils.toPx(getContext(),
                        (INNER_RADIUS_DIP - CENTER_RADIUS_DIP) / 2 + CENTER_RADIUS_DIP)) {
                    if (y < 0) {
                        mIsTrackingSaturation = true;
                        redrawByTrackingSaturation(x, y);
                    } else if (y > 0) {
                        mIsTrackingBright = true;
                        redrawByTrackingBright(x, y);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsTrackingWheel) {
                    redrawByTrackingWheel(x, y);
                } else if (mIsTrackingBright) {
                    redrawByTrackingBright(x, y);
                } else if (mIsTrackingSaturation) {
                    redrawByTrackingSaturation(x, y);
                }
                break;
            case MotionEvent.ACTION_UP:
                mIsTrackingWheel = false;
                mIsTrackingBright = false;
                mIsTrackingSaturation = false;
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * Returns the currently selected HSV values.
     * @return the currently selected HSV values
     */
    public float[] getCurrentHsv() {
        return new float[]{mHue,  mSaturationRatio, mBrightRatio};
    }

    /**
     * Returns the currently selected color value in the form 0xAARRGGBB.
     * @return the currently selected color value in the form 0xAARRGGBB
     */
    public int getCurrentColor() {
        return ColorPickerUtils.getHSVColor(mHue, mSaturationRatio, mBrightRatio);
    }

    /**
     * Used to do a one-time initialization of the ColorPickerView.
     * @param listener ColorChangeListener
     */
    public void initializePicker(ColorChangeListener listener) {
        initializePicker(DEFAULT_HUE, DEFAULT_SATURATION, DEFAULT_BRIGHT, listener);
    }

    /**
     * Used to do a one-time initialization of the ColorPickerView.
     * @param hsv initial HSV values
     * @param listener ColorChangeListener
     */
    public void initializePicker(float[] hsv, ColorChangeListener listener) {
        initializePicker(hsv[0], hsv[1], hsv[2], listener);
    }

    private void initializePicker(float hue, float saturation, float bright, ColorChangeListener listener) {
        mListener = listener;

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mWheelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWheelPaint.setStyle(Paint.Style.STROKE);
        mWheelPaint.setStrokeWidth(ColorPickerUtils.toPx(getContext(), STROKE_WIDTH_DIP));
        Shader outerShader = new SweepGradient(0, 0, WHEEL_COLORS, null);
        mWheelPaint.setShader(outerShader);

        mBrightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBrightPaint.setStyle(Paint.Style.STROKE);
        mBrightPaint.setStrokeWidth(ColorPickerUtils.toPx(getContext(), STROKE_WIDTH_DIP));
        mSaturationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSaturationPaint.setStyle(Paint.Style.STROKE);
        mSaturationPaint.setStrokeWidth(ColorPickerUtils.toPx(getContext(), STROKE_WIDTH_DIP));

        mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCenterPaint.setStyle(Paint.Style.FILL);
        mCenterOldPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCenterOldPaint.setStyle(Paint.Style.FILL);
        mWheelThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWheelThumbPaint.setStyle(Paint.Style.FILL);
        mBrightThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBrightThumbPaint.setStyle(Paint.Style.FILL);
        mSaturationThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSaturationThumbPaint.setStyle(Paint.Style.FILL);

        mHue = hue;
        mSaturationRatio = saturation;
        mBrightRatio = bright;
        int currentColor = ColorPickerUtils.getHSVColor(mHue,  mSaturationRatio, mBrightRatio);
        mCenterOldPaint.setColor(currentColor);
        mCenterPaint.setColor(currentColor);

        int wheelColor = ColorPickerUtils.getHSVColor(mHue, 1, 1);
        mWheelThumbPaint.setColor(wheelColor);
        mSaturationColors = new int[] {
                wheelColor, 0xffffffff, 0xffffffff,
        };
        mBrightColors = new int[] {
                0xff000000, wheelColor, wheelColor,
        };
        Shader brightShader = new SweepGradient(0, 0, mBrightColors, BRIGHT_POS);
        mBrightPaint.setShader(brightShader);
        Shader saturationShader = new SweepGradient(0, 0, mSaturationColors, SATURATION_POS);
        mSaturationPaint.setShader(saturationShader);
        mBrightThumbPaint.setColor(ColorPickerUtils.getHSVColor(mHue, 1, mBrightRatio));
        mSaturationThumbPaint.setColor(ColorPickerUtils.getHSVColor(mHue, mSaturationRatio, 1));

        float r = ColorPickerUtils.toPx(getContext(), OUTER_RADIUS_DIP);
        double x = r * Math.cos(mHue * 2 * PI / 360);
        double y = -r * Math.sin(mHue * 2 * PI / 360);
        mWheelThumbPoint = ColorPickerUtils.getFixedPoint((float)x, (float)y, r);

        r = ColorPickerUtils.toPx(getContext(), INNER_RADIUS_DIP);
        float arg = (float)((mSaturationRatio + 0.25) * 2 * PI / 3);
        x = r * Math.cos(arg);
        y = - r * Math.sin(arg);
        mSaturationThumbPoint = ColorPickerUtils.getFixedPoint((float)x, (float)y, r);

        arg = (float)((mBrightRatio + 0.25) * 2 * PI / 3);
        x = r * Math.cos(arg);
        y = r * Math.sin(arg);
        mBrightThumbPoint = ColorPickerUtils.getFixedPoint((float)x, (float)y, r);

        int backgroundColor = ColorPickerUtils.getHSVColor((hue + 180) % 360, 0.05f, 0.95f);
        mBackgroundColors = new int[] {
                backgroundColor, backgroundColor, 0xffffffff,
        };
        RadialGradient backgroundShader = new RadialGradient(0, 0, 2 * r, mBackgroundColors, BACKGROUND_POS, Shader.TileMode.CLAMP);
        mBackgroundPaint.setShader(backgroundShader);
    }

    private void redrawByTrackingWheel(float x, float y) {

        float r = ColorPickerUtils.toPx(getContext(), OUTER_RADIUS_DIP);
        mWheelThumbPoint = ColorPickerUtils.getFixedPoint(x, y, r);

        float angle = (float) java.lang.Math.atan2(y, x);
        float unit = angle / (2 * PI);
        if (unit < 0) {
            unit += 1;
        }
        int wheelColor = ColorPickerUtils.interpretColor(WHEEL_COLORS, unit);
        mWheelThumbPaint.setColor(wheelColor);

        mHue = ColorPickerUtils.getHue(wheelColor);
        int currentColor = ColorPickerUtils.getHSVColor(mHue,  mSaturationRatio, mBrightRatio);

        mBrightThumbPaint.setColor(ColorPickerUtils.getHSVColor(mHue, 1, mBrightRatio));
        mSaturationThumbPaint.setColor(ColorPickerUtils.getHSVColor(mHue, mSaturationRatio, 1));
        mCenterPaint.setColor(currentColor);

        mSaturationColors = new int[] {
                wheelColor, 0xffffffff, 0xffffffff,
        };
        mBrightColors = new int[] {
                0xff000000, wheelColor, wheelColor,
        };
        Shader brightShader = new SweepGradient(0, 0, mBrightColors, BRIGHT_POS);
        mBrightPaint.setShader(brightShader);
        Shader saturationShader = new SweepGradient(0, 0, mSaturationColors, SATURATION_POS);
        mSaturationPaint.setShader(saturationShader);

        r = ColorPickerUtils.toPx(getContext(), INNER_RADIUS_DIP);
        int backgroundColor = ColorPickerUtils.getHSVColor((mHue + 180) % 360, 0.05f, 0.95f);
        mBackgroundColors = new int[] {
                backgroundColor, backgroundColor, 0xffffffff,
        };
        RadialGradient rg = new RadialGradient(0, 0, 2 * r, mBackgroundColors, BACKGROUND_POS, Shader.TileMode.CLAMP);
        mBackgroundPaint.setShader(rg);

        if (mListener != null)
            mListener.onColorChanged(new float[]{mHue, mSaturationRatio, mBrightRatio});
        invalidate();
    }

    private void redrawByTrackingSaturation(float x, float y) {

        float r = ColorPickerUtils.toPx(getContext(), INNER_RADIUS_DIP);
        FloatPoint tempThumbPoint = ColorPickerUtils.getFixedPoint(x, y, r);
        if (tempThumbPoint.y > 0) {
            return;
        }
        if (tempThumbPoint.y > -r * Math.sin(PI / 6) * 7 / 8) {
            tempThumbPoint.y = (float)(-r * Math.sin(PI / 6));
            if (x > 0)
                tempThumbPoint.x = (float)(r * Math.cos(PI / 6));
            else
                tempThumbPoint.x = (float)(-r * Math.cos(PI / 6));
        }

        mSaturationThumbPoint = tempThumbPoint;
        double arg = Math.acos(mSaturationThumbPoint.x / r);
        mSaturationRatio = (float)(3 * arg / (2 * PI) - 0.25);

        int currentColor = ColorPickerUtils.getHSVColor(mHue,  mSaturationRatio, mBrightRatio);
        mSaturationThumbPaint.setColor(ColorPickerUtils.getHSVColor(mHue, mSaturationRatio, 1));
        mCenterPaint.setColor(currentColor);

        if (mListener != null)
            mListener.onColorChanged(new float[]{mHue, mSaturationRatio, mBrightRatio});
        invalidate();
    }

    private void redrawByTrackingBright(float x, float y) {

        float r = ColorPickerUtils.toPx(getContext(), INNER_RADIUS_DIP);
        FloatPoint tempThumbPoint = ColorPickerUtils.getFixedPoint(x, y, r);
        if (tempThumbPoint.y < 0) {
            return;
        }
        if (tempThumbPoint.y < r * Math.sin(PI / 6) * 7 / 8) {
            tempThumbPoint.y = (float)(r * Math.sin(PI / 6));
            if (x > 0)
                tempThumbPoint.x = (float)(r * Math.cos(PI / 6));
            else
                tempThumbPoint.x = (float)(-r * Math.cos(PI / 6));
        }

        mBrightThumbPoint = tempThumbPoint;
        double arg = Math.acos(mBrightThumbPoint.x / r);
        mBrightRatio = (float)(3 * arg / (2 * PI) - 0.25);

        int currentColor = ColorPickerUtils.getHSVColor(mHue,  mSaturationRatio, mBrightRatio);
        mBrightThumbPaint.setColor(ColorPickerUtils.getHSVColor(mHue, 1, mBrightRatio));
        mCenterPaint.setColor(currentColor);

        if (mListener != null)
            mListener.onColorChanged(new float[]{mHue, mSaturationRatio, mBrightRatio});
        invalidate();
    }
}
