package lx.af.demo.activity.main;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import lx.af.demo.R;
import lx.af.demo.activity.ActivityTest;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseActivity;
import lx.af.utils.ViewUtils.BufferedOnClickListener;
import lx.af.widget.iconify.widget.IconTextView;

public class MainActivity extends BaseActivity implements
        TabHost.OnTabChangeListener,
        View.OnClickListener,
        ActionBar.Default.Callback {

    private FragmentTabHost mTabHost;
    private TextView mTitle;
    private TextView mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableFeature(FEATURE_DOUBLE_BACK_EXIT);
        setContentView(R.layout.activity_main);
        mTabHost = obtainView(android.R.id.tabhost);
        initTabs();
    }

    @Override
    public void onActionBarCreated(View actionBar, IconTextView left, TextView title, IconTextView right) {
        left.setVisibility(View.GONE);
        actionBar.setOnClickListener(new BufferedOnClickListener() {
            @Override
            public void onBufferedClick(View v, int clickCount) {
                if (clickCount >= 2) {
                    startActivity(ActivityTest.class);
                }
            }
        });
        mTitle = title;
        mMenu = right;
        mMenu.setText("{md-more}");
        mMenu.setOnClickListener(this);
    }

    @Override
    public void onTabChanged(String s) {
        final int size = mTabHost.getTabWidget().getTabCount();
        for (int i = 0; i < size; i++) {
            View v = mTabHost.getTabWidget().getChildAt(i);
            if (i == mTabHost.getCurrentTab()) {
                v.setSelected(true);
                MainTab tab = MainTab.getTabByIndex(i);
                mTitle.setText(tab.getTitleRes()); // impossible to be null
                mMenu.setVisibility(tab == MainTab.TAB1 ? View.VISIBLE : View.INVISIBLE);
            } else {
                v.setSelected(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_bar_btn_right: {
                // menu clicked
                toastShort("menu clicked");
                break;
            }
        }
    }

    private void initTabs() {
        mTabHost.setup(this, getSupportFragmentManager(), R.id.main_tab_content_layout);
        mTabHost.setOnTabChangedListener(this);
        mTabHost.setCurrentTab(0);

        for (MainTab mainTab : MainTab.values()) {
            TabHost.TabSpec tab = mTabHost.newTabSpec(getString(mainTab.getTitleRes()));
            View indicator = View.inflate(this, R.layout.main_tab_indicator, null);
            TextView title = (TextView) indicator.findViewById(R.id.tab_title);
            TextView icon = (TextView) indicator.findViewById(R.id.tab_icon);
            title.setText(getString(mainTab.getTitleRes()));
            icon.setText(mainTab.getIconRes());
            tab.setIndicator(indicator);
            tab.setContent(new TabHost.TabContentFactory() {
                @Override
                public View createTabContent(String tag) {
                    return new View(MainActivity.this);
                }
            });
            mTabHost.addTab(tab, mainTab.getClz(), null);
        }
    }
}
