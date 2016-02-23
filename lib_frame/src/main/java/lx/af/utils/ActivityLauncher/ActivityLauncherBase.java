package lx.af.utils.ActivityLauncher;

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
public abstract class ActivityLauncherBase<T> {

    protected Activity mActivity;
    protected Fragment mFragment;

    protected int mInAnimResId;
    protected int mOutAnimResId;

    protected ActivityLauncherBase(Activity activity) {
        mActivity = activity;
    }

    protected ActivityLauncherBase(Fragment fragment) {
        mFragment = fragment;
    }

    protected Activity getActivity() {
        if (mActivity != null) {
            return mActivity;
        } else {
            return mFragment.getActivity();
        }
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
        if (mInAnimResId != 0 || mOutAnimResId != 0) {
            getActivity().overridePendingTransition(mInAnimResId, mOutAnimResId);
        }
    }

    protected Intent newIntent(Class<?> cls) {
        return new Intent(getActivity(), cls);
    }

    protected String getPackageName() {
        return getActivity().getApplicationContext().getPackageName();
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
