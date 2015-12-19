package lx.af.demo.activity;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import lx.af.demo.R;
import lx.af.demo.base.BaseDemoActivity;
import lx.af.manager.GlobalThreadManager;
import lx.af.utils.ActivityUtils.ActivityResultCallback;
import lx.af.utils.ActivityUtils.ContentPicker;
import lx.af.utils.ActivityUtils.MediaPicker;
import lx.af.utils.ScreenUtils;
import lx.af.view.MediaPlayButton;
import lx.af.view.VideoPlayView;

public final class ActivityPlayViewDemo extends BaseDemoActivity implements
        OnClickListener,
        BaseDemoActivity.ActionBarImpl {

    private MediaPlayButton mPlayBtn;
    private Button mBtnLoad;

    private LinearLayout mContentView;
    private LinearLayout mContainer1;
    private LinearLayout mContainer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_view);
        mContentView = obtainView(R.id.apv_layout);
        mPlayBtn = obtainView(R.id.apv_play_btn);
        mPlayBtn.setOnClickListener(this);
        mBtnLoad = obtainView(R.id.apv_btn_load);
        mBtnLoad.setOnClickListener(this);

        mContainer1 = new LinearLayout(this);
        mContainer1.setOrientation(LinearLayout.HORIZONTAL);
        mContentView.addView(mContainer1);
        mContainer2 = new LinearLayout(this);
        mContainer2.setOrientation(LinearLayout.HORIZONTAL);
        mContentView.addView(mContainer2);

        int size = ScreenUtils.getScreenWidth() / 2;

        addToLinearLayout(mContainer1, size);
        addToLinearLayout(mContainer1, size);
        addToLinearLayout(mContainer2, size);
        addToLinearLayout(mContainer2, size);

        load();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.apv_play_btn: {
                if (mPlayBtn.isPlayShowing()) {
                    mPlayBtn.showPause(false);
                } else {
                    mPlayBtn.showPlay(false);
                }
                break;
            }
            case R.id.apv_btn_load: {
                load();
                break;
            }
        }
    }

    private void load() {
        mBtnLoad.setEnabled(false);
        GlobalThreadManager.runInThreadPool(new Runnable() {
            @Override
            public void run() {
                mPlayBtn.spin();
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException ignore) {
                }

                for (int progress = 0; progress <= 360; progress += 10) {
                    mPlayBtn.setProgress(progress);
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException ignore) {
                    }
                }

                mPlayBtn.hideProgress();
                mPlayBtn.showPlay(true);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBtnLoad.setEnabled(true);
                    }
                });
            }
        });
    }

    public VideoContainer addToLinearLayout(LinearLayout layout, int size) {
        int m = ScreenUtils.dip2px(10);
        int s = size - 2 * m;
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(s, s);
        params.setMargins(m, m, m, m);
        VideoContainer vc = new VideoContainer(layout.getContext());
        layout.addView(vc, params);
        return vc;
    }

    private class VideoContainer extends RelativeLayout
            implements OnClickListener {

        private VideoPlayView mPlayView;

        public VideoContainer(Context context) {
            super(context);
            this.setBackgroundColor(Color.GRAY);
            this.setOnClickListener(this);

            mPlayView = new VideoPlayView(context);
            LayoutParams params =
                    new LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            this.addViewInLayout(mPlayView, 0, params);
        }

        @Override
        public void onClick(View v) {
            if (mPlayView.isPlaying()) {
                mPlayView.pause();
            } else if (mPlayView.isPaused()) {
                mPlayView.start();
            } else {
                //MediaPicker.of(ActivityPlayViewDemo.this).pickVideo().start(new ActivityResultCallback<Uri>() {
                ContentPicker.of(ActivityPlayViewDemo.this).pickVideo().start(new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(int resultCode, @NonNull Uri result) {
                        Toast.makeText(getContext(), "play " + result, Toast.LENGTH_LONG).show();
                        mPlayView.setVideoURI(result);
                        mPlayView.start();
                    }
                });
            }
        }
    }
}
