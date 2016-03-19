package lx.af.widget;

/*
 The MIT License (MIT)

 Copyright (c) 2014 justin

 Permission is hereby granted, free of charge, to any person obtaining a copy of
 this software and associated documentation files (the "Software"), to deal in
 the Software without restriction, including without limitation the rights to
 use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 the Software, and to permit persons to whom the Software is furnished to do so,
 subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Android roll digital text view.
 * digital will rolling from 0 to actual value (increase), or vice versa (decrease).
 *
 * import from https://github.com/zzhouj/Android-RollDigitalTextView
 */
public class RunningDigitView extends TextView {

    private static final String TAG = "RollDigitalTextView";
    private static final boolean DEBUG = false;

    private static void DEBUG_LOG(String msg) {
        if (DEBUG) {
            Log.v(TAG, msg);
        }
    }

    private static final int MIN_ROLLING_DURATION = 600; // ms
    private static final int DEFAULT_ROLLING_DURATION = 2000; // ms

    private static final int MAX_VALUE = 97;

    private Scroller mScroller;
    private DecimalFormat mDecimalFormat;
    private long mRollingDuration = DEFAULT_ROLLING_DURATION;
    private boolean mLastCompleteShown;

    private double mOriginDigit;
    private double mCurrentDigit;

    public RunningDigitView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initRollDigitTextView();
    }

    public RunningDigitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initRollDigitTextView();
    }

    public RunningDigitView(Context context) {
        super(context);
        initRollDigitTextView();
    }

    private void initRollDigitTextView() {
        final Context context = getContext();
        mScroller = new Scroller(context, new LinearInterpolator());
    }

    public void setDigit(double digit) {
        if (mOriginDigit == digit) {
            return;
        }
        mScroller.abortAnimation();
        mOriginDigit = digit;
        mCurrentDigit = digit;
        super.setText(formatDigital(mOriginDigit));
    }

    public double getDigit() {
        return mOriginDigit;
    }

    public long getRollingDuration() {
        return mRollingDuration;
    }

    public void setRollingDuration(long rollingDuration) {
        if (rollingDuration < MIN_ROLLING_DURATION) {
            rollingDuration = MIN_ROLLING_DURATION;
        }
        mRollingDuration = rollingDuration;
    }

    public void setDecimalFormat(DecimalFormat format) {
        mDecimalFormat = format;
    }

    public void startRolling(boolean increase) {
        if (mOriginDigit > 0) {
            int sx;
            int dx;
            if (increase) {
                sx = 0;
                dx = MAX_VALUE;
            } else {
                sx = MAX_VALUE;
                dx = -MAX_VALUE;
            }
            mScroller.abortAnimation();
            mScroller.startScroll(sx, 0, dx, 0, (int) (mRollingDuration));
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public void computeScroll() {
        if (mOriginDigit > 0) {
            if (!mScroller.isFinished() && mScroller.computeScrollOffset()) {
                final int scrollX = Math.max(0, Math.min(MAX_VALUE, Math.abs(mScroller.getCurrX())));
                mCurrentDigit = scrollX * (mOriginDigit / MAX_VALUE);
                DEBUG_LOG("computeScroll mCurrentDigit=" + mCurrentDigit);
                super.setText(formatDigital(mCurrentDigit));

                // Keep on drawing until the animation has finished.
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
    }

    private CharSequence formatDigital(double digital) {
        if (mDecimalFormat == null) {
            mDecimalFormat = new DecimalFormat("#.##");
        }
        return mDecimalFormat.format(digital);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final boolean isCompleteShown = isCompleteShown();
        if (mLastCompleteShown != isCompleteShown) {
            mLastCompleteShown = isCompleteShown;
            post(new Runnable() {
                @Override
                public void run() {
                    startRolling(mLastCompleteShown);
                }
            });
        }
    }

    private boolean isCompleteShown() {
        if (isShown()) {
            final Rect visibleRect = new Rect();
            if (getLocalVisibleRect(visibleRect)
                    && visibleRect.width() == getWidth()
                    && visibleRect.height() == getHeight()) {
                return true;
            }
        }
        return false;
    }

}
