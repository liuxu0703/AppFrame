package lx.af.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import java.util.LinkedList;

import lx.af.dialog.LoadingDialog;
import lx.af.utils.AlertUtils;

/**
 * Created by liuxu on 15-2-11.
 *
 */
public abstract class BaseFragment extends Fragment {

    protected String TAG;

    private LoadingDialog mLoadingDialog;

    private final LinkedList<LifeCycleListener> mLifeCycleListeners = new LinkedList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (LifeCycleListener listener : mLifeCycleListeners) {
            listener.onFragmentCreate(savedInstanceState, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        for (LifeCycleListener listener : mLifeCycleListeners) {
            listener.onFragmentSaveInstanceState(this, outState);
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        for (LifeCycleListener listener : mLifeCycleListeners) {
            listener.onFragmentViewStateRestored(this, savedInstanceState);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        for (LifeCycleListener listener : mLifeCycleListeners) {
            listener.onFragmentResume(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        for (LifeCycleListener listener : mLifeCycleListeners) {
            listener.onFragmentPause(this);
        }
    }

    @Override
    public void onDestroy() {
        dismissLoadingDialog();
        super.onDestroy();
        for (LifeCycleListener listener : mLifeCycleListeners) {
            listener.onFragmentDestroy(this);
        }
        mLifeCycleListeners.clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (LifeCycleListener listener : mLifeCycleListeners) {
            listener.onFragmentActivityResult(this, requestCode, resultCode, data);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if (getActivity() != null) {
            startActivity(intent);
        }
    }

    public void startActivity(Class cls){
        if (getActivity() != null) {
            startActivity(new Intent(getActivity(), cls));
        }
    }

    /**
     * get and convert view
     * @param id view id for findViewById() method
     * @param <T> subclass of View
     * @return the view
     */
    @SuppressWarnings("unchecked")
    public  <T extends View> T obtainView(int id) {
        return (T) getView().findViewById(id);
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
        if (getActivity() == null) {
            return;
        }
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            // loading dialog already fired, just change the message
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoadingDialog.setMessage(msg);
                }
            });
        } else {
            if (mLoadingDialog == null) {
                mLoadingDialog = new LoadingDialog(getActivity(), msg);
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
    // interface

    public interface LifeCycleListener {
        void onFragmentCreate(Bundle savedInstanceState, BaseFragment fragment);
        void onFragmentResume(BaseFragment fragment);
        void onFragmentPause(BaseFragment fragment);
        void onFragmentDestroy(BaseFragment fragment);
        void onFragmentActivityResult(BaseFragment fragment, int requestCode, int resultCode, Intent data);
        void onFragmentSaveInstanceState(BaseFragment fragment, Bundle outState);
        void onFragmentViewStateRestored(BaseFragment fragment, Bundle savedInstanceState);
    }

    public static class LifeCycleAdapter implements LifeCycleListener {
        public void onFragmentCreate(Bundle savedInstanceState, BaseFragment fragment) {}
        public void onFragmentResume(BaseFragment fragment) {}
        public void onFragmentPause(BaseFragment fragment) {}
        public void onFragmentDestroy(BaseFragment fragment) {}
        public void onFragmentActivityResult(BaseFragment fragment, int requestCode, int resultCode, Intent data) {}
        public void onFragmentSaveInstanceState(BaseFragment fragment, Bundle outState) {}
        public void onFragmentViewStateRestored(BaseFragment fragment, Bundle savedInstanceState) {}
    }

}
