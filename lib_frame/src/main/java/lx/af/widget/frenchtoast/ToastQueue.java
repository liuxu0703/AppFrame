package lx.af.widget.frenchtoast;

import android.support.annotation.MainThread;

import java.util.ArrayDeque;
import java.util.Deque;

import static lx.af.widget.frenchtoast.ToastInternals.MAIN_HANDLER;
import static lx.af.widget.frenchtoast.ToastInternals.assertMainThread;

/**
 * import from https://github.com/pyricau/frenchtoast
 *
 * A queue on which you can enqueue Toasts to show with a specific duration. One Toast
 * is shown at a time. You can tie this queue to a lifecycle (e.g. an activity) so that
 * the toasts are only displayed in the relevant context. If a Toast is displayed when the queue is
 * paused, then it's duration will be reset and it will be shown for its whole duration again when
 * the queue gets resumed. The queue is resumed by default.
 */
final class ToastQueue {

    static final class EnqueuedToast {
        final Mixture mixture;
        final long durationMs;

        EnqueuedToast(Mixture mixture, long durationMs) {
            this.mixture = mixture;
            this.durationMs = durationMs;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof EnqueuedToast)) {
                return false;
            }
            EnqueuedToast that = (EnqueuedToast) other;
            return mixture == that.mixture;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(mixture);
        }
    }

    private final Deque<EnqueuedToast> toastDeque = new ArrayDeque<>();

    private final Runnable hideToast = new Runnable() {
        @Override
        public void run() {
            hideToast();
        }
    };

    private boolean paused;

    @MainThread
    public ToastQueue() {
        assertMainThread();
    }

    @MainThread
    public void clear() {
        assertMainThread();
        if (toastDeque.isEmpty()) {
            return;
        }
        if (!paused) {
            EnqueuedToast firstToast = toastDeque.getFirst();
            firstToast.mixture.hide();
            MAIN_HANDLER.removeCallbacks(hideToast);
        }
        toastDeque.clear();
    }

    @MainThread
    public boolean cancel(Mixture canceledMixture) {
        assertMainThread();
        if (toastDeque.isEmpty()) {
            return false;
        }

        boolean showNext = false;

        if (!paused) {
            EnqueuedToast firstToast = toastDeque.getFirst();
            if (firstToast.mixture == canceledMixture) {
                firstToast.mixture.hide();
                MAIN_HANDLER.removeCallbacks(hideToast);
                showNext = true;
            }
        }

        EnqueuedToast toDelete = new EnqueuedToast(canceledMixture, 0);
        boolean removed = toastDeque.remove(toDelete);

        if (showNext) {
            showFirstToast();
        }

        return removed;
    }

    @MainThread
    public void pause() {
        assertMainThread();
        if (paused) {
            return;
        }
        paused = true;
        if (toastDeque.isEmpty()) {
            return;
        }
        EnqueuedToast firstToast = toastDeque.getFirst();
        firstToast.mixture.hide();
        MAIN_HANDLER.removeCallbacks(hideToast);
    }

    @MainThread
    public void resume() {
        assertMainThread();
        if (!paused) {
            return;
        }
        paused = false;
        showFirstToast();
    }

    @MainThread
    public void enqueue(Mixture mixture, long durationMs) {
        assertMainThread();
        boolean empty = toastDeque.isEmpty();
        toastDeque.add(new EnqueuedToast(mixture, durationMs));
        if (empty) {
            showFirstToast();
        }
    }

    private void showFirstToast() {
        if (toastDeque.isEmpty() || paused) {
            return;
        }
        EnqueuedToast currentToast = toastDeque.getFirst();
        currentToast.mixture.show();
        MAIN_HANDLER.postDelayed(hideToast, currentToast.durationMs);
    }

    private void hideToast() {
        EnqueuedToast currentToast = toastDeque.removeFirst();
        currentToast.mixture.hide();
        showFirstToast();
    }
}
