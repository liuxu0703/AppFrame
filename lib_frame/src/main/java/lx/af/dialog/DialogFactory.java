package lx.af.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;

import lx.af.R;
import lx.af.utils.ResourceUtils;

/**
 * author: lx
 * date: 16-4-9
 */
public final class DialogFactory {

    private static final String TAG = "DialogFactory";

    private DialogFactory() {}

    public static Dialog showMessageDialog(Context context, String message) {
        Dialog dlg = new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create();
        try {
            dlg.show();
        } catch (WindowManager.BadTokenException e) {
            Log.w(TAG, "show dialog fail", e);
        }
        return dlg;
    }

    public static Dialog showMessageDialog(Context context, int stringId) {
        return showMessageDialog(context, ResourceUtils.getString(stringId));
    }

    public static Dialog showConfirmDialog(Context context, String message, final Runnable action) {
        Dialog dlg = new AlertDialog.Builder(context)
                .setMessage(message)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        action.run();
                    }
                }).create();
        try {
            dlg.show();
        } catch (WindowManager.BadTokenException e) {
            Log.w(TAG, "show dialog fail", e);
        }
        return dlg;
    }

    public static Dialog showConfirmDialog(Context context, String message, final Runnable okAction, final Runnable cancelAction) {
        Dialog dlg = new AlertDialog.Builder(context)
                .setMessage(message)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelAction.run();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        okAction.run();
                    }
                }).create();
        try {
            dlg.show();
        } catch (WindowManager.BadTokenException e) {
            Log.w(TAG, "show dialog fail", e);
        }
        return dlg;
    }

    public static Dialog showConfirmDialog(Context context, int stringId, final Runnable action) {
        return showConfirmDialog(context, ResourceUtils.getString(stringId), action);
    }

    public static Dialog showDeleteConfirmDialog(Context context, final Runnable deleteAction) {
        Dialog dlg = new AlertDialog.Builder(context)
                .setTitle(R.string.dlg_delete_title)
                .setMessage(R.string.dlg_delete_message)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAction.run();
                    }
                }).create();
        try {
            dlg.show();
        } catch (WindowManager.BadTokenException e) {
            Log.w(TAG, "show dialog fail", e);
        }
        return dlg;
    }

    public static LoadingDialog showLoadingDialog(Context context, boolean cancelable) {
        LoadingDialog dlg = new LoadingDialog(context, null);
        dlg.setCancelable(cancelable);
        try {
            dlg.show();
        } catch (WindowManager.BadTokenException e) {
            Log.w(TAG, "show dialog fail", e);
        }
        return dlg;
    }

}
