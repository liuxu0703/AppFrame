package lx.af.demo.activity.DemoFrame;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.viewpagerindicator.PageIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import lx.af.adapter.AbsListAdapter;
import lx.af.demo.R;
import lx.af.demo.adapter.ImagePagerAdapter;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseActivity;
import lx.af.demo.consts.TestRes;
import lx.af.manager.GlobalThreadManager;
import lx.af.utils.ViewInject.ViewInject;
import lx.af.utils.ViewUtils.ActionBarScrollFadeHelper;
import lx.af.utils.ViewUtils.ViewPagerAutoFlipper;
import lx.af.view.SwipeRefresh.SwipeRefreshLayout;
import lx.af.view.SwipeRefresh.SwipeRefreshListLayout;
import lx.af.widget.LoadingBkgView;
import lx.af.widget.iconify.widget.IconTextView;

/**
 * author: lx
 * date: 15-12-15
 */
public class SwipeRefreshDemo extends BaseActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        SwipeRefreshListLayout.OnLoadMoreListener,
        ActionBar.Default.Callback.Overlay {

    @ViewInject(id = R.id.activity_swipe_refresh_layout)
    private SwipeRefreshListLayout mSwipeRefreshLayout;
    @ViewInject(id = R.id.activity_swipe_refresh_listview)
    private ListView mListView;
    @ViewInject(id = R.id.activity_swipe_refresh_loading_view)
    private LoadingBkgView mLoadingView;
    @ViewInject(id = R.id.activity_swipe_refresh_btn_add)
    private View mBtnAdd;

    private ViewPager mHeaderViewPager;
    private PageIndicator mHeaderPagerIndicator;
    private TextView mActionBarTitle;
    private View mActionBarBack;
    private View mActionBarMenu;

    private ListAdapter mListAdapter;

    private int mLoadCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setOnLoadMoreListener(this);
        mSwipeRefreshLayout.setLoadMorePreCount(3);

        View header = View.inflate(this, R.layout.swipe_refresh_header, null);
        mHeaderViewPager = (ViewPager) header.findViewById(R.id.swipe_refresh_header_pager);
        mHeaderPagerIndicator = (PageIndicator)
                header.findViewById(R.id.swipe_refresh_header_pager_indicator);
        mListView.addHeaderView(header);

        mHeaderViewPager.setAdapter(new ImagePagerAdapter(generateImage()));
        mHeaderPagerIndicator.setViewPager(mHeaderViewPager);
        ViewPagerAutoFlipper.newInstance(mHeaderViewPager).setInterval(4000).start();

        ActionBarScrollFadeHelper
                .with(getActionBarView())
                .startOffset(mHeaderViewPager)
                .endOffset(header)
                .addFadeWithView(mActionBarTitle)
                .addFadeWithView(mActionBarMenu)
                .addFadeReverseView(mBtnAdd)
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
    }

    @Override
    public void onLoadMore() {
        GlobalThreadManager.runInUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                mListAdapter.addAll(generateList(mListAdapter.getCount() - 1, 10));
                mListAdapter.notifyDataSetChanged();
                mLoadCount ++;
                if (mLoadCount > 3) {
                    mSwipeRefreshLayout.setLoadState(SwipeRefreshListLayout.LoadState.NO_MORE);
                } else {
                    //mSwipeRefreshLayout.setLoading(false);
                    mSwipeRefreshLayout.setLoadState(SwipeRefreshListLayout.LoadState.IDLE);
                }
            }
        }, 2000);
    }

    @Override
    public void onRefresh() {
        mLoadCount = 0;
        mSwipeRefreshLayout.setLoadState(SwipeRefreshListLayout.LoadState.IDLE);

        GlobalThreadManager.runInUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                mListAdapter = new ListAdapter(SwipeRefreshDemo.this, generateList(0, 10));
                mListView.setAdapter(mListAdapter);
                mHeaderViewPager.setAdapter(new ImagePagerAdapter(generateImage()));
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }

    private void initData() {
        mLoadingView.loading();
        GlobalThreadManager.runInUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                mLoadingView.done();
                mListAdapter = new ListAdapter(SwipeRefreshDemo.this, generateList(0, 15));
                mListView.setAdapter(mListAdapter);
            }
        }, 2000);
    }

    private List<String> generateList(int start, int count) {
        Random random = new Random();
        int r = random.nextInt(100);
        ArrayList<String> list = new ArrayList<>(count);
        for (int i = start + 1; i < start + count; i ++) {
            list.add("list item index (" + r + ") " + i);
        }
        return list;
    }

    private List<String> generateImage() {
        Random random = new Random();
        int r = random.nextInt(1);
        if (r == 1) {
            return Arrays.asList(TestRes.TEST_IMG_SCENE);
        } else {
            return Arrays.asList(TestRes.TEST_IMG_FRUIT);
        }
    }

    private class ListAdapter extends AbsListAdapter<String> {

        public ListAdapter(Context context, List<String> list) {
            super(context, list);
            setNotifyOnChange(true);
        }

        @Override
        public View getView(Context context, int position, View convertView, ViewGroup parent) {
            TextView tv;
            if (convertView == null) {
                tv = new TextView(context);
                tv.setPadding(30, 30, 30, 30);
                tv.setTextSize(18);
                tv.setTextColor(Color.BLACK);
                tv.setGravity(Gravity.CENTER);
                convertView = tv;
            } else {
                tv = (TextView) convertView;
            }
            tv.setText(getItem(position));
            return convertView;
        }
    }

}
