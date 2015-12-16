package lx.af.utils.ActivityUtils;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.util.ArrayList;

import lx.af.activity.ImageSelectActivity;
import lx.af.base.BaseActivity;
import lx.af.base.BaseFragment;

import static lx.af.activity.ImageSelectActivity.EXTRA_DEFAULT_SELECTED_LIST;
import static lx.af.activity.ImageSelectActivity.EXTRA_RESULT;
import static lx.af.activity.ImageSelectActivity.EXTRA_SELECT_COUNT;
import static lx.af.activity.ImageSelectActivity.EXTRA_SHOW_CAMERA;

/**
 * author: lx
 * date: 15-12-8
 */
public class ImageSelector {

    private Activity mActivity;
    private Fragment mFragment;

    private boolean mShowCamera = true;
    private int mCount = 9;
    private ArrayList<String> mPreSelect;

    private int mEnterAnimId;
    private int mExitAnimId;

    public static ImageSelector of(Activity activity) {
        ImageSelector select = new ImageSelector();
        select.mActivity = activity;
        return select;
    }

    public static ImageSelector of(Fragment fragment) {
        ImageSelector select = new ImageSelector();
        select.mFragment = fragment;
        return select;
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

    public ImageSelector overridePendingTransition(int enterAnim, int exitAnim) {
        mEnterAnimId = enterAnim;
        mExitAnimId = exitAnim;
        return this;
    }

    public Intent createIntent() {
        Intent intent;
        if (mActivity != null) {
            intent = new Intent(mActivity, ImageSelectActivity.class);
        } else {
            intent = new Intent(mFragment.getActivity(), ImageSelectActivity.class);
        }
        intent.putExtra(EXTRA_SHOW_CAMERA, mShowCamera);
        intent.putExtra(EXTRA_SELECT_COUNT, mCount);
        if (mPreSelect != null) {
            intent.putExtra(EXTRA_DEFAULT_SELECTED_LIST, mPreSelect);
        }
        return intent;
    }

    public void start(int requestCode) {
        start(requestCode, null);
    }

    public void start(ActivityResultCallback<ArrayList<String>> c) {
        start(RequestCode.IMAGE_MULTI_SELECT, c);
    }

    public void start(int requestCode, ActivityResultCallback<ArrayList<String>> c) {
        if (requestCode == 0) {
            requestCode = RequestCode.IMAGE_MULTI_SELECT;
        }
        Intent intent = createIntent();
        if (c != null) {
            ResultHandler handler;
            if (mActivity != null && mActivity instanceof BaseActivity) {
                handler = new ResultHandler((BaseActivity) mActivity, intent, requestCode, c);
                handler.start();
            } else if (mFragment != null && mFragment instanceof BaseFragment) {
                handler = new ResultHandler((BaseFragment) mFragment, intent, requestCode, c);
                handler.start();
            } else {
                throw new IllegalArgumentException(
                        "ActivityResultCallback only support BaseActivity and BaseFragment");
            }
        } else {
            Activity activity;
            if (mActivity != null) {
                activity = mActivity;
                mActivity.startActivityForResult(intent, requestCode);
            } else {
                activity = mFragment.getActivity();
                mFragment.startActivityForResult(intent, requestCode);
            }
            if (mEnterAnimId != 0 && mExitAnimId != 0) {
                activity.overridePendingTransition(mEnterAnimId, mExitAnimId);
            }
        }
    }


    private static class ResultHandler extends ActivityResultHandler<ArrayList<String>> {

        public ResultHandler(BaseActivity activity, Intent intent,
                             int requestCode, ActivityResultCallback<ArrayList<String>> c) {
            super(activity, intent, requestCode, c);
        }

        public ResultHandler(BaseFragment fragment, Intent intent,
                             int requestCode, ActivityResultCallback<ArrayList<String>> c) {
            super(fragment, intent, requestCode, c);
        }

        @Override
        protected ArrayList<String> extractResult(@NonNull Intent data) {
            ArrayList<String> list = data.getStringArrayListExtra(EXTRA_RESULT);
            if (list != null && list.size() != 0) {
                return list;
            }
            return null;
        }
    }

}
