package lx.af.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import lx.af.R;
import lx.af.view.ProgressWheel;

/**
 * Created by liuxu on 15-5-11.
 * dialog with a loading progress bar.
 */
public class LoadingDialog extends Dialog {

    private TextView mTv;
    private ProgressWheel mProgress;
    private DismissHandler mDismissHandler;
    private long mMinDismissTime = 0;
    private long mStartTime;

    /**
     * create a loading dialog.
     * @param context the context.
     */
    public LoadingDialog(Context context) {
        this(context, null);
    }

    /**
     * create a loading dialog with message.
     * @param context the context.
     * @param resId the loading message resource id.
     */
    public LoadingDialog(Context context, int resId) {
        this(context, context.getString(resId));
    }

    /**
     * create a loading dialog with message.
     * @param context the context.
     * @param text the loading message.
     */
    public LoadingDialog(Context context, String text) {
        super(context, R.style.dialog_loading);
        setContentView(R.layout.dlg_loading);
        setCancelable(false);

        mTv = (TextView) findViewById(R.id.dlg_loading_text);
        mProgress = (ProgressWheel) findViewById(R.id.dlg_loading_progress);

        if (!TextUtils.isEmpty(text)) {
            mTv.setText(text);
        } else {
            mTv.setVisibility(View.GONE);
        }

        mProgress.spin();
    }

    @Override
    public void show() {
        super.show();
        mStartTime = System.currentTimeMillis();
        if (mDismissHandler != null) {
            mDismissHandler.dismissTimeout();
        }
    }

    @Override
    public void dismiss() {
        if (mMinDismissTime <= 0) {
            dismissSuper();
        } else {
            long time = System.currentTimeMillis() - mStartTime;
            if (time < mMinDismissTime) {
                mDismissHandler.dismissDelay(mMinDismissTime - time);
            } else {
                dismissSuper();
            }
        }
    }

    public void dismissSuper() {
        super.dismiss();
    }

    /**
     * set a timeout mechanism for the loading dialog:
     * the loading dialog will be automatically dismissed in a given time.
     * @param delayMillis the timeout time, in milli seconds.
     * @param listener callback to receive the timeout event.
     */
    public void setLoadingTimeout(int delayMillis, OnTimeoutListener listener) {
        if (delayMillis != 0) {
            mDismissHandler = new DismissHandler(this, delayMillis, listener);
        }
    }

    /**
     * set a minimum time for the loading dialog to dismiss.
     * @param millis the minimum time
     */
    public void setLoadingMinTime(int millis) {
        mMinDismissTime = (long) millis;
    }

    /**
     * callback for timeout event.
     */
    public interface OnTimeoutListener {

        /**
         * callback for timeout event.
         */
        public void onTimeout();
    }

    private static class DismissHandler extends Handler {

        private static final int MSG_DISMISS = 101;
        private static final int MSG_DISMISS_TIMEOUT = 102;

        private LoadingDialog mDialog;
        private OnTimeoutListener mTimeoutListener;
        private int mDelayMillis;

        DismissHandler(LoadingDialog dialog, int delayMillis, OnTimeoutListener listener) {
            mDialog = dialog;
            mTimeoutListener = listener;
            mDelayMillis = delayMillis;
        }

        void dismissDelay(long delayMillis) {
            removeMessages(MSG_DISMISS);
            sendEmptyMessageDelayed(MSG_DISMISS, delayMillis);
        }

        void dismissTimeout() {
            removeMessages(MSG_DISMISS_TIMEOUT);
            sendEmptyMessageDelayed(MSG_DISMISS_TIMEOUT, mDelayMillis);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DISMISS_TIMEOUT: {
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismissSuper();
                        if (mTimeoutListener != null) {
                            mTimeoutListener.onTimeout();
                        }
                    }
                    break;
                }
                case MSG_DISMISS: {
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismissSuper();
                    }
                    break;
                }
            }
        }
    }

}
