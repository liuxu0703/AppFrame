package lx.af.utils;

import lx.af.utils.log.Log;
import lx.af.utils.log.LogUtils;

/**
 * author: liuxu
 *
 * collect log when crash happens.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = CrashHandler.class.getSimpleName();

    private static CrashHandler sInstance = new CrashHandler();
    private static Thread.UncaughtExceptionHandler mDefaultHandler;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return sInstance;
    }

    public static void init() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(sInstance);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        //Log.e(TAG, "shit happens ...", ex);
        LogUtils.saveLogToSdcard("CrashLogPack");
        // we only want the log, let the default handler handle it
        mDefaultHandler.uncaughtException(thread, ex);
    }

}
