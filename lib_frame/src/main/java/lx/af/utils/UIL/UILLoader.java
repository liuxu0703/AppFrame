package lx.af.utils.UIL;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import lx.af.R;
import lx.af.utils.UIL.displayer.BaseDisplayer;
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
    protected String mUri;
    protected DisplayImageOptions.Builder mOptionsBuilder;
    protected BaseDisplayer mDisplayer;
    protected BaseAnimator mDisplayerAnimator;

    protected ImageLoadingListener mLoadListener;
    protected ImageLoadingProgressListener mProgressListener;

    protected int mMaxWidth = -1;
    protected int mMaxHeight = -1;
    protected int mBorderWidth;
    protected int mBorderColor;
    protected int mCornerRadius;
    protected int mBlurRadius;
    protected boolean mAsCircle;
    protected boolean mAsSquare;

    public static UILLoader of(ImageView imageView, String uri) {
        return new UILLoader(imageView, uri);
    }

    public void display() {
        sImageLoader.displayImage(
                mUri, new ImageViewAware(mImageView),
                getOptions(), getImageSize(),
                mLoadListener, mProgressListener);
    }

    public UILLoader(ImageView imageView, String uri) {
        mImageView = imageView;
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

    public UILLoader imageDefault(int imageRes) {
        DisplayImageOptions.Builder builder = getOptionsBuilder();
        builder.showImageForEmptyUri(imageRes);
        builder.showImageOnFail(imageRes);
        builder.showImageOnLoading(imageRes);
        return this;
    }

    public UILLoader imageOnLoading(int imageRes) {
        getOptionsBuilder().showImageOnLoading(imageRes);
        return this;
    }

    public UILLoader imageForEmptyUri(int imageRes) {
        getOptionsBuilder().showImageForEmptyUri(imageRes);
        return this;
    }

    public UILLoader imageOnFail(int imageRes) {
        getOptionsBuilder().showImageOnFail(imageRes);
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

    public UILLoader setLoadListener(ListenerAdapter listener) {
        mLoadListener = listener;
        return this;
    }

    public UILLoader setProgressListener(ImageLoadingProgressListener listener) {
        mProgressListener = listener;
        return this;
    }

    // ====================================================

    protected DisplayImageOptions getOptions() {
        return getOptionsBuilder().displayer(getBitmapDisplayer()).build();
    }

    protected DisplayImageOptions.Builder getOptionsBuilder() {
        if (mOptionsBuilder == null) {
            mOptionsBuilder = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.img_default)
                    .showImageForEmptyUri(R.drawable.img_default)
                    .showImageOnFail(R.drawable.img_default)
                    .cacheOnDisk(true)
                    .cacheInMemory(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565);
        }
        return mOptionsBuilder;
    }

    protected BaseDisplayer getBitmapDisplayer() {
        if (mDisplayer != null) {
            return mDisplayer;
        } else {
            BaseDisplayer displayer;
            if (mAsCircle) {
                displayer = new CircleDisplayer(mBorderWidth, mBorderColor);
            } else if (mBorderWidth > 0 || mCornerRadius > 0 || mAsSquare) {
                displayer = new RoundedDisplayer(mCornerRadius, mBorderWidth, mBorderColor)
                        .setAsSquare(mAsSquare);
            } else {
                displayer = new DefaultDisplayer();
            }
            if (mBlurRadius > 0) {
                displayer.setBlur(mBlurRadius);
            }
            if (mDisplayerAnimator == null) {
                mDisplayerAnimator = new FadeInAnimator();
            }
            displayer.setDisplayerAnimator(mDisplayerAnimator);
            return displayer;
        }
    }

    protected ImageSize getImageSize() {
        if (mMaxWidth > 0 && mMaxHeight > 0) {
            return new ImageSize(mMaxWidth, mMaxHeight);
        } else {
            return null;
        }
    }

}
