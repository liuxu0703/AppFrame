package lx.af.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import lx.af.R;
import lx.af.utils.ResourceUtils;

/**
 * author: lx
 * date: 16-4-9
 */
public final class DialogFactory {

    private DialogFactory() {}

    public static void showMessageDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create().show();
    }

    public static void showMessageDialog(Context context, int stringId) {
        showMessageDialog(context, ResourceUtils.getString(stringId));
    }

    public static void showConfirmDialog(Context context, String message, final Runnable action) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        action.run();
                    }
                }).create().show();
    }

    public static void showConfirmDialog(Context context, int stringId, final Runnable action) {
        showConfirmDialog(context, ResourceUtils.getString(stringId), action);
    }

    public static void showDeleteConfirmDialog(Context context, final Runnable deleteAction) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.dlg_delete_title)
                .setMessage(R.string.dlg_delete_message)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAction.run();
                    }
                }).create().show();
    }

}
