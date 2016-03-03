package lx.af.utils.ViewUtils;

import android.view.View;

import lx.af.manager.GlobalThreadManager;

/**
 * author: lx
 * date: 16-2-20
 */
public abstract class BufferedOnClickListener implements View.OnClickListener {

    private long delay = 500;
    private ClickRunnable runnable;

    public BufferedOnClickListener(long delay) {
        this.delay = delay;
    }

    public BufferedOnClickListener() {
    }

    @Override
    public void onClick(View v) {
        if (runnable == null) {
            runnable = new ClickRunnable(v, this);
        }
        GlobalThreadManager.runInUiThreadBuffered(runnable, delay);
    }

    public abstract void onBufferedClick(View v);

    private static class ClickRunnable implements Runnable {

        View view;
        BufferedOnClickListener listener;

        public ClickRunnable(View view, BufferedOnClickListener listener) {
            this.view = view;
            this.listener = listener;
        }

        @Override
        public void run() {
            listener.onBufferedClick(view);
        }
    }

}
