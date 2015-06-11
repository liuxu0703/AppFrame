package lx.af.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import lx.af.R;

/**
 * Created by liuxu on 15-5-14.
 * create ImageLoaderOptions for Universal Image Loader.
 */
public class ImageLoaderOptionsFactory {

    /**
     * options for display normal picture
     */
    public static DisplayImageOptions createOptions() {
        return createOptions(R.drawable.img_gallery_default);
    }

    /**
     * options for display normal picture
     * @param stubImgId image res id for display on loading and on error.
     */
    public static DisplayImageOptions createOptions(int stubImgId) {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(stubImgId)
                .showImageForEmptyUri(stubImgId)
                .showImageOnFail(stubImgId)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

}
