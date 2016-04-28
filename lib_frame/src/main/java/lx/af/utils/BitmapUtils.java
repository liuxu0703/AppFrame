package lx.af.utils;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * created by liuxu. many methods here are collected from various site.
 * operations about bitmap.
 */
public final class BitmapUtils {

    private static Application sApp;

    private BitmapUtils() {
    }

    public static void init(Application app) {
        sApp = app;
    }

    /**
     * decode bitmap from resource id.
     */
    public static Bitmap res2bitmap(int resId) {
        return BitmapFactory.decodeResource(sApp.getResources(), resId, null);
    }

    /**
     * decode bitmap from resource id
     */
    public static Bitmap res2bitmap(int resId, BitmapFactory.Options opt) {
        return BitmapFactory.decodeResource(sApp.getResources(), resId, opt);
    }

    /**
     * author: liuxu
     * decode bitmap with minimum memory.
     * NOTE: if image quality is your first concern, do not use this method.
     * @param path full path for the picture
     * @return bitmap
     */
    public static Bitmap file2bitmap(String path) {
        return file2bitmap(path, 0, 0, true);
    }

    public static Bitmap file2bitmap(String path, int width, int height) {
        return file2bitmap(path, width, height, true);
    }

    /**
     * author: liuxu
     * de-sample according to given width and height. if required width or height is
     * smaller than the origin picture's with or height, de-sample it.
     * NOTE: if image quality is your first concern, do not use this method.
     * @param path full path for the picture
     * @param width the required width
     * @param height the required height
     * @param considerExifRotate true and the bitmap will be rotated according to exif (if exists)
     * @return bitmap
     */
    public static Bitmap file2bitmap(String path, int width, int height, boolean considerExifRotate) {
        final BitmapFactory.Options options = new BitmapFactory.Options();

        if (width != 0 && height != 0) {
            // decode with inJustDecodeBounds=true to check size
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            // calculate inSampleSize according to the requested size
            options.inSampleSize = calculateInSampleSize(options, width, height);
            options.inJustDecodeBounds = false;
        }

        // decode bitmap with the calculated inSampleSize
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        if (considerExifRotate) {
            int exifDegree = getFileExifDegree(path);
            if (exifDegree == 0) {
                return bitmap;
            } else {
                return rotateBitmap(bitmap, exifDegree);
            }
        } else {
            return bitmap;
        }
    }

    public static int getFileExifDegree(String path) {
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Bitmap rotateBitmap(Bitmap src, int degree) {
        Bitmap ret = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        } catch (OutOfMemoryError ignore) {
        }
        if (ret == null) {
            ret = src;
        }
        if (src != ret) {
            src.recycle();
        }
        return ret;
    }

    /**
     * author: liuxu
     * de-sample according to given width and height
     * @param options options
     * @param reqWidth the required width
     * @param reqHeight the required height
     * @return the calculated sample size
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;

        int initSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width < height) {
                initSize = Math.round((float) height / (float) reqHeight);
            } else {
                initSize = Math.round((float) width / (float) reqWidth);
            }
        }

        /*
         * the function rounds up the sample size to a power of 2 or multiple of 8 because
         * BitmapFactory only honors sample size this way. For example, BitmapFactory
         * down samples an image by 2 even though the request is 3.
         */
        int roundedSize;
        if (initSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    /**
     * author: liuxu
     * save  bitmap into file
     * @param bitmap the bitmap
     * @param path full path of the file
     * @return true if success
     */
    public static boolean saveBitmap(Bitmap bitmap, String path) {
        return saveBitmap(bitmap, path, 100);
    }

    /**
     * author: liuxu
     * save  bitmap into file
     * @param bitmap the bitmap
     * @param path full path of the file
     * @param quality  Hint to the compressor, 0-100. 0 meaning compress for
     *                 small size, 100 meaning compress for max quality. Some
     *                 formats, like PNG which is lossless, will ignore the
     *                 quality setting
     * @return true if success
     */
    public static boolean saveBitmap(Bitmap bitmap, String path, int quality) {
        if (bitmap == null) {
            return false;
        }
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
                //Log.e(TAG, "saveBitmap, mkdir for parent fail");
                return false;
            }
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            //Log.d(TAG, "saveBitmap fail", e);
            return false;
        }
        return true;
    }

    /**
     * convert bitmap into byte[]
     * @param bitmap the source bitmap
     * @return the byte[]
     */
    public static byte[] bitmap2bytes(Bitmap bitmap) {
        return bitmap2bytes(bitmap, 100);
    }

    /**
     * convert bitmap into byte[]
     * @param bitmap the source bitmap
     * @param quality  Hint to the compressor, 0-100. 0 meaning compress for
     *                 small size, 100 meaning compress for max quality. Some
     *                 formats, like PNG which is lossless, will ignore the
     *                 quality setting
     * @return the byte[]
     */
    public static byte[] bitmap2bytes(Bitmap bitmap, int quality) {
        if (null == bitmap) return new byte[] {};
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] result = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * calculate hashcode for a bitmap.
     * only calculate part of the first row for performance purpose.
     * if you want to involve every pixels, use {@link #bitmap2hashFull(Bitmap)}.
     * @return hash code
     */
    public static long bitmap2hash(Bitmap bitmap) {
        if (bitmap == null) return 0;
        long hash = 31;
        int end = bitmap.getWidth() / 5;
        for (int x = 0; x < end; x ++) {
            hash *= (bitmap.getPixel(x, 0) + 31);
        }
        return hash;
    }

    /**
     * calculate hashcode for a bitmap.
     * all pixels in the bitmap is considered for the hashcode, and if you want
     * a better performance for the calculation, use {@link #bitmap2hash(Bitmap)}.
     * @return hash code
     */
    public static long bitmap2hashFull(Bitmap bitmap) {
        if (bitmap == null) return 0;
        long hash = 31;
        for (int x = 0; x < bitmap.getWidth(); x ++) {
            for (int y = 0; y < bitmap.getHeight(); y ++) {
                hash *= (bitmap.getPixel(x,y) + 31);
            }
        }
        return hash;
    }

    /**
     * get view screen shot bitmap.
     * @param v the view
     * @return bitmap
     */
    public static Bitmap capture(View v) {
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        return v.getDrawingCache();
    }

    /**
     * get activity screen shot bitmap. system status bar is not included.
     * @param activity the activity
     * @return bitmap
     */
    public static Bitmap capture(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();
        Bitmap b = Bitmap.createBitmap(bitmap, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    public static Bitmap convertToBlackWhite(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] pixels = new int[width * height];

        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];

                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
        return ThumbnailUtils.extractThumbnail(newBmp, 380, 460);
    }

    public static WatermarkHelper.EditBuilder addWaterMark() {
        return new WatermarkHelper.EditBuilder();
    }

}

