package lx.af.utils.ActivityUtils;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import lx.af.base.BaseActivity;
import lx.af.base.BaseFragment;

/**
 * author: lx
 * date: 15-12-6
 */
abstract class ActivityResultHandler<T> implements
    BaseActivity.LifeCycleListener,
    BaseFragment.LifeCycleListener {

    private BaseActivity mActivity;
    private BaseFragment mFragment;

    private Intent mIntent;
    private int mRequestCode;
    private ActivityResultCallback<T> mCallback;

    public ActivityResultHandler(BaseActivity activity, Intent intent,
                                 int requestCode, ActivityResultCallback<T> c) {
        this.mActivity = activity;
        this.mIntent = intent;
        this.mRequestCode = requestCode;
        this.mCallback = c;
    }

    public ActivityResultHandler(BaseFragment fragment, Intent intent,
                                 int requestCode, ActivityResultCallback<T> c) {
        this.mFragment = fragment;
        this.mIntent = intent;
        this.mRequestCode = requestCode;
        this.mCallback = c;
    }

    void start() {
        if (mActivity != null) {
            mActivity.addLifeCycleListener(this);
            mActivity.startActivityForResult(mIntent, mRequestCode);
        } else {
            mFragment.addLifeCycleListener(this);
            mFragment.startActivityForResult(mIntent, mRequestCode);
        }
    }

    protected abstract T extractResult(@NonNull Intent data);

    @Override
    public void onActivityResult(BaseActivity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == mRequestCode) {
            activity.removeLifeCycleListener(this);
        } else {
            return;
        }
        if (resultCode != Activity.RESULT_OK || data == null) {
            return;
        }
        T result = extractResult(data);
        if (result != null) {
            mCallback.onActivityResult(requestCode, result);
        }
    }

    @Override
    public void onFragmentActivityResult(BaseFragment fragment, int requestCode, int resultCode, Intent data) {
        if (requestCode == mRequestCode) {
            fragment.removeLifeCycleListener(this);
        } else {
            return;
        }
        if (resultCode != Activity.RESULT_OK || data == null) {
            return;
        }
        T result = extractResult(data);
        if (result != null) {
            mCallback.onActivityResult(requestCode, result);
        }
    }

    // ====================================

    @Override
    public void onActivityCreated(BaseActivity activity) {
    }

    @Override
    public void onActivityStarted(BaseActivity activity) {
    }

    @Override
    public void onActivityResumed(BaseActivity activity) {
    }

    @Override
    public void onActivityPaused(BaseActivity activity) {

    }

    @Override
    public void onActivityStopped(BaseActivity activity) {
    }

    @Override
    public void onActivityDestroyed(BaseActivity activity) {
    }

    @Override
    public void onFragmentCreate(BaseFragment fragment) {
    }

    @Override
    public void onFragmentResume(BaseFragment fragment) {
    }

    @Override
    public void onFragmentPause(BaseFragment fragment) {
    }

    @Override
    public void onFragmentDestroy(BaseFragment fragment) {
    }
}
