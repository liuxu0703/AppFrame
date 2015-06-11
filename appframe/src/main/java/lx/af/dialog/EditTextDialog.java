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
public class EditTextDialog extends Dialog {

    private OnEditResult mResultListener;
    private TextView mTitle;
    private EditText mEditor;

    /**
     * construct dialog.
     * @param context the context.
     * @param defaultValue default value for the edit box.
     * @param listener callback for the result.
     */
    public EditTextDialog(Context context, String defaultValue, final OnEditResult listener) {
        super(context, R.style.dialog_custom);
        setContentView(R.layout.dlg_edit);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        mTitle = (TextView) findViewById(R.id.dlg_edit_title);
        mEditor = (EditText) findViewById(R.id.dlg_edit_editor);
        mEditor.setText(defaultValue);
        mEditor.setSelection(defaultValue == null ? 0 : defaultValue.length());
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
     * limit input type for the edit box.
     * see android.view.inputmethod.EditorInfo for detail.
     * @param inputType input type for the edit box.
     */
    public void setInputType(int inputType) {
        mEditor.setInputType(inputType);
    }

    /**
     * set hint text for the edit box
     * @param hint the hint.
     */
    public void setHint(String hint) {
        mEditor.setHint(hint);
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

}
