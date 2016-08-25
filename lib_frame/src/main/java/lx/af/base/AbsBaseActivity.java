package lx.af.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.LinkedList;

import lx.af.R;
import lx.af.dialog.LoadingDialog;
import lx.af.utils.AlertUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * author: liuxu
 * date: 2015-12-06
 *
 * activity base
 */
public abstract class AbsBaseActivity extends AppCompatActivity {

    public static final int FEATURE_DOUBLE_BACK_EXIT = 0x01 << 1;
    public static final int FEATURE_DISABLE_KEY_BACK = 0x01 << 2;

    public String TAG;

    private LoadingDialog mLoadingDialog;
    private View mCContentView;  // custom content view
    private View mCActionBar;  // custom action bar
    private View mCActionBarDivider;  // custom action bar divider;

    private int mFeatures = 0x00;
    private long mDoubleBackTime = 0;
    private boolean mIsForeground = false;

    private final LinkedList<LifeCycleListener> mLifeCycleListeners = new LinkedList<>();
    private final LinkedList<LifeCycleListener> mLifeCycleListenersCopy = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TAG = this.getClass().getSimpleName();
        super.onCreate(savedInstanceState);
        // iterate copied listener list to prevent potential ConcurrentModificationException
        mLifeCycleListenersCopy.addAll(mLifeCycleListeners);
        for (LifeCycleListener listener : mLifeCycleListenersCopy) {
            listener.onActivityCreated(this, savedInstanceState);
        }
        mLifeCycleListenersCopy.clear();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // iterate copied listener list to prevent potential ConcurrentModificationException
        mLifeCycleListenersCopy.addAll(mLifeCycleListeners);
        for (LifeCycleListener listener : mLifeCycleListenersCopy) {
            listener.onActivitySaveInstanceState(this, outState);
        }
        mLifeCycleListenersCopy.clear();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // iterate copied listener list to prevent potential ConcurrentModificationException
        mLifeCycleListenersCopy.addAll(mLifeCycleListeners);
        for (LifeCycleListener listener : mLifeCycleListenersCopy) {
            listener.onActivityRestoreInstanceState(this, savedInstanceState);
        }
        mLifeCycleListenersCopy.clear();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // iterate copied listener list to prevent potential ConcurrentModificationException
        mLifeCycleListenersCopy.addAll(mLifeCycleListeners);
        for (LifeCycleListener listener : mLifeCycleListenersCopy) {
            listener.onActivityStarted(this);
        }
        mLifeCycleListenersCopy.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsForeground = true;
        // iterate copied listener list to prevent potential ConcurrentModificationException
        mLifeCycleListenersCopy.addAll(mLifeCycleListeners);
        for (LifeCycleListener listener : mLifeCycleListenersCopy) {
            listener.onActivityResumed(this);
        }
        mLifeCycleListenersCopy.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsForeground = false;
        // iterate copied listener list to prevent potential ConcurrentModificationException
        mLifeCycleListenersCopy.addAll(mLifeCycleListeners);
        for (LifeCycleListener listener : mLifeCycleListenersCopy) {
            listener.onActivityPaused(this);
        }
        mLifeCycleListenersCopy.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // iterate copied listener list to prevent potential ConcurrentModificationException
        mLifeCycleListenersCopy.addAll(mLifeCycleListeners);
        for (LifeCycleListener listener : mLifeCycleListenersCopy) {
            listener.onActivityStopped(this);
        }
        mLifeCycleListenersCopy.clear();
    }

    @Override
    protected void onDestroy() {
        dismissLoadingDialog();
        super.onDestroy();
        // iterate copied listener list to prevent potential ConcurrentModificationException
        mLifeCycleListenersCopy.addAll(mLifeCycleListeners);
        for (LifeCycleListener listener : mLifeCycleListenersCopy) {
            listener.onActivityDestroyed(this);
        }
        mLifeCycleListeners.clear();
        mLifeCycleListenersCopy.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // iterate copied listener list to prevent potential ConcurrentModificationException
        mLifeCycleListenersCopy.addAll(mLifeCycleListeners);
        for (LifeCycleListener listener : mLifeCycleListenersCopy) {
            listener.onActivityResult(this, requestCode, resultCode, data);
        }
        mLifeCycleListenersCopy.clear();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (isFeatureEnabled(FEATURE_DISABLE_KEY_BACK)) {
                return true;
            } else if (isFeatureEnabled(FEATURE_DOUBLE_BACK_EXIT)) {
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
        if (!mIsForeground) {
            // avoid BadTokenException
            return;
        }
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

    protected View getActionBarDivider() {
        return mCActionBarDivider;
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
            if (mCActionBar == null) {
                super.setContentView(view, params);
                return;
            }

            mCActionBar.setId(R.id.action_bar_id);
            mCActionBarDivider = adapter.getActionBarDivider(this);
            RelativeLayout contentView = new RelativeLayout(this);
            ActionBarAdapter.Type type = adapter.getActionBarType();
            type = type == null ? ActionBarAdapter.Type.NORMAL : type;

            { // add action bar
                RelativeLayout.LayoutParams actionBarParams;
                if (mCActionBar.getLayoutParams() != null) {
                    actionBarParams = new RelativeLayout.LayoutParams(mCActionBar.getLayoutParams());
                } else {
                    actionBarParams = new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                }
                actionBarParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                contentView.addView(mCActionBar, actionBarParams);
            }

            if (mCActionBarDivider != null) { // add action bar divider
                RelativeLayout.LayoutParams dividerParams;
                if (mCActionBarDivider.getLayoutParams() != null) {
                    dividerParams = new RelativeLayout.LayoutParams(mCActionBarDivider.getLayoutParams());
                } else {
                    dividerParams = new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                }
                dividerParams.addRule(RelativeLayout.BELOW, R.id.action_bar_id);
                contentView.addView(mCActionBarDivider, dividerParams);
            }

            // add the real content view
            switch (type) {
                case NORMAL: {
                    RelativeLayout.LayoutParams contentViewParams =
                            new RelativeLayout.LayoutParams(params);
                    contentViewParams.addRule(RelativeLayout.BELOW, R.id.action_bar_id);
                    contentView.addView(mCContentView, 0, contentViewParams);
                    super.setContentView(contentView);
                    break;
                }
                case OVERLAY: {
                    // use RelativeLayout as content view
                    contentView.addView(mCContentView, 0, params);
                    super.setContentView(contentView);
                    break;
                }
            }

            super.setContentView(contentView);
        }
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

    public static abstract class LifeCycleAdapter implements LifeCycleListener {
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
