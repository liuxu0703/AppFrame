package lx.af.widget.frenchtoast;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import static lx.af.widget.frenchtoast.ToastInternals.assertMainThread;
import static lx.af.widget.frenchtoast.ToastInternals.checkNotNull;

/**
 * import from https://github.com/pyricau/frenchtoast
 */
public final class SmartToaster {

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
        return new ApplicationToaster();
    }

    private SmartToaster() {
        throw new AssertionError();
    }
}
