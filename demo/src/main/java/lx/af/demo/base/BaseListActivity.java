package lx.af.demo.base;

import android.os.Bundle;
import android.widget.ListView;

import lx.af.demo.R;
import lx.af.view.SwipeRefresh.SwipeRefreshListLayout;

/**
 * author: lx
 * date: 15-12-14
 */
public abstract class BaseListActivity extends BaseActivity {

    private ListView mListView;
    private SwipeRefreshListLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_refresh);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
