package lx.af.utils;

import android.app.Application;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.Formatter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import lx.af.R;
import lx.af.manager.GlobalThreadManager;

/**
 * Created by liuxu on 15-4-15.
 * operation about path.
 */
public class PathUtils {

    private static final long SPACE_THRESHOLD = 1024 * 1024 * 5;

    private static Application sApp;
    private static File sSdRoot;

    private PathUtils() {
    }

    public static void init(Application app) {
        sApp = app;
        sSdRoot = new File(Environment.getExternalStorageDirectory(), "lx");
        GlobalThreadManager.runInThreadPool(new Runnable() {
            @Override
            public void run() {
                clearDir(getTmpDir());
            }
        });
    }

    /**
     * set a name for dir in sdcard root
     * @param name the name (not the path!)
     */
    public static void setSdDir(String name) {
        sSdRoot = new File(Environment.getExternalStorageDirectory(), name);
    }

    public static File getSdDir() {
        return sSdRoot;
    }

    /**
     * get a dir full path under main dir of the app on sdcard root path.
     * the sdcard is  not always available (for one case, sdcard not mounted).
     * @return external cache dir full path, or null if not available.
     */
    public static File getSdDir(String subdir) {
        File dir = new File(sSdRoot, subdir);
        ensureDirExists(dir);
        return dir;
    }

    /**
     * get temp file dir.
     * all files under temp dir will be cleaned on app restart.
     */
    public static File getTmpDir() {
        return getCacheDir("tmp");
    }

    /**
     * get a temp file path.
     * all files under temp dir will be cleaned on app restart.
     */
    public static File generateTmpPath() {
        return generateTmpPath(null);
    }

    /**
     * get a temp file path with suffix.
     * all files under temp dir will be cleaned on app restart.
     */
    public static File generateTmpPath(String suffix) {
        Random random = new Random();
        String prefix = "" + random.nextInt(1000);
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        return new File(getTmpDir(), prefix + "_" + df.format(new Date()) + suffix);
    }

    /**
     * get cache dir full path of the app.
     * the cache dir is in /data partition and is not visible to users.
     * @return cache dir full path
     */
    public static File getCacheDir() {
        return sApp.getCacheDir();
    }

    /**
     * get a dir full path under cache dir of the app.
     * the cache dir is in /data partition and is not visible to users.
     * @param subdir sub dir for cache dir
     * @return the subdir full path
     */
    public static File getCacheDir(String subdir) {
        if (TextUtils.isEmpty(subdir)) {
            return sApp.getCacheDir();
        }
        File dir = new File(sApp.getCacheDir().getAbsolutePath(), subdir);
        ensureDirExists(dir);
        return dir;
    }

    /**
     * get external cache dir of the app.
     * the external cache dir is in sdcard partition and may not always
     * be available (for one case, sdcard not mounted).
     * @return external cache dir full path, or null if not available.
     */
    public static File getExtCacheDir() {
        return sApp.getExternalCacheDir();
    }

    /**
     * get a dir full path under external cache dir of the app.
     * the external cache dir is in sdcard partition and may not always
     * be available (for one case, sdcard not mounted).
     * @return dir full path, or null if not available.
     */
    public static File getExtCacheDir(String subdir) {
        File d = sApp.getExternalCacheDir();
        if (d == null) {
            return null;
        }
        File dir = new File(d, subdir);
        ensureDirExists(dir);
        return dir;
    }

    public static File getExtPublicDCIM() {
        File d = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        ensureDirExists(d);
        return d;
    }

    public static File getExtPublicPictures() {
        File d = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        ensureDirExists(d);
        return d;
    }

    public static String getPathSizeReadable(File path) {
        long size = getPathSize(path);
        return Formatter.formatShortFileSize(sApp, size);
    }

    public static long getPathSize(File file) {
        if (file == null || !file.exists()) {
            return 0;
        }
        long length = 0;
        if (file.isFile()) {
            length = file.length();
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; files != null && i < files.length; i++) {
                length = length + getPathSize(files[i]);
            }
        }
        return length;
    }

    public static boolean clearDir(File dir) {
        if (dir == null || !dir.exists()) {
            return true;
        }
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return true;
        }
        boolean failed = false;
        for (File f : files) {
            if (f.isDirectory()) {
                failed = !deleteDir(f);
            } else {
                failed = !f.delete();
            }
        }
        return failed;
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i ++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    /**
     * ensure dir or parent dir for a given path exists.
     * when failed to create dir, respective toast will be given.
     * @param path the given path
     * @param isDir whether the given path is a dir or a file
     * @return true on success ; false on fail
     */
    public static boolean ensurePathExistsWithErrorToast(File path, boolean isDir) {
        if (!isSdcardMounted()) {
            AlertUtils.toastShort(R.string.toast_path_sdcard_not_mounted);
            return false;
        }
        if (isSdcardFull()) {
            AlertUtils.toastShort(R.string.toast_path_sdcard_full);
            return false;
        }
        boolean success = isDir ? ensureDirExists(path) : ensureParentExists(path);
        if (!success) {
            AlertUtils.toastShort(R.string.toast_path_create_dir_fail);
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
    public static boolean ensurePathExists(File path, boolean isDir) {
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

    public static boolean ensureParentExists(File file) {
        return ensureDirExists(file.getParentFile());
    }

    public static boolean ensureDirExists(File dirFile) {
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


    // ========================================


}
