package lx.af.utils.log;

import android.app.Application;
import android.content.Context;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import lx.af.utils.FileUtils;

/**
 * author: liuxu
 * date: 2015-01-14
 *
 * log configuration and operations about log.
 */
public class LogUtils {

    public static boolean LOG_ENABLED = true;
    public static boolean LOG2FILE = LOG_ENABLED && true;
    public static boolean LOG2CONSOLE = LOG_ENABLED && true;

    public static String LOG_PREFIX = "MyApp";
    public static String LOG_SUFFIX = ".log";
    public static String LOG_DIR;
    public static long LOG_MAX_SIZE = 1024 * 1024 * 1; // max size for a single log file, in byte
    public static int LOG_MAX_COUNT = 8; // max log file count

    public static String LOG_PACK_SUFFIX = ".zip";
    public static int LOG_PACK_MAX_COUNT = 5; // max log pack count

    private static Application sApp;
    private static String TAG;

    /**
     * init. this should be called in Application.onCreate().
     */
    public static void init(Application app) {
        sApp = app;
        TAG = sApp.getClass().getSimpleName();
        LOG_DIR = getLogDir(sApp);
        purge();

        Log.setFilePathGenerator(
                new FilePathGenerator.LimitSizeFilePathGenerator(
                        LOG_DIR, LOG_PREFIX, LOG_SUFFIX, LOG_MAX_SIZE));
        Log.addLogFilter(new LogFilter.LevelFilter(Log.LEVEL.VERBOSE));
        Log.setLog2FileEnabled(LOG2FILE);
        Log.setLog2ConsoleEnabled(LOG2CONSOLE);
    }

    public static String saveLogToSdcard(String prefix) {
        File logPackDirFile = getLogPackDir();
        if (logPackDirFile == null) {
            Log.e(TAG, "save log to sdcard failed, sdcard not available");
            return null;
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String saveName = new StringBuilder()
                .append(prefix).append("-")
                .append(df.format(new Date())).append(".zip").toString();

        File savePathFile = new File(logPackDirFile, saveName);
        if (!logPackDirFile.isDirectory()) {
            if (!logPackDirFile.delete()) {
                Log.e(TAG, "save log to sdcard failed, remove dir fail");
                return null;
            }
        }
        if (!logPackDirFile.exists()) {
            if (!logPackDirFile.mkdirs()) {
                Log.e(TAG, "save log to sdcard failed, mkdir fail");
                return null;
            }
        }

        String savePath = savePathFile.getAbsolutePath();
        try {
            FileUtils.dir2zip(LOG_DIR, savePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // scan file into database so that we can view it through USB MTP
        FileUtils.scanFile(sApp, savePath);
        return savePath;
    }

    public static String saveLogToSdcard() {
        return saveLogToSdcard("LogPack");
    }

    // TODO: save log to server
    public static void saveLogToServer() {

    }

    // where log is instantly write to.
    private static String getLogDir(Context context) {
        return context.getCacheDir() + "/logs";
    }

    // where the saved log (by saveLogToSdcard() method) goes.
    private static File getLogPackDir() {
        return sApp.getExternalFilesDir("logs");
    }

    // delete overdue logs
    private static void purge() {
        // purge log dir
        purge(LOG_DIR, new FileFilter() {
            @Override
            public boolean accept(File file) {
                String name = file.getName();
                return name.startsWith(LOG_PREFIX) && name.endsWith(LOG_SUFFIX);
            }
        }, LOG_MAX_COUNT);

        // purge log pack dir
        File logPackDirFile = getLogPackDir();
        if (logPackDirFile != null) {
            purge(logPackDirFile.getAbsolutePath(), new FileFilter() {
                @Override
                public boolean accept(File file) {
                    String name = file.getName();
                    return name.endsWith(LOG_PACK_SUFFIX);
                }
            }, LOG_PACK_MAX_COUNT);

            // scan log package dir
            FileUtils.scanFile(sApp, logPackDirFile.getAbsolutePath());
        }
    }

    private static void purge(String dir, FileFilter filter, int maxCount) {
        List<File> files = FileUtils.getSubFiles(dir, filter, 1, false);
        if (files == null || files.size() == 0) {
            return;
        }

        if (files.size() >= maxCount) {
            // sort according to modify time
            Collections.sort(files, new Comparator<File>() {
                @Override
                public int compare(File f1, File f2) {
                    return (int) (f1.lastModified() - f2.lastModified());
                }
            });

            // delete the oldest
            int deleteCount = files.size() - maxCount;
            for (int i = 0; i < deleteCount; i++) {
                File f = files.get(i);
                //Logger.d(IvBabyApplication.TAG, "purge log " + f);
                f.delete();
            }
        }
    }
}
