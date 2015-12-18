package lx.af.utils.ActivityUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.util.ArrayList;

import lx.af.activity.ImageSelector.ImageSelectActivity;

import static lx.af.activity.ImageSelector.ImageSelectActivity.EXTRA_DEFAULT_SELECTED_LIST;
import static lx.af.activity.ImageSelector.ImageSelectActivity.EXTRA_RESULT;
import static lx.af.activity.ImageSelector.ImageSelectActivity.EXTRA_SELECT_COUNT;
import static lx.af.activity.ImageSelector.ImageSelectActivity.EXTRA_SHOW_CAMERA;

/**
 * author: lx
 * date: 15-12-8
 */
public class ImageSelector extends ActivityLauncherBase<ArrayList<String>> {

    private int mCount = 9;
    private boolean mShowCamera = true;
    private ArrayList<String> mPreSelect;

    protected ImageSelector(Activity activity) {
        super(activity);
    }

    protected ImageSelector(Fragment fragment) {
        super(fragment);
    }

    public static ImageSelector of(Activity activity) {
        return new ImageSelector(activity);
    }

    public static ImageSelector of(Fragment fragment) {
        return new ImageSelector(fragment);
    }

    public ImageSelector showCamera(boolean showCamera) {
        mShowCamera = showCamera;
        return this;
    }

    public ImageSelector singleSelect() {
        return count(1);
    }

    public ImageSelector count(int count) {
        mCount = count;
        return this;
    }

    public ImageSelector preSelect(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (mPreSelect == null) {
                mPreSelect = new ArrayList<>(1);
            }
            mPreSelect.add(path);
        }
        return this;
    }

    public ImageSelector preSelect(ArrayList<String> paths) {
        mPreSelect = paths;
        return this;
    }

    @Override
    protected ArrayList<String> extractResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<String> list = data.getStringArrayListExtra(EXTRA_RESULT);
            if (list != null && list.size() != 0) {
                return list;
            }
        }
        return null;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mCount = savedInstanceState.getInt("image_selector_count");
        mShowCamera = savedInstanceState.getBoolean("image_selector_show_camera");
        mPreSelect = savedInstanceState.getStringArrayList("image_selector_pre_select_list");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("image_selector_count", mCount);
        outState.putBoolean("image_selector_show_camera", mShowCamera);
        outState.putStringArrayList("image_selector_pre_select_list", mPreSelect);
    }

    @Override
    public Intent createIntent() {
        Intent intent = newIntent(ImageSelectActivity.class);
        intent.putExtra(EXTRA_SHOW_CAMERA, mShowCamera);
        intent.putExtra(EXTRA_SELECT_COUNT, mCount);
        if (mPreSelect != null) {
            intent.putExtra(EXTRA_DEFAULT_SELECTED_LIST, mPreSelect);
        }
        return intent;
    }

    @Override
    protected int getDefaultRequestCode() {
        return RequestCode.IMAGE_FROM_IMAGE_SELECTOR;
    }

}
