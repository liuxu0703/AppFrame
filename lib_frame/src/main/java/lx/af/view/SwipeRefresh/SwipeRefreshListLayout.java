package lx.af.view.SwipeRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * author: lx
 * date: 15-12-15
 */
public class SwipeRefreshListLayout extends SwipeRefreshLayout implements
        AbsListView.OnScrollListener {

    /**
     * callback when scrolled to the bottom and load more data is required
     */
    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public enum LoadState {
        IDLE, LOADING, NO_MORE,
    }

    // ============================================

    private ListView mListView;
    private ILoadMoreFooter mLoadMoreFooter;
    private OnLoadMoreListener mOnLoadMoreListener;
    private AbsListView.OnScrollListener mOnScrollListener;

    private LoadState mState = LoadState.IDLE;
    private int mLoadMorePreCount = 1;

    public SwipeRefreshListLayout(Context context) {
        this(context, null);
    }

    public SwipeRefreshListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mListView == null) {
            getListView();
        }
    }

    private void getListView() {
        int count = getChildCount();
        if (count > 0) {
            View childView = getChildAt(0);
            if (childView instanceof ListView) {
                mListView = (ListView) childView;
                mListView.setOnScrollListener(this);
            }
        }
    }

    private void loadMore() {
        if (mOnLoadMoreListener != null) {
            setLoading(true);
            mOnLoadMoreListener.onLoadMore();
        }
    }

    // =============================================

    public void setLoadState(LoadState state) {
        mState = state;
        if (mOnLoadMoreListener == null) {
            return;
        }
        if (mLoadMoreFooter == null) {
            mLoadMoreFooter = new DefaultLoadMoreFooter(getContext());
            mLoadMoreFooter.init(mListView);
        }
        mLoadMoreFooter.refreshLoadState(mListView, state);
    }

    public void setLoading(boolean loading) {
        setLoadState(loading ? LoadState.LOADING : LoadState.IDLE);
    }

    public void setLoadMoreEnabled(boolean enabled) {
        setLoadState(enabled ? LoadState.IDLE : LoadState.NO_MORE);
    }

    public void setLoadMorePreCount(int count) {
        mLoadMorePreCount = count;
    }

    public void setLoadMoreFooterView(ILoadMoreFooter footer) {
        if (!(footer instanceof View)) {
            throw new IllegalArgumentException("footer must be instance of view");
        }
        mLoadMoreFooter = footer;
        mLoadMoreFooter.init(mListView);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener loadListener) {
        mOnLoadMoreListener = loadListener;
    }

    public void setOnScrollListener(AbsListView.OnScrollListener listener) {
        mOnScrollListener = listener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mOnLoadMoreListener != null && mState == LoadState.IDLE &&
                firstVisibleItem + visibleItemCount + mLoadMorePreCount >= totalItemCount &&
                totalItemCount != 0 &&
                totalItemCount != mListView.getHeaderViewsCount() + mListView.getFooterViewsCount()) {
            loadMore();
        }

        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

}
