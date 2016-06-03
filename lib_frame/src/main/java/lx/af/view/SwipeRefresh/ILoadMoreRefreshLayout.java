package lx.af.view.SwipeRefresh;

/**
 * author: lx
 * date: 16-5-18
 */
public interface ILoadMoreRefreshLayout {

    /**
     * callback when scrolled to the bottom and load more data is required
     */
    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public enum LoadState {
        IDLE, LOADING, NO_MORE,
    }

}
