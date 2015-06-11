package lx.af.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import lx.af.R;
import lx.af.dialog.LoadingDialog;
import lx.af.manager.ActivityManager;
import lx.af.utils.AlertUtils;
import lx.af.utils.ScreenUtils;
import lx.af.view.SwipeBack.SwipeBackLayout;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * author: liuxu
 * date: 2015-02-06
 *
 * activity base
 */
public abstract class BaseActivity extends FragmentActivity {

    private static ActivityManager mActivityManager = ActivityManager.getInstance();

    protected String TAG = "IvBabyActivity";

    private LoadingDialog mLoadingDialog;
    private View mActionBarContentView;
    private SwipeBackLayout mSwipeBackLayout;
    private boolean mIsForeground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        mActivityManager.onActivityCreate(this);
        if (this instanceof SwipeBackImpl) {
            initSwipeBack();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (this instanceof SwipeBackImpl) {
            mSwipeBackLayout.attachToActivity(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mActivityManager.onActivityResume(this);
        mIsForeground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mActivityManager.onActivityPause(this);
        mIsForeground = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivityManager.onActivityDestory(this);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
    }

    public void startActivity(Class cls) {
        startActivity(new Intent(BaseActivity.this, cls));
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }

    public void finishWithoutAnim(){
        super.finish();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v != null) {
            return v;
        }
        if (mSwipeBackLayout != null) {
            v = mSwipeBackLayout.findViewById(id);
            if (v != null) {
                return v;
            }
        }
        if (mActionBarContentView != null) {
            v = mActionBarContentView.findViewById(id);
        }
        return v;
    }

    @Override
    public void setContentView(int layoutResID) {
        if (this instanceof ActionBarImpl) {
            super.setContentView(R.layout.activity_base);
            LayoutInflater inflater = LayoutInflater.from(this);
            View contentView = inflater.inflate(layoutResID, null);
            initActionBar(contentView, null);
        } else {
            super.setContentView(layoutResID);
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (this instanceof ActionBarImpl) {
            super.setContentView(R.layout.activity_base);
            initActionBar(view, params);
        } else {
            super.setContentView(view, params);
        }
    }

    @Override
    public void setContentView(View view) {
        if (this instanceof ActionBarImpl) {
            super.setContentView(R.layout.activity_base);
            initActionBar(view, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        } else {
            super.setContentView(view);
        }
    }

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

    public void showLoadingDialog(String msg, boolean cancelable){
        dismissLoadingDialog();
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(BaseActivity.this, msg);
            mLoadingDialog.setCancelable(cancelable);
        }
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
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
    public  <T extends View> T getView(int id) {
        return (T)findViewById(id);
    }


    // ========================================================
    // about swipe back

    // by zhangzz
    private void initSwipeBack() {
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getWindow().getDecorView().setBackgroundDrawable(null);
        mSwipeBackLayout = new SwipeBackLayout(this);
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mSwipeBackLayout.setEdgeSize(ScreenUtils.getScreenWidth());
    }

    /**
     * by implementing this interface, sub class of BaseActivity will get the ability
     * of "finish activity by swiping back screen".
     * NOTE: AppTheme should be set properly in AndroidManifest.xml.
     */
    public interface SwipeBackImpl {
    }


    // ========================================================
    // add by liuxu, 2015-01-12, about action bar

    private void initActionBar(View contentView, ViewGroup.LayoutParams params) {
        mActionBarContentView = contentView;
        FrameLayout frame = getView(R.id.activity_base_content_frame);
        frame.removeAllViews();
        if (params == null) {
            frame.addView(contentView);
        } else {
            frame.addView(contentView, params);
        }
        View actionBar = getView(R.id.activity_base_action_bar);
        ImageView back = getView(R.id.action_bar_back);
        TextView title = getView(R.id.action_bar_title);
        // use activity label as default action bar title
        title.setText(getTitle());

        if (this instanceof ActionBarCallbacks) {
            final ActionBarCallbacks callbacks = (ActionBarCallbacks) this;

            // check if menu is valid
            View menu = callbacks.onCreateActionBarMenu();
            if (menu != null) {
                ViewStub stub = getView(R.id.action_bar_menu_stub);
                stub.inflate();
                FrameLayout menuFrame = getView(R.id.activity_base_action_bar_menu_frame);
                menuFrame.addView(menu);
            }

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!callbacks.onActionBarBackClicked(v)) {
                        BaseActivity.this.finish();
                    }
                }
            });
            // TODO: adjust title width according to width of back and menu
            callbacks.onActionBarCreated(actionBar, back, title, menu);

        } else {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BaseActivity.this.finish();
                }
            });
        }
    }

    /**
     * create a default text button to be used as menu.
     * call this in ActionBarCallbacks.onCreateActionBarMenu() to generate a menu.
     * @param context context
     * @return the button
     */
    public static TextView createMenuDefaultTxtBtn(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return (TextView) inflater.inflate(R.layout.action_bar_menu_txt_btn, null);
    }

    /**
     * create a default image button to be used as menu.
     * call this in ActionBarCallbacks.onCreateActionBarMenu() to generate a menu.
     * @param context context
     * @return the button
     */
    public static ImageView createMenuDefaultImgBtn(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return (ImageView) inflater.inflate(R.layout.action_bar_menu_img_btn, null);
    }

    /**
     * by implementing this interface, activity extends from BaseActivity
     * will get an ActionBar with a back button and a title uses activity
     * label as text.
     * if menu button is needed, or further customize is needed, try implement
     * ActionBarCallbacks instead.
     */
    public interface ActionBarImpl {
    }

    /**
     * by implementing this interface, activity extends from BaseActivity
     * will get an ActionBar with a back button, a title uses activity
     * label as text, and a menu button.
     * if only back button and title is needed, try implement ActionBarImpl
     * instead.
     */
    public interface ActionBarCallbacks extends ActionBarImpl {

        /**
         * called to get a view for menu.
         * @return the menu view, or null if menu is not needed.
         * @see #createMenuDefaultImgBtn to create a default ImageView as menu
         * @see #createMenuDefaultTxtBtn to create a default TextView as menu
         */
        public View onCreateActionBarMenu();

        /**
         * called when the ActionBar is first inflated.
         * @param actionBar the action bar
         * @param back back button
         * @param title TextView for title
         * @param menu the menu, can be null
         */
        public void onActionBarCreated(View actionBar, ImageView back, TextView title, @Nullable View menu);

        /**
         * called when back button is clicked.
         * Activity.finish() will be called if false is returned from this method
         * @param back back button
         * @return true if click event is handled, false otherwise
         */
        public boolean onActionBarBackClicked(View back);

    }


    // ========================================================

}
