package lx.af.demo.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import lx.af.demo.R;

/**
 * author: lx
 * date: 16-3-22
 */
public final class ImageLoaderHelper {

    private static DisplayImageOptions sAvatarOptions;
    private static DisplayImageOptions sDefaultOptions;

    private ImageLoaderHelper() {}


    // ==========================================


    public static DisplayImageOptions getDefaultOptions() {
        if (sDefaultOptions == null) {
            sDefaultOptions = createOptions(R.drawable.img_default);
        }
        return sDefaultOptions;
    }

    public static DisplayImageOptions getAvatarOptions() {
        if (sAvatarOptions == null) {
            sAvatarOptions = createOptions(R.drawable.ic_default_avatar);
        }
        return sAvatarOptions;
    }

    public static DisplayImageOptions createOptions(int defaultResId) {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultResId)
                .showImageForEmptyUri(defaultResId)
                .showImageOnFail(defaultResId)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .build();
    }


    // ==========================================


    public static void displayImage(ImageView imgView, String uri, int defaultImgId) {
        ImageLoader.getInstance().displayImage(uri, imgView, createOptions(defaultImgId));
    }

    public static void displayImage(ImageView imgView, String uri) {
        if (uri != null) {
            ImageLoader.getInstance().displayImage(uri, imgView);
        }
    }

    public static void displayImageWithDefault(ImageView imgView, String uri) {
        ImageLoader.getInstance().displayImage(uri, imgView, getDefaultOptions());
    }

    public static void displayAvatar(ImageView imgView, String uri) {
        ImageLoader.getInstance().displayImage(uri, imgView, getAvatarOptions());
    }

}
