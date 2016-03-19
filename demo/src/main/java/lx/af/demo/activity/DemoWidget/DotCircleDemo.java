package lx.af.demo.activity.DemoWidget;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

import lx.af.demo.R;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseActivity;
import lx.af.widget.DotCircleProgress;

/**
 * author: lx
 * date: 16-3-19
 */
public class DotCircleDemo extends BaseActivity implements
        ActionBar.Default,
        View.OnClickListener {

    private DotCircleProgress mProgLarge;
    private DotCircleProgress mProgTiny;
    private DotCircleProgress mProgClock;
    private TextView mProgLargeText;
    private TextView mBtnToggleSpin;
    private TextView mBtnAnimProgress;
    private TextView mBtnSetProgress;

    private int mProgress = 240;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dot_circle);
        mProgLarge = (DotCircleProgress) findViewById(R.id.dot_circle_large);
        mProgTiny = (DotCircleProgress) findViewById(R.id.dot_circle_tiny);
        mProgClock = (DotCircleProgress) findViewById(R.id.dot_circle_clock);
        mProgLargeText = (TextView) findViewById(R.id.dot_circle_large_text);
        mBtnToggleSpin = (TextView) findViewById(R.id.dot_circle_btn_toggle_spin);
        mBtnAnimProgress = (TextView) findViewById(R.id.dot_circle_btn_anim_progress);
        mBtnSetProgress = (TextView) findViewById(R.id.dot_circle_btn_set_progress);

        mBtnToggleSpin.setOnClickListener(this);
        mBtnAnimProgress.setOnClickListener(this);
        mBtnSetProgress.setOnClickListener(this);

        mProgLargeText.setText(mProgLarge.getProgress() + "");
        generateProgress();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dot_circle_btn_toggle_spin: {
                mProgClock.startSpin();
                if (mProgLarge.isSpinning()) {
                    mProgLarge.stopSpin();
                    mProgTiny.stopSpin();
                    mBtnToggleSpin.setText("start spin");
                    mProgLargeText.setVisibility(View.VISIBLE);
                    mProgLarge.setProgressWithAnim(mProgLarge.getProgress());
                } else {
                    mProgLarge.startSpin();
                    mProgTiny.startSpin();
                    mBtnToggleSpin.setText("stop spin");
                    mProgLargeText.setVisibility(View.GONE);
                }
                break;
            }
            case R.id.dot_circle_btn_anim_progress: {
                mProgLarge.setProgress(mProgress);
                mProgTiny.setProgress(mProgress / 2);
                mProgLargeText.setText(mProgress + "");
                generateProgress();
                break;
            }
            case R.id.dot_circle_btn_set_progress: {
                mProgLarge.setProgressWithAnim(mProgress);
                mProgTiny.setProgressWithAnim(mProgress / 2);
                mProgLargeText.setText(mProgress + "");
                generateProgress();
                break;
            }
        }
    }

    private void generateProgress() {
        mProgress = new Random().nextInt(340) + 20;
        mBtnAnimProgress.setText("to " + mProgress);
        mBtnSetProgress.setText("progress " + mProgress);
    }

}
