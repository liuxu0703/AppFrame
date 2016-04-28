package lx.af.widget.divider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import lx.af.R;

/**
 * author: lx
 * date: 16-3-25
 *
 * a divider line with a quote triangle.
 *
 * attributes are as follows:
 *
 * <attr name="dividerQuoteLeft" format="dimension" />
 * <attr name="dividerQuoteAngle" format="integer" />
 * <attr name="dividerQuoteAnimDuration" format="integer" />
 * <attr name="dividerLineWidth" format="dimension" />
 * <attr name="dividerLineColor" format="color" />
 * <attr name="dividerUpperColor" format="color" />
 * <attr name="dividerLowerColor" format="color" />
 *
 * dividerQuoteLeft:            distance from quote triangle to view left edge.
 * dividerQuoteAngle:           angle of the quote triangle, default is 60 (equilateral triangle).
 * dividerQuoteAnimDuration:    duration when animate quote move. default is 500 millis.
 * dividerLineWidth:            divider and quote line width. default is 1 pixel.
 * dividerLineColor:            divider and quote line color.
 * dividerUpperColor:           color of space above divider and quote. default is transparent.
 * dividerLowerColor:           color of space bellow divider and quote. default is transparent.
 *
 * this view will not honor WRAP_CONTENT. the quote triangle's height
 * is determined by the view's height.
 */
public class QuoteDivider extends View {

    private static final int MIN_HEIGHT = 6;
    private static final int DEFAULT_LINE_WIDTH = 1;
    private static final int DEFAULT_LINE_COLOR = Color.parseColor("#d8d8d8");
    private static final int DEFAULT_UPPER_COLOR = Color.TRANSPARENT;
    private static final int DEFAULT_LOWER_COLOR = Color.TRANSPARENT;
    private static final int DEFAULT_QUOTE_ANGLE = 60; // in degree
    private static final int DEFAULT_QUOTE_ANIM_DURATION = 500;

    private int mTriangleLeft;
    private int mTriangleRight;
    private int mTriangleAngle;
    private int mLineWidth;
    private int mLineColor;
    private int mUpperColor;
    private int mLowerColor;
    private boolean mTriangleMiddle = false;

    private int mTriangleX;
    private int mTriangleFinalX;
    private int mTriangleStartX;
    private int mTriangleHeight;
    private int mTriangleBottomHalf;

    private Paint mPaint = new Paint();
    private Paint mPaintLine = new Paint();
    private Path mTrianglePath = new Path();
    private Point mPointTriangle1 = new Point();
    private Point mPointTriangle2 = new Point();
    private Point mPointTriangle3 = new Point();
    private Point mPointLineBegin = new Point();
    private Point mPointLineEnd = new Point();

    private boolean mIsAnim = false;
    private long mAnimDuration = DEFAULT_QUOTE_ANIM_DURATION;
    private long mAnimStartTime;
    private Interpolator mInterpolator;

    public QuoteDivider(Context context) {
        super(context);
        initView(context, null);
    }

    public QuoteDivider(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    /**
     * set distance between view left edge and the quote triangle, with an animation.
     */
    public void setQuoteLeftWithAnim(int left) {
        mTriangleStartX = mTriangleX;
        mTriangleFinalX = left - mTriangleBottomHalf;
        mAnimStartTime = System.currentTimeMillis();
        mIsAnim = true;
        ensureInterpolator();
        postInvalidate();
    }

    /**
     * set distance between view left edge and the quote triangle.
     */
    public void setQuoteLeft(int left) {
        mTriangleLeft = left;
        post(new Runnable() {
            @Override
            public void run() {
                resetTriangleValues();
                postInvalidate();
            }
        });
    }

    public void setQuoteRight(int right) {
        mTriangleRight = right;
        post(new Runnable() {
            @Override
            public void run() {
                resetTriangleValues();
                postInvalidate();
            }
        });
    }

    /**
     * set angle of the quote triangle.
     */
    public void setQuoteAngle(int angle) {
        mTriangleAngle = angle;
        post(new Runnable() {
            @Override
            public void run() {
                resetTriangleValues();
                postInvalidate();
            }
        });
    }

    /**
     * set line and quote line color.
     */
    public void setLineColor(int color) {
        mLineColor = color;
        postInvalidate();
    }

    /**
     * set color to space above divider and quote.
     */
    public void setUpperColor(int color) {
        mUpperColor = color;
        postInvalidate();
    }

    /**
     * set color to space bellow divider and quote.
     */
    public void setLowerColor(int color) {
        mLowerColor = color;
        postInvalidate();
    }


    // ==============================================


    private void initView(Context context, AttributeSet attrs) {
        setMinimumHeight(MIN_HEIGHT);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.QuoteDivider);
            mLineColor = a.getColor(R.styleable.QuoteDivider_dividerLineColor, DEFAULT_LINE_COLOR);
            mUpperColor = a.getColor(R.styleable.QuoteDivider_dividerUpperColor, DEFAULT_UPPER_COLOR);
            mLowerColor = a.getColor(R.styleable.QuoteDivider_dividerLowerColor, DEFAULT_LOWER_COLOR);
            mLineWidth = a.getDimensionPixelOffset(R.styleable.QuoteDivider_dividerLineWidth, DEFAULT_LINE_WIDTH);
            mTriangleAngle = a.getInteger(R.styleable.QuoteDivider_dividerQuoteAngle, DEFAULT_QUOTE_ANGLE);
            mTriangleLeft = a.getDimensionPixelOffset(R.styleable.QuoteDivider_dividerQuoteLeft, 0);
            mTriangleRight = a.getDimensionPixelOffset(R.styleable.QuoteDivider_dividerQuoteRight, 0);
            mTriangleMiddle = a.getBoolean(R.styleable.QuoteDivider_dividerQuoteMiddle, false);
            mAnimDuration = a.getInteger(R.styleable.QuoteDivider_dividerQuoteAnimDuration, DEFAULT_QUOTE_ANIM_DURATION);
            a.recycle();
        } else {
            mLineWidth = DEFAULT_LINE_WIDTH;
            mLineColor = DEFAULT_LINE_COLOR;
            mUpperColor = DEFAULT_UPPER_COLOR;
            mLowerColor = DEFAULT_LOWER_COLOR;
        }

        resetPaint();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            resetTriangleValues();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        int pTop = getPaddingTop() + mLineWidth / 2;
        int pBottom = getPaddingBottom() - mLineWidth / 2;
        mPointLineBegin.set(0, pTop + mTriangleHeight);
        mPointLineEnd.set(w, pTop + mTriangleHeight);
        mPointTriangle1.set(mTriangleX, pTop + mTriangleHeight);
        mPointTriangle2.set(mTriangleX + mTriangleBottomHalf, pTop);
        mPointTriangle3.set(mTriangleX + 2 * mTriangleBottomHalf, pTop + mTriangleHeight);

        // draw upper part
        if (mUpperColor != 0) {
            mPaint.setColor(mUpperColor);
            canvas.drawRect(0, 0, w, pTop + mTriangleHeight, mPaint);
        }

        // draw lower part
        if (mLowerColor != 0) {
            mPaint.setColor(mLowerColor);
            if (pBottom != 0) {
                canvas.drawRect(0, pTop + mTriangleHeight, w, h, mPaint);
            }
        }

        // draw triangle as part of lower part
        if (mUpperColor != mLowerColor) {
            mTrianglePath.reset();
            mTrianglePath.moveTo(mPointTriangle1.x, mPointTriangle1.y);
            mTrianglePath.lineTo(mPointTriangle2.x, mPointTriangle2.y);
            mTrianglePath.lineTo(mPointTriangle3.x, mPointTriangle3.y);
            mTrianglePath.close();
            mPaint.setColor(mLowerColor);
            canvas.drawPath(mTrianglePath, mPaint);
        }

        // draw divider line
        mTrianglePath.reset();
        mTrianglePath.moveTo(mPointLineBegin.x, mPointLineBegin.y);
        mTrianglePath.lineTo(mPointTriangle1.x, mPointTriangle1.y);
        mTrianglePath.lineTo(mPointTriangle2.x, mPointTriangle2.y);
        mTrianglePath.lineTo(mPointTriangle3.x, mPointTriangle3.y);
        mTrianglePath.lineTo(mPointLineEnd.x, mPointLineEnd.y);
        mPaintLine.setColor(mLineColor);
        canvas.drawPath(mTrianglePath, mPaintLine);

        if (mIsAnim) {
            scheduleAnim();
        }
    }

    private void resetTriangleValues() {
        mTriangleHeight = getHeight() - getPaddingTop() - getPaddingBottom() - mLineWidth;
        if (mTriangleHeight < 0) {
            throw new IllegalStateException(
                    "not enough space to draw divider, triangle height=" + mTriangleHeight);
        }
        if (mTriangleMiddle) {
            mTriangleLeft = getWidth() / 2;
        } else if (mTriangleLeft == 0 && mTriangleRight != 0) {
            mTriangleLeft = getWidth() - mTriangleRight;
        }
        double angle = (mTriangleAngle / 2) * (Math.PI / 180);
        mTriangleBottomHalf = (int) (mTriangleHeight * Math.tan(angle));
        mTriangleFinalX = mTriangleLeft - mTriangleBottomHalf;
        mTriangleX = mTriangleFinalX;
    }

    private void resetPaint() {
        mPaintLine.setAntiAlias(true);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setStrokeWidth(mLineWidth);
        mPaintLine.setColor(mLineColor);
    }

    private void ensureInterpolator() {
        if (mInterpolator == null) {
            mInterpolator = new AccelerateDecelerateInterpolator();
        }
    }

    private void scheduleAnim() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - mAnimStartTime;
        if (elapsedTime >= mAnimDuration) {
            mTriangleX = mTriangleFinalX;
            mIsAnim = false;
        } else {
            float normalizedTime = ((float) elapsedTime) / ((float) mAnimDuration);
            float interpolatedTime = mInterpolator.getInterpolation(normalizedTime);
            mTriangleX = (int) (mTriangleStartX + ((mTriangleFinalX - mTriangleStartX) * interpolatedTime));
            postInvalidateDelayed((long) interpolatedTime);
        }
    }

}