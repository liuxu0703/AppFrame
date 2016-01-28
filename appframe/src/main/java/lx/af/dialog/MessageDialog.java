package lx.af.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import lx.af.R;

/**
 * Created by liuxu on 15-5-8.
 * dialog that shows a message.
 */
public class MessageDialog extends Dialog {

    public static Builder builder(Context context) {
        return new Builder(context);
    }

    /**
     * create a dialog with a message and a confirm button.
     * @param context context.
     * @param msg the message.
     */
    public MessageDialog(Context context, String msg) {
        this(context, null, msg, false, null, null);
    }

    /**
     * create a dialog with a message and a confirm button.
     * @param context context.
     * @param msgResId the message string resource id.
     */
    public MessageDialog(Context context, int msgResId) {
        this(context, null, context.getString(msgResId), false, null, null);
    }

    /**
     * create a dialog with a message and a confirm button.
     * @param context context.
     * @param msg the message.
     * @param confirmListener callback for the confirm button.
     */
    public MessageDialog(Context context, String msg, View.OnClickListener confirmListener) {
        this(context, null, msg, false, confirmListener, null);
    }

    /**
     * create a dialog with a message and a confirm button.
     * @param context context.
     * @param msgResId the message string resource id.
     * @param confirmListener callback for the confirm button.
     */
    public MessageDialog(Context context, int msgResId, View.OnClickListener confirmListener) {
        this(context, null, context.getString(msgResId), false, confirmListener, null);
    }

    public MessageDialog(Context context, String title, String msg, boolean cancelEnabled,
                         View.OnClickListener confirmListener,
                         View.OnClickListener cancelListener) {
        super(context, R.style.dialog_custom);
        setContentView(R.layout.dlg_alert);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        TextView tvTitle = (TextView) findViewById(R.id.dlg_alert_title);
        TextView tvMessage = (TextView) findViewById(R.id.dlg_alert_message);
        TextView btnCancel = (TextView) findViewById(R.id.dlg_alert_cancel);
        TextView btnConfirm = (TextView) findViewById(R.id.dlg_alert_ok);

        tvMessage.setText(msg);
        btnConfirm.setOnClickListener(new DismissListener(this, confirmListener));

        if (title == null) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
        }

        if (cancelEnabled) {
            btnCancel.setOnClickListener(new DismissListener(this, cancelListener));
        } else {
            btnCancel.setVisibility(View.GONE);
        }

    }

    /**
     * builder for the message dialog.
     */
    public static class Builder {

        private Context mContext;
        private String mTitle;
        private String mMessage;
        private View.OnClickListener mConfirmListener;
        private View.OnClickListener mCancelListener;
        private boolean mIsEnableCancel = false;

        public Builder(Context context) {
            mContext = context;
        }

        /**
         * set title for the message dialog.
         */
        public Builder setTitle(int resId) {
            mTitle = mContext.getString(resId);
            return this;
        }

        /**
         * set title for the message dialog.
         */
        public Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        /**
         * set message for the message dialog.
         */
        public Builder setMessage(int resId) {
            mMessage = mContext.getString(resId);
            return this;
        }

        /**
         * set message for the message dialog.
         */
        public Builder setMessage(String message) {
            mMessage = message;
            return this;
        }

        /**
         * set callback for confirm button.
         */
        public Builder setConfirmListener(View.OnClickListener listener) {
            mConfirmListener = listener;
            return this;
        }

        /**
         * set callback for cancel button.
         * @param listener the callback.
         *                 pass null if cancel button is request with no more action
         *                 then dismiss the dialog.
         */
        public Builder setCancelListener(View.OnClickListener listener) {
            mCancelListener = listener;
            mIsEnableCancel = true;
            return this;
        }

        /**
         * create the dialog.
         * remember to call show() to display the dialog.
         */
        public MessageDialog create() {
            return new MessageDialog(mContext, mTitle, mMessage, mIsEnableCancel,
                    mConfirmListener, mCancelListener);
        }
    }


    private static class DismissListener implements View.OnClickListener {
        Dialog dialog;
        View.OnClickListener listener;

        DismissListener(Dialog dlg, View.OnClickListener listener) {
            this.dialog = dlg;
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onClick(v);
            }
            dialog.dismiss();
        }
    }
    
}
