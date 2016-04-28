package lx.af.utils.UIL.displayer;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * author: lx
 * date: 16-4-27
 */
public class RoundedDisplayer extends AnimateDisplayer {

    private final int mCornerRadius;
    private final int mBorderWidth;
    private final int mBorderColor;

    public RoundedDisplayer(int cornerRadius) {
        this(cornerRadius, 0, 0);
    }

    public RoundedDisplayer(int cornerRadius, int borderWidth, int borderColor) {
        mCornerRadius = cornerRadius;
        mBorderColor = borderColor;
        mBorderWidth = borderWidth;
    }

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        if (!(imageAware instanceof ImageViewAware)) {
            throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
        }
        RoundedDrawable drawable = RoundedDrawable.fromBitmap(bitmap);
        drawable.setCornerRadius(mCornerRadius);
        if (mBorderWidth != 0) {
            drawable.setBorderColor(mBorderColor);
            drawable.setBorderWidth(mBorderWidth);
        }
        imageAware.setImageDrawable(drawable);
    }

}
