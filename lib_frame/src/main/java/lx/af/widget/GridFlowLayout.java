package lx.af.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.media.ExifInterface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.LinkedList;

import lx.af.R;
import lx.af.utils.ScreenUtils;

/**
 * author: lx
 * date: 16-5-28
 */
public class GridFlowLayout extends ViewGroup {

    private LinkedList<View> mViewRecycler = new LinkedList<>();
    private BaseAdapter mAdapter;
    private int mItemSize;

    private int mSpacing = ScreenUtils.dip2px(2);
    private int mColumnCount = 4;
    private int mMaxItemCount = 8;
    private float mItemRatio = 1f;

    public GridFlowLayout(Context context) {
        super(context);
        initView(context, null);
    }

    public GridFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GridFlowLayout);
            mColumnCount = a.getInteger(R.styleable.GridFlowLayout_gridColumnCount, 4);
            mMaxItemCount = a.getInteger(R.styleable.GridFlowLayout_gridMaxCount, 8);
            mSpacing = a.getDimensionPixelOffset(R.styleable.GridFlowLayout_gridSpacing, 0);
            mItemRatio = a.getFloat(R.styleable.GridFlowLayout_gridRatio, 1f);
            a.recycle();
        }
    }

    public void setMaxCount(int maxCount) {
        mMaxItemCount = maxCount;
    }

    public void setColumnCount(int columnCount) {
        mColumnCount = columnCount;
    }

    public void setAdapter(BaseAdapter adapter) {
        // adapter should not be null
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataObserver);
        }
        mAdapter = adapter;
        mAdapter.registerDataSetObserver(mDataObserver);
        recycleAllViews();
        layoutAllViews();
        requestLayout();
    }

    public BaseAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAdapter != null) {
            try {
                mAdapter.unregisterDataSetObserver(mDataObserver);
            } catch (Exception ignore) {
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int itemWidth = mItemSize;
        int itemHeight = (int) (mItemSize * mItemRatio);
        int count = getChildCount();
        int index = 0;
        int x = 0, y = 0;
        for (int i = 0; i < count; i ++) {
            View view = getChildAt(i);
            if (view.getVisibility() == View.GONE) {
                continue;
            }
            view.measure(
                    MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY));
            view.layout(x, y, x + itemWidth, y + itemHeight);

            if ((index + 1) % mColumnCount == 0) {
                // new row
                x = 0;
                y = y + itemHeight + mSpacing;
            } else {
                // same row
                x = x + itemWidth + mSpacing;
            }
            index ++;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        if (count == 0) {
            setMeasuredDimension(
                    resolveSize(0, widthMeasureSpec),
                    resolveSize(0, heightMeasureSpec));
            return;
        }

        int visibleCount = count;
        for (int i = 0; i < count; i ++) {
            View view = getChildAt(i);
            if (view.getVisibility() == View.GONE) {
                visibleCount --;
            }
        }

        int rowCount = (visibleCount - 1) / mColumnCount + 1;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        mItemSize = (width - (mColumnCount - 1) * mSpacing) / mColumnCount;
        int itemHeight = (int) (mItemSize * mItemRatio);
        int height = rowCount * itemHeight + (rowCount - 1) * mSpacing;

        setMeasuredDimension(
                resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
    }

    private void recycleAllViews() {
        int count = getChildCount();
        for (int i = 0; i < count; i ++) {
            mViewRecycler.add(getChildAt(i));
        }
        removeAllViewsInLayout();
    }

    private void layoutAllViews() {
        if (mAdapter != null && mAdapter.getCount() != 0) {
            for (int i = 0; i < mAdapter.getCount(); i ++) {
                if (i + 1 > mMaxItemCount) {
                    break;
                }
                View view = mAdapter.getView(i, getConvertView(), GridFlowLayout.this);
                addViewInLayout(view, -1, new LayoutParams(mItemSize, mItemSize));
            }
        }
    }

    private View getConvertView() {
        if (mViewRecycler.size() > 0) {
            return mViewRecycler.pop();
        } else {
            return null;
        }
    }

    private DataSetObserver mDataObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            recycleAllViews();
            layoutAllViews();
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            recycleAllViews();
            requestLayout();
        }
    };

}
