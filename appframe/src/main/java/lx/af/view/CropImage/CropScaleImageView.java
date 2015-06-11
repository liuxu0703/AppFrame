package lx.af.view.CropImage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

/**
 * Created by sam on 14-12-31.
 *
 */
public class CropScaleImageView extends ImageView implements OnTouchListener {

    private static final String TAG = "CropImage";

    // an adjust value which makes the bitmap a little larger than clip size
    private static final float MIN_ADJUST_SCALE = 0.1f;

    private Matrix mMatrix;
    private final float[] mMatrixValues = new float[9];

    // clip rect width and height
    private int mClipWidth = 750;
    private int mClipHeight = 750;

    // width and height for the view
    private int mWidth;
    private int mHeight;

    // drawable origin width and height
    private int mIntrinsicWidth;
    private int mIntrinsicHeight;

    // current scale of the bitmap
    private float mScale;
    // min zoom scale for bitmap
    private float mMinScale;
    // max zoom scale for bitmap
    private float mMaxScale = 3.5f;

    private float mPrevDistance;
    private boolean mIsScaling;
    private int mPrevMoveX;
    private int mPrevMoveY;
    private GestureDetector mDetector;

    public CropScaleImageView(Context context, AttributeSet attr) {
        super(context, attr);
        initialize();
    }

    public CropScaleImageView(Context context) {
        super(context);
        initialize();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        this.initialize();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        this.initialize();
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        mWidth = r - l;
        mHeight = b - t;
        mMatrix.reset();

        // min size should be a little larger than clip size
        float minScaleX = (float) mClipWidth / (float) mIntrinsicWidth;
        float minScaleY = (float) mClipHeight / (float) mIntrinsicHeight;
        mMinScale = minScaleX > minScaleY ? minScaleX : minScaleY;
        mMinScale += MIN_ADJUST_SCALE;

        int paddingHeight;
        int paddingWidth;
        if (mScale * mIntrinsicHeight > mHeight) {
            // scaling vertical
            mScale = (float) mHeight / (float) mIntrinsicHeight;
            paddingWidth = (r - mWidth) / 2;
            paddingHeight = 0;
        } else {
            // scaling horizontal
            mScale = (float) mWidth / (float) mIntrinsicWidth;
            paddingHeight = (b - mHeight) / 2;
            paddingWidth = 0;
        }
        // init scale should be the one that makes the bitmap occupies a full screen.
        mScale = mMinScale > mScale ? mMinScale : mScale;
        // for bitmap with a low definition, the scale that makes the bitmap
        // occupies a full screen may be larger than default max scale.
        // in such case, choose a large one as max.
        mMaxScale = mScale > mMaxScale ? mScale : mMaxScale;

        mMatrix.postScale(mScale, mScale);
        // position the bitmap in center.
        mMatrix.postTranslate(paddingWidth, paddingHeight);
        setImageMatrix(mMatrix);
        adjustInit();
        return super.setFrame(l, t, r, b);
    }

    private void initialize() {
        this.setScaleType(ScaleType.MATRIX);
        this.mMatrix = new Matrix();
        Drawable d = getDrawable();
        if (d != null) {
            mIntrinsicWidth = d.getIntrinsicWidth();
            mIntrinsicHeight = d.getIntrinsicHeight();
            setOnTouchListener(this);
        }
        mDetector = new GestureDetector(getContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        zoomMax();
                        adjustInit();
                        return super.onDoubleTap(e);
                    }
                });
    }

    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    private float getScale() {
        return getValue(mMatrix, Matrix.MSCALE_X);
    }

    private float getTranslateX() {
        return getValue(mMatrix, Matrix.MTRANS_X);
    }

    private float getTranslateY() {
        return getValue(mMatrix, Matrix.MTRANS_Y);
    }

    private void zoom(float scale) {
        if (getScale() * scale < mMinScale) {
            return;
        }
        if (scale >= 1 && getScale() * scale > mMaxScale) {
            return;
        }

        RectF rect = getMatrixRectF();
        float transX = rect.left + mWidth * scale / 2;
        float transY = rect.top + mHeight * scale / 2;
        mMatrix.postScale(scale, scale, transX, transY);
        setImageMatrix(mMatrix);
    }

    private void zoomMax() {
        if (mMinScale != getScale() && (getScale() - mMinScale) > 0.1f) {
            // threshold 0.1f
            float scale = mMinScale / getScale();
            zoom(scale);
        } else {
            float scale = mMaxScale / getScale();
            zoom(scale);
        }
    }

    // adjust bitmap position according to clip border.
    // call this after a zoom or translation is done.
    private void adjustToBorder() {
        float deltaX = 0, deltaY = 0;
        final float viewWidth = getWidth();
        final float viewHeight = getHeight();
        int paddingHorizontal = (mWidth - mClipWidth) / 2;
        int paddingVertical = (mHeight - mClipHeight) / 2;
        RectF rect = getMatrixRectF();

        if (rect.top > paddingVertical) {
            deltaY = paddingVertical -rect.top;
        }
        if (rect.bottom < viewHeight - paddingVertical) {
            deltaY = viewHeight - paddingVertical - rect.bottom;
        }
        if (rect.left > paddingHorizontal) {
            deltaX = paddingHorizontal -rect.left;
        }
        if (rect.right < viewWidth - paddingHorizontal) {
            deltaX = viewWidth - paddingHorizontal - rect.right;
        }
        mMatrix.postTranslate(deltaX, deltaY);
        setImageMatrix(mMatrix);
    }

    // position the bitmap in center. called on init.
    private void adjustInit() {
        int width = (int) (mIntrinsicWidth * getScale());
        int height = (int) (mIntrinsicHeight * getScale());

        if (getTranslateX() < -(width - mWidth)) {
            mMatrix.postTranslate(-(getTranslateX() + width - mWidth), 0);
        }
        if (getTranslateX() > 0) {
            mMatrix.postTranslate(-getTranslateX(), 0);
        }
        if (getTranslateY() < -(height - mHeight)) {
            mMatrix.postTranslate(0, -(getTranslateY() + height - mHeight));
        }
        if (getTranslateY() > 0) {
            mMatrix.postTranslate(0, -getTranslateY());
        }
        if (width < mWidth) {
            mMatrix.postTranslate((mWidth - width) / 2, 0);
        }
        if (height < mHeight) {
            mMatrix.postTranslate(0, (mHeight - height) / 2);
        }
        setImageMatrix(mMatrix);
    }

    private float distance(float x0, float x1, float y0, float y1) {
        float x = x0 - x1;
        float y = y0 - y1;
        return FloatMath.sqrt(x * x + y * y);
    }

    private float dispDistance() {
        return FloatMath.sqrt(mWidth * mWidth + mHeight * mHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDetector.onTouchEvent(event)) {
            return true;
        }
        int touchCount = event.getPointerCount();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_1_DOWN:
            case MotionEvent.ACTION_POINTER_2_DOWN:
                if (touchCount >= 2) {
                    float distance = distance(
                            event.getX(0), event.getX(1), event.getY(0), event.getY(1));
                    mPrevDistance = distance;
                    mIsScaling = true;
                } else {
                    mPrevMoveX = (int) event.getX();
                    mPrevMoveY = (int) event.getY();
                }
            case MotionEvent.ACTION_MOVE:
                if (touchCount >= 2 && mIsScaling) {
                    float dist = distance(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
                    float scale = (dist - mPrevDistance) / dispDistance();
                    mPrevDistance = dist;
                    scale += 1;
                    scale = scale * scale;
                    zoom(scale);
                    adjustToBorder();
                } else if (!mIsScaling) {
                    int distanceX = mPrevMoveX - (int) event.getX();
                    int distanceY = mPrevMoveY - (int) event.getY();
                    mPrevMoveX = (int) event.getX();
                    mPrevMoveY = (int) event.getY();
                    mMatrix.postTranslate(-distanceX, -distanceY);
                    adjustToBorder();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_POINTER_2_UP:
                if (event.getPointerCount() <= 1) {
                    mIsScaling = false;
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return super.onTouchEvent(event);
    }

    private RectF getMatrixRectF() {
        Matrix matrix = mMatrix;
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (d != null) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    public void setClipSize(int width, int height) {
        mClipWidth = width;
        mClipHeight = height;
    }

    public Bitmap clip() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        return Bitmap.createBitmap(
                bitmap,
                (mWidth - mClipWidth) / 2,
                (mHeight - mClipHeight) / 2,
                mClipWidth,
                mClipHeight);
    }

}