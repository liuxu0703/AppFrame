package lx.af.widget;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * author: lx
 * date: 16-3-21
 */
public class RunningDigitView extends TextView {

    private static final long MIN_DURATION = 600; // im millisecond
    private static final long DEFAULT_DURATION = 1500; // in millisecond
    private static final long COUNT_DOWN_STEP = 20; // in millisecond

    private CountDownTimer mCountDown;

    private DecimalFormat mDecimalFormat;
    private double mDigit = 0.0f;
    private long mDuration = DEFAULT_DURATION;
    private boolean mIncrease = true;

    public RunningDigitView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public RunningDigitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public RunningDigitView(Context context) {
        super(context);
        initView();
    }

    public double getDigit() {
        return mDigit;
    }

    public RunningDigitView setDigit(double digit) {
        if (mCountDown != null) {
            mCountDown.cancel();
        }
        mDigit = digit;
        setText(formatDigital(mDigit));
        return this;
    }

    public RunningDigitView setDuration(long rollingDuration) {
        if (rollingDuration < MIN_DURATION) {
            rollingDuration = MIN_DURATION;
        }
        mDuration = rollingDuration;
        return this;
    }

    public RunningDigitView setDecimalFormat(DecimalFormat format) {
        mDecimalFormat = format;
        return this;
    }

    public RunningDigitView setIncrease(boolean increase) {
        mIncrease = increase;
        return this;
    }

    public void startRunning() {
        if (mCountDown != null) {
            mCountDown.cancel();
        }
        mCountDown = new CountDown(mDigit, mDuration, mIncrease);
        mCountDown.start();
    }

    private void initView() {

    }

    private CharSequence formatDigital(double digital) {
        if (mDecimalFormat == null) {
            mDecimalFormat = new DecimalFormat("0.0");
        }
        return mDecimalFormat.format(digital);
    }

    private class CountDown extends CountDownTimer {

        private double target;
        private double digit;
        private double step;
        private boolean increase;

        public CountDown(double digit, long duration, boolean increase) {
            super(duration, COUNT_DOWN_STEP);
            this.target = digit;
            long count = duration / COUNT_DOWN_STEP;
            this.step = digit / count;
            this.digit = increase ? 0.0f : digit;
            this.increase = increase;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (increase) {
                digit += step;
            } else {
                digit -= step;
            }
            setText(formatDigital(digit));
        }

        @Override
        public void onFinish() {
            setText(formatDigital(target));
        }
    }

}
