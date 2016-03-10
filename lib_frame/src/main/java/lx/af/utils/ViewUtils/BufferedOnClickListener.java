package lx.af.utils.ViewUtils;

import android.view.View;

import lx.af.manager.GlobalThreadManager;

/**
 * author: lx
 * date: 16-2-20
 *
 * subclass of {@link android.view.View.OnClickListener}
 * 1. used to buffer click event to prevent more than one click action being committed
 * in a given time.
 * 2. used to detect double or triple (or more) click event on a view
 */
public abstract class BufferedOnClickListener implements View.OnClickListener {

    private long delay = 300;
    private ClickRunnable runnable;

    /**
     * @param delay buffer time, in millis
     */
    public BufferedOnClickListener(long delay) {
        this.delay = delay;
    }

    /**
     * default buffer time 300 millis
     */
    public BufferedOnClickListener() {
    }

    /**
     * click action after delay
     * @param v the last clicked view
     * @param clickCount click count in buffer time on the last clicked view
     */
    public abstract void onBufferedClick(View v, int clickCount);

    @Override
    public void onClick(View v) {
        if (runnable == null) {
            runnable = new ClickRunnable(this);
        }
        if (runnable.view == v) {
            runnable.count ++;
        } else {
            runnable.view = v;
            runnable.count = 0;
        }
        GlobalThreadManager.runInUiThreadBuffered(runnable, delay);
    }

    private static class ClickRunnable implements Runnable {

        View view;
        BufferedOnClickListener listener;
        int count;

        public ClickRunnable(BufferedOnClickListener listener) {
            this.listener = listener;
            this.count = 0;
        }

        @Override
        public void run() {
            listener.onBufferedClick(view, count);
            count = 0;
        }
    }

}
