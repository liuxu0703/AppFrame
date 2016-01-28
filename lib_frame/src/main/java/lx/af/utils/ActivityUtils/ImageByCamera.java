package lx.af.utils.ActivityUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import java.io.File;

import lx.af.utils.log.Log;

/**
 * author: lx
 * date: 15-12-16
 */
public class ImageByCamera extends ActivityLauncherBase<Uri> {

    private Uri mOutputUri;

    protected ImageByCamera(Activity activity) {
        super(activity);
    }

    protected ImageByCamera(Fragment fragment) {
        super(fragment);
    }


    public static ImageByCamera of(Activity activity) {
        return new ImageByCamera(activity);
    }

    public static ImageByCamera of(Fragment fragment) {
        return new ImageByCamera(fragment);
    }

    public ImageByCamera output(Uri uri) {
        mOutputUri = uri;
        return this;
    }

    public ImageByCamera output(String path) {
        mOutputUri = Uri.parse("file://" + path);
        return this;
    }

    @Override
    protected Uri extractResult(int resultCode, Intent data) {
        Log.d("liuxu", "111 ImageByCamera, onActivityResult, result=" + resultCode + ", data=" + data+", output="+mOutputUri);
        if (resultCode == Activity.RESULT_OK) {
            return mOutputUri;
        } else {
            return null;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mOutputUri = savedInstanceState.getParcelable("image_by_camera_output_uri");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("image_by_camera_output_uri", mOutputUri);
    }

    @Override
    protected int getDefaultRequestCode() {
        return RequestCode.IMAGE_FROM_CAMERA;
    }

    @Override
    public Intent createIntent() {
        if (mOutputUri == null) {
            File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File file = new File(dcim, getPackageName() + "_" + System.currentTimeMillis());
            mOutputUri = Uri.fromFile(file);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputUri);
        Log.d("liuxu", "111 ImageByCamera, createIntent, output=" + mOutputUri);
        return intent;
    }

}
