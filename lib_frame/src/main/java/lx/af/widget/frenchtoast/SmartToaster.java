package lx.af.widget.frenchtoast;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.LayoutRes;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Toast;

import static lx.af.widget.frenchtoast.ToastInternals.assertMainThread;
import static lx.af.widget.frenchtoast.ToastInternals.checkNotNull;

/**
 * import from https://github.com/pyricau/frenchtoast
 */
public final class SmartToaster {

    public static final int LENGTH_LONG = 3_500;
    public static final int LENGTH_SHORT = 2_000;

    @MainThread
    public static void init(@NonNull Application application) {
        ActivityToaster.init(application);
        ApplicationToaster.init(application);
    }

    @MainThread
    public static ActivityToaster with(@NonNull Activity activity) {
        assertMainThread();
        checkNotNull(activity, "context");
        return new ActivityToaster(activity);
    }

    @MainThread
    public static ApplicationToaster with(@NonNull Application app) {
        assertMainThread();
        checkNotNull(app, "app");
        return ApplicationToaster.getInstance();
    }

    @MainThread
    public static ApplicationToaster shortLength() {
        return ApplicationToaster.getInstance().shortLength();
    }

    @MainThread
    public static ApplicationToaster longLength() {
        return ApplicationToaster.getInstance().longLength();
    }

    public static void showText(CharSequence text) {
        ApplicationToaster.getInstance().showText(text);
    }

    public static void showText(@StringRes int stringResId) {
        ApplicationToaster.getInstance().showText(stringResId);
    }

    public static void showLayout(@LayoutRes int layoutResId) {
        ApplicationToaster.getInstance().showLayout(layoutResId);
    }

    public static void showView(View view) {
        ApplicationToaster.getInstance().showView(view);
    }

    @MainThread
    public static void showDipped(Toast toast) {
        ApplicationToaster.getInstance().showDipped(toast);
    }

    private SmartToaster() {
        throw new AssertionError();
    }

}
