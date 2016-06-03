package lx.af.utils.UIL;

import android.media.ExifInterface;

/**
 * author: lx
 * date: 16-5-31
 */
public class ImageInfo {

    public String uri; // picture uri

    public int width; // picture width, in pixel

    public int height; // picture height, in pixel

    public int size; // picture size, in byte

    public ExifInterface exif; // picture exif info

    public ImageInfo() {}

}
