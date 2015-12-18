package lx.af.utils.ActivityUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import lx.af.base.BaseActivity;
import lx.af.base.BaseFragment;

/**
 * author: lx
 * date: 15-12-17
 */
abstract class ActivityLauncherBase<T> {

    protected Activity mActivity;
    protected Fragment mFragment;

    protected ActivityLauncherBase(Activity activity) {
        mActivity = activity;
    }

    protected ActivityLauncherBase(Fragment fragment) {
        mFragment = fragment;
    }

    protected abstract T extractResult(int resultCode, Intent data);

    protected abstract void onRestoreInstanceState(Bundle savedInstanceState);

    protected abstract void onSaveInstanceState(Bundle outState);

    protected abstract int getDefaultRequestCode();

    public abstract Intent createIntent();

    public void start(int requestCode) {
        start(requestCode, null);
    }

    public void start(ActivityResultCallback<T> c) {
        start(getDefaultRequestCode(), c);
    }

    public void start(int requestCode, ActivityResultCallback<T> c) {
        Intent intent = createIntent();
        if (c != null) {
            ResultHandler handler;
            if (mActivity != null && mActivity instanceof BaseActivity) {
                handler = new ResultHandler((BaseActivity) mActivity, intent, requestCode, c);
            } else if (mFragment != null && mFragment instanceof BaseFragment) {
                handler = new ResultHandler((BaseFragment) mFragment, intent, requestCode, c);
            } else {
                throw new IllegalArgumentException(
                        "ActivityResultCallback only support BaseActivity and BaseFragment");
            }
            handler.start();
        } else {
            if (mActivity != null) {
                mActivity.startActivityForResult(intent, requestCode);
            } else {
                mFragment.startActivityForResult(intent, requestCode);
            }
        }
    }

    protected Intent newIntent(Class<?> cls) {
        if (mActivity != null) {
            return new Intent(mActivity, cls);
        } else {
            return new Intent(mFragment.getActivity(), cls);
        }
    }

    protected String getPackageName() {
        Activity activity;
        if (mActivity != null) {
            activity = mActivity;
        } else {
            activity = mFragment.getActivity();
        }
        return activity.getApplicationContext().getPackageName();
    }

    private class ResultHandler extends ActivityResultHandler<T> {

        public ResultHandler(BaseActivity activity, Intent intent,
                             int requestCode, ActivityResultCallback<T> c) {
            super(activity, intent, requestCode, c);
        }

        public ResultHandler(BaseFragment fragment, Intent intent,
                             int requestCode, ActivityResultCallback<T> c) {
            super(fragment, intent, requestCode, c);
        }

        @Override
        protected T extractResult(int resultCode, Intent data) {
            return ActivityLauncherBase.this.extractResult(resultCode, data);
        }

        @Override
        public void onActivitySaveInstanceState(BaseActivity activity, Bundle outState) {
            onSaveInstanceState(outState);
        }

        @Override
        public void onActivityRestoreInstanceState(BaseActivity activity, Bundle savedInstanceState) {
            onRestoreInstanceState(savedInstanceState);
        }

        @Override
        public void onFragmentSaveInstanceState(BaseFragment fragment, Bundle outState) {
            onSaveInstanceState(outState);
        }

        @Override
        public void onFragmentViewStateRestored(BaseFragment fragment, Bundle savedInstanceState) {
            onRestoreInstanceState(savedInstanceState);
        }
    }

}
