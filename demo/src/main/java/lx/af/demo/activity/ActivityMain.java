package lx.af.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import lx.af.demo.R;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseDemoActivity;

public class ActivityMain extends BaseDemoActivity implements
        ActionBar.Default.OnCreateCallback {

    public final static ArrayList<DemoButtonData> BUTTON_DATA_LIST =
            new ArrayList<>();

    static {
        BUTTON_DATA_LIST.add(new DemoButtonData("Test",
                ActivityTest.class));
        BUTTON_DATA_LIST.add(new DemoButtonData("Image Pick and Crop",
                ActivityImageOperateDemo.class));
        BUTTON_DATA_LIST.add(new DemoButtonData("ViewPager Auto Flip",
                ActivityViewPager.class));
        BUTTON_DATA_LIST.add(new DemoButtonData("Auto Slide Pager",
                ActivityAutoSlidePager.class));
        BUTTON_DATA_LIST.add(new DemoButtonData("Swipe Refresh List",
                ActivitySwipeRefresh.class));
        BUTTON_DATA_LIST.add(new DemoButtonData("Bar Code Scanner",
                ActivityScanner.class));
        BUTTON_DATA_LIST.add(new DemoButtonData("Home Key Watcher",
                ActivityHomeWatcherDemo.class));
        BUTTON_DATA_LIST.add(new DemoButtonData("Video Play",
                ActivityPlayViewDemo.class));
        BUTTON_DATA_LIST.add(new DemoButtonData("Danmaku",
                ActivityDanmaku.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableFeature(FEATURE_DOUBLE_BACK_EXIT);
        setContentView(R.layout.activity_main);
        LinearLayout buttonContainer = (LinearLayout) this
                .findViewById(R.id.main_container);
        for (DemoButtonData data : BUTTON_DATA_LIST) {
            DemoButton button = new DemoButton(this, data);
            DemoButton.addToLinearLayout(buttonContainer, button);
        }
    }

    @Override
    public void onActionBarCreated(View actionBar, ImageView back, TextView title, @Nullable View menu) {
        back.setVisibility(View.INVISIBLE);
    }

    private static class DemoButton extends Button implements View.OnClickListener {

        private final static int MARGIN = 10;

        private final Context mContext;
        private final DemoButtonData mData;

        public DemoButton(Context context, DemoButtonData data) {
            super(context);
            mContext = context;
            mData = data;
            initView();
        }

        private void initView() {
            setTag(mData.mTitle); // so that we could find it later
            setText(mData.mTitle);
            setTextColor(Color.WHITE);
            setGravity(Gravity.CENTER);
            setBackgroundResource(R.drawable.btn_action);
            setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, mData.mActivity);
            mContext.startActivity(intent);
        }

        public static void addToLinearLayout(LinearLayout parent, DemoButton button) {
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
            parent.addView(button, llp);
        }
    }

    @SuppressWarnings("rawtypes")
    private static class DemoButtonData {

        String mTitle;
        Class mActivity;

        DemoButtonData(String title, Class activity) {
            mTitle = title;
            mActivity = activity;
        }
    }

}
