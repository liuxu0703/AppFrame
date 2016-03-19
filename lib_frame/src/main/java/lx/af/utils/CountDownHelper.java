package lx.af.utils;

import lx.af.manager.GlobalThreadManager;

/**
 * author: lx
 * date: 16-3-10
 */
public class CountDownHelper {

    private int start;
    private int stop;
    private int step;
    private long interval;

    private int count;
    private boolean running = false;
    private CountDownCallback callback;

    public static Builder build() {
        return new Builder();
    }

    public CountDownHelper(int start, int stop, int step, long interval, CountDownCallback c) {
        this.start = start;
        this.stop = stop;
        this.step = step != 0 ? step : 1;
        this.interval = interval != 0 ? interval : 1000;
        this.callback = c;
        check();
    }

    public void start(boolean immediately) {
        count = start;
        running = true;
        GlobalThreadManager.getUiThreadHandler().removeCallbacks(mCountDownRunnable);
        if (immediately) {
            GlobalThreadManager.getUiThreadHandler().post(mCountDownRunnable);
        } else {
            GlobalThreadManager.getUiThreadHandler().postDelayed(mCountDownRunnable, interval);
        }
    }

    public void stop() {
        running = false;
        GlobalThreadManager.getUiThreadHandler().removeCallbacks(mCountDownRunnable);
    }

    public void reset() {
        stop();
        check();
    }

    public void reset(int start, int stop, int step, long interval) {
        stop();
        this.start = start;
        this.stop = stop;
        this.step = step != 0 ? step : 1;
        this.interval = interval != 0 ? interval : 1000;
        check();
    }

    public boolean isRunning() {
        return running;
    }

    private void check() {
        if (callback == null) {
            throw new IllegalArgumentException("should have a count down callback");
        }
        if ((start == stop) || (step > 0 && start < stop) || (step < 0 && start > stop)) {
            throw new IllegalArgumentException("unlimited count down," +
                    "from " + start + " to " + stop + ", step=" + step);
        }
    }

    private Runnable mCountDownRunnable = new Runnable() {
        @Override
        public void run() {
            count -= step;
            boolean end = count <= stop;
            if (end) {
                stop();
            } else {
                GlobalThreadManager.getUiThreadHandler().postDelayed(mCountDownRunnable, interval);
            }
            callback.onCountDown(count, end);
        }
    };


    public static class Builder {

        private int start;
        private int stop = 0;
        private int step = 1;
        private long interval = 1000;
        private CountDownCallback callback;

        public Builder() {}

        public Builder setStartCount(int start) {
            this.start = start;
            return this;
        }

        public Builder setStopCount(int stop) {
            this.stop = stop;
            return this;
        }

        public Builder setStep(int step) {
            this.step = step;
            return this;
        }

        public Builder setInterval(long interval) {
            this.interval = interval;
            return this;
        }

        public Builder setCallback(CountDownCallback c) {
            this.callback = c;
            return this;
        }

        public CountDownHelper build() {
            return new CountDownHelper(start, stop, step, interval, callback);
        }

        public CountDownHelper start(boolean immediately) {
            CountDownHelper helper = build();
            helper.start(immediately);
            return helper;
        }
    }


    public interface CountDownCallback {

        void onCountDown(int count, boolean end);

    }

}
