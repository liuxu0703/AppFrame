package lx.af.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
 *
 * operations about bitmap.
 */
public final class BitmapUtils {

    private BitmapUtils() {
    }

    /**
     * author: liuxu
     * decode bitmap with minimum memory.
     * NOTE: if image quality is your first concern, do not use this method.
     * @param path full path for the picture
     * @return bitmap
     */
    public static Bitmap decodeBitmap(String path) {
        return decodeBitmapForSize(path, 0, 0);
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
    public static Bitmap decodeBitmapImageView(String path, ImageView imageView) {
        return decodeBitmapForSize(path, imageView.getWidth(), imageView.getHeight());
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
    public static Bitmap decodeBitmapForSize(String path, int width, int height) {
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
        if (null == bitmap)
            return new byte[] {};
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
     * get view screen shot bitmap.
     * @param v the view
     * @return bitmap
     */
    public static Bitmap captureView(View v) {
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        return v.getDrawingCache();
    }

    /**
     * get activity screen shot bitmap. system status bar is not included.
     * @param activity the activity
     * @return bitmap
     */
    public static Bitmap captureActivityView(Activity activity) {
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

}

