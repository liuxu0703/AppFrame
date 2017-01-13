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

    public static void toastLong(final String msg) {
        GlobalThreadManager.runInUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(sApp, msg, Toast.LENGTH_LONG).show();
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
                Toast.makeText(sApp, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
