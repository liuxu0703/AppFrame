package lx.af.utils.UIL.displayer;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * author: lx
 * date: 16-4-27
 */
public class CircleDisplayer extends BaseDisplayer {

    private final int mBorderWidth;
    private final int mBorderColor;

    public CircleDisplayer() {
        this(0, 0);
    }

    public CircleDisplayer(int borderWidth, int borderColor) {
        mBorderColor = borderColor;
        mBorderWidth = borderWidth;
    }

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware) {
        if (!(imageAware instanceof ImageViewAware)) {
            throw new IllegalArgumentException(
                    "ImageAware should wrap ImageView. ImageViewAware is expected.");
        }
        RoundedDrawable drawable = RoundedDrawable.fromBitmap(bitmap);
        drawable.setOval(true);
        if (mBorderWidth != 0) {
            drawable.setBorderColor(mBorderColor);
            drawable.setBorderWidth(mBorderWidth);
        }
        drawable.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageView imageView = (ImageView) imageAware.getWrappedView();
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageAware.setImageDrawable(drawable);
    }

}
