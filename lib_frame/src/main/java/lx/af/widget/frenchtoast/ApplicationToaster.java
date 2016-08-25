package lx.af.widget.frenchtoast;

import android.annotation.SuppressLint;
import android.app.Application;
import android.support.annotation.LayoutRes;
import android.support.annotation.MainThread;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import static lx.af.widget.frenchtoast.ToastInternals.assertMainThread;

/**
 * author: lx
 * date: 16-6-30
 */
public class ApplicationToaster {

    private static ToastQueue sQueue;
    private static Application sApp;
    private static ApplicationToaster sInstance;

    private long durationMs = SmartToaster.LENGTH_SHORT;

    private ApplicationToaster() {
    }

    static void init(Application app) {
        sApp = app;
        sQueue = new ToastQueue();
        sInstance = new ApplicationToaster();
    }

    static ApplicationToaster getInstance() {
        return sInstance;
    }

    @MainThread
    public ApplicationToaster shortLength() {
        assertMainThread();
        durationMs = SmartToaster.LENGTH_SHORT;
        return this;
    }

    @MainThread
    public ApplicationToaster longLength() {
        assertMainThread();
        durationMs = SmartToaster.LENGTH_LONG;
        return this;
    }

    public void showText(CharSequence text) {
        int length = durationMs == SmartToaster.LENGTH_LONG ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        @SuppressLint("ShowToast") Toast toast =
                Toast.makeText(sApp, text, length);
        showDipped(toast);
    }

    public void showText(@StringRes int stringResId) {
        int length = durationMs == SmartToaster.LENGTH_LONG ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        @SuppressLint("ShowToast") Toast toast =
                Toast.makeText(sApp, stringResId, length);
        showDipped(toast);
    }

    public void showLayout(@LayoutRes int layoutResId) {
        View view = LayoutInflater.from(sApp).inflate(layoutResId, null);
        showView(view);
    }

    public void showView(View view) {
        Toast toast = new Toast(sApp);
        toast.setView(view);
        showDipped(toast);
    }

    @MainThread
    public void showDipped(Toast toast) {
        assertMainThread();
        Mixture mixture = Mixture.dip(toast);
        sQueue.enqueue(mixture, durationMs);
        new Toasted(sQueue, mixture);
    }

}
