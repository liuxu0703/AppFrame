package lx.af.activity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lx.af.R;
import lx.af.base.BaseActivity;
import lx.af.view.ImageBrowser.IBGalleryViewPager;
import lx.af.view.ImageBrowser.IBImageViewContainer;
import lx.af.view.ImageBrowser.IBImageViewContainer.LoadImageCallback;
import lx.af.view.ImageBrowser.IBPagerAdapter;

/**
 * author: lx
 * date: 15-10-10
 *
 * big image browser.
 * if you want to add menu button or bottom bar, subclass this activity
 * and override {@link #getActionBarMenu()}, {@link #getBottomBar()}.
 */
public class ImageBrowserActivity extends BaseActivity {

    /** image uri list to be displayed */
    public static final String EXTRA_CURRENT_IMAGE_URI = "IMImageBrowserActivity.uri_current";
    /** image uri for init display */
    public static final String EXTRA_IMAGE_URI_LIST = "IMImageBrowserActivity.uri_list";


    public enum ImageValidation {
        UNKNOWN, VALID, INVALID,
    }


    // hide function bar after X milliseconds without further operation
    private static final int FUNCTION_BAR_HIDE_DELAY = 3000;

    private Animation mAnimActionBarShow;
    private Animation mAnimBottomBarShow;
    private Animation mAnimActionBarHide;
    private Animation mAnimBottomBarHide;

    private IBGalleryViewPager mPager;
    private TextView mTvPageIdx;
    private View mActionBar;
    private FrameLayout mBottomBar;

    private Handler mUIHandler = new Handler();
    private List<String> mImgUris;
    private Map<String, ImageInfo> mImgInfoMap;
    private String mCurrentImgUri;
    private Map<String, SoftReference<IBImageViewContainer>> mImageMap = new HashMap<>();

    private Runnable mHideFunctionBarRunnable = new Runnable() {
        @Override
        public void run() {
            hideFunctionBar();
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_browser_activity);

        Bundle bundle = getIntent().getExtras();
        mCurrentImgUri = bundle.getString(EXTRA_CURRENT_IMAGE_URI);
        mImgUris = bundle.getStringArrayList(EXTRA_IMAGE_URI_LIST);
        if (mImgUris == null || mImgUris.size() == 0) {
            //throw new IllegalStateException("image uri list null !");
            Toast.makeText(this, R.string.image_Browser_toast_list_null, Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else {
            mImgInfoMap = new HashMap<>(mImgUris.size());
            for (String uri : mImgUris) {
                mImgInfoMap.put(uri, new ImageInfo(uri));
            }
        }

        mAnimActionBarShow = AnimationUtils.loadAnimation(this, R.anim.slide_top_out);
        mAnimBottomBarShow = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in);
        mAnimActionBarHide = AnimationUtils.loadAnimation(this, R.anim.slide_top_in);
        mAnimBottomBarHide = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out);

        mPager = getView(R.id.activity_image_browser_pager);
        mTvPageIdx = getView(R.id.activity_image_browser_page_idx);
        mActionBar = getView(R.id.activity_image_browser_action_bar);
        mBottomBar = getView(R.id.activity_image_browser_function_bar);
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

        int currentIdx = 0;
        if (mCurrentImgUri != null) {
            currentIdx = mImgUris.indexOf(mCurrentImgUri);
        }
        currentIdx = currentIdx == -1 ? 0 : currentIdx;
        mTvPageIdx.setText((currentIdx + 1) + "/" + mImgUris.size());
        mPager.setAdapter(new ImagePaperAdapter(this, mImgUris));
        mPager.setOffscreenPageLimit(1);
        mPager.setCurrentItem(currentIdx);
        mPager.setOnPageChangeListener(new OnPageChangeListener() {
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
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        View menu = getActionBarMenu();
        if (menu != null) {
            FrameLayout menuContainer = getView(R.id.activity_image_browser_menu_container);
            menuContainer.addView(menu, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }

        View bar = getBottomBar();
        if (bar != null) {
            mBottomBar.addView(bar, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }

        if (!isAutoHideFunctionBar()) {
            mActionBar.setVisibility(View.VISIBLE);
            if (bar != null) {
                mBottomBar.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageMap = null;
        System.gc();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.image_browser_exit);
    }

    private void hideFunctionBar() {
        mActionBar.startAnimation(mAnimActionBarHide);
        mActionBar.setVisibility(View.GONE);
        mBottomBar.startAnimation(mAnimBottomBarHide);
        mBottomBar.setVisibility(View.GONE);
    }

    private void showFunctionBar() {
        mActionBar.startAnimation(mAnimActionBarShow);
        mActionBar.setVisibility(View.VISIBLE);
        mBottomBar.startAnimation(mAnimBottomBarShow);
        mBottomBar.setVisibility(View.VISIBLE);
    }

    private void hideShowFunctionBar() {
        if (isAutoHideFunctionBar()) {
            if (mActionBar.getVisibility() == View.VISIBLE) {
                hideFunctionBar();
                mUIHandler.removeCallbacks(mHideFunctionBarRunnable);
            } else {
                showFunctionBar();
                mUIHandler.removeCallbacks(mHideFunctionBarRunnable);
                mUIHandler.postDelayed(mHideFunctionBarRunnable, FUNCTION_BAR_HIDE_DELAY);
            }
        }
    }

    private class ImagePaperAdapter extends IBPagerAdapter {

        public ImagePaperAdapter(Context context, List<String> resources) {
            super(context, resources);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position,
                                   Object object) {
            super.setPrimaryItem(container, position, object);
            IBGalleryViewPager pager = (IBGalleryViewPager) container;
            IBImageViewContainer img = (IBImageViewContainer) object;
            pager.mCurrentView = img.getImageView();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            IBImageViewContainer touchImageView = null;
            String url = mResources.get(position);
            if (mImageMap.containsKey(url)) {
                SoftReference<IBImageViewContainer> softReference = mImageMap.get(url);
                if (softReference.get() != null) {
                    touchImageView = softReference.get();
                }
            }

            if (touchImageView == null) {
                touchImageView = new IBImageViewContainer(mContext);
                touchImageView.setUri(mResources.get(position));
                touchImageView.setLoadImageCallback(mLoadImageCallback);
                touchImageView.setLayoutParams(new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT));
            }

            touchImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideShowFunctionBar();
                }
            });

            view.addView(touchImageView, 0);
            return touchImageView;
        }

        private LoadImageCallback mLoadImageCallback = new LoadImageCallback() {
            @Override
            public void onImageLoadComplete(String imgUri, boolean loadSuccess) {
                ImageBrowserActivity.this.onImageLoadComplete(imgUri, loadSuccess);
            }
        };
    }


    // ==========================================


    protected String getCurrentImageUri() {
        return mCurrentImgUri;
    }

    protected ImageValidation getCurrentImageValidation() {
        ImageInfo info = mImgInfoMap.get(mCurrentImgUri);
        return info.valid;
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
     * @return true and the action bar and bottom bar (if presents) will be
     *         hidden in 3 seconds without any operation on the image;
     *         false and the action bar and bottom bar will always be shown.
     */
    protected boolean isAutoHideFunctionBar() {
        return true;
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
     * called when action bar back button is clicked
     * @return true if the event is handled;
     *         false if you want this class to handle it.
     */
    protected boolean onActionBarBackClicked() {
        return false;
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


    // ==========================================


    private static class ImageInfo {
        String uri;
        ImageValidation valid = ImageValidation.UNKNOWN;

        public ImageInfo(String uri) {
            this.uri = uri;
        }
    }

}
