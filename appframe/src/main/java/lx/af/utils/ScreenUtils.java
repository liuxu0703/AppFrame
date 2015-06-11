package lx.af.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.WindowManager;

/**
 * created by liuxu. many methods here are collected from various sites.
 *
 * operations about screen info.
 */
public final class ScreenUtils {

    private static Application sApp;
    private static int[] sScreenSize;

    public static void init(Application app) {
        sApp = app;
    }

    /**
     * convert sp to px
     */
    public static int sp2px(float spValue) {
        final float fontScale = sApp.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * convert dip to px
     */
    public static int dip2px(float dip) {
        WindowManager wm = (WindowManager) sApp.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        return (int) FloatMath.ceil(dip * metric.density);
    }

    /**
     * convert px to dip
     */
    public static float px2dip(int px) {
        WindowManager wm = (WindowManager) sApp.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        if (metric.density > 0) {
            return px / metric.density;
        } else {
            return px;
        }
    }

    /**
     * get screen width, in pixel.
     * @return screen width
     */
    public static int getScreenWidth() {
        return getScreenSize()[0];
    }

    /**
     * get screen height, in pixel.
     * @return screen height
     */
    public static int getScreenHeight() {
        return getScreenSize()[1];
    }

    /**
     * get screen size as an array: int[] { width, height }
     * @return size
     */
    public static int[] getScreenSize() {
        if (sScreenSize == null) {
            WindowManager wm = (WindowManager) sApp.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics metric = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metric);
            sScreenSize = new int[] { metric.widthPixels, metric.heightPixels };
        }
        return sScreenSize;
    }

    /**
     * get screen density.
     * @return screen density
     */
    public static float getScreenDensity() {
        WindowManager wm = (WindowManager) sApp.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.density;
    }

    /**
     * get activity height, which is the screen height minus status bar height
     * @param activity must be instance of activity
     * @return activity height
     */
    public static int getActivityHeight(Activity activity) {
        return getScreenHeight() - getStatusBarHeight(activity);
    }

    /**
     * get status bar height
     * @param activity must be instance of activity
     * @return status bar height
     */
    public static int getStatusBarHeight(Activity activity){
        int height;
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        height = rect.top;
        if (height == 0) {
            Class<?> cls;
            try {
                cls = Class.forName("com.android.internal.R$dimen");
                Object localObject = cls.newInstance();
                String sbh = cls.getField("status_bar_height").get(localObject).toString();
                int i5 = Integer.parseInt(sbh);
                height = activity.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return height;
    }
}
