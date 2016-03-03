package lx.af.demo.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import lx.af.demo.R;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseDemoActivity;
import lx.af.manager.GlobalThreadManager;
import lx.af.utils.ScreenUtils;
import lx.af.utils.ViewInject.ViewInject;
import lx.af.utils.log.Log;
import lx.af.widget.Danmaku.Danmaku;
import lx.af.widget.Danmaku.ViewAdapter;

/**
 * author: lx
 * date: 16-3-1
 */
public class ActivityDanmaku extends BaseDemoActivity implements
        View.OnClickListener,
        ActionBar.Default {

    @ViewInject(id = R.id.danmaku_btn_toggle, click = "onClick")
    private Button mBtnToggle;
    @ViewInject(id = R.id.danmaku_btn_add_data, click = "onClick")
    private Button mBtnAddData;
    @ViewInject(id = R.id.danmaku_bkg_img)
    private ImageView mHeadBkg;
    @ViewInject(id = R.id.danmaku_layout)
    private Danmaku mDanmakuLayout;

    @ViewInject(id = R.id.danmaku_test_view)
    private View mTestView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danmaku);
        mDanmakuLayout.setViewAdapter(new DanmakuAdapter());
        refreshToggleButton();

        GlobalThreadManager.runInUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                TranslateAnimation anim = new TranslateAnimation(
                        TranslateAnimation.ABSOLUTE, 0,
                        TranslateAnimation.ABSOLUTE, 300,
                        TranslateAnimation.ABSOLUTE, 0,
                        TranslateAnimation.ABSOLUTE, 0);
                anim.setDuration(9000);
                anim.setFillAfter(true);
                mTestView.startAnimation(anim);
            }
        }, 1500);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.danmaku_btn_toggle: {
                mDanmakuLayout.toggleDanmaku();
                refreshToggleButton();
                break;
            }
            case R.id.danmaku_btn_add_data: {
                mDanmakuLayout.addData(DATA);
                break;
            }
        }
    }

    private void refreshToggleButton() {
        if (mDanmakuLayout.isDanmakuRunning()) {
            mBtnToggle.setText("stop");
        } else {
            mBtnToggle.setText("start");
        }
    }

    private class DanmakuAdapter implements ViewAdapter {

        @Override
        public View getView(Object data, View view, ViewGroup parent) {
            String str = (String) data;
            TextView tv;
            if (view != null) {
                tv = (TextView) view;
            } else {
                tv = new TextView(ActivityDanmaku.this);
                tv.setBackgroundColor(getResources().getColor(R.color.black));
                tv.setTextColor(getResources().getColor(R.color.white));
            }
            tv.setText(str);
            return tv;
        }
    }



    private static final ArrayList<Object> DATA = new ArrayList<>();
    static {
        DATA.add("1111111111111111111111");
        DATA.add("22222222222");
        DATA.add("3333333333333333");
        DATA.add("4444444");
        DATA.add("5555555555");
        DATA.add("6666");
        DATA.add("77");
        DATA.add("8888888888888888888");
        DATA.add("999999999");
        DATA.add("00000000000000");
    }

}
