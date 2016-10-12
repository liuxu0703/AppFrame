package lx.af.utils.ActivityLauncher;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lx.af.utils.PathUtils;

/**
 * author: lx
 * date: 16-10-10
 */
public class ImageByCameraHelper {

    private static final int REQUEST_CODE = 6532;
    private static final String SAVED_INSTANCE_KEY_OUTPUT_PATH = "image_by_camera_helper_output_uri";

    private String mOutputPath;

    public ImageByCameraHelper() {
    }

    public void startCamera(Activity activity) {
        activity.startActivityForResult(createStartIntent(null), REQUEST_CODE);
    }

    public void startCamera(Fragment fragment) {
        fragment.startActivityForResult(createStartIntent(null), REQUEST_CODE);
    }

    public void startCamera(Activity activity, String outputPath) {
        activity.startActivityForResult(createStartIntent(outputPath), REQUEST_CODE);
    }

    public void startCamera(Fragment fragment, String outputPath) {
        fragment.startActivityForResult(createStartIntent(outputPath), REQUEST_CODE);
    }

    public String getOutputPath() {
        return mOutputPath;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mOutputPath = savedInstanceState.getString(SAVED_INSTANCE_KEY_OUTPUT_PATH);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        if (!TextUtils.isEmpty(mOutputPath)) {
            outState.putString(SAVED_INSTANCE_KEY_OUTPUT_PATH, mOutputPath);
        }
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE || resultCode != Activity.RESULT_OK) {
            return false;
        } else {
            File file = new File(mOutputPath);
            return file.exists();
        }
    }

    private Intent createStartIntent(String outputPath) {
        mOutputPath = outputPath;
        if (mOutputPath == null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
            File file = new File(PathUtils.getExtPublicDCIM(), "img_" + df.format(new Date()) + ".jpg");
            mOutputPath = file.getPath();
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse("file://" + mOutputPath));
        return intent;
    }

}
