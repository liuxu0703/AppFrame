package lx.af.demo.activity.DemoFrame;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.viewpagerindicator.PageIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import lx.af.demo.R;
import lx.af.demo.adapter.ImagePagerAdapter;
import lx.af.demo.adapter.PostListAdapter;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseActivity;
import lx.af.demo.model.PostModel;
import lx.af.manager.GlobalThreadManager;
import lx.af.test.TestImageHelper;
import lx.af.utils.ViewUtils.ActionBarScrollFadeHelper;
import lx.af.utils.ViewUtils.BufferedOnClickListener;
import lx.af.utils.ViewUtils.ViewPagerAutoFlipper;
import lx.af.view.SwipeRefresh.SwipeRefreshLayout;
import lx.af.view.SwipeRefresh.SwipeRefreshListLayout;
import lx.af.widget.LoadingBkgView;
import lx.af.widget.iconify.widget.IconTextView;

/**
 * author: lx
 * date: 15-12-15
 */
public class PostListActivity extends BaseActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        SwipeRefreshListLayout.OnLoadMoreListener,
        ActionBar.Default.Callback.Overlay {

    @InjectView(R.id.activity_swipe_refresh_listview)
    ListView mListView;
    @InjectView(R.id.activity_swipe_refresh_layout)
    SwipeRefreshListLayout mSwipeRefreshLayout;
    @InjectView(R.id.activity_swipe_refresh_loading_view)
    LoadingBkgView mLoadingView;

    private ViewPager mHeaderViewPager;
    private PageIndicator mPageIndicator;
    private TextView mActionBarTitle;
    private View mActionBarBack;
    private View mActionBarMenu;

    private ViewPagerAutoFlipper mPageFlipper;
    private PostListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);
        ButterKnife.inject(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setOnLoadMoreListener(this);
        mSwipeRefreshLayout.setLoadMorePreCount(1);

        View header = View.inflate(this, R.layout.post_list_header, null);
        mPageIndicator = (PageIndicator)
                header.findViewById(R.id.swipe_refresh_header_pager_indicator);
        mHeaderViewPager = (ViewPager) header.findViewById(R.id.swipe_refresh_header_pager);
        mPageFlipper = ViewPagerAutoFlipper.newInstance(mHeaderViewPager).setInterval(2500);
        mLoadingView.setRetryClickCallback(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });

        mAdapter = new PostListAdapter(this);
        mListView.addHeaderView(header);
        mListView.setAdapter(mAdapter);

        ActionBarScrollFadeHelper
                .with(getActionBarView())
                .startOffset(mHeaderViewPager)
                .endOffset(header)
                .addFadeWithView(mActionBarTitle)
                .addFadeWithView(mActionBarMenu)
                .addFadeReverseDrawable(mActionBarBack.getBackground())
                .start(mSwipeRefreshLayout);

        initData();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onActionBarCreated(View actionBar, IconTextView left, TextView title, IconTextView right) {
        mActionBarTitle = title;
        mActionBarBack = left;
        mActionBarBack.setBackgroundResource(R.drawable.swipe_activity_back_bkg);
        right.setText("{md-add}");
        right.setClickable(true);
        mActionBarMenu = right;
        mActionBarTitle.setOnClickListener(new BufferedOnClickListener() {
            @Override
            public void onBufferedClick(View v, int clickCount) {
                if (clickCount >= 2) {
                    // double click title, scroll to top and refresh list
                    mListView.smoothScrollToPositionFromTop(0, 0);
                    loadData(true);
                }
            }
        });
    }

    @Override
    public void onLoadMore() {
        loadData(false);
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }

    private void initData() {
        mLoadingView.loading();
        GlobalThreadManager.runInThreadPool(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignore) {
                }
                final List<PostModel> list = PostModel.createRandomList(8, true);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (list == null) {
                            mLoadingView.fail("simulate load data fail, click to reload");
                        } else if (list.size() == 0) {
                            mLoadingView.empty("simulate data list empty, click to reload");
                        } else {
                            mLoadingView.done(300);
                            mAdapter.addAll(list);
                            mAdapter.notifyDataSetChanged();
                            resetHeaderViewPager(TestImageHelper.randomImageListL(2, 5));
                        }
                    }
                });
            }
        });
    }

    private void loadData(final boolean refresh) {
        GlobalThreadManager.runInThreadPool(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(refresh ? 1600 : 800);
                } catch (InterruptedException ignore) {
                }
                final List<PostModel> postList = PostModel.createRandomList(10, true);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (refresh) {
                            resetHeaderViewPager(TestImageHelper.randomImageListL(2, 5));
                        }

                        if (postList == null) {
                            toastShort("simulate load data fail, refresh=" + refresh);
                            mSwipeRefreshLayout.setLoadMoreFailed();
                            return;
                        }

                        if (refresh) {
                            mAdapter.reset(postList);
                        } else {
                            mAdapter.addAll(postList);
                        }
                        mAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setLoadMoreEnabled(postList.size() > 0);
                        mSwipeRefreshLayout.setLoading(false);
                    }
                });
            }
        });
    }

    private void resetHeaderViewPager(ArrayList<String> imageList) {
        mHeaderViewPager.setAdapter(new ImagePagerAdapter(imageList));
        mPageIndicator.setViewPager(mHeaderViewPager);
        mPageFlipper.start();
    }

}
