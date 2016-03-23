package lx.af.view.SwipeRefresh;

import android.widget.ListView;

/**
 * author: lx
 * date: 16-3-2
 */
public interface ILoadMoreFooter {

    void init(SwipeRefreshListLayout refreshLayout, ListView parent);

    void refreshLoadState(ListView parent, SwipeRefreshListLayout.LoadState state);

}
