package lx.af.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import lx.af.R;

/**
 * author: lx
 * date: 15-10-28
 */
public class MediaPlayButton extends View {


    private RectF mCircleBounds = new RectF();
    private Rect mIconBounds = new Rect();
    private Rect mIconAnimBounds = new Rect();

    private Paint mPaintProgress = new Paint();
    private Paint mPaintTrack = new Paint();

    private Drawable mDrawablePlay;
    private Drawable mDrawablePause;
    private Drawable mDrawableIcon;

    private int mProgressColor = Color.parseColor("#1E90FF");
    private int mProgressTrackColor = Color.parseColor("#20000000");
    private int mProgressWidth = 10; // in px
    private int mProgressArcDegree = 100; // in degree
    private int mProgressSpeed = 3; // in degree/redraw_interval
    private int mIconPadding = 30; // in px
    private int mIconPlayIconId = android.R.drawable.ic_media_play;
    private int mIconPauseIconId = android.R.drawable.ic_media_pause;
    private int mIconAnimDuration = 200;
    private boolean mIconVisible = true;

    private boolean mIsSpinning = false;
    private boolean mIsAnimBtn = false;
    private int mProgress = 0;
    private int mIconAnimSpeed = 3;
    private int mIconAnimInterval;


    public MediaPlayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // make view square
        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();
        if (widthWithoutPadding > heightWithoutPadding) {
            size = heightWithoutPadding;
        } else {
            size = widthWithoutPadding;
        }

        setMeasuredDimension(
                size + getPaddingLeft() + getPaddingRight(),
                size + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw progress track
        canvas.drawArc(mCircleBounds, 0, 360, false, mPaintTrack);

        // draw progress
        if (mIsSpinning) {
            canvas.drawArc(mCircleBounds, mProgress - 90, mProgressArcDegree, false, mPaintProgress);
        } else {
            canvas.drawArc(mCircleBounds, -90, mProgress, false, mPaintProgress);
        }

        // draw icon
        if (mIconVisible) {
            mDrawableIcon.setBounds(mIconAnimBounds);
            mDrawableIcon.draw(canvas);
        }

        if (mIsSpinning) {
            scheduleRedrawProgress();
        }
        if (mIsAnimBtn) {
            scheduleRedrawIcon();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setupBounds(w, h);
        setupPaints();
        invalidate();
    }

    // =========================================
    // private method

    private void initView(AttributeSet attrs) {
        parseAttrs(getContext().obtainStyledAttributes(attrs, R.styleable.MediaPlayButton));

        setClickable(true);
        mDrawablePlay = ContextCompat.getDrawable(getContext(), mIconPlayIconId);
        mDrawablePause = ContextCompat.getDrawable(getContext(), mIconPauseIconId);
        mDrawableIcon = mDrawablePlay;
    }

    private void parseAttrs(TypedArray a) {
        mProgressColor = a.getColor(R.styleable.MediaPlayButton_progressColor, mProgressColor);
        mProgressTrackColor = a.getColor(R.styleable.MediaPlayButton_progressTrackColor, mProgressTrackColor);
        mProgressWidth = (int) a.getDimension(R.styleable.MediaPlayButton_progressWidth, mProgressWidth);
        mProgressArcDegree = a.getInt(R.styleable.MediaPlayButton_progressArcDegree, mProgressArcDegree);
        mProgressSpeed = a.getInt(R.styleable.MediaPlayButton_progressSpeedDegree, mProgressSpeed);
        mIconPadding = (int) a.getDimension(R.styleable.MediaPlayButton_iconPadding, mIconPadding);
        mIconPlayIconId = a.getResourceId(R.styleable.MediaPlayButton_iconPlaySrc, mIconPlayIconId);
        mIconPauseIconId = a.getResourceId(R.styleable.MediaPlayButton_iconPauseSrc, mIconPauseIconId);
        mIconAnimDuration = a.getInt(R.styleable.MediaPlayButton_iconAnimDuration, mIconAnimDuration);
        mIconVisible = a.getBoolean(R.styleable.MediaPlayButton_iconVisible, mIconVisible);
        a.recycle();
    }

    private void setupPaints() {
        mPaintProgress.setAntiAlias(true);
        mPaintProgress.setStyle(Style.STROKE);
        mPaintProgress.setColor(mProgressColor);
        mPaintProgress.setStrokeWidth(mProgressWidth);

        mPaintTrack.setAntiAlias(true);
        mPaintTrack.setStyle(Style.STROKE);
        mPaintTrack.setColor(mProgressTrackColor);
        mPaintTrack.setStrokeWidth(mProgressWidth);
    }

    private void setupBounds(int layout_width, int layout_height) {
        // Width should equal to Height, find the min value to setup the circle
        int minValue = Math.min(layout_width, layout_height);

        // Calc the Offset if needed
        int xOffset = layout_width - minValue;
        int yOffset = layout_height - minValue;

        // Add the offset
        int paddingTop = getPaddingTop() + (yOffset / 2);
        int paddingBottom = getPaddingBottom() + (yOffset / 2);
        int paddingLeft = getPaddingLeft() + (xOffset / 2);
        int paddingRight = getPaddingRight() + (xOffset / 2);

        int width = getWidth();
        int height = getHeight();

        mCircleBounds.set(
                paddingLeft + mProgressWidth,
                paddingTop + mProgressWidth,
                width - paddingRight - mProgressWidth,
                height - paddingBottom - mProgressWidth);

        mIconBounds.set(
                (int) mCircleBounds.left + mIconPadding,
                (int) mCircleBounds.top + mIconPadding,
                (int) mCircleBounds.right - mIconPadding,
                (int) mCircleBounds.bottom - mIconPadding
        );

        mIconAnimBounds.set(mIconBounds);
    }

    private void scheduleRedrawIcon() {
        mIconAnimBounds.inset(-mIconAnimSpeed, -mIconAnimSpeed);
        if (mIconAnimBounds.contains(mIconBounds)) {
            mIconAnimBounds.set(mIconBounds);
            mIsAnimBtn = false;
        }
        postInvalidateDelayed(mIconAnimInterval);
    }

    private void scheduleRedrawProgress() {
        mProgress += mProgressSpeed;
        if (mProgress > 360) {
            mProgress = 0;
        }
        postInvalidate();
    }

    private void showIcon(Drawable icon, boolean anim) {
        if (mDrawableIcon == icon && !anim && mIconVisible) {
            // icon not change; no animation is requested.
            // no need to trigger onDraw()
            return;
        }

        mIconVisible = true;
        mDrawableIcon = icon;
        if (anim) {
            int length = mIconBounds.width() / 2;
            if (length != 0) {
                mIconAnimInterval = mIconAnimDuration / (length / mIconAnimSpeed);
                mIconAnimBounds.inset(length, length);
                mIsAnimBtn = true;
            }
        }
        postInvalidate();
    }

    // =========================================
    // public method

    /**
     * start progress spin
     */
    public void spin() {
        mIsSpinning = true;
        postInvalidate();
    }

    /**
     * stop progress spin
     */
    public void stopSpin() {
        mIsSpinning = false;
        mProgress = 0;
        postInvalidate();
    }

    /**
     * Set the progress to a specific value
     */
    public void setProgress(int i) {
        mIsSpinning = false;
        mProgress = i;
        postInvalidate();
    }

    /**
     * hide progress bar
     */
    public void hideProgress() {
        mIsSpinning = false;
        mProgress = 0;
        postInvalidate();
    }

    /**
     * show play button.
     * @param withAnim show the button with a scale animation
     */
    public void showPlay(boolean withAnim) {
        showIcon(mDrawablePlay, withAnim);
    }

    /**
     * show pause button.
     * @param withAnim show the button with a scale animation
     */
    public void showPause(boolean withAnim) {
        showIcon(mDrawablePause, withAnim);
    }

    /**
     * is progress spinning
     */
    public boolean isSpinning() {
        return mIsSpinning;
    }

    /**
     * is play/pause icon visible
     */
    public boolean isIconVisible() {
        return mIconVisible;
    }

    /**
     * is play button showing
     */
    public boolean isPlayShowing() {
        return mDrawableIcon == mDrawablePlay;
    }

    /**
     * set play/pause button visible/invisible
     * @param visible true to show; false to hide
     */
    public void setIconVisible(boolean visible) {
        mIconVisible = visible;
    }

    /**
     * set progress color
     */
    public void setProgressColor(int color) {
        mProgressColor = color;
        postInvalidate();
    }

    /**
     * set progress track color
     */
    public void setProgressTrackColor(int color) {
        mProgressTrackColor = color;
        postInvalidate();
    }

    /**
     * set play icon
     */
    public void setPlayIconDrawable(Drawable drawable) {
        mDrawablePlay = drawable;
        if (mDrawableIcon == mDrawablePlay) {
            postInvalidate();
        }
    }

    /**
     * set pause icon
     */
    public void setPauseIconDrawable(Drawable drawable) {
        mDrawablePause = drawable;
        if (mDrawableIcon == mDrawablePause) {
            postInvalidate();
        }
    }

    /**
     * set play icon
     */
    public void setPlayIconResource(int resId) {
        mIconPlayIconId = resId;
        mDrawablePlay = ContextCompat.getDrawable(getContext(), mIconPlayIconId);
        setPlayIconDrawable(mDrawablePlay);
    }

    /**
     * set pause icon
     */
    public void setPauseIconResource(int resId) {
        mIconPauseIconId = resId;
        mDrawablePause = ContextCompat.getDrawable(getContext(), mIconPauseIconId);
        setPlayIconDrawable(mDrawablePause);
    }

}