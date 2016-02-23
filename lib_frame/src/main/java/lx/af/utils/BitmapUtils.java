package lx.af.utils;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;

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
        return file2bitmap(path, 0, 0);
    }

    /**
     * author: liuxu
     * de-sample according to width and height of the given ImageView. if required
     * width or height is smaller than the origin picture's with or height, de-sample it.
     * NOTE: if image quality is your first concern, do not use this method.
     * @param path full path for the picture
     * @param imageView the ImageView used to determine bitmap width and height
     * @return bitmap
     */
    public static Bitmap file2bitmapImageView(String path, ImageView imageView) {
        return file2bitmap(path, imageView.getWidth(), imageView.getHeight());
    }

    /**
     * author: liuxu
     * de-sample according to given width and height. if required width or height is
     * smaller than the origin picture's with or height, de-sample it.
     * NOTE: if image quality is your first concern, do not use this method.
     * @param path full path for the picture
     * @param width the required width
     * @param height the required height
     * @return bitmap
     */
    public static Bitmap file2bitmap(String path, int width, int height) {
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
        return BitmapFactory.decodeFile(path, options);
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
            if (width > height) {
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

    /**
     * add water mark to bitmap
     */
    public static Bitmap addWaterMark(Bitmap bitmap, Bitmap waterMark) {
        if (bitmap == null) {
            return null;
        }
        if (waterMark == null) {
            return bitmap;
        }
        int fw = bitmap.getWidth();
        int fh = bitmap.getHeight();
        int sw = waterMark.getWidth();
        int sh = waterMark.getHeight();
        if (fw < 2 * sw) {
            float scale = (float) fw / (2 * sw);
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            waterMark = Bitmap.createBitmap(waterMark, 0, 0, waterMark.getWidth(), waterMark.getHeight(), matrix, true);

            sw = waterMark.getWidth();
            sh = waterMark.getHeight();
        }

        Bitmap ret = Bitmap.createBitmap(fw, fh, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(ret);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawBitmap(waterMark, fw - sw - 10, fh - sh, null);
        return ret;
    }

}

