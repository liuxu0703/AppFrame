package lx.af.view.CropImage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by sam on 14-10-16.
 *
 */
public class CropImageBorderView extends View
        implements ViewTreeObserver.OnGlobalLayoutListener {

    private static final String TAG = "CropImage";

    private int mHorizontalPadding;
    private int mVerticalPadding;
    private int mWidth;
    private int mHeight;
    private int mBorderColor = Color.parseColor("#FFFFFF");
    private int mBorderWidth = 2;
    private Paint mPaintBlock;
    private Paint mPaintLine;

    public CropImageBorderView(Context context) {
        this(context, null);
    }

    public CropImageBorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageBorderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mHorizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mHorizontalPadding, getResources().getDisplayMetrics());
        mBorderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mBorderWidth, getResources().getDisplayMetrics());

        mPaintBlock = new Paint();
        mPaintBlock.setAntiAlias(true);
        mPaintBlock.setColor(Color.parseColor("#AA000000"));
        mPaintBlock.setStyle(Paint.Style.FILL);
        mPaintLine = new Paint();
        mPaintLine.setAntiAlias(true);
        mPaintLine.setColor(mBorderColor);
        mPaintLine.setStrokeWidth(mBorderWidth);
        mPaintLine.setStyle(Paint.Style.STROKE);
    }

    public void setClipSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, mHorizontalPadding, getHeight(), mPaintBlock);
        canvas.drawRect(getWidth() - mHorizontalPadding, 0, getWidth(), getHeight(), mPaintBlock);
        canvas.drawRect(mHorizontalPadding, 0, getWidth() - mHorizontalPadding, mVerticalPadding, mPaintBlock);
        canvas.drawRect(mHorizontalPadding, getHeight() - mVerticalPadding, getWidth() - mHorizontalPadding,
                getHeight(), mPaintBlock);

        canvas.drawRect(mHorizontalPadding, mVerticalPadding, getWidth() - mHorizontalPadding,
                getHeight() - mVerticalPadding, mPaintLine);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        mHorizontalPadding = (getWidth() - mWidth) / 2;
        mVerticalPadding = (getHeight() - mHeight) / 2;
    }

}
