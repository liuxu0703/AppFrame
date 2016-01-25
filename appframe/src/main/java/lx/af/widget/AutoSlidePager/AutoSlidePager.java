package lx.af.widget.AutoSlidePager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import lx.af.R;

/**
 * author: lx
 * date: 15-10-23
 */
public class AutoSlidePager extends ViewGroup {

    private static final float DEFAULT_LINE_HEIGHT = 10; // in px
    private static final float DEFAULT_LINE_PADDING_TOP = 10; // in px
    private static final int DEFAULT_LINE_COLOR = Color.parseColor("#336699");
    private static final int DEFAULT_SLIDE_INTERVAL = 3000; // in millis
    private static final int DEFAULT_ANIM_DURATION = 300; // in millis
    private static final int DEFAULT_DIVIDER_WIDTH = 20; // in px

    private OnPageSelectListener mPageSelectListener;

    private AutoSlideLine mSlideLine;
    private ArrayList<View> mPageViews = new ArrayList<>();
    private int mCurrentPageIdx;

    private int mLineHeight;
    private int mLinePaddingTop;
    private int mLineColor;
    private int mLineAnimDuration;
    private int mItemDividerWidth;
    private int mAutoSlideInterval;
    private boolean mAutoSlide;

    private Runnable mAutoSlideRunnable = new Runnable() {
        @Override
        public void run() {
            setCurrentPageIndex(mCurrentPageIdx + 1);
            postDelayed(mAutoSlideRunnable, mAutoSlideInterval);
        }
    };

    private OnClickListener mItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int idx = mPageViews.indexOf(v);
            if (idx != -1) {
                setCurrentPageIndex(idx);
            }
        }
    };

    public AutoSlidePager(Context context) {
        super(context);
        init(context, null);
    }

    public AutoSlidePager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AutoSlidePager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void parseAttr(Context cxt, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = cxt.obtainStyledAttributes(attrs, R.styleable.AutoSlidePager, 0, 0);
            mAutoSlideInterval = a.getInt(R.styleable.AutoSlidePager_autoSlideInterval, DEFAULT_SLIDE_INTERVAL);
            mAutoSlide = a.getBoolean(R.styleable.AutoSlidePager_autoSlide, true);
            mLineColor = a.getColor(R.styleable.AutoSlidePager_indicatorColor, DEFAULT_LINE_COLOR);
            mLineHeight = (int) a.getDimension(R.styleable.AutoSlidePager_indicatorHeight, DEFAULT_LINE_HEIGHT);
            mLinePaddingTop = (int) a.getDimension(R.styleable.AutoSlidePager_indicatorHeight, DEFAULT_LINE_PADDING_TOP);
            mLineAnimDuration = a.getInt(R.styleable.AutoSlidePager_indicatorAnimDuration, DEFAULT_ANIM_DURATION);
            mItemDividerWidth = (int) a.getDimension(R.styleable.AutoSlidePager_itemDividerWidth, DEFAULT_DIVIDER_WIDTH);
            a.recycle();
        } else {
            mAutoSlideInterval = DEFAULT_SLIDE_INTERVAL;
            mAutoSlide = true;
            mLineColor = DEFAULT_LINE_COLOR;
            mLineHeight = (int) DEFAULT_LINE_HEIGHT;
            mLineAnimDuration = DEFAULT_ANIM_DURATION;
            mItemDividerWidth = DEFAULT_DIVIDER_WIDTH;
        }
    }

    private void init(Context context, AttributeSet attrs) {
        parseAttr(context, attrs);

        mSlideLine = new AutoSlideLine(context, this);
        mSlideLine.setLineColor(mLineColor);
        mSlideLine.setAnimDuration(mLineAnimDuration);
        LayoutParams p2 = new LayoutParams(
                LayoutParams.MATCH_PARENT, mLineHeight);
        addViewInLayout(mSlideLine, -1, p2);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        resetAutoSlide();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mAutoSlideRunnable);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();

            if (child instanceof AutoSlideLine) {
                child.layout(lp.x, lp.y,
                        lp.x + lp.itemWidth,
                        lp.y + lp.itemHeight);
            } else {
                child.layout(lp.x, lp.y,
                        lp.x + child.getMeasuredWidth(),
                        lp.y + child.getMeasuredHeight());
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mSlideLine.setVisibility(View.INVISIBLE);
        int largestItemHeight = 0;

        mPageViews.clear();
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child instanceof AutoSlideLine) {
                continue;
            }

            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (child.getVisibility() == View.GONE) {
                lp.itemWidth = 0;
                lp.itemHeight = 0;
            } else {
                lp.itemHeight = child.getMeasuredHeight();
                lp.itemWidth = child.getMeasuredWidth();
            }
            mPageViews.add(child);

            // find the largest item height
            if (largestItemHeight < lp.itemHeight) {
                largestItemHeight = lp.itemHeight;
            }
        }

        // calculate item view (x, y)
        int x = getPaddingLeft();
        for (int i = 0; i < mPageViews.size(); i ++) {
            View page = mPageViews.get(i);
            LayoutParams lp = (LayoutParams) page.getLayoutParams();

            // determine x
            if (i != 0 && lp.itemWidth != 0) {
                x += mItemDividerWidth;
            }
            lp.x = x;
            x = lp.x + lp.itemWidth;

            // determine y
            lp.y = getPaddingTop() + largestItemHeight - lp.itemHeight;
        }

        // calculate indicator line (x, y)
        {
            measureChild(mSlideLine, widthMeasureSpec, heightMeasureSpec);
            LayoutParams lp = (LayoutParams) mSlideLine.getLayoutParams();
            lp.width = x - getPaddingLeft();
            lp.height = mLineHeight;
            lp.itemWidth = lp.width;
            lp.itemHeight = lp.height;
            lp.x = getPaddingLeft();
            lp.y = getPaddingTop() + largestItemHeight + mLinePaddingTop;
            mSlideLine.setLayoutParams(lp);
        }

        // calculate total width, height
        int width = x + getPaddingRight();
        int height = getPaddingTop() + largestItemHeight +
                mLinePaddingTop + mLineHeight + getPaddingBottom();

        setMeasuredDimension(
                resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (!(child instanceof AutoSlideLine)) {
            child.setClickable(true);
            child.setOnClickListener(mItemClickListener);
        }
    }

    @Override
    protected boolean addViewInLayout(@NonNull View child, int index, ViewGroup.LayoutParams params, boolean preventRequestLayout) {
        boolean ret = super.addViewInLayout(child, index, params, preventRequestLayout);
        if (!(child instanceof AutoSlideLine)) {
            child.setClickable(true);
            child.setOnClickListener(mItemClickListener);
        }
        return ret;
    }

    /**
     * set current selected page index.
     * calling this method will trigger slide animation and OnPageSelectListener callback.
     * make sure page index is in the boundary ( 0 <= page < getPageCount() ).
     * @param idx the page index.
     */
    public void setCurrentPageIndex(int idx) {
        if (mPageViews.size() == 0) {
            return;
        }
        if (idx < 0) {
            mCurrentPageIdx = mPageViews.size() - 1;
        } else if (idx >= mPageViews.size()) {
            mCurrentPageIdx = 0;
        } else {
            mCurrentPageIdx = idx;
        }

        View page = getPage(mCurrentPageIdx);
        LayoutParams params = (LayoutParams) page.getLayoutParams();
        if (params.itemWidth == 0) {
            // ignore page with 0 width
            setCurrentPageIndex(mCurrentPageIdx + 1);
        } else {
            resetAutoSlide();
            mSlideLine.setVisibility(View.VISIBLE);
            mSlideLine.setCurrentPage(mCurrentPageIdx, true);
            if (mPageSelectListener != null) {
                mPageSelectListener.onPageSelected(mCurrentPageIdx, mPageViews.get(mCurrentPageIdx));
            }
        }
    }

    /**
     * enable/disable auto slide
     * @param autoSlide true to enable auto slide; false otherwise.
     */
    public void setAutoSlide(boolean autoSlide) {
        mAutoSlide = autoSlide;
        resetAutoSlide();
    }

    /**
     * set auto slide interval.
     * @param autoSlideInterval interval
     */
    public void setAutoSlideInterval(int autoSlideInterval) {
        mAutoSlideInterval = autoSlideInterval;
    }

    /**
     * set indicator line height
     * @param height line height
     */
    public void setIndicatorHeight(int height) {
        mLineHeight = height;
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT, mLineHeight);
        mSlideLine.setLayoutParams(params);
        invalidate();
    }

    /**
     * set indicator line color
     * @param color line color
     */
    public void setIndicatorColor(int color) {
        mLineColor = color;
        mSlideLine.setLineColor(color);
    }

    /**
     * set divider itemWidth between views.
     * @param width divider itemWidth
     */
    public void setItemDividerWidth(int width) {
        mItemDividerWidth = width;

    }

    /**
     * set OnPageSelectListener
     */
    public void setOnPageSelectListener(OnPageSelectListener pageSelectListener) {
        this.mPageSelectListener = pageSelectListener;
    }

    public int getPageCount() {
        return mPageViews.size();
    }

    /**
     * get current selected page index
     */
    public int getCurrentPageIndex() {
        return mCurrentPageIdx;
    }

    /**
     * get current selected page view
     */
    public View getCurrentPage() {
        if (mPageViews.size() == 0) {
            return null;
        } else {
            return mPageViews.get(mCurrentPageIdx);
        }
    }

    /**
     * get page view by page index
     * @param position the page index
     */
    public View getPage(int position) {
        return mPageViews.get(position);
    }

    private void resetAutoSlide() {
        removeCallbacks(mAutoSlideRunnable);
        if (mAutoSlide) {
            postDelayed(mAutoSlideRunnable, mAutoSlideInterval);
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p.width, p.height);
    }


    /**
     * layout params for this view
     */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        private int x;
        private int y;
        private int itemWidth;
        private int itemHeight;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }
    }


    /**
     * page select callback
     */
    public interface OnPageSelectListener {

        /**
         * callback when a page is selected
         * @param position the selected position
         * @param view the selected view
         */
        void onPageSelected(int position, View view);

    }

}
