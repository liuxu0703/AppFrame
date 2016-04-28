package lx.af.utils.UIL;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;

import lx.af.R;
import lx.af.utils.UIL.displayer.AnimateDisplayer;
import lx.af.utils.UIL.displayer.CircleDisplayer;
import lx.af.utils.UIL.displayer.RoundedDisplayer;

/**
 * author: lx
 * date: 16-4-27
 */
public class UILLoader {

    protected static ImageLoader sImageLoader = ImageLoader.getInstance();

    protected ImageView mImageView;
    protected String mUri;
    protected DisplayImageOptions.Builder mOptionsBuilder;
    protected BitmapDisplayer mBitmapDisplayer;

    protected int mBorderWidth;
    protected int mBorderColor;
    protected int mCornerRadius;
    protected boolean mAsCircle;

    public static UILLoader of(ImageView imageView, String uri) {
        return new UILLoader(imageView, uri);
    }

    public void display() {
        if (mOptionsBuilder == null) {
            mOptionsBuilder = getOptionsBuilder();
        }
        if (mBitmapDisplayer == null) {
            mBitmapDisplayer = getBitmapDisplayer();
        }
        mOptionsBuilder.displayer(getBitmapDisplayer());
        sImageLoader.displayImage(mUri, mImageView, mOptionsBuilder.build());
    }

    public UILLoader(ImageView imageView, String uri) {
        mImageView = imageView;
        mUri = uri;
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

    public UILLoader bitmapDisplayer(BitmapDisplayer displayer) {
        mBitmapDisplayer = displayer;
        return this;
    }

    // ====================================================

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

    protected BitmapDisplayer getBitmapDisplayer() {
        if (mAsCircle) {
            return new CircleDisplayer(mBorderWidth, mBorderColor);
        } else if (mBorderWidth > 0 || mCornerRadius > 0) {
            return new RoundedDisplayer(mCornerRadius, mBorderWidth, mBorderColor);
        } else {
            return new AnimateDisplayer();
        }
    }

}
