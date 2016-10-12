package lx.af.utils.ActivityLauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import lx.af.activity.ImageSelector.ImageSelectActivity;

import static lx.af.activity.ImageSelector.ImageSelectActivity.EXTRA_ACTIVITY_TITLE;
import static lx.af.activity.ImageSelector.ImageSelectActivity.EXTRA_DEFAULT_SELECTED_LIST;
import static lx.af.activity.ImageSelector.ImageSelectActivity.EXTRA_RESULT;
import static lx.af.activity.ImageSelector.ImageSelectActivity.EXTRA_SELECT_COUNT;
import static lx.af.activity.ImageSelector.ImageSelectActivity.EXTRA_SHOW_CAMERA;

/**
 * author: lx
 * date: 15-12-8
 */
public class ImageSelectorLauncher extends ActivityLauncherBase<ArrayList<String>> {

    private int mCount = 9;
    private boolean mShowCamera = true;
    private String mTitle;
    private ArrayList<String> mPreSelect;

    protected ImageSelectorLauncher(Activity activity) {
        super(activity);
    }

    protected ImageSelectorLauncher(Fragment fragment) {
        super(fragment);
    }

    public static ImageSelectorLauncher of(Context context) {
        return new ImageSelectorLauncher((Activity) context);
    }

    public static ImageSelectorLauncher of(Activity activity) {
        return new ImageSelectorLauncher(activity);
    }

    public static ImageSelectorLauncher of(Fragment fragment) {
        return new ImageSelectorLauncher(fragment);
    }

    public ImageSelectorLauncher title(String title) {
        mTitle = title;
        return this;
    }

    public ImageSelectorLauncher showCamera(boolean showCamera) {
        mShowCamera = showCamera;
        return this;
    }

    public ImageSelectorLauncher singleSelect() {
        return count(1);
    }

    public ImageSelectorLauncher count(int count) {
        mCount = count;
        return this;
    }

    public ImageSelectorLauncher preSelect(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (mPreSelect == null) {
                mPreSelect = new ArrayList<>(1);
            }
            mPreSelect.add(path);
        }
        return this;
    }

    public ImageSelectorLauncher preSelect(List<String> paths) {
        if (paths != null && paths.size() != 0) {
            if (paths instanceof ArrayList) {
                mPreSelect = (ArrayList<String>) paths;
            } else {
                mPreSelect = new ArrayList<>(paths.size());
                mPreSelect.addAll(paths);
            }
        }
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
        if (mTitle != null) {
            intent.putExtra(EXTRA_ACTIVITY_TITLE, mTitle);
        }
        return intent;
    }

    @Override
    protected int getDefaultRequestCode() {
        return RequestCode.IMAGE_FROM_IMAGE_SELECTOR;
    }

}
