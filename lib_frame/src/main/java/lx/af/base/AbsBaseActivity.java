package lx.af.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.LinkedList;

import lx.af.R;
import lx.af.dialog.LoadingDialog;
import lx.af.utils.AlertUtils;
import lx.af.utils.ViewInject.ViewInjectUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * author: liuxu
 * date: 2015-12-06
 *
 * activity base
 */
public abstract class AbsBaseActivity extends FragmentActivity {

    public static final int FEATURE_DOUBLE_BACK_EXIT = 0x01 << 1;

    public String TAG;

    private LoadingDialog mLoadingDialog;
    private View mCContentView;  // custom content view
    private View mCActionBar;  // custom action bar

    private int mFeatures = 0x00;
    private long mDoubleBackTime = 0;
    private boolean mIsForeground = false;

    private final LinkedList<LifeCycleListener> mLifeCycleListeners = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TAG = this.getClass().getSimpleName();
        super.onCreate(savedInstanceState);
        for (LifeCycleListener listener : mLifeCycleListeners) {
            listener.onActivityCreated(this, savedInstanceState);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        for (LifeCycleListener listener : mLifeCycleListeners) {
            listener.onActivitySaveInstanceState(this, outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        for (LifeCycleListener listener : mLifeCycleListeners) {
            listener.onActivityRestoreInstanceState(this, savedInstanceState);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        for (LifeCycleListener listener : mLifeCycleListeners) {
            listener.onActivityStarted(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsForeground = true;
        for (LifeCycleListener listener : mLifeCycleListeners) {
            listener.onActivityResumed(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsForeground = false;
        for (LifeCycleListener listener : mLifeCycleListeners) {
            listener.onActivityPaused(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (LifeCycleListener listener : mLifeCycleListeners) {
            listener.onActivityStopped(this);
        }
    }

    @Override
    protected void onDestroy() {
        dismissLoadingDialog();
        super.onDestroy();
        for (LifeCycleListener listener : mLifeCycleListeners) {
            listener.onActivityDestroyed(this);
        }
        mLifeCycleListeners.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (LifeCycleListener listener : mLifeCycleListeners) {
            listener.onActivityResult(this, requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (isFeatureEnabled(FEATURE_DOUBLE_BACK_EXIT)) {
                long current = System.currentTimeMillis();
                long interval = current - mDoubleBackTime;
                if (interval > 2000) {
                    toastShort(R.string.toast_double_click_exit);
                    mDoubleBackTime = current;
                } else {
                    finish();
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v != null) {
            return v;
        }
        if (mCContentView != null) {
            v = mCContentView.findViewById(id);
        }
        return v;
    }

    @Override
    public void setContentView(int layoutResID) {
        View contentView = View.inflate(this, layoutResID, null);
        setContentViewInner(contentView, null);
    }

    @Override
    public void setContentView(View view) {
        setContentViewInner(view, null);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        setContentViewInner(view, params);
    }

    /**
     * enable a feature
     * @param feature one or a combine of below features:
     *                {@link #FEATURE_DOUBLE_BACK_EXIT}
     */
    public void enableFeature(int feature) {
        mFeatures |= feature;
    }

    /**
     * disable a feature
     * @param feature one or a combine of below features:
     *                {@link #FEATURE_DOUBLE_BACK_EXIT}
     */
    public void disableFeature(int feature) {
        mFeatures &= ~feature;
    }

    /**
     * check if the activity is running in foreground.
     * AKA, if the activity is in life cycle between onResume() and onPause().
     * @return true if running in foreground.
     */
    public boolean isForeground() {
        return mIsForeground;
    }

    /**
     * start activity with default intent option
     * @param cls activity class
     */
    public void startActivity(Class cls) {
        startActivity(new Intent(AbsBaseActivity.this, cls));
    }

    /**
     * get and convert view
     * @param id view id for findViewById() method
     * @param <T> subclass of View
     * @return the view
     */
    @SuppressWarnings("unchecked")
    public  <T extends View> T obtainView(int id) {
        return (T) findViewById(id);
    }

    public void addLifeCycleListener(LifeCycleListener listener) {
        if (mLifeCycleListeners.contains(listener)) {
            return;
        }
        mLifeCycleListeners.add(listener);
    }

    public void removeLifeCycleListener(LifeCycleListener listener) {
        mLifeCycleListeners.remove(listener);
    }

    // ======================================
    // about loading dialog and toast

    public void toastLong(String msg) {
        AlertUtils.toastLong(msg);
    }

    public void toastLong(int resId) {
        AlertUtils.toastLong(resId);
    }

    public void toastShort(String msg) {
        AlertUtils.toastShort(msg);
    }

    public void toastShort(int resId) {
        AlertUtils.toastShort(resId);
    }

    public void showLoadingDialog(int id) {
        showLoadingDialog(getString(id));
    }

    public void showLoadingDialog() {
        showLoadingDialog(null);
    }

    public void showLoadingDialog(String msg) {
        showLoadingDialog(msg, false);
    }

    public void showLoadingDialog(final String msg, boolean cancelable) {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            // loading dialog already fired, just change the message
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoadingDialog.setMessage(msg);
                }
            });
        } else {
            if (mLoadingDialog == null) {
                mLoadingDialog = new LoadingDialog(AbsBaseActivity.this, msg);
                mLoadingDialog.setCancelable(cancelable);
            }
            mLoadingDialog.show();
        }
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    // ======================================
    // about action bar

    protected ActionBarAdapter getActionBarAdapter() {
        return null;
    }

    protected View getActionBarView() {
        return mCActionBar;
    }

    private void setContentViewInner(View view, ViewGroup.LayoutParams params) {
        mCContentView = view;
        if (params == null) {
            params = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        }
        ActionBarAdapter adapter = getActionBarAdapter();
        if (adapter == null) {
            super.setContentView(view, params);
        } else {
            mCActionBar = adapter.getActionBarView(this);
            ActionBarAdapter.Type type = adapter.getActionBarType();
            type = type == null ? ActionBarAdapter.Type.NORMAL : type;
            switch (type) {
                case NORMAL: {
                    // use LinearLayout as content view
                    LinearLayout contentView = new LinearLayout(this);
                    contentView.setOrientation(LinearLayout.VERTICAL);
                    contentView.setDividerDrawable(
                            getResources().getDrawable(R.drawable.shape_action_bar_divider));
                    contentView.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                    contentView.addView(mCActionBar, MATCH_PARENT, WRAP_CONTENT);
                    contentView.addView(mCContentView, params);
                    super.setContentView(contentView);
                    break;
                }
                case OVERLAY: {
                    // use RelativeLayout as content view
                    RelativeLayout contentView = new RelativeLayout(this);
                    RelativeLayout.LayoutParams ap = new RelativeLayout.LayoutParams(
                            MATCH_PARENT, WRAP_CONTENT);
                    ap.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    contentView.addView(mCContentView, params);
                    contentView.addView(mCActionBar, ap);
                    super.setContentView(contentView);
                    break;
                }
            }
        }

        ViewInjectUtils.inject(AbsBaseActivity.class, this);
    }

    private boolean isFeatureEnabled(int feature) {
        return (mFeatures & feature) != 0;
    }

    // ======================================
    // interface

    public interface LifeCycleListener {
        void onActivityCreated(AbsBaseActivity activity, Bundle savedInstanceState);
        void onActivityStarted(AbsBaseActivity activity);
        void onActivityResumed(AbsBaseActivity activity);
        void onActivityPaused(AbsBaseActivity activity);
        void onActivityStopped(AbsBaseActivity activity);
        void onActivityDestroyed(AbsBaseActivity activity);
        void onActivityResult(AbsBaseActivity activity, int requestCode, int resultCode, Intent data);
        void onActivitySaveInstanceState(AbsBaseActivity activity, Bundle outState);
        void onActivityRestoreInstanceState(AbsBaseActivity activity, Bundle savedInstanceState);
    }

    public static class LifeCycleAdapter implements LifeCycleListener {
        public void onActivityCreated(AbsBaseActivity activity, Bundle savedInstanceState) {}
        public void onActivityStarted(AbsBaseActivity activity) {}
        public void onActivityResumed(AbsBaseActivity activity) {}
        public void onActivityPaused(AbsBaseActivity activity) {}
        public void onActivityStopped(AbsBaseActivity activity) {}
        public void onActivityDestroyed(AbsBaseActivity activity) {}
        public void onActivityResult(AbsBaseActivity activity, int requestCode, int resultCode, Intent data) {}
        public void onActivitySaveInstanceState(AbsBaseActivity activity, Bundle outState) {}
        public void onActivityRestoreInstanceState(AbsBaseActivity activity, Bundle savedInstanceState) {}
    }

}
