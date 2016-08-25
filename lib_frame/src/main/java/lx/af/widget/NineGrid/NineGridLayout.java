package lx.af.widget.NineGrid;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lx.af.R;

/**
 * author: lx
 * date: 15-12-23
 *
 * container to display nine views as grid.
 *
 * it is bad practice to nest GridView in another adapter view like ListView,
 * since the adapter view will not recycle the second level adapter view.
 * this layout is aimed to solve the problem.
 *
 * actually we support more than nine views, but we do not recycle them. so
 * do not put too much view in this layout.
 */
public class NineGridLayout extends ViewGroup {

    private int mSize1Column;
    private int mSize2Column;
    private int mSize3Column;
    private int mSpaceSize;
    private boolean mAutoSize = false;
    private boolean mSizeMatchParent = false;

    private ArrayList<View> mViews = new ArrayList<>(9);
    private Map<View, Integer> mViewMap = new HashMap<>(9);
    private NineGridAdapter mAdapter;

    public NineGridLayout(Context context) {
        super(context);
        init(context, null);
    }

    public NineGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public NineGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context cxt, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = cxt.obtainStyledAttributes(attrs, R.styleable.NineGridLayout, 0, 0);
            mSize1Column = a.getDimensionPixelSize(R.styleable.NineGridLayout_viewSizeOneColumn, 0);
            mSize2Column = a.getDimensionPixelSize(R.styleable.NineGridLayout_viewSizeTwoColumn, 0);
            mSize3Column = a.getDimensionPixelSize(R.styleable.NineGridLayout_viewSizeThreeColumn, 0);
            mSizeMatchParent = a.getBoolean(R.styleable.NineGridLayout_viewSizeMatchParent, false);
            mSpaceSize = a.getDimensionPixelSize(R.styleable.NineGridLayout_gridSpaceSize, 0);
            a.recycle();
        }

        if (!mSizeMatchParent && (mSize1Column == 0 || mSize2Column == 0 || mSize3Column == 0)) {
            mAutoSize = true;
        }
    }

    public void setAdapter(NineGridAdapter adapter) {
        if (adapter == null || adapter.getCount() == 0) {
            return;
        }

        mAdapter = adapter;
        mViewMap.clear();
        int c = adapter.getCount() - mViews.size();
        if (c > 0) {
            for (int i = 0; i < c; i ++) {
                View view = adapter.initItemView(getContext());
                mViews.add(view);
            }
        }

        removeAllViews();
        int count = adapter.getCount();
        for (int i = 0; i < count; i ++) {
            View view = mViews.get(i);
            mViewMap.put(view, i);
            LayoutParams params = adapter.getLayoutParams(view, i, count);
            if (params == null) {
                params = generateDefaultLayoutParams();
            }
            addViewInLayout(view, -1, params);
            adapter.displayItemView(view, i, count);
        }
    }

    public NineGridAdapter getAdapter() {
        return mAdapter;
    }

    public Object getDataByView(View view) {
        if (mAdapter == null) {
            return null;
        }
        int position = getPositionByView(view);
        if (position == -1) {
            return null;
        } else {
            return mAdapter.getData(position);
        }
    }

    public int getPositionByView(View view) {
        Object obj = mViewMap.get(view);
        if (obj == null) {
            return -1;
        }
        return (int) obj;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            child.layout(lp.x, lp.y, lp.x + lp.itemWidth, lp.y + lp.itemHeight);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mSizeMatchParent) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            mSize1Column = width;
            mSize2Column = (width - mSpaceSize) / 2;
            mSize3Column = (width - 2 * mSpaceSize) / 3;
        } else if (mAutoSize) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            mSize1Column = width;
            mSize2Column = mSize3Column = (width - 2 * mSpaceSize) / 3;
        }

        int count = getChildCount();
        if (count == 0) {
            setMeasuredDimension(
                    resolveSize(0, widthMeasureSpec),
                    resolveSize(0, heightMeasureSpec));
            return;
        }

        int width;
        int height;

        if (count == 1) {
            // one column
            View view = getChildAt(0);
            measureChild(view, widthMeasureSpec, heightMeasureSpec);
            int w = view.getMeasuredWidth();
            int h = view.getMeasuredHeight();
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.x = lp.y = 0;
            if (lp.aspectHW != 0) {
                lp.itemWidth = mSize1Column;
                lp.itemHeight = (int) (lp.itemWidth * lp.aspectHW);
            } else if (w != 0) {
                lp.itemWidth = mSize1Column;
                lp.itemHeight = lp.itemWidth * h / w;
            }
            width = lp.itemWidth;
            height = lp.itemHeight;

        } else if (count == 2) {
            View v1 = getChildAt(0);
            LayoutParams lp1 = (LayoutParams) v1.getLayoutParams();
            lp1.x = 0;
            lp1.y = 0;
            lp1.itemWidth = mSize2Column;
            lp1.itemHeight = mSize2Column;
            View v2 = getChildAt(1);
            LayoutParams lp2 = (LayoutParams) v2.getLayoutParams();
            lp2.x = mSize2Column + mSpaceSize;
            lp2.y = 0;
            lp2.itemWidth = mSize2Column;
            lp2.itemHeight = mSize2Column;
            width = mSize2Column * 2 + mSpaceSize;
            height = mSize2Column;

        } else if (count == 4) {
            int x = 0;
            int y = 0;
            for (int i = 0; i < count; i ++) {
                View child = getChildAt(i);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                lp.x = x;
                lp.y = y;
                lp.itemWidth = mSize2Column;
                lp.itemHeight = mSize2Column;

                if (i == 0 || i == 2) {
                    x = x + mSize2Column + mSpaceSize;
                } else if (i == 1) {
                    x = 0;
                    y = y + mSize2Column + mSpaceSize;
                }
            }

            width = mSize2Column * 2 + mSpaceSize;
            height = mSize2Column * 2 + mSpaceSize;

        } else {
            // more column
            int x = 0;
            int y = 0;
            for (int i = 0; i < count; i ++) {
                View child = getChildAt(i);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                lp.x = x;
                lp.y = y;
                lp.itemWidth = mSize3Column;
                lp.itemHeight = mSize3Column;

                if (i % 3 == 2) {
                    x = 0;
                    if (i != count - 1) {
                        y = y + mSize3Column + mSpaceSize;
                    }
                } else {
                    x = x + mSize3Column + mSpaceSize;
                }
            }

            width = mSize3Column * 3 + mSpaceSize * 2;
            height = y + mSize3Column;
        }

        setMeasuredDimension(
                resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
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
     * layout params for this ViewGroup
     *
     * some of the view may not return correct width and height when calling
     * {@link View#getMeasuredHeight()}, {@link View#getMeasuredWidth()}
     * (fresco SimpleDraweeView for one).
     * in this case, you can pass {@link LayoutParams#aspectHW} in
     * {@link NineGridAdapter#displayItemView(View, int, int)}. the aspect will
     * be honored in measure phase when there is only one item in adapter.
     */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        public int x;
        public int y;
        public int itemWidth;
        public int itemHeight;
        public float aspectHW; // height / width

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        public LayoutParams() {
            super(WRAP_CONTENT, WRAP_CONTENT);
        }
    }


    public interface NineGridAdapter {

        int getCount();

        Object getData(int position);

        View initItemView(Context context);

        LayoutParams getLayoutParams(View view, int position, int total);

        void displayItemView(View view, int position, int total);

    }

}
