package lx.af.manager;

import android.app.Activity;

import java.util.Stack;

public final class ActivityManager {
    private static Stack<Activity> sActivityStack = new Stack<>();
    private static Activity sForegroundActivity;
    private static ActivityManager sInstance = new ActivityManager();

    private ActivityManager() {}

    public static ActivityManager getInstance() {
        return sInstance;
    }

    public void onActivityCreate(Activity activity) {
        assert (activity != null);
        sActivityStack.add(activity);
    }

    public void onActivityDestory(Activity activity) {
        assert (activity != null);
        sActivityStack.remove(activity);
    }

    public void onActivityResume(Activity activity) {
        assert (activity != null);
        sForegroundActivity = activity;
    }

    public void onActivityPause(Activity activity) {
        sForegroundActivity = null;
    }

    public Activity currentActivity() {
        if (sActivityStack.isEmpty()) {
            return null;
        } else {
            return sActivityStack.lastElement();
        }
    }

    public Activity foregroundActivity() {
        return sForegroundActivity;
    }

    public void finishAllActivity() {
        while (true) {
            if (sActivityStack.isEmpty()) {
                break;
            }
            Activity activity = sActivityStack.pop();
            if (activity == null) {
                continue;
            }
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

}