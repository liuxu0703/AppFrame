package lx.af.utils.UIL.displayer;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * author: lx
 * date: 16-4-27
 */
public class RoundedDisplayer extends BaseDrawableDisplayer {

    private final int mCornerRadius;
    private final int mBorderWidth;
    private final int mBorderColor;
    private boolean mAsSquare = false;

    public RoundedDisplayer(int cornerRadius) {
        this(cornerRadius, 0, 0);
    }

    public RoundedDisplayer(int cornerRadius, int borderWidth, int borderColor) {
        mCornerRadius = cornerRadius;
        mBorderColor = borderColor;
        mBorderWidth = borderWidth;
    }

    public RoundedDisplayer setAsSquare(boolean asSquare) {
        mAsSquare = asSquare;
        return this;
    }

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware) {
        if (!(imageAware instanceof ImageViewAware)) {
            throw new IllegalArgumentException(
                    "ImageAware should wrap ImageView. ImageViewAware is expected.");
        }
        if (mAsSquare) {
            ImageView imageView = (ImageView) imageAware.getWrappedView();
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        imageAware.setImageDrawable(createDisplayDrawable(bitmap));
    }

    @Override
    public Drawable createDisplayDrawable(Bitmap bitmap) {
        RoundedDrawable drawable = RoundedDrawable.fromBitmap(bitmap);
        drawable.setCornerRadius(mCornerRadius);
        if (mBorderWidth != 0) {
            drawable.setBorderColor(mBorderColor);
            drawable.setBorderWidth(mBorderWidth);
        }
        if (mAsSquare) {
            drawable.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        return drawable;
    }

}
