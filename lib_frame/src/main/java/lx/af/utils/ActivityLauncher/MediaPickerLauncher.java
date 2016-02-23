package lx.af.utils.ActivityLauncher;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

/**
 * author: lx
 * date: 15-12-16
 */
public class MediaPickerLauncher extends ActivityLauncherBase<Uri> {

    private String mMimeType;

    protected MediaPickerLauncher(Activity activity) {
        super(activity);
    }

    protected MediaPickerLauncher(Fragment fragment) {
        super(fragment);
    }

    public static MediaPickerLauncher of(Activity activity) {
        return new MediaPickerLauncher(activity);
    }

    public static MediaPickerLauncher of(Fragment fragment) {
        return new MediaPickerLauncher(fragment);
    }


    public MediaPickerLauncher mimeType(String mimeType) {
        mMimeType = mimeType;
        return this;
    }

    public MediaPickerLauncher pickImage() {
        return mimeType("image/*");
    }

    public MediaPickerLauncher pickVideo() {
        return mimeType("video/*");
    }

    public MediaPickerLauncher pickAudio() {
        return mimeType("audio/*");
    }

    @Override
    protected Uri extractResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            return data == null ? null : data.getData();
        } else {
            return null;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public Intent createIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mMimeType);
        return intent;
    }

    @Override
    protected int getDefaultRequestCode() {
        return RequestCode.MEDIA_PICKER;
    }

}
