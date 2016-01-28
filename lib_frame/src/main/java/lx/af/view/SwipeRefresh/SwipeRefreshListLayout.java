package lx.af.view.SwipeRefresh;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;

import lx.af.R;

/**
 * author: lx
 * date: 15-12-15
 */
public class SwipeRefreshListLayout extends SwipeRefreshLayout implements
        AbsListView.OnScrollListener {

    private ListView mListView;
    private View mListViewFooter;
    private OnLoadMoreListener mOnLoadMoreListener;
    private AbsListView.OnScrollListener mOnScrollListener;

    private int mYDown;
    private int mLastY;
    private int mTouchSlop;
    private boolean mIsLoading = false;

    public SwipeRefreshListLayout(Context context) {
        this(context, null);
    }

    public SwipeRefreshListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mListViewFooter = LayoutInflater.from(context).inflate(
                R.layout.swipe_refresh_footer, mListView, false);
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

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mYDown = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                mLastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                if (canLoad()) {
                    loadMore();
                }
                break;
            default:
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    private boolean canLoad() {
        return isBottom() && !mIsLoading && isPullUp();
    }

    private boolean isBottom() {
        if (mListView != null && mListView.getAdapter() != null) {
            return mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
        }
        return false;
    }

    private boolean isPullUp() {
        return (mYDown - mLastY) >= mTouchSlop;
    }

    private void loadMore() {
        if (mOnLoadMoreListener != null) {
            setLoading(true);
            mOnLoadMoreListener.onLoadMore();
        }
    }

    public void setLoading(boolean loading) {
        mIsLoading = loading;
        if (mIsLoading) {
            mListView.addFooterView(mListViewFooter);
        } else {
            mListView.removeFooterView(mListViewFooter);
            mYDown = 0;
            mLastY = 0;
        }
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
        if (canLoad()) {
            loadMore();
        }
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }


    /**
     * callback when scrolled to the bottom and load more data is required
     */
    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
