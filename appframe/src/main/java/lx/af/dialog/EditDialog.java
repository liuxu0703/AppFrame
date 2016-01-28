package lx.af.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import lx.af.R;

/**
 * Created by liuxu on 15-5-8.
 * dialog with an edit box.
 */
public class EditDialog extends Dialog {

    private OnEditResult mResultListener;
    private TextView mTitle;
    private EditText mEditor;

    public static Builder builder(Context context) {
        return new Builder(context);
    }

    /**
     * construct dialog.
     * @param context the context.
     * @param title title for the dialog
     * @param defaultValue default value for the edit box.
     * @param hint hint for the edit box
     * @param inputType input type for the edit box.
     * @param listener callback for the result.
     */
    public EditDialog(Context context,
                      String title, String hint, String defaultValue, int inputType,
                      final OnEditResult listener) {
        super(context, R.style.dialog_custom);
        setContentView(R.layout.dlg_edit);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        mTitle = (TextView) findViewById(R.id.dlg_edit_title);
        mTitle.setText(title);
        mEditor = (EditText) findViewById(R.id.dlg_edit_editor);
        mEditor.setText(defaultValue);
        mEditor.setHint(hint);
        mEditor.setSelection(defaultValue == null ? 0 : defaultValue.length());
        if (inputType != 0) {
            mEditor.setInputType(inputType);
        }
        mResultListener = listener;

        findViewById(R.id.dlg_edit_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.dlg_edit_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mResultListener != null) {
                    mResultListener.onEditResult(mEditor.getText().toString());
                }
                dismiss();
            }
        });
    }

    /**
     * edit result callback.
     */
    public interface OnEditResult {

        /**
         * when confirm button is clicked, string in the edit box will be
         * returned as edit result in this callback.
         * @param result the edit result.
         */
        public void onEditResult(String result);
    }


    public static class Builder {

        private Context context;
        private String title;
        private String content;
        private String hint;
        private int inputType;
        private OnEditResult listener;

        public Builder(Context context) {
            this.context = context;
        }

        public EditDialog create() {
            return new EditDialog(context, title, hint, content, inputType, listener);
        }

        public Builder setOnResultListener(OnEditResult l) {
            listener = l;
            return this;
        }

        public Builder setTitle(int resId) {
            this.title = context.getString(resId);
            return this;
        }

        public Builder setDefaultValue(int resId) {
            this.content = context.getString(resId);
            return this;
        }

        public Builder setHint(int resId) {
            this.hint = context.getString(resId);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDefaultValue(String content) {
            this.content = content;
            return this;
        }

        public Builder setHint(String hint) {
            this.hint = hint;
            return this;
        }

        public Builder setInputType(int inputType) {
            this.inputType = inputType;
            return this;
        }
    }

}
