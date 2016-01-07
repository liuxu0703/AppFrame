package lx.af.demo.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import lx.af.adapter.AbsListAdapter;
import lx.af.demo.R;
import lx.af.demo.adapter.ImagePagerAdapter;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseDemoActivity;
import lx.af.demo.consts.TestRes;
import lx.af.manager.GlobalThreadManager;
import lx.af.view.SwipeRefresh.SwipeRefreshLayout;
import lx.af.view.SwipeRefresh.SwipeRefreshListLayout;

/**
 * author: lx
 * date: 15-12-15
 */
public class ActivitySwipeRefresh extends BaseDemoActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        SwipeRefreshListLayout.OnLoadMoreListener,
        ActionBar.Default {

    private SwipeRefreshListLayout mSwipeRefreshLayout;
    private ListView mListView;
    private ViewPager mViewPager;

    private ListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_refresh);
        mListView = obtainView(R.id.activity_swipe_refresh_listview);
        mSwipeRefreshLayout = obtainView(R.id.activity_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setOnLoadMoreListener(this);

        View header = View.inflate(this, R.layout.swipe_refresh_header, null);
        mViewPager = (ViewPager) header.findViewById(R.id.swipe_refresh_header_pager);
        mListView.addHeaderView(header);

        mListAdapter = new ListAdapter(this, generateList(0, 15));
        mListView.setAdapter(mListAdapter);

        mViewPager.setAdapter(new ImagePagerAdapter(generateImage()));
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

    @Override
    public void onLoadMore() {
        GlobalThreadManager.runInUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                mListAdapter.addAll(generateList(mListAdapter.getCount() - 1, 10));
                mListAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setLoading(false);
            }
        }, 5000);
    }

    @Override
    public void onRefresh() {
        GlobalThreadManager.runInUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                mListAdapter = new ListAdapter(ActivitySwipeRefresh.this, generateList(0, 10));
                mListView.setAdapter(mListAdapter);

                mViewPager.setAdapter(new ImagePagerAdapter(generateImage()));

                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 2500);
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
        }

        @Override
        public View getView(Context context, int position, View convertView, ViewGroup parent) {
            TextView tv;
            if (convertView == null) {
                tv = new TextView(context);
                tv.setPadding(25, 25, 25, 25);
                tv.setTextSize(20);
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
