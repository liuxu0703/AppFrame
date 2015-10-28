package lx.af.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import lx.af.base.BaseActivity;
import lx.af.demo.R;

public class ActivityMain extends BaseActivity implements
        BaseActivity.ActionBarCallbacks {

    public final static ArrayList<DemoButtonData> BUTTON_DATA_LIST =
            new ArrayList<>();

    static {
        BUTTON_DATA_LIST.add(new DemoButtonData("StorageManagerUtils",
                ActivityStorageUtilsDemo.class));
        BUTTON_DATA_LIST.add(new DemoButtonData("Home Key Watcher",
                ActivityHomeWatcherDemo.class));
        BUTTON_DATA_LIST.add(new DemoButtonData("File Picker",
                ActivityFilePickerDemo.class));
        BUTTON_DATA_LIST.add(new DemoButtonData("Auto Slide Pager",
                ActivityAutoSlidePager.class));
        BUTTON_DATA_LIST.add(new DemoButtonData("Video Play",
                ActivityPlayViewDemo.class));
        BUTTON_DATA_LIST.add(new DemoButtonData("Image Pick and Crop",
                ActivityAvatarDemo.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout buttonContainer = (LinearLayout) this
                .findViewById(R.id.main_container);
        for (DemoButtonData data : BUTTON_DATA_LIST) {
            DemoButton button = new DemoButton(this, data);
            DemoButton.addToLinearLayout(buttonContainer, button);
        }
    }

    @Override
    public View onCreateActionBarMenu() {
        return null;
    }

    @Override
    public void onActionBarCreated(View actionBar, ImageView back, TextView title, @Nullable View menu) {
        back.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onActionBarBackClicked(View back) {
        return false;
    }

    private static class DemoButton extends Button implements View.OnClickListener {

        private final static int MARGIN = 10;

        private final Context mContext;
        private final DemoButtonData mData;

        public DemoButton(Context context, DemoButtonData data) {
            super(context);
            this.mContext = context;
            this.mData = data;
            this.initView();
        }

        private void initView() {
            this.setTag(this.mData.mTitle); // so that we could find it later
            this.setText(this.mData.mTitle);
            this.setBackgroundResource(R.drawable.btn_action);
            this.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(this.mContext, this.mData.mActivity);
            this.mContext.startActivity(intent);
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
            this.mTitle = title;
            this.mActivity = activity;
        }
    }

}
