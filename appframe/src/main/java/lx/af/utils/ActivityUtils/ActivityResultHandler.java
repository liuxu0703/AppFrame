package lx.af.utils.ActivityUtils;

import android.content.Intent;
import android.os.Bundle;

import lx.af.base.BaseActivity;
import lx.af.base.BaseFragment;
import lx.af.utils.log.Log;

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

    protected abstract T extractResult(int resultCode, Intent data);

    @Override
    public void onActivityResult(BaseActivity activity, int requestCode, int resultCode, Intent data) {
        Log.d("liuxu", "111 ResultHandler, onActivityResult, request="+requestCode+", result="+resultCode+", data="+data);
        if (requestCode == mRequestCode) {
            activity.removeLifeCycleListener(this);
        } else {
            return;
        }
        T result = extractResult(resultCode, data);
        Log.d("liuxu", "111 ResultHandler, onActivityResult, result=" + resultCode + ", ret=" + result);
        if (result != null) {
            mCallback.onActivityResult(resultCode, result);
        }
    }

    @Override
    public void onFragmentActivityResult(BaseFragment fragment, int requestCode, int resultCode, Intent data) {
        if (requestCode == mRequestCode) {
            fragment.removeLifeCycleListener(this);
        } else {
            return;
        }
        T result = extractResult(resultCode, data);
        if (result != null) {
            mCallback.onActivityResult(resultCode, result);
        }
    }

    // ====================================


    @Override
    public void onActivityCreated(BaseActivity activity, Bundle savedInstanceState) {
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
    public void onFragmentCreate(Bundle savedInstanceState, BaseFragment fragment) {
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
