package lx.af.activity.ImageBrowser;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lx.af.R;
import lx.af.activity.ImageBrowser.ImagePagerAdapter.ClickImageCallback;
import lx.af.activity.ImageBrowser.ImagePagerAdapter.LoadImageCallback;
import lx.af.base.AbsBaseActivity;

/**
 * author: lx
 * date: 15-10-10
 *
 * big image browser.
 * if you want to add menu button or bottom bar, subclass this activity
 * and override {@link #getActionBarMenu()}, {@link #getBottomBar()}.
 */
public class ImageBrowserActivity extends AbsBaseActivity {

    /** image uri list to be displayed */
    public static final String EXTRA_CURRENT_IMAGE_URI = "ImageBrowserActivity_uri_current";
    /** image uri for init display */
    public static final String EXTRA_IMAGE_URI_LIST = "ImageBrowserActivity_uri_list";
    /** exit browser by tap image */
    public static final String EXTRA_TAP_EXIT = "ImageBrowserActivity_tap_exit";
    /** image uri to be displayed before the real image uri */
    public static final String EXTRA_PRELOAD_URI = "ImageBrowserActivity_uri_preload";
    /** preload uri prefix */
    public static final String EXTRA_PRELOAD_URI_PREFIX = "preload_";
    /** auto hide function bar (action bar + bottom bar) */
    public static final String EXTRA_AUTO_HIDE_FUNCTION_BAR = "ImageBrowserActivity_auto_hide_action_bar";

    public enum ImageValidation {
        /** image has not be loaded, validation state unknown */
        UNKNOWN,
        /** uri load success and image displayed */
        VALID,
        /** uri load failed or image cannot be displayed */
        INVALID,
    }

    // hide function bar after X milliseconds without further operation
    private static final int FUNCTION_BAR_HIDE_DELAY = 3000;

    private Animation mAnimActionBarShow;
    private Animation mAnimBottomBarShow;
    private Animation mAnimActionBarHide;
    private Animation mAnimBottomBarHide;

    private ViewPager mPager;
    private TextView mTvPageIdx;
    private View mActionBar;
    private FrameLayout mBottomBar;

    private ImagePagerAdapter mAdapter;
    private Handler mUIHandler = new Handler();
    private List<String> mImgUris;
    private Map<String, ImageInfo> mImgInfoMap;
    private String mCurrentImgUri;

    private boolean mIsAutoHideFunctionBar = true;
    private boolean mIsTapExit = false;

    private Runnable mHideFunctionBarRunnable = new Runnable() {
        @Override
        public void run() {
            hideFunctionBar();
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        List<String> list = intent.getStringArrayListExtra(EXTRA_IMAGE_URI_LIST);
        mCurrentImgUri = intent.getStringExtra(EXTRA_CURRENT_IMAGE_URI);
        mIsTapExit = intent.getBooleanExtra(EXTRA_TAP_EXIT, false);
        mIsAutoHideFunctionBar = intent.getBooleanExtra(EXTRA_AUTO_HIDE_FUNCTION_BAR, !mIsTapExit);

        if (!isBrowserEnabled()) {
            // browse is not allowed, add only one image to the list
            mImgUris = new ArrayList<>(1);
            if (mCurrentImgUri != null) {
                mImgUris.add(mCurrentImgUri);
            } else if (list != null && list.size() != 0) {
                mImgUris.add(list.get(0));
            }
        } else {
            if (list != null && list.size() != 0) {
                mImgUris = list;
            } else if (mCurrentImgUri != null) {
                mImgUris = new ArrayList<>(1);
                mImgUris.add(mCurrentImgUri);
            }
        }

        if (mImgUris == null || mImgUris.size() == 0) {
            //throw new IllegalStateException("image uri list null !");
            Toast.makeText(this, R.string.image_Browser_toast_list_null, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (mCurrentImgUri == null) {
            mCurrentImgUri = mImgUris.get(0);
        }

        String preloadUri = intent.getStringExtra(EXTRA_PRELOAD_URI);
        mImgInfoMap = new HashMap<>(mImgUris.size());
        for (String uri : mImgUris) {
            String preload = intent.getStringExtra(EXTRA_PRELOAD_URI_PREFIX + uri);
            if (TextUtils.isEmpty(preload)) {
                preload = preloadUri;
            }
            mImgInfoMap.put(uri, new ImageInfo(uri, preload));
        }

        mAnimActionBarShow = AnimationUtils.loadAnimation(this, R.anim.slide_top_out);
        mAnimBottomBarShow = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in);
        mAnimActionBarHide = AnimationUtils.loadAnimation(this, R.anim.slide_top_in);
        mAnimBottomBarHide = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out);

        setContentView(R.layout.image_browser_activity);
        mPager = obtainView(R.id.activity_image_browser_pager);
        mTvPageIdx = obtainView(R.id.activity_image_browser_page_idx);
        mActionBar = obtainView(R.id.activity_image_browser_action_bar);
        mBottomBar = obtainView(R.id.activity_image_browser_function_bar);
        findViewById(R.id.activity_image_browser_action_bar_back)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!onActionBarBackClicked()) {
                    // sub class does not handle this event
                    finish();
                }
            }
        });

        int currentIdx = mImgUris.indexOf(mCurrentImgUri);
        currentIdx = currentIdx == -1 ? 0 : currentIdx;
        mTvPageIdx.setText((currentIdx + 1) + "/" + mImgUris.size());

        mAdapter = new ImagePagerAdapter(mImgUris, mImgInfoMap);
        mAdapter.setLoadImageCallback(mLoadImageCallback);
        mAdapter.setClickImageCallback(mClickImageCallback);

        mPager.setAdapter(mAdapter);
        mPager.setOffscreenPageLimit(1);
        mPager.setCurrentItem(currentIdx);
        mPager.addOnPageChangeListener(mViewPagerChangeListener);
    }

    private OnPageChangeListener mViewPagerChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            mCurrentImgUri = mImgUris.get(position);
            onBrowseImage(mImgUris, position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mTvPageIdx.setText((mPager.getCurrentItem() + 1) + "/" + mImgUris.size());
        }
    };

    private LoadImageCallback mLoadImageCallback = new LoadImageCallback() {
        @Override
        public void onImageLoadComplete(String imgUri, boolean loadSuccess) {
            ImageBrowserActivity.this.onImageLoadComplete(imgUri, loadSuccess);
        }
    };

    private ClickImageCallback mClickImageCallback = new ClickImageCallback() {
        @Override
        public void onImageClicked(String imageUri, View view) {
            ImageBrowserActivity.this.onImageClicked(imageUri, view);
        }

        @Override
        public boolean onImageLongClicked(String imageUri, View view) {
            return ImageBrowserActivity.this.onImageLongClicked(imageUri, view);
        }
    };

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        View menu = getActionBarMenu();
        if (menu != null) {
            FrameLayout menuContainer = obtainView(R.id.activity_image_browser_menu_container);
            menuContainer.addView(menu, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }

        View bar = getBottomBar();
        if (bar != null) {
            mBottomBar.addView(bar, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }

        if (mImgUris.size() == 1) {
            if (menu != null) {
                mTvPageIdx.setVisibility(View.GONE);
            } else {
                mActionBar.setAlpha(0);
            }
        }
        if (!isAutoHideFunctionBar()) {
            showFunctionBar();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.image_browser_exit);
    }

    protected void hideFunctionBar() {
        mActionBar.startAnimation(mAnimActionBarHide);
        mActionBar.setVisibility(View.GONE);
        mBottomBar.startAnimation(mAnimBottomBarHide);
        mBottomBar.setVisibility(View.GONE);
    }

    protected void showFunctionBar() {
        mActionBar.startAnimation(mAnimActionBarShow);
        mActionBar.setVisibility(View.VISIBLE);
        mBottomBar.startAnimation(mAnimBottomBarShow);
        mBottomBar.setVisibility(View.VISIBLE);
    }

    protected void hideShowFunctionBar() {
        if (mActionBar.getVisibility() == View.VISIBLE) {
            hideFunctionBar();
            mUIHandler.removeCallbacks(mHideFunctionBarRunnable);
        } else {
            showFunctionBar();
            mUIHandler.removeCallbacks(mHideFunctionBarRunnable);
            mUIHandler.postDelayed(mHideFunctionBarRunnable, FUNCTION_BAR_HIDE_DELAY);
        }
    }

    // ==========================================

    /**
     * @return current displayed image uri
     */
    protected String getCurrentImageUri() {
        return mCurrentImgUri;
    }

    /**
     * @return image uri list in the browser
     */
    protected List<String> getImageUriList() {
        return mImgUris;
    }

    /**
     * @return validation of current displayed image
     */
    protected ImageValidation getCurrentImageValidation() {
        ImageInfo info = mImgInfoMap.get(mCurrentImgUri);
        return info.valid;
    }

    /**
     * generate a action bar menu button. called on activity create.
     * the button will be shown on the right side of the action bar, with
     * both width and height as WRAP_CONTENT.
     * @return the menu button view, or null if you do not want one.
     */
    protected View getActionBarMenu() {
        return null;
    }

    /**
     * generate a bottom bar view. called on activity create.
     * the view will be added to the bottom of the activity ui, with
     * width as MATCH_PARENT and height as WRAP_CONTENT.
     * @return the bottom bar view, or null if you do not want one.
     */
    protected View getBottomBar() {
        return null;
    }

    /**
     * @return true and the action bar will always be shown, and tap image
     *         will exit image browser.
     */
    protected boolean isTapExit() {
        return mIsTapExit;
    }

    /**
     * @return true and the action bar and bottom bar (if presents) will be
     *         hidden in 3 seconds without any operation on the image;
     *         false and the action bar and bottom bar will always be shown.
     */
    protected boolean isAutoHideFunctionBar() {
        return mIsAutoHideFunctionBar;
    }

    /**
     * @return true and user can browser through the image list;
     *         false and only the first image will be displayed, browse is disabled.
     */
    protected boolean isBrowserEnabled() {
        return true;
    }

    /**
     * called when an image is browsed.
     * @param images list of uri of all images
     * @param position current browsed image uri position
     */
    protected void onBrowseImage(List<String> images, int position) {
        if (getCurrentImageValidation() == ImageValidation.INVALID) {
            Toast.makeText(this,
                    R.string.image_Browser_toast_load_image_fail, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * called when an image uri is loaded into mem and displayed.
     * @param imgUri the image uri
     * @param success whether the load is success.
     */
    protected void onImageLoadComplete(String imgUri, boolean success) {
        if (!success && mCurrentImgUri.equals(imgUri)) {
            Toast.makeText(this,
                    R.string.image_Browser_toast_load_image_fail, Toast.LENGTH_SHORT).show();
        }
        ImageInfo info = mImgInfoMap.get(imgUri);
        info.valid = success ? ImageValidation.VALID : ImageValidation.INVALID;
    }

    /**
     * called when click on an image
     */
    protected void onImageClicked(String imageUri, View view) {
        if (isTapExit()) {
            finish();
            return;
        }
        if (isAutoHideFunctionBar()) {
            hideShowFunctionBar();
        }
    }

    /**
     * called when long click on an image.
     * @return true if the event is handled
     */
    protected boolean onImageLongClicked(String imageUri, View view) {
        return false;
    }

    /**
     * called when action bar back button is clicked
     * @return true if the event is handled;
     *         false if you want this class to handle it.
     */
    protected boolean onActionBarBackClicked() {
        return false;
    }


    // ==========================================


    static class ImageInfo {
        String uri;
        String preloadUri;
        ImageValidation valid = ImageValidation.UNKNOWN;

        public ImageInfo(String uri) {
            this(uri, null);
        }

        public ImageInfo(String uri, String preloadUri) {
            this.uri = uri;
            this.preloadUri = preloadUri;
        }
    }

}
