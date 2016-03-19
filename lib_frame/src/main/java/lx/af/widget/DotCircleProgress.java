package lx.af.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import lx.af.R;

/**
 * author: lx
 * date: 16-3-14
 *
 * a circle constructed by dots. can be used as progress bar or percentage indicator.
 *
 * attributes are as follows:
 *
 * <attr name="dotCount" format="integer" />
 * <attr name="dotRadius" format="dimension" />
 * <attr name="dotColor" format="color" />
 * <attr name="dotProgress" format="integer" />
 * <attr name="dotSpinRadius" format="dimension" />
 * <attr name="dotSpinColor" format="color" />
 * <attr name="dotSpinTailCount" format="integer" />
 * <attr name="dotSpinSpeed" format="integer" />
 *
 * dotCount:            dot count. default is 24.
 * dotRadius:           dot radius. default radius is determined by dot count.
 * dotColor:            dot color. default is white
 * dotProgress:         initial progress, range [0-360]. default is 0.
 * dotSpinRadius:       spinning dot radius. default is dotRadius * 2.
 * dotSpinColor:        spinning dot color. default is the same as dotColor.
 * dotSpinSpeed:        spinning speed, degree per second. default is 240.
 *                      for example, if set to 360, it will spend 1 second to startSpin a whole circle.
 * dotSpinTailCount:    spinning tail count. default is dotCount / 6 .
 *
 */
public class DotCircleProgress extends View {

    private static final int DEFAULT_DOT_COUNT = 24;
    private static final int DEFAULT_DOT_COLOR = Color.WHITE;
    private static final int DEFAULT_SPIN_SPEED = 240; // in degree angle
    private static final int DEFAULT_PROGRESS_ANIM_DURATION = 600; // in millis

    private int mSize; // view size (both width and height), since this is a square view.

    private int mDotCount;
    private int mDotRadius;
    private int mDotColor;

    private int mSpinDotRadius;
    private int mSpinDotColor;
    private int mSpinTailCount;
    private int mSpinInterval;
    private int mSpinProgress = 0;
    private boolean mIsSpinning = false;

    private int mProgress;
    private int mAnimProgress = 0;
    private int mAnimDuration = DEFAULT_PROGRESS_ANIM_DURATION;
    private int mAnimInterval;
    private boolean mIsAnimProgress = false;

    private Paint mDotPaint = new Paint();
    private Paint mDotSpinPaint = new Paint();
    private Paint mDotTrackPaint = new Paint();

    private ArrayList<PointF> mDotCenterList;
    private ArrayList<Integer> mSpinTailRadiusList;

    public DotCircleProgress(Context context) {
        super(context);
        initView(context, null);
    }

    public DotCircleProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    /**
     * start spin
     */
    public void startSpin() {
        if (!mIsSpinning) {
            mIsAnimProgress = false;
            mIsSpinning = true;
            postInvalidate();
        }
    }

    /**
     * stop spin
     */
    public void stopSpin() {
        mIsSpinning = false;
        mSpinProgress = 0;
    }

    /**
     * set progress.
     * use {@link #setProgressWithAnim(int)} to show progress with animation
     * @param progress range [0-360]
     */
    public void setProgress(int progress) {
        int p = convertProgress(progress);
        if (mProgress != p) {
            mProgress = p;
            postInvalidate();
        }
    }

    /**
     * set progress.
     * use {@link #setProgress(int)} to show progress without animation
     * @param progress range [0-360]
     */
    public void setProgressWithAnim(int progress) {
        mProgress = convertProgress(progress);
        mIsSpinning = false;
        mIsAnimProgress = true;
        mAnimProgress = 0;
        mAnimInterval = mProgress == 0 ? 0 : mAnimDuration / mProgress;
        postInvalidate();
    }

    /**
     * @return true if spinning
     */
    public boolean isSpinning() {
        return mIsSpinning;
    }

    /**
     * @return current progress, range [0-360]
     */
    public int getProgress() {
        return mProgress * 360 / mDotCount;
    }


    // =============================================
    // private and override methods


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // make view square
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();
        if (widthWithoutPadding > heightWithoutPadding) {
            mSize = heightWithoutPadding;
        } else {
            mSize = widthWithoutPadding;
        }

        setMeasuredDimension(
                mSize + getPaddingLeft() + getPaddingRight(),
                mSize + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w == 0 || h == 0) {
            return;
        }
        if (oldw == w && oldh == h) {
            return;
        }
        resetDotCenterList();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mDotCenterList.size(); i ++) {
            PointF p = mDotCenterList.get(i);
            int radius;
            Paint paint;
            if (mIsSpinning) {
                radius = getSpinRadius(i);
                paint = (radius != mDotRadius) ? mDotSpinPaint : mDotPaint;
            } else if (mIsAnimProgress) {
                radius = mDotRadius;
                paint = (i >= mAnimProgress) ? mDotTrackPaint : mDotPaint;
            } else {
                radius = mDotRadius;
                paint = (i >= mProgress) ? mDotTrackPaint : mDotPaint;
            }
            canvas.drawCircle(p.x, p.y, radius, paint);
        }

        if (mIsSpinning) {
            scheduleDrawSpin();
        } else if (mIsAnimProgress) {
            scheduleDrawProgress();
        }
    }

    private void initView(Context context, AttributeSet attrs) {
        int spinSpeed;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DotCircle);
            mDotCount = a.getInteger(R.styleable.DotCircle_dotCount, DEFAULT_DOT_COUNT);
            mDotColor = a.getColor(R.styleable.DotCircle_dotColor, DEFAULT_DOT_COLOR);
            mDotRadius = a.getDimensionPixelOffset(R.styleable.DotCircle_dotRadius, 0);
            mSpinDotRadius = a.getDimensionPixelOffset(R.styleable.DotCircle_dotSpinRadius, mDotRadius * 2);
            mSpinDotColor = a.getColor(R.styleable.DotCircle_dotSpinColor, mDotColor);
            mSpinTailCount = a.getInteger(R.styleable.DotCircle_dotSpinTailCount, mDotCount / 6);
            mAnimDuration = a.getInteger(R.styleable.DotCircle_dotProgressAnimDuration, DEFAULT_PROGRESS_ANIM_DURATION);
            int progress = a.getInteger(R.styleable.DotCircle_dotProgress, 0);
            mProgress = convertProgress(progress);
            spinSpeed = a.getInt(R.styleable.DotCircle_dotSpinSpeed, DEFAULT_SPIN_SPEED);
            a.recycle();
        } else {
            mDotCount = DEFAULT_DOT_COUNT;
            mDotColor = DEFAULT_DOT_COLOR;
            mSpinDotColor = mDotColor;
            mSpinTailCount = mDotCount / 6;
            spinSpeed = DEFAULT_SPIN_SPEED;
        }

        mSpinInterval = (360 * 1000) / (spinSpeed * mDotCount);
        if (mSpinTailCount >= mDotCount) {
            mSpinTailCount = mDotCount - 1;
        }
        if (mSpinTailCount < 0) {
            mSpinTailCount = 0;
        }
        mSpinTailRadiusList = new ArrayList<>(mSpinTailCount);

        initSpinRadius();
        resetPaint();
    }

    private void resetPaint() {
        mDotPaint.setColor(mDotColor);
        mDotPaint.setAntiAlias(true);
        mDotTrackPaint.setColor(mDotColor);
        mDotTrackPaint.setAlpha(64);
        mDotTrackPaint.setAntiAlias(true);
        mDotSpinPaint.setColor(mSpinDotColor);
        mDotSpinPaint.setAntiAlias(true);
    }

    private void resetDotCenterList() {
        if (mDotCenterList == null) {
            mDotCenterList = new ArrayList<>(mDotCount);
        } else {
            mDotCenterList.clear();
        }
        float radius = mSize / 2f;
        float centerX = getPaddingLeft() + radius;
        float centerY = getPaddingTop() + radius;

        if (mDotRadius == 0) {
            int perimeter = (int) (2 * Math.PI * radius);
            mDotRadius = (perimeter / mDotCount) / 5;
            initSpinRadius();
        }

        float r = radius - Math.max(mDotRadius, mSpinDotRadius);
        double angle = (360d / mDotCount) * (Math.PI / 180);
        for (int i = 0; i < mDotCount; i ++) {
            float x = centerX + (float) (r * Math.sin(i * angle));
            float y = centerY - (float) (r * Math.cos(i * angle));
            mDotCenterList.add(new PointF(x, y));
        }
    }

    private void scheduleDrawSpin() {
        mSpinProgress += 1;
        if (mSpinProgress >= mDotCount) {
            mSpinProgress = 0;
        }
        postInvalidateDelayed(mSpinInterval);
    }

    private void scheduleDrawProgress() {
        mAnimProgress += 1;
        if (mAnimProgress > mProgress) {
            mAnimProgress = mProgress;
            mIsAnimProgress = false;
        } else {
            postInvalidateDelayed(mAnimInterval);
        }
    }

    private void initSpinRadius() {
        if (mDotRadius == 0) {
            return;
        }
        if (mSpinDotRadius == 0) {
            mSpinDotRadius = mDotRadius * 2;
        }
        int delta = (mSpinDotRadius - mDotRadius) / (mSpinTailCount + 1);
        for (int i = 1; i <= mSpinTailCount; i ++) {
            int radius = mSpinDotRadius - i * delta;
            mSpinTailRadiusList.add(radius);
        }
    }

    private int getSpinRadius(int index) {
        if (index == mSpinProgress) {
            return mSpinDotRadius;
        }

        int m = mSpinProgress - index;
        if (m < 0) {
            m = m + mDotCount;
        }
        if (m > mSpinTailCount) {
            return mDotRadius;
        }
        return mSpinTailRadiusList.get(m - 1);
    }

    /**
     * convert degree progress to dot count
     * @param degreeProgress range [0-360].
     *                       less than 0 will be treat as 0;
     *                       greater than 360 will be treat as 360.
     * @return dot count progress
     */
    private int convertProgress(int degreeProgress) {
        if (degreeProgress > 360) {
            degreeProgress = 360;
        }
        if (degreeProgress < 0) {
            degreeProgress = 0;
        }
        return mDotCount * degreeProgress / 360;
    }

}
