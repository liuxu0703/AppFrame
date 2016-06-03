package lx.af.manager;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * author: liuxu
 * date: 2014-11-25
 *
 * to easy access thread pool and UI thread.
 */
public final class GlobalThreadManager {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 2;

    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<>(64);

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "GlobalThreadManager#" + mCount.getAndIncrement());
        }
    };

    private static Application sApp;
    private static ThreadPoolExecutor mThreadPool;
    private static Handler mUiThreadHandler;

    private GlobalThreadManager() {}

    public static void init(Application app) {
        sApp = app;
    }

    /**
     * get global thread pool instance.
     */
    public static ThreadPoolExecutor getThreadPoolInstance() {
        if (mThreadPool == null) {
            synchronized (GlobalThreadManager.class) {
                if (mThreadPool == null) {
                    mThreadPool = new ThreadPoolExecutor(
                            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                            KEEP_ALIVE, TimeUnit.SECONDS,
                            sPoolWorkQueue, sThreadFactory);
                    mThreadPool.setRejectedExecutionHandler(new DiscardOldestPolicy());
                }
            }
        }
        return mThreadPool;
    }

    /**
     * get handler looping in main (UI) thread.
     */
    public static Handler getUiThreadHandler() {
        if (mUiThreadHandler == null) {
            synchronized (GlobalThreadManager.class) {
                if (mUiThreadHandler == null) {
                    mUiThreadHandler = new Handler(sApp.getMainLooper());
                }
            }
        }
        return mUiThreadHandler;
    }

    /**
     * do something in this thread pool.
     * Tasks will run in parallel.
     * @param runnable can be an AsyncTask
     */
    public static void runInThreadPool(Runnable runnable) {
        getThreadPoolInstance().execute(runnable);
    }

    /**
     * do something in ui thread
     * @param runnable runnable
     */
    public static void runInUiThread(Runnable runnable) {
        getUiThreadHandler().post(runnable);
    }

    /**
     * do something in ui thread, with delay
     * @param runnable runnable
     * @param delay delay time, in millisecond
     */
    public static void runInUiThreadDelayed(Runnable runnable, long delay) {
        getUiThreadHandler().postDelayed(runnable, delay);
    }

    /**
     * do something in ui thread. the same runnable instance will be executed
     * only once if more than one call is made in a given time (bufferTime).
     * @param runnable runnable
     * @param bufferTime time to wait
     */
    public static void runInUiThreadBuffered(Runnable runnable, long bufferTime) {
        Handler handler = getUiThreadHandler();
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, bufferTime);
    }

    public static void removeUiThreadCallback(Runnable runnable) {
        getUiThreadHandler().removeCallbacks(runnable);
    }

    /**
     * check if the current calling thread is running in main thread.
     * @return true if current thread is main thread
     */
    public static boolean isInMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * if current calling thread is main thread, throw exception.
     * this method is used to prevent heavy operation from being executed in the main thread.
     * @param msg message for exception
     */
    public static void throwExceptionIfInMainThread(String msg) {
        if (isInMainThread()) {
            throw new IllegalThreadStateException(msg);
        }
    }

    /**
     * if current calling thread is not main thread, throw exception.
     * @param msg message for exception
     */
    public static void throwExceptionIfNotInMainThread(String msg) {
        if (isInMainThread()) {
            throw new IllegalThreadStateException(msg);
        }
    }

    /**
     * release.
     */
    public static void shutdownThreadPool() {
        if (mThreadPool != null) {
            mThreadPool.shutdown();
            mThreadPool = null;
        }
    }
}
