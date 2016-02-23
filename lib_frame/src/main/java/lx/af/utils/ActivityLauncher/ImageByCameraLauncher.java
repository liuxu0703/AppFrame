package lx.af.utils.ActivityLauncher;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lx.af.utils.PathUtils;

/**
 * author: lx
 * date: 15-12-16
 */
public class ImageByCameraLauncher extends ActivityLauncherBase<Uri> {

    private Uri mOutputUri;

    protected ImageByCameraLauncher(Activity activity) {
        super(activity);
    }

    protected ImageByCameraLauncher(Fragment fragment) {
        super(fragment);
    }


    public static ImageByCameraLauncher of(Activity activity) {
        return new ImageByCameraLauncher(activity);
    }

    public static ImageByCameraLauncher of(Fragment fragment) {
        return new ImageByCameraLauncher(fragment);
    }

    public ImageByCameraLauncher output(Uri uri) {
        mOutputUri = uri;
        return this;
    }

    public ImageByCameraLauncher output(String path) {
        mOutputUri = Uri.parse("file://" + path);
        return this;
    }

    @Override
    protected Uri extractResult(int resultCode, Intent data) {
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
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
            File file = new File(PathUtils.getExtPublicDCIM(), "img_" + df.format(new Date()) + ".jpg");
            mOutputUri = Uri.fromFile(file);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputUri);
        return intent;
    }

}
