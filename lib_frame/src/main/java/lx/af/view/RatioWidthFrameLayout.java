package lx.af.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import lx.af.R;

/**
 * author: lx
 * date: 16-5-22
 */
public class RatioWidthFrameLayout extends FrameLayout {

    private float mRatio;

    public RatioWidthFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseAttr(context, attrs);
    }

    public RatioWidthFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttr(context, attrs);
    }

    public RatioWidthFrameLayout(Context context) {
        super(context);
    }

    public void setRatio(float ratio) {
        mRatio = ratio;
        requestLayout();
    }

    public float getRatio() {
        return mRatio;
    }

    protected void setRatioInLayout(float ratio) {
        mRatio = ratio;
    }

    private void parseAttr(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RatioWidth);
        mRatio = a.getFloat(R.styleable.RatioWidth_ratioByWidth, 0f);
        a.recycle();
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (mRatio > 0) {
            params.height = (int) (params.width * mRatio);
        }
        super.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mRatio > 0) {
            int width = getDefaultSize(0, widthMeasureSpec);
            int height;
            height = (int) (width * mRatio);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            setMeasuredDimension(width, height);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
