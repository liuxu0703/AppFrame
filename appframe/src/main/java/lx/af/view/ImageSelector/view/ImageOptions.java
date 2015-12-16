package lx.af.view.ImageSelector.view;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import lx.af.R;

/**
 * Created by liuxu on 15-4-22.
 *
 */
public class ImageOptions {

    private static DisplayImageOptions sDisplayImageOptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.img_gallery_default)
            .showImageForEmptyUri(R.drawable.img_gallery_default)
            .showImageOnFail(R.drawable.img_gallery_default)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    private static DisplayImageOptions sScrollImageOptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.img_gallery_default)
            .showImageForEmptyUri(R.drawable.img_gallery_default)
            .showImageOnFail(R.drawable.img_gallery_default)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .delayBeforeLoading(350)
            .build();


    public static DisplayImageOptions getDisplayImageOptions() {
        return sDisplayImageOptions;
    }

    public static DisplayImageOptions getScrollImageOptions() {
        return sScrollImageOptions;
    }

}
