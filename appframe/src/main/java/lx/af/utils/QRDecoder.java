package lx.af.utils;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import lx.af.activity.ImageSelector.ImageSelectActivity;
import lx.af.base.AbsBaseActivity;
import lx.af.utils.ActivityUtils.ActivityResultCallback;
import lx.af.utils.ActivityUtils.ImageSelector;

/**
 * author: lx
 * date: 16-1-23
 * QRCode decode helper based on goole zxing package.
 */
public class QRDecoder {

    public interface QRDecodeCallback {
        /**
         * pick an image from gallery using {@link ImageSelectActivity} and try decode it as QRCode.
         * if nothing is picked from gallery, this method will not be invoked
         * @param text
         */
        void onQRDecodeResult(String text);
    }

    /**
     * pick an image using {@link ImageSelectActivity} and try decode it as QRCode.
     * @param activity the caller activity
     */
    public static void decodeFromGallery(AbsBaseActivity activity, final QRDecodeCallback c) {
        ImageSelector.of(activity).singleSelect().showCamera(false).start(
                new ActivityResultCallback<ArrayList<String>>() {
            @Override
            public void onActivityResult(int resultCode, @NonNull final ArrayList<String> result) {
                if (result.size() != 0) {
                    c.onQRDecodeResult(decode(result.get(0)));
                }
            }
        });
    }

    /**
     * decode a file as image and try decode the image as QRCode.
     * @return the decoded string, or null on fail
     */
    public static String decode(String imagePath) {
        return decode(BitmapUtils.file2bitmap(imagePath, 200, 200));
    }

    /**
     * try decode a bitmap as QRCode.
     * @return the decoded string, or null on fail
     */
    public static String decode(Bitmap bitmap) {
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        Collection<BarcodeFormat> decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
        decodeFormats.addAll(EnumSet.of(BarcodeFormat.QR_CODE));
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");

        int lWidth = bitmap.getWidth();
        int lHeight = bitmap.getHeight();
        int[] lPixels = new int[lWidth * lHeight];
        bitmap.getPixels(lPixels, 0, lWidth, 0, 0, lWidth, lHeight);
        BinaryBitmap image = new BinaryBitmap(new HybridBinarizer(
                new RGBLuminanceSource(lWidth, lHeight, lPixels)));

        try {
            Result result = new MultiFormatReader().decode(image, hints);
            return result.getText();
        } catch (NotFoundException e) {
            Log.e("liuxu", "decode QRCode bitmap fail", e);
            return null;
        }
    }

}
