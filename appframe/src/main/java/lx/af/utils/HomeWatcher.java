package lx.af.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * watcher for home key
 */
public class HomeWatcher {
    private final Context mContext;
    private final IntentFilter mFilter;
    private OnHomePressedListener mListener;
    private InnerRecevier mRecevier;

    /**
     * callback when home key is pressed.
     */
    public interface OnHomePressedListener {

        /**
         * callback when home key is pressed.
         */
        public void onHomePressed();

        /**
         * callback when home key is long pressed
         */
        public void onHomeLongPressed();
    }

    public HomeWatcher(Context context) {
        this.mContext = context;
        this.mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    }

    /**
     * add callback
     * @param listener callback
     */
    public void setOnHomePressedListener(OnHomePressedListener listener) {
        this.mListener = listener;
        this.mRecevier = new InnerRecevier();
    }

    /**
     * start monitor home key press event.
     */
    public void startWatch() {
        if (this.mRecevier != null) {
            this.mContext.registerReceiver(this.mRecevier, this.mFilter);
        }
    }

    /**
     * stop monitor home key press event.
     */
    public void stopWatch() {
        if (this.mRecevier != null) {
            this.mContext.unregisterReceiver(this.mRecevier);
        }
    }


    private class InnerRecevier extends BroadcastReceiver {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent
                        .getStringExtra(this.SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (HomeWatcher.this.mListener != null) {
                        if (reason.equals(this.SYSTEM_DIALOG_REASON_HOME_KEY)) {
                            // home key short press
                            HomeWatcher.this.mListener.onHomePressed();
                        } else if (reason
                                .equals(this.SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                            // home key long press
                            HomeWatcher.this.mListener.onHomeLongPressed();
                        }
                    }
                }
            }
        }
    }
}
