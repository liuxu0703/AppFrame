package lx.af.demo.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import lx.af.demo.R;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseDemoActivity;
import lx.af.widget.AutoSlidePager.AutoSlidePager;

/**
 * author: lx
 * date: 15-10-26
 */
public class ActivityAutoSlidePager extends BaseDemoActivity implements
        ActionBar.Default,
        View.OnClickListener {


    private AutoSlidePager mSlidePager1;
    private AutoSlidePager mSlidePager2;
    private TextView mTvPageInfo1;
    private TextView mTvPageInfo2;
    private TextView mPage_aaa;
    private TextView mPage_ddd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_slide_pager);
        findViewById(R.id.aasp_btn_add_page).setOnClickListener(this);
        findViewById(R.id.aasp_btn_hide_page_aaa).setOnClickListener(this);
        findViewById(R.id.aasp_btn_hide_page_ddd).setOnClickListener(this);

        mPage_aaa = obtainView(R.id.aasp_page_aaa);
        mPage_ddd = obtainView(R.id.aasp_page_ddd);
        mTvPageInfo1 = obtainView(R.id.aasp_tv_page_info1);
        mTvPageInfo2 = obtainView(R.id.aasp_tv_page_info2);

        mSlidePager1 = obtainView(R.id.aasp_slide_pager1);
        mSlidePager1.setOnPageSelectListener(new AutoSlidePager.OnPageSelectListener() {
            @Override
            public void onPageSelected(int position, View view) {
                TextView tv = (TextView) view;
                mTvPageInfo1.setText("selected page info: " + tv.getText());
            }
        });

        mSlidePager2 = obtainView(R.id.aasp_slide_pager2);
        mSlidePager2.setOnPageSelectListener(new AutoSlidePager.OnPageSelectListener() {
            @Override
            public void onPageSelected(int position, View view) {
                TextView tv = (TextView) view;
                mTvPageInfo2.setText("selected page info: " + tv.getText());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aasp_btn_add_page: {
                TextView tv = new TextView(this);
                tv.setText("new_slide");
                tv.setTextColor(Color.parseColor("#ffffff"));
                tv.setBackgroundColor(Color.parseColor("#40E0D0"));
                tv.setPadding(10, 10, 10, 10);
                mSlidePager2.addView(tv,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                break;
            }
            case R.id.aasp_btn_hide_page_aaa: {
                if (mPage_aaa.getVisibility() == View.GONE) {
                    mPage_aaa.setVisibility(View.VISIBLE);
                    ((TextView) v).setText("hide page aaa");
                } else {
                    mPage_aaa.setVisibility(View.GONE);
                    ((TextView) v).setText("show page aaa");
                }
                break;
            }
            case R.id.aasp_btn_hide_page_ddd: {
                if (mPage_ddd.getVisibility() == View.GONE) {
                    mPage_ddd.setVisibility(View.VISIBLE);
                    ((TextView) v).setText("hide page ddd");
                } else {
                    mPage_ddd.setVisibility(View.GONE);
                    ((TextView) v).setText("show page ddd");
                }
                break;
            }
        }
    }

}
