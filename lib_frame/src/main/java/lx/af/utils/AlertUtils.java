package lx.af.utils;

import android.app.Application;
import android.widget.Toast;

import lx.af.manager.GlobalThreadManager;

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

    public static void toastLong(String msg) {
        showToast(msg, Toast.LENGTH_LONG);
    }

    public static void toastShort(int resId) {
        toastShort(sApp.getString(resId));
    }

    public static void toastShort(String msg) {
        showToast(msg, Toast.LENGTH_SHORT);
    }

    private static void showToast(final String msg, final int duration) {
        GlobalThreadManager.runInUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(sApp, msg, duration).show();
            }
        });
    }

}
