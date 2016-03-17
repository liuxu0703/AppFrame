package lx.af.demo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

import lx.af.demo.R;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseDemoActivity;
import lx.af.utils.ViewInject.ViewInject;
import lx.af.utils.ViewUtils.ActionBarScrollFadeHelper;
import lx.af.view.ObservableScrollView;
import lx.af.widget.DotCircleProgress;
import lx.af.widget.iconify.widget.IconTextView;

/**
 * author: lx
 * date: 16-3-14
 */
public class ActivityScrollViewFade extends BaseDemoActivity implements
        ActionBar.Default.Callback.Overlay,
        View.OnClickListener {

    @ViewInject(id = R.id.activity_scroll_view_fade_text)
    private TextView mTvText;
    @ViewInject(id = R.id.activity_scroll_view_fade_circle_dot_big)
    private DotCircleProgress mCircleBig;
    @ViewInject(id = R.id.activity_scroll_view_fade_circle_dot_tiny)
    private DotCircleProgress mCircleTiny;
    @ViewInject(id = R.id.activity_scroll_view_fade_scrollview)
    private ObservableScrollView mScrollView;
    @ViewInject(id = R.id.activity_scroll_view_fade_btn_toggle_spin, click = "onClick")
    private Button mBtnToggleSpin;
    @ViewInject(id = R.id.activity_scroll_view_fade_btn_set_progress, click = "onClick")
    private Button mBtnSetProgress;
    @ViewInject(id = R.id.activity_scroll_view_fade_btn_set_progress_anim, click = "onClick")
    private Button mBtnSetProgressAnim;

    private View mTvTitle;

    private int mProgress = 270;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_view_fade);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 45; i ++) {
            sb.append("Test Scroll Fade, text line ").append(i + 1).append("\n");
        }
        mTvText.setText(sb.toString());
        refreshProgressBtn();

        ActionBarScrollFadeHelper
                .with(getActionBarView())
                .endOffset(mCircleBig)
                .startOffset(200)
                .addFadeWithView(mTvTitle)
                .addFadeReverseView(mCircleTiny)
                .start(mScrollView);
    }

    @Override
    public void onActionBarCreated(View actionBar, IconTextView left, TextView title, IconTextView right) {
        mTvTitle = title;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_scroll_view_fade_btn_toggle_spin: {
                if (mCircleBig.isSpinning()) {
                    mCircleBig.stopSpin();
                    mCircleTiny.stopSpin();
                } else {
                    mCircleBig.spin();
                    mCircleTiny.spin();
                }
                refreshProgressBtn();
                break;
            }
            case R.id.activity_scroll_view_fade_btn_set_progress: {
                mCircleBig.setProgress(mProgress);
                mCircleTiny.setProgress(mProgress / 2);
                refreshProgressBtn();
                break;
            }
            case R.id.activity_scroll_view_fade_btn_set_progress_anim: {
                mCircleBig.setProgressWithAnim(mProgress);
                mCircleTiny.setProgressWithAnim(mProgress / 2);
                refreshProgressBtn();
                break;
            }
        }
    }

    private void refreshProgressBtn() {
        if (mCircleBig.isSpinning()) {
            mBtnToggleSpin.setText("stop spin");
        } else {
            mBtnToggleSpin.setText("start spin");
        }

        mProgress = new Random().nextInt(340) + 20;
        mBtnSetProgressAnim.setText("to " + mProgress);
        mBtnSetProgress.setText("progress " + mProgress);
    }

}
