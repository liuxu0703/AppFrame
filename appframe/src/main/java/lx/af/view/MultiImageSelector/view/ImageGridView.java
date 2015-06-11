package lx.af.view.MultiImageSelector.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.GridView;

/**
 * Created by liuxu on 15-4-24.
 * img grid view for multi image selector.
 */
public class ImageGridView extends GridView implements AbsListView.OnScrollListener {

    private OnScrollListener mScrollListener;
    private boolean mIsScrolling;

    public ImageGridView(Context context) {
        super(context);
        initView();
    }

    public ImageGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ImageGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        super.setOnScrollListener(this);
    }

    public boolean isScrolling() {
        return mIsScrolling;
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mIsScrolling = (scrollState != SCROLL_STATE_IDLE);
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }
}
