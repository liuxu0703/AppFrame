package lx.af.demo.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lx.af.base.BaseActivity;
import lx.af.demo.R;
import lx.af.demo.base.BaseDemoActivity;
import lx.af.manager.GlobalThreadManager;
import lx.af.utils.ScreenUtils;
import lx.af.utils.log.Log;
import lx.af.view.MediaButton.MediaPlayButton;
import lx.af.view.VideoPlayView;
import lx.af.view.FilePicker.FilePickerDialog;
import lx.af.view.FilePicker.FilePickerList;

public final class ActivityPlayViewDemo extends BaseDemoActivity implements
        OnClickListener,
        BaseActivity.ActionBarImpl {

    private int mScreenWidth;
    private int mVideoSize;

    private MediaPlayButton mPlayBtn;
    private Button mBtnLoad;

    private LinearLayout mContentView;
    private LinearLayout mContainer1;
    private LinearLayout mContainer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_view);
        mContentView = getView(R.id.apv_layout);
        mPlayBtn = getView(R.id.apv_play_btn);
        mPlayBtn.setOnClickListener(this);
        mBtnLoad = getView(R.id.apv_btn_load);
        mBtnLoad.setOnClickListener(this);

        mContainer1 = new LinearLayout(this);
        mContainer1.setOrientation(LinearLayout.HORIZONTAL);
        mContentView.addView(mContainer1);
        mContainer2 = new LinearLayout(this);
        mContainer2.setOrientation(LinearLayout.HORIZONTAL);
        mContentView.addView(mContainer2);

        mScreenWidth = ScreenUtils.getScreenWidth();
        mVideoSize = mScreenWidth / 2;

        VideoContainer.addToLinearLayout(mContainer1, mVideoSize);
        VideoContainer.addToLinearLayout(mContainer1, mVideoSize);
        VideoContainer.addToLinearLayout(mContainer2, mVideoSize);
        VideoContainer.addToLinearLayout(mContainer2, mVideoSize);

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

    private static class VideoContainer extends RelativeLayout
            implements OnClickListener {

        public static final int MARGGIN = 10; // in dp

        public static final String[] VIDEO_SUFFIX = new String[] {
                "rm", "rmvb", "avi", "mp4", "mov", "wma", "flv", "mkv",
        };

        private VideoPlayView mPlayView;
        private Pattern mPattern;

        public VideoContainer(Context context) {
            super(context);
            this.setBackgroundColor(Color.GRAY);
            this.setOnClickListener(this);

            initPattern();

            mPlayView = new VideoPlayView(context);
            LayoutParams params =
                    new LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            this.addViewInLayout(mPlayView, 0, params);
        }

        // use regex to match video files
        private void initPattern() {
            StringBuilder sb = new StringBuilder();
            sb.append("\\w+\\.(");
            for (int i = 0; i < VIDEO_SUFFIX.length; i++) {
                String s = VIDEO_SUFFIX[i];
                if (i != 0) {
                    sb.append("|");
                }
                sb.append(s).append("$");
            }
            sb.append(")");
            mPattern = Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE);
        }

        private void playVideo(String path) {
            Toast.makeText(getContext(), "play " + path,
                    Toast.LENGTH_LONG).show();
            mPlayView.setVideoPath(path);
            mPlayView.start();
        }

        // pick all video files
        private void showDlg() {
            new FilePickerDialog.Builder(getContext())
                    .setFilePickerFilter(new FilePickerList.FilePickerFilter() {

                        @Override
                        public boolean canBeSelected(File file) {
                            if (file.isDirectory()) {
                                return false;
                            }
                            Matcher matcher = mPattern.matcher(
                                    file.getAbsolutePath());
                            return matcher.find();
                        }

                        @Override
                        public boolean canBeDisplayed(File file) {
                            if (file.getName().charAt(0) == '.') {
                                return false;
                            } else if (file.isDirectory()) {
                                // directory should always be displayed
                                return true;
                            } else {
                                Matcher matcher = mPattern.matcher(
                                        file.getAbsolutePath());
                                return matcher.find();
                            }
                        }

                    })
                    .setFileSelectCallback(new FilePickerDialog.FileSelectCallback() {
                        @Override
                        public void onFileSelected(String file) {
                            if (file == null) {
                                Toast.makeText(getContext(),
                                        "path null", Toast.LENGTH_SHORT).show();
                            } else {
                                playVideo(file);
                            }
                        }
                    }).create().show();
        }

        @Override
        public void onClick(View v) {
            if (mPlayView.isPlaying()) {
                mPlayView.pause();
            } else if (mPlayView.isPaused()) {
                mPlayView.start();
            } else {
                showDlg();
            }
        }

        public static VideoContainer addToLinearLayout(LinearLayout layout, int size) {
            int m = ScreenUtils.dip2px(MARGGIN);
            int s = size - 2 * m;
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(s, s);
            params.setMargins(m, m, m, m);
            VideoContainer vc = new VideoContainer(layout.getContext());
            layout.addView(vc, params);
            return vc;
        }

    }
}
