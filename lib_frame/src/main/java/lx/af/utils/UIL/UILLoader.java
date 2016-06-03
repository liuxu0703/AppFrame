package lx.af.utils.UIL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import lx.af.utils.UIL.displayer.BaseDisplayer;
import lx.af.utils.UIL.displayer.BaseDrawableDisplayer;
import lx.af.utils.UIL.displayer.CircleDisplayer;
import lx.af.utils.UIL.displayer.DefaultDisplayer;
import lx.af.utils.UIL.displayer.RoundedDisplayer;
import lx.af.utils.UIL.displayer.animator.BaseAnimator;
import lx.af.utils.UIL.displayer.animator.FadeInAnimator;
import lx.af.utils.UIL.displayer.animator.FloatInAnimator;
import lx.af.utils.UIL.displayer.animator.ScaleInAnimator;

/**
 * author: lx
 * date: 16-4-27
 */
public class UILLoader {

    protected static ImageLoader sImageLoader = ImageLoader.getInstance();

    protected ImageView mImageView;
    protected ImageAware mImageViewAware;
    protected String mUri;
    protected DisplayImageOptions.Builder mOptionsBuilder;
    protected BaseDisplayer mDisplayer;
    protected BaseAnimator mDisplayerAnimator;

    protected ImageLoadingListener mLoadListener;
    protected ImageLoadingProgressListener mProgressListener;
    protected ImageInfoCallback mImageInfoCallback;

    protected int mResIdOnFail;
    protected int mResIdOnEmptyUri;
    protected int mResIdOnLoading;

    protected int mMaxWidth = -1;
    protected int mMaxHeight = -1;
    protected int mBorderWidth;
    protected int mBorderColor;
    protected int mCornerRadius;
    protected int mBlurRadius;
    protected int mVisibilityOnFail = -1;
    protected boolean mAsCircle;
    protected boolean mAsSquare;

    public static UILLoader of(ImageView imageView, String uri) {
        return new UILLoader(imageView, uri);
    }

    public void display() {
        if (TextUtils.isEmpty(mUri) && mResIdOnEmptyUri != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(mImageView.getResources(), mResIdOnEmptyUri);
            if (bitmap != null) {
                getBitmapDisplayer().display(bitmap, mImageViewAware, LoadedFrom.MEMORY_CACHE);
            }
            return;
        }

        sImageLoader.displayImage(
                mUri, mImageViewAware,
                getOptions(), getImageSize(),
                mInnerLoadListener, mProgressListener);
    }

    public UILLoader(ImageView imageView, String uri) {
        mImageView = imageView;
        mImageViewAware = new ImageViewAware(mImageView);
        mUri = uri;
    }

    public UILLoader maxSize(int maxWidth, int maxHeight) {
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
        return this;
    }

    public UILLoader border(int borderWidth, int borderColor) {
        mBorderWidth = borderWidth;
        mBorderColor = borderColor;
        return this;
    }

    public UILLoader corner(int cornerRadius) {
        mCornerRadius = cornerRadius;
        return this;
    }

    public UILLoader asCircle() {
        mAsCircle = true;
        return this;
    }

    public UILLoader asSquare() {
        mAsSquare = true;
        return this;
    }

    /**
     * show image with blur effect
     * @param radius [1,10] recommended
     */
    public UILLoader blur(int radius) {
        mBlurRadius = radius;
        return this;
    }

    public UILLoader setVisibilityOnFail(int visibility) {
        mVisibilityOnFail = visibility;
        return this;
    }

    public UILLoader imageDefault(int imageRes) {
        mResIdOnFail = mResIdOnEmptyUri = mResIdOnLoading = imageRes;
        return this;
    }

    public UILLoader imageOnLoading(int imageRes) {
        mResIdOnLoading = imageRes;
        return this;
    }

    public UILLoader imageForEmptyUri(int imageRes) {
        mResIdOnEmptyUri = imageRes;
        return this;
    }

    public UILLoader imageOnFail(int imageRes) {
        mResIdOnFail = imageRes;
        return this;
    }

    public UILLoader delayBeforeLoading(int delayInMillis) {
        getOptionsBuilder().delayBeforeLoading(delayInMillis);
        return this;
    }

    public UILLoader resetBeforeLoading() {
        getOptionsBuilder().resetViewBeforeLoading(true);
        return this;
    }

    public UILLoader displayer(BaseDisplayer displayer) {
        mDisplayer = displayer;
        return this;
    }

    public UILLoader displayerAnimator(BaseAnimator animator) {
        mDisplayerAnimator = animator;
        return this;
    }

    public UILLoader animateFadeIn() {
        mDisplayerAnimator = new FadeInAnimator();
        return this;
    }

    public UILLoader animateScaleIn() {
        mDisplayerAnimator = new ScaleInAnimator();
        return this;
    }

    public UILLoader animateFloatIn() {
        mDisplayerAnimator = new FloatInAnimator();
        return this;
    }

    public UILLoader setLoadListener(ImageLoadingListener listener) {
        mLoadListener = listener;
        return this;
    }

    public UILLoader setProgressListener(ImageLoadingProgressListener listener) {
        mProgressListener = listener;
        return this;
    }

    public UILLoader setImageInfoCallback(ImageInfoCallback c) {
        mImageInfoCallback = c;
        return this;
    }

    // ====================================================

    protected DisplayImageOptions getOptions() {
        BaseDisplayer displayer = getBitmapDisplayer();
        if (displayer instanceof BaseDrawableDisplayer) {
            if (mResIdOnLoading != 0) {
                Drawable drawable = createDrawableByDisplayer(
                        (BaseDrawableDisplayer) displayer, mResIdOnLoading);
                if (drawable != null) {
                    getOptionsBuilder().showImageOnLoading(drawable);
                }
            }
        } else {
            if (mResIdOnEmptyUri != 0) {
                getOptionsBuilder().showImageForEmptyUri(mResIdOnEmptyUri);
            }
            if (mResIdOnFail != 0) {
                getOptionsBuilder().showImageOnFail(mResIdOnFail);
            }
            if (mResIdOnLoading != 0) {
                getOptionsBuilder().showImageOnLoading(mResIdOnLoading);
            }
        }
        return getOptionsBuilder().displayer(displayer).build();
    }

    protected DisplayImageOptions.Builder getOptionsBuilder() {
        if (mOptionsBuilder == null) {
            mOptionsBuilder = new DisplayImageOptions.Builder()
                    .cacheOnDisk(true)
                    .cacheInMemory(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565);
        }
        return mOptionsBuilder;
    }

    protected BaseDisplayer getBitmapDisplayer() {
        if (mDisplayer == null) {
            if (mAsCircle) {
                mDisplayer = new CircleDisplayer(mBorderWidth, mBorderColor);
            } else if (mBorderWidth > 0 || mCornerRadius > 0 || mAsSquare) {
                mDisplayer = new RoundedDisplayer(mCornerRadius, mBorderWidth, mBorderColor)
                        .setAsSquare(mAsSquare);
            } else {
                mDisplayer = new DefaultDisplayer();
            }
            if (mBlurRadius > 0) {
                mDisplayer.setBlur(mBlurRadius);
            }
            if (mDisplayerAnimator == null) {
                mDisplayerAnimator = new FadeInAnimator();
            }
            mDisplayer.setDisplayerAnimator(mDisplayerAnimator);
            mDisplayer.setImageInfoCallback(mImageInfoCallback);
        }
        return mDisplayer;
    }

    protected ImageSize getImageSize() {
        if (mMaxWidth > 0 && mMaxHeight > 0) {
            return new ImageSize(mMaxWidth, mMaxHeight);
        } else {
            return null;
        }
    }

    protected Drawable createDrawableByDisplayer(BaseDrawableDisplayer displayer, int resId) {
        if (resId == 0) {
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeResource(mImageView.getResources(), resId);
        if (bitmap == null) {
            return null;
        }
        return displayer.createDisplayDrawable(bitmap);
    }


    private ImageLoadingListener mInnerLoadListener = new ImageLoadingListener() {
        @Override
        public void onLoadingStarted(String imageUri, View view) {
            if (mLoadListener != null) {
                mLoadListener.onLoadingStarted(imageUri, view);
            }
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            if (mVisibilityOnFail == View.VISIBLE) {
                mImageView.setVisibility(View.VISIBLE);
            } else if (mVisibilityOnFail == View.INVISIBLE) {
                mImageView.setVisibility(View.INVISIBLE);
            } else if (mVisibilityOnFail == View.GONE) {
                mImageView.setVisibility(View.GONE);
            }

            if (mResIdOnFail != 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(view.getResources(), mResIdOnFail);
                if (bitmap != null) {
                    mDisplayer.display(bitmap, mImageViewAware, LoadedFrom.MEMORY_CACHE);
                }
            }

            if (mLoadListener != null) {
                mLoadListener.onLoadingFailed(imageUri, view, failReason);
            }
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (mLoadListener != null) {
                mLoadListener.onLoadingComplete(imageUri, view, loadedImage);
            }
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            if (mLoadListener != null) {
                mLoadListener.onLoadingCancelled(imageUri, view);
            }
        }
    };

}
