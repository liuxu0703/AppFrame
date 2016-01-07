package lx.af.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import java.util.LinkedList;

import lx.af.dialog.LoadingDialog;
import lx.af.utils.AlertUtils;
import lx.af.utils.ViewInject.ViewInjectUtils;

/**
 * Created by liuxu on 15-12-11.
 *
 */
public abstract class AbsBaseFragment extends Fragment {

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewInjectUtils.inject(this, view);
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

    public void startActivity(Class cls){
        if (getActivity() != null) {
            startActivity(new Intent(getActivity(), cls));
        }
    }

    /**
     * get and convert view.
     * designed to be used only in {@link Fragment#onViewCreated(View, Bundle)}.
     * if used elsewhere, this method may return a null object.
     * @param id view id for findViewById() method
     * @param <T> subclass of View
     * @return the view
     */
    @SuppressWarnings("unchecked")
    public  <T extends View> T obtainView(int id) {
        if (getView() == null) {
            return null;
        }
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

    public void toastLong(String msg){
        AlertUtils.toastLong(msg);
    }

    public void toastLong(int resId){
        AlertUtils.toastLong(resId);
    }

    public void toastShort(String msg){
        AlertUtils.toastShort(msg);
    }

    public void toastShort(int resId){
        AlertUtils.toastShort(resId);
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
        void onFragmentCreate(Bundle savedInstanceState, AbsBaseFragment fragment);
        void onFragmentResume(AbsBaseFragment fragment);
        void onFragmentPause(AbsBaseFragment fragment);
        void onFragmentDestroy(AbsBaseFragment fragment);
        void onFragmentActivityResult(AbsBaseFragment fragment, int requestCode, int resultCode, Intent data);
        void onFragmentSaveInstanceState(AbsBaseFragment fragment, Bundle outState);
        void onFragmentViewStateRestored(AbsBaseFragment fragment, Bundle savedInstanceState);
    }

    public static class LifeCycleAdapter implements LifeCycleListener {
        public void onFragmentCreate(Bundle savedInstanceState, AbsBaseFragment fragment) {}
        public void onFragmentResume(AbsBaseFragment fragment) {}
        public void onFragmentPause(AbsBaseFragment fragment) {}
        public void onFragmentDestroy(AbsBaseFragment fragment) {}
        public void onFragmentActivityResult(AbsBaseFragment fragment, int requestCode, int resultCode, Intent data) {}
        public void onFragmentSaveInstanceState(AbsBaseFragment fragment, Bundle outState) {}
        public void onFragmentViewStateRestored(AbsBaseFragment fragment, Bundle savedInstanceState) {}
    }

}
