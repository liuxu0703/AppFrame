package lx.af.view.SwipeRefresh;

import android.view.View;
import android.widget.AbsListView;

/**
 * author: lx
 * date: 16-3-2
 */
public interface ILoadMoreFooter {

    View getLoadMoreFooterView();

    void refreshLoadState(AbsListView parent, ILoadMoreRefreshLayout.LoadState state);

}
