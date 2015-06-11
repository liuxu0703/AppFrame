package lx.af.utils;

import android.app.Application;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import lx.af.R;

/**
 * Created by liuxu on 15-4-15.
 * operation about path.
 */
public class PathUtils {

    private static final long SPACE_THRESHOLD = 1024 * 1024 * 5;

    private static Application sApp;
    private static String sSdRoot;

    private PathUtils() {
    }

    public static void init(Application app) {
        sApp = app;
        sSdRoot = Environment.getExternalStorageDirectory().getPath() + "/lx_default_dir/";
    }

    /**
     * set a name for dir in sdcard root
     * @param name the name (not the path!)
     */
    public static void setSdRoot(String name) {
        sSdRoot = Environment.getExternalStorageDirectory().getPath() + "/" + name + "/";
    }

    public static String getSdRootDir() {
        return sSdRoot;
    }

    /**
     * where image files are saved by request of gallery or camera.
     */
    public static String generateGallerySavePath() {
        return generateGallerySavePath("");
    }

    /**
     * where image files are saved by request of gallery or camera.
     */
    public static String generateGallerySavePath(String prefix) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return sSdRoot + "gallery/" + prefix + "_" + df.format(new Date()) + ".jpg";
    }

    /**
     * ensure dir or parent dir for a given path exists.
     * when failed to create dir, respective toast will be given.
     * @param path the given path
     * @param isDir whether the given path is a dir or a file
     * @return true on success ; false on fail
     */
    public static boolean ensurePathExistsWithErrorToast(String path, boolean isDir) {
        if (!isSdcardMounted()) {
            AlertUtils.showToastShort(R.string.toast_path_sdcard_not_mounted);
            return false;
        }
        if (isSdcardFull()) {
            AlertUtils.showToastShort(R.string.toast_path_sdcard_full);
            return false;
        }
        boolean success = isDir ? ensureDirExists(path) : ensureParentExists(path);
        if (!success) {
            AlertUtils.showToastShort(R.string.toast_path_create_dir_fail);
            return false;
        } else {
            return true;
        }
    }

    /**
     * ensure dir or parent dir for a given path exists.
     * @param path the given path
     * @param isDir whether the given path is a dir or a file
     * @return true on success ; false on fail
     */
    public static boolean ensurePathExists(String path, boolean isDir) {
        if (!isSdcardMounted()) {
            return false;
        }
        if (isSdcardFull()) {
            return false;
        }
        return isDir ? ensureDirExists(path) : ensureParentExists(path);
    }

    public static boolean isSdcardMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static boolean isSdcardFull() {
        long avail = Environment.getExternalStorageDirectory().getUsableSpace();
        return avail < SPACE_THRESHOLD;
    }

    public static boolean ensureParentExists(String fileName) {
        File file = new File(fileName);
        return ensureDirExists(file.getParent());
    }

    public static boolean ensureDirExists(String dir) {
        File dirFile = new File(dir);
        if (dirFile.exists()) {
            if (dirFile.isDirectory()) {
                return true;
            } else {
                if (!dirFile.delete()) {
                    return false;
                }
            }
        }
        return dirFile.mkdirs();
    }

    /**
     * get cache dir full path of the app.
     * the cache dir is in /data partition and is not visible to users.
     * @return cache dir full path
     */
    public static String getCacheDir() {
        return sApp.getCacheDir().getAbsolutePath() + "/";
    }

    /**
     * get a dir full path under cache dir of the app.
     * the cache dir is in /data partition and is not visible to users.
     * @param subdir sub dir for cache dir
     * @return the subdir full path
     */
    public static String getCacheDir(String subdir) {
        if (TextUtils.isEmpty(subdir)) {
            return sApp.getCacheDir().getAbsolutePath() + "/";
        }
        String dir = sApp.getCacheDir().getAbsolutePath() + "/" + subdir;
        ensureDirExists(dir);
        return dir + "/";
    }

}
