package lx.af.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;

import lx.af.R;

/**
 * author: lx
 * date: 15-12-11
 */
public class RatioWidthViewPager extends android.support.v4.view.ViewPager {

    private float mRatio;

    public RatioWidthViewPager(Context context) {
        super(context);
    }

    public RatioWidthViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RatioWidth);
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
            int height = (int) (width * mRatio);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            setMeasuredDimension(width, height);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
