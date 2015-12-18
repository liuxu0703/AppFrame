package lx.af.utils.ActivityUtils;

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
public class ImageByGallery extends ActivityLauncherBase<Uri> {

    protected ImageByGallery(Activity activity) {
        super(activity);
    }

    protected ImageByGallery(Fragment fragment) {
        super(fragment);
    }

    public static ImageByGallery of(Activity activity) {
        return new ImageByGallery(activity);
    }

    public static ImageByGallery of(Fragment fragment) {
        return new ImageByGallery(fragment);
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
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        return intent;
    }

    @Override
    protected int getDefaultRequestCode() {
        return RequestCode.IMAGE_FROM_GALLERY;
    }

}
