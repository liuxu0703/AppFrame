package lx.af.utils.ActivityLauncher;

import android.content.Intent;
import android.os.Bundle;

import lx.af.base.AbsBaseActivity;
import lx.af.base.AbsBaseFragment;

/**
 * author: lx
 * date: 15-12-6
 */
public abstract class ActivityResultHandler<T> implements
    AbsBaseActivity.LifeCycleListener,
    AbsBaseFragment.LifeCycleListener {

    private AbsBaseActivity mActivity;
    private AbsBaseFragment mFragment;

    private Intent mIntent;
    private int mRequestCode;
    private ActivityResultCallback<T> mCallback;

    public ActivityResultHandler(AbsBaseActivity activity, Intent intent,
                                 int requestCode, ActivityResultCallback<T> c) {
        this.mActivity = activity;
        this.mIntent = intent;
        this.mRequestCode = requestCode;
        this.mCallback = c;
    }

    public ActivityResultHandler(AbsBaseFragment fragment, Intent intent,
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
    public void onActivityResult(AbsBaseActivity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == mRequestCode) {
            activity.removeLifeCycleListener(this);
        } else {
            return;
        }
        T result = extractResult(resultCode, data);
        if (result != null) {
            mCallback.onActivityResult(resultCode, result);
        }
    }

    @Override
    public void onFragmentActivityResult(AbsBaseFragment fragment, int requestCode, int resultCode, Intent data) {
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
    public void onActivityCreated(AbsBaseActivity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(AbsBaseActivity activity) {
    }

    @Override
    public void onActivityResumed(AbsBaseActivity activity) {
    }

    @Override
    public void onActivityPaused(AbsBaseActivity activity) {

    }

    @Override
    public void onActivityStopped(AbsBaseActivity activity) {
    }

    @Override
    public void onActivityDestroyed(AbsBaseActivity activity) {
    }

    @Override
    public void onFragmentCreate(Bundle savedInstanceState, AbsBaseFragment fragment) {
    }

    @Override
    public void onFragmentResume(AbsBaseFragment fragment) {
    }

    @Override
    public void onFragmentPause(AbsBaseFragment fragment) {
    }

    @Override
    public void onFragmentDestroy(AbsBaseFragment fragment) {
    }
}
