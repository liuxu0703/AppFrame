package lx.af.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.LinkedList;

import lx.af.dialog.LoadingDialog;
import lx.af.utils.AlertUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * author: liuxu
 * date: 2015-02-06
 *
 * activity base
 * TODO: double click finish
 */
public abstract class BaseActivity extends FragmentActivity {

    public String TAG;

    private LoadingDialog mLoadingDialog;
    private View mCContentView;  // custom content view
    private View mCActionBar;  // custom action bar

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
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
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

    public void startActivity(Class cls) {
        startActivity(new Intent(BaseActivity.this, cls));
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
     * check if the activity is running in foreground.
     * AKA, if the activity is in life cycle between onResume() and onPause().
     * @return true if running in foreground.
     */
    public boolean isForeground() {
        return mIsForeground;
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

    public void showToastLong(String msg){
        AlertUtils.showToastLong(msg);
    }

    public void showToastLong(int resId){
        AlertUtils.showToastLong(resId);
    }

    public void showToastShort(String msg){
        AlertUtils.showToastShort(msg);
    }

    public void showToastShort(int resId){
        AlertUtils.showToastShort(resId);
    }

    public void showLoadingDialog(int id){
        showLoadingDialog(getString(id));
    }

    public void showLoadingDialog(){
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
                mLoadingDialog = new LoadingDialog(BaseActivity.this, msg);
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
                    contentView.addView(mCActionBar, ap);
                    contentView.addView(mCContentView, params);
                    super.setContentView(contentView);
                    break;
                }
            }
        }
    }

    // ======================================
    // interface

    public interface LifeCycleListener {
        void onActivityCreated(BaseActivity activity, Bundle savedInstanceState);
        void onActivityStarted(BaseActivity activity);
        void onActivityResumed(BaseActivity activity);
        void onActivityPaused(BaseActivity activity);
        void onActivityStopped(BaseActivity activity);
        void onActivityDestroyed(BaseActivity activity);
        void onActivityResult(BaseActivity activity, int requestCode, int resultCode, Intent data);
        void onActivitySaveInstanceState(BaseActivity activity, Bundle outState);
        void onActivityRestoreInstanceState(BaseActivity activity, Bundle savedInstanceState);
    }

    public static class LifeCycleAdapter implements LifeCycleListener {
        public void onActivityCreated(BaseActivity activity, Bundle savedInstanceState) {}
        public void onActivityStarted(BaseActivity activity) {}
        public void onActivityResumed(BaseActivity activity) {}
        public void onActivityPaused(BaseActivity activity) {}
        public void onActivityStopped(BaseActivity activity) {}
        public void onActivityDestroyed(BaseActivity activity) {}
        public void onActivityResult(BaseActivity activity, int requestCode, int resultCode, Intent data) {}
        public void onActivitySaveInstanceState(BaseActivity activity, Bundle outState) {}
        public void onActivityRestoreInstanceState(BaseActivity activity, Bundle savedInstanceState) {}
    }

}
