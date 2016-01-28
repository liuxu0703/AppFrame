package lx.af.utils.ActivityUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * author: lx
 * date: 15-12-16
 */
public class ContentPicker extends ActivityLauncherBase<Uri> {

    private String mMimeType;
    private boolean mIsLocalOnly = true;
    private boolean mIsOpenable = true;

    protected ContentPicker(Activity activity) {
        super(activity);
    }

    protected ContentPicker(Fragment fragment) {
        super(fragment);
    }

    public static ContentPicker of(Activity activity) {
        return new ContentPicker(activity);
    }

    public static ContentPicker of(Fragment fragment) {
        return new ContentPicker(fragment);
    }

    public ContentPicker mimeType(String mimeType) {
        mMimeType = mimeType;
        return this;
    }

    public ContentPicker pickImage() {
        return mimeType("image/*");
    }

    public ContentPicker pickVideo() {
        return mimeType("video/*");
    }

    public ContentPicker pickAudio() {
        return mimeType("audio/*");
    }

    public ContentPicker localOnly(boolean isLocalOnly) {
        mIsLocalOnly = isLocalOnly;
        return this;
    }

    public ContentPicker openable(boolean isOpenable) {
        mIsOpenable = isOpenable;
        return this;
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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType(mMimeType);
        if (mIsLocalOnly) {
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        }
        if (mIsOpenable) {
            intent.putExtra(Intent.CATEGORY_OPENABLE, true);
        }
        return intent;
    }

    @Override
    protected int getDefaultRequestCode() {
        return RequestCode.CONTENT_PICKER;
    }

}
