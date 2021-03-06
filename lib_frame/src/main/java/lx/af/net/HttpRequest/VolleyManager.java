package lx.af.net.HttpRequest;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * author: lx
 * date: 16-1-1
 */
public class VolleyManager {
    private static final String TAG = Volley.class.getSimpleName();

    private static Application sApp;
    private static RequestQueue mRequestQueue;
    private static Handler mUiThreadHandler;

    private VolleyManager() {}

    /**
     * init essential objects.
     * this should be called in Application.onCreate().
     */
    public static void init(Application app) {
        sApp = app;
        mRequestQueue = Volley.newRequestQueue(sApp);
        mUiThreadHandler = new Handler(sApp.getMainLooper());
    }

    public static RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    /**
     * start request.
     * @param request the request
     * @param tag tag for the request, can be used to cancel pending requests
     * @param <T> the request
     */
    public static <T> void addRequest(Request<T> request, String tag) {
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        mRequestQueue.add(request);
    }

    /**
     * start request with a default tag.
     * @param request the request
     * @param <T> the request
     */
    public static <T> void addRequest(Request<T> request) {
        request.setTag(TAG);
        mRequestQueue.add(request);
    }

    /**
     * cancel all pending request with tag.
     * @param tag the tag
     */
    public static void cancelPendingRequests(Object tag) {
        mRequestQueue.cancelAll(tag);
    }

    /**
     * cancel all pending request with default tag:
     * all request added by addRequest() method without a tag param has a default tag.
     */
    public static void cancelPendingRequests() {
        mRequestQueue.cancelAll(TAG);
    }

    public static void runOnUiThread(Runnable runnable) {
        mUiThreadHandler.post(runnable);
    }

    /**
     * if current calling thread is main thread, throw exception.
     * prevent network request on the main thread.
     */
    public static void throwIfRequestInMainThread() {
        if (isInMainThread()) {
            throw new NetworkOnMainThreadException();
        }
    }

    /**
     * check if the current calling thread is running in main thread.
     * @return true if current thread is main thread
     */
    public static boolean isInMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static boolean isNetworkAvailable() {
        boolean connected = false;
        ConnectivityManager cwjManager = (ConnectivityManager) sApp
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cwjManager.getActiveNetworkInfo();
        if (info != null) {
            connected = info.isAvailable() || info.isConnected();
        }
        return connected;
    }

}
