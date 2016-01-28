package lx.af.utils.ActivityUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import lx.af.base.AbsBaseActivity;
import lx.af.base.AbsBaseFragment;

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
            if (mActivity != null && mActivity instanceof AbsBaseActivity) {
                handler = new ResultHandler((AbsBaseActivity) mActivity, intent, requestCode, c);
            } else if (mFragment != null && mFragment instanceof AbsBaseFragment) {
                handler = new ResultHandler((AbsBaseFragment) mFragment, intent, requestCode, c);
            } else {
                throw new IllegalArgumentException(
                        "ActivityResultCallback only support AbsBaseActivity and AbsBaseFragment");
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

        public ResultHandler(AbsBaseActivity activity, Intent intent,
                             int requestCode, ActivityResultCallback<T> c) {
            super(activity, intent, requestCode, c);
        }

        public ResultHandler(AbsBaseFragment fragment, Intent intent,
                             int requestCode, ActivityResultCallback<T> c) {
            super(fragment, intent, requestCode, c);
        }

        @Override
        protected T extractResult(int resultCode, Intent data) {
            return ActivityLauncherBase.this.extractResult(resultCode, data);
        }

        @Override
        public void onActivitySaveInstanceState(AbsBaseActivity activity, Bundle outState) {
            onSaveInstanceState(outState);
        }

        @Override
        public void onActivityRestoreInstanceState(AbsBaseActivity activity, Bundle savedInstanceState) {
            onRestoreInstanceState(savedInstanceState);
        }

        @Override
        public void onFragmentSaveInstanceState(AbsBaseFragment fragment, Bundle outState) {
            onSaveInstanceState(outState);
        }

        @Override
        public void onFragmentViewStateRestored(AbsBaseFragment fragment, Bundle savedInstanceState) {
            onRestoreInstanceState(savedInstanceState);
        }
    }

}
