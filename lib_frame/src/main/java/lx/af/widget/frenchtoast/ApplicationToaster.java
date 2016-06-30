package lx.af.widget.frenchtoast;

import android.annotation.SuppressLint;
import android.app.Application;
import android.support.annotation.LayoutRes;
import android.support.annotation.MainThread;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
import static lx.af.widget.frenchtoast.ToastInternals.assertMainThread;

/**
 * author: lx
 * date: 16-6-30
 */
public class ApplicationToaster {

    private static final int ANDROID_LONG_DELAY_MS = 3_500;
    private static final int ANDROID_SHORT_DELAY_MS = 2_000;

    private static ToastQueue sQueue;
    private static Application sApp;

    private long durationMs = ANDROID_SHORT_DELAY_MS;

    static void init(Application app) {
        sApp = app;
        sQueue = new ToastQueue();
    }

    ApplicationToaster() {
    }

    @MainThread
    public ApplicationToaster shortLength() {
        assertMainThread();
        durationMs = ANDROID_SHORT_DELAY_MS;
        return this;
    }

    @MainThread
    public ApplicationToaster longLength() {
        assertMainThread();
        durationMs = ANDROID_LONG_DELAY_MS;
        return this;
    }

    public Toasted showText(CharSequence text) {
        int length = durationMs == ANDROID_LONG_DELAY_MS ? LENGTH_LONG : LENGTH_SHORT;
        @SuppressLint("ShowToast") Toast toast =
                Toast.makeText(sApp, text, length);
        return showDipped(toast);
    }

    public Toasted showText(@StringRes int stringResId) {
        int length = durationMs == ANDROID_LONG_DELAY_MS ? LENGTH_LONG : LENGTH_SHORT;
        @SuppressLint("ShowToast") Toast toast =
                Toast.makeText(sApp, stringResId, length);
        return showDipped(toast);
    }

    public Toasted showLayout(@LayoutRes int layoutResId) {
        View view = LayoutInflater.from(sApp).inflate(layoutResId, null);
        return showView(view);
    }

    public Toasted showView(View view) {
        Toast toast = new Toast(sApp);
        toast.setView(view);
        return showDipped(toast);
    }

    @MainThread
    public Toasted showDipped(Toast toast) {
        assertMainThread();
        Mixture mixture = Mixture.dip(toast);
        sQueue.enqueue(mixture, durationMs);
        return new Toasted(sQueue, mixture);
    }

}
