package lx.af.utils;

import android.app.Application;

import lx.af.manager.GlobalThreadManager;
import lx.af.widget.frenchtoast.SmartToaster;

/**
 * Created by liuxu on 15-3-19.
 * toast and message dialog
 */
public final class AlertUtils {

    private static Application sApp;

    private AlertUtils() {}

    public static void init(Application app) {
        sApp = app;
    }

    public static void toastLong(int resId) {
        toastLong(sApp.getString(resId));
    }

    public static void toastLong(final String msg) {
        GlobalThreadManager.runInUiThread(new Runnable() {
            @Override
            public void run() {
                SmartToaster.with(sApp).longLength().showText(msg);
            }
        });
    }

    public static void toastShort(int resId) {
        toastShort(sApp.getString(resId));
    }

    public static void toastShort(final String msg) {
        GlobalThreadManager.runInUiThread(new Runnable() {
            @Override
            public void run() {
                SmartToaster.with(sApp).shortLength().showText(msg);
            }
        });
    }

}
