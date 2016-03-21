package lx.af.demo.activity.DemoWidget;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Random;

import lx.af.demo.R;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseActivity;
import lx.af.widget.DotCircleProgress;
import lx.af.widget.RunningDigitView;

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
    private RunningDigitView mRunningDigit;
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
        mRunningDigit = (RunningDigitView) findViewById(R.id.dot_circle_running_text);
        mBtnToggleSpin = (TextView) findViewById(R.id.dot_circle_btn_toggle_spin);
        mBtnAnimProgress = (TextView) findViewById(R.id.dot_circle_btn_anim_progress);
        mBtnSetProgress = (TextView) findViewById(R.id.dot_circle_btn_set_progress);

        mBtnToggleSpin.setOnClickListener(this);
        mBtnAnimProgress.setOnClickListener(this);
        mBtnSetProgress.setOnClickListener(this);

        mRunningDigit
                .setDecimalFormat(new DecimalFormat("00.00"))
                .setDuration(1200)
                .setDigit(mProgLarge.getProgress())
                .startRunning();
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
                    mProgLarge.setProgressWithAnim(mProgLarge.getProgress());
                } else {
                    mProgLarge.startSpin();
                    mProgTiny.startSpin();
                }
                mBtnToggleSpin.setText(mProgLarge.isSpinning() ? "stop spin\n" : "start spin\n");
                mRunningDigit.setVisibility(mProgLarge.isSpinning() ? View.GONE : View.VISIBLE);
                break;
            }
            case R.id.dot_circle_btn_anim_progress: {
                mProgLarge.setProgressWithAnim(mProgress);
                mProgTiny.setProgressWithAnim(mProgress / 2);
                mRunningDigit.setDigit(mProgLarge.getProgress()).startRunning();
                generateProgress();
                break;
            }
            case R.id.dot_circle_btn_set_progress: {
                mProgLarge.setProgress(mProgress);
                mProgTiny.setProgress(mProgress / 2);
                mRunningDigit.setDigit(mProgLarge.getProgress()).startRunning();
                generateProgress();
                break;
            }
        }
    }

    private void generateProgress() {
        mProgress = new Random().nextInt(340) + 20;
        mBtnAnimProgress.setText("anim to\n" + mProgress);
        mBtnSetProgress.setText("progress\n" + mProgress);
    }

}
