package lx.af.widget.frenchtoast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static android.widget.Toast.LENGTH_SHORT;
import static lx.af.widget.frenchtoast.ToastInternals.MAIN_HANDLER;
import static lx.af.widget.frenchtoast.ToastInternals.assertMainThread;

/**
 * author: lx
 * date: 16-6-30
 * import from https://github.com/pyricau/frenchtoast
 */
public class ActivityToaster {

    private static final int ANDROID_LONG_DELAY_MS = 3_500;
    private static final int ANDROID_SHORT_DELAY_MS = 2_000;
    private static final int IGNORED = LENGTH_SHORT;

    private static QueueHolder queueHolder;

    private final Activity activity;
    private long durationMs = ANDROID_LONG_DELAY_MS;

    @MainThread
    static void init(@NonNull Application application) {
        if (queueHolder != null) {
            throw new IllegalStateException("Already installed.");
        }
        queueHolder = new QueueHolder();
        application.registerActivityLifecycleCallbacks(queueHolder);
    }

    ActivityToaster(Activity activity) {
        this.activity = activity;
    }

    @MainThread
    public ActivityToaster shortLength() {
        assertMainThread();
        durationMs = ANDROID_SHORT_DELAY_MS;
        return this;
    }

    @MainThread
    public ActivityToaster longLength() {
        assertMainThread();
        durationMs = ANDROID_LONG_DELAY_MS;
        return this;
    }

    @MainThread
    public ActivityToaster length(long duration, TimeUnit timeUnit) {
        assertMainThread();
        durationMs = timeUnit.toMillis(duration);
        return this;
    }

    @MainThread
    public void clear() {
        assertMainThread();
        queueHolder.clear(activity);
    }

    public Toasted showText(CharSequence text) {
        @SuppressLint("ShowToast") Toast toast =
                Toast.makeText(activity.getApplicationContext(), text, IGNORED);
        return showDipped(toast);
    }

    public Toasted showText(@StringRes int stringResId) {
        @SuppressLint("ShowToast") Toast toast =
                Toast.makeText(activity.getApplicationContext(), stringResId, IGNORED);
        return showDipped(toast);
    }

    public Toasted showLayout(@LayoutRes int layoutResId) {
        Context context = activity.getApplicationContext();
        View view = LayoutInflater.from(context).inflate(layoutResId, null);
        Toast toast = new Toast(context);
        toast.setView(view);
        return showDipped(toast);
    }

    public Toasted showView(View view) {
        Context context = activity.getApplicationContext();
        Toast toast = new Toast(context);
        toast.setView(view);
        return showDipped(toast);
    }

    @MainThread
    public Toasted showDipped(Toast toast) {
        assertMainThread();
        Mixture mixture = Mixture.dip(toast);
        ToastQueue queue = queueHolder.getOrCreateActivityToastQueue(activity);
        queue.enqueue(mixture, durationMs);
        return new Toasted(queue, mixture);
    }


    static final class QueueHolder implements Application.ActivityLifecycleCallbacks {

        private static final String FRENCH_TOAST_ACTIVITY_UNIQUE_ID = "FRENCH_TOAST_ACTIVITY_UNIQUE_ID";
        final Map<Activity, Holder> createdActivities = new LinkedHashMap<>();
        final Map<String, ToastQueue> retainedQueues = new LinkedHashMap<>();

        final Runnable clearRetainedQueues = new Runnable() {
            @Override
            public void run() {
                clearRetainedQueues();
            }
        };

        @Override
        public void onActivityPaused(Activity activity) {
            Holder holder = createdActivities.get(activity);
            holder.paused = true;
            if (holder.queueOrNull != null) {
                holder.queueOrNull.pause();
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            Holder holder = createdActivities.get(activity);
            holder.paused = false;
            if (holder.queueOrNull != null) {
                holder.queueOrNull.resume();
            }
            holder.savedUniqueId = null;
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Holder holder = createdActivities.remove(activity);
            if (holder.queueOrNull == null) {
                return;
            }
            if (activity.isChangingConfigurations() && holder.savedUniqueId != null) {
                retainedQueues.put(holder.savedUniqueId, holder.queueOrNull);
                // onCreate() is always called from the same message as the previous onDestroy().
                MAIN_HANDLER.post(clearRetainedQueues);
            } else {
                holder.queueOrNull.clear();
            }
        }

        private void clearRetainedQueues() {
            for (ToastQueue queue : retainedQueues.values()) {
                queue.clear();
            }
            retainedQueues.clear();
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            Holder holder = createdActivities.get(activity);
            String activityUniqueId = UUID.randomUUID().toString();
            outState.putString(FRENCH_TOAST_ACTIVITY_UNIQUE_ID, activityUniqueId);
            holder.savedUniqueId = activityUniqueId;
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            Holder holder = new Holder();
            createdActivities.put(activity, holder);
            if (!retainedQueues.isEmpty()) {
                String uniqueId = savedInstanceState.getString(FRENCH_TOAST_ACTIVITY_UNIQUE_ID);
                if (uniqueId != null) {
                    holder.queueOrNull = retainedQueues.remove(uniqueId);
                }
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        void clear(Activity activity) {
            Holder holder = getHolderOrThrow(activity);
            if (holder.queueOrNull != null) {
                holder.queueOrNull.clear();
            }
        }

        ToastQueue getOrCreateActivityToastQueue(Activity activity) {
            Holder holder = getHolderOrThrow(activity);
            if (holder.queueOrNull == null) {
                ToastQueue toastQueue = new ToastQueue();
                if (holder.paused) {
                    toastQueue.pause();
                }
                holder.queueOrNull = toastQueue;
            }

            return holder.queueOrNull;
        }

        private Holder getHolderOrThrow(Activity activity) {
            Holder holder = createdActivities.get(activity);
            if (holder == null) {
                throw new NullPointerException("Unknown activity "
                        + activity
                        + ", make sure it's not destroyed "
                        + " and that you did not forget to call ActivityToasts.install() "
                        + "from Application.onCreate()");
            }
            return holder;
        }
    }


    static final class Holder {
        boolean paused;
        ToastQueue queueOrNull;
        String savedUniqueId;
    }

}
