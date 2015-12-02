package lx.af.manager;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.LinkedList;
import java.util.Stack;

/**
 * author: lx
 * date: 15-10-10
 */
public final class ActivityTaskManager implements Application.ActivityLifecycleCallbacks {

    private static LinkedList<Activity> sActivityContainer = new LinkedList<>();
    private static Stack<Activity> sForegroundStack = new Stack<>();
    private static ActivityTaskManager sInstance = new ActivityTaskManager();
    private static LinkedList<AppStateListener> sAppStateListeners;

    private ActivityTaskManager() {}

    public static ActivityTaskManager getInstance() {
        return sInstance;
    }

    public static boolean isApplicationForeground() {
        return !sForegroundStack.isEmpty();
    }

    public static Activity foregroundActivity() {
        if (sForegroundStack.isEmpty()) {
            return null;
        } else {
            return sForegroundStack.peek();
        }
    }

    public static void finishAllActivity() {
        while (true) {
            if (sActivityContainer.isEmpty()) {
                break;
            }
            Activity activity = sActivityContainer.pop();
            if (activity == null) {
                continue;
            }
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static void registerAppBackgroundListener(AppStateListener listener) {
        if (sAppStateListeners == null) {
            sAppStateListeners = new LinkedList<>();
        }
        sAppStateListeners.add(listener);
    }

    public static void unregisterAppBackgroundListener(AppStateListener listener) {
        if (sAppStateListeners != null) {
            sAppStateListeners.remove(listener);
        }
    }



    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        sActivityContainer.add(activity);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        sActivityContainer.remove(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (sForegroundStack.isEmpty()) {
            if (sAppStateListeners != null && sAppStateListeners.size() != 0) {
                for (AppStateListener l : sAppStateListeners) {
                    l.onAppGoForeground(activity);
                }
            }
        }
        sForegroundStack.add(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        sForegroundStack.remove(activity);
        if (sForegroundStack.isEmpty()) {
            if (sAppStateListeners != null && sAppStateListeners.size() != 0) {
                for (AppStateListener l : sAppStateListeners) {
                    l.onAppGoBackground(activity);
                }
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }


    /**
     * app state (foreground / background) listener
     */
    public interface AppStateListener {

        /**
         * called when activity goes background
         * @param lastForegroundActivity last foreground activity
         */
        void onAppGoBackground(Activity lastForegroundActivity);

        /**
         * called when activity goes foreground
         * @param foregroundActivity current foreground activity
         */
        void onAppGoForeground(Activity foregroundActivity);

    }

}
