package lx.af.utils.UIL.displayer;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * author: lx
 * date: 16-4-28
 */
public class DefaultDisplayer extends BaseDisplayer {

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware) {
        if (!(imageAware instanceof ImageViewAware)) {
            throw new IllegalArgumentException(
                    "ImageAware should wrap ImageView. ImageViewAware is expected.");
        }
        imageAware.setImageBitmap(bitmap);
    }

}
