package lx.af.demo.utils;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lx.af.utils.PathUtils;

/**
 * author: lx
 * date: 16-1-20
 */
public final class Paths {

    public static final File CAMERA_PATH =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

    public static final File QR_CODE_PATH = PathUtils.getSdDir("qr_code");

    public static final File CROP_PATH = PathUtils.getSdDir("crop_image");

    public static File generateCropImagePath() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        return new File(CROP_PATH, df.format(new Date()) + ".jpg");
    }

    public static File generateCameraImagePath() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        return new File(CAMERA_PATH, df.format(new Date()) + ".jpg");
    }

    public static File generateQRCodeImagePath() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        return new File(QR_CODE_PATH, df.format(new Date()) + ".jpg");
    }

}
