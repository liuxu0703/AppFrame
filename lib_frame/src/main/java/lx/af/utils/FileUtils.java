package lx.af.utils;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import lx.af.base.AbsBaseApp;

/**
 * created by liuxu. many methods here are collected from various sites.
 * date: 2014-11-18
 *
 * operations about file.
 */
public class FileUtils {

    public static final String TAG = FileUtils.class.getSimpleName();

    // ===============================================================
    // zip operations

    /**
     * author: liuxu
     * pack dir into a zip file
     * @param dir the dir to zip.
     * @param zipName the target zip file path
     * @throws java.io.IOException
     */
    public static void dir2zip(String dir, String zipName) throws IOException {
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            throw new IOException("dir not exists");
        }
        File[] fileList = dirFile.listFiles();
        if (fileList == null || fileList.length == 0) {
            throw new IOException("dir is empty");
        }

        final int bufferSize = 1024;
        ZipOutputStream zos;
        ZipEntry ze;
        byte[] buf = new byte[bufferSize];
        int readLen;

        zos = new ZipOutputStream(new FileOutputStream(zipName));
        for(File f : fileList) {
            ze = new ZipEntry(getRelativeFileName(dir, f));
            ze.setSize(f.length());
            ze.setTime(f.lastModified());
            zos.putNextEntry(ze);
            InputStream is = new BufferedInputStream(new FileInputStream(f));
            while ((readLen=is.read(buf, 0, bufferSize)) != -1) {
                zos.write(buf, 0, readLen);
            }
            is.close();
        }
        zos.close();
    }

    // returns a file path relative to baseDir
    private static String getRelativeFileName(String baseDir, File file){
        File real = file;
        File base = new File(baseDir);
        String ret = real.getName();
        while (true) {
            real = real.getParentFile();
            if(real == null)
                break;
            if(real.equals(base)) {
                break;
            } else {
                ret = real.getName() + "/" + ret;
            }
        }
        return ret;
    }


    // ===============================================================
    // get sub files

    /**
     * author: liuxu
     * get a list of all files and folders under a folder, including itself
     * @param folder the folder
     */
    public static List<File> getSubFiles(String folder) {
        return getSubFiles(folder, null, 0, true);
    }

    /**
     * author: liuxu
     * get a list of files and folders under a folder.
     * @param folder the folder
     * @param filter a FileFilter to filter files
     * @param depth only search files at the depth limit. 0 and negative
     *              value will be considered as no limit for depth.
     * @param includeSelf whether the folder itself should be added into the list
     */
    public static List<File> getSubFiles(String folder, FileFilter filter,
                                         int depth, boolean includeSelf) {
        File folderFile = new File(folder);
        SubFileHandler handler;
        try {
            handler = new SubFileHandler(folderFile, filter, depth, includeSelf);
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
            return null;
        }
        return handler.getSubFiles();
    }

    // ===============================================================
    // about media store

    /**
     * get file path in file system by content uri in MediaStore
     * @param activity activity
     * @param contentUri uri
     * @return path in file system
     */
    public static String getRealPathFromUri(Activity activity, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        // 4.0 and above version the cursor will close automatically
        if (Build.VERSION.SDK_INT < 14) {
            cursor.close();
        }
        return path;
    }

    /**
     * author: liuxu
     * scan a file or folder
     * @param file path the file or folder to be scanned
     */
    public static void scanFile(String file) {
        scanFile(file, null);
    }

    /**
     * author: liuxu
     * scan a file or folder. ScannerListener.onScanCompleted() will
     * be invoked in main thread (UI thread) when scan is done.
     * see ScannerListener for details.
     * @param file path the file or folder to be scanned
     * @param listener callback when scan is done, can be null
     */
    public static void scanFile(String file, MediaScannerHelper.ScannerListener listener) {
        MediaScannerHelper.newInstance(AbsBaseApp.getInstance(), file, listener).scan();
    }

    // ===============================================================
    // file operations, mostly from android.os.FileUtils

    /**
     * get the available size of sdcard, in MB.
     */
    @SuppressWarnings("deprecation")
    public static long getAvailaleSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return (availableBlocks * blockSize) / 1024 / 1024; // in MB
    }

    /**
     * copy a file from srcFile to destFile, return true if succeed,
     * false otherwise.
     */
    public static boolean copyFile(String origin, String target) {
        return copyFile(origin, target, false);
    }

    /**
     * copy a file from srcFile to destFile, return true if succeed,
     * false otherwise.
     * @param createParent
     *            if set to true, the parent dir of target file will
     *            be created if not exists.
     *            if set to false, when the parent dir does not exists,
     *            the method will just return false.
     * @return true if succeed, false otherwise.
     */

    public static boolean copyFile(String origin, String target, boolean createParent) {
        File srcFile = new File(origin);
        File destFile = new File(target);
        if (!srcFile.exists()) {
            return false;
        }
        File destParent = destFile.getParentFile();
        if (!destParent.exists()) {
            if (createParent) {
                if (!destParent.mkdirs()) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return copyFile(srcFile, destFile);
    }

    /**
     * copy a file from srcFile to destFile, return true if succeed,
     * false otherwise.
     * @return true if succeed, false otherwise.
     */
    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    /**
     * Copy data from a source stream to destFile.
     * Return true if succeed, return false if failed.
     */
    public static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            if (destFile.exists()) {
                if (!destFile.delete()) {
                    return false;
                }
            }
            FileOutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.flush();
                try {
                    out.getFD().sync();
                } catch (IOException ignore) {
                }
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Read a text file into a String.
     */
    public static String readTextFile(File file) throws IOException {
        return readTextFile(file, 0, null);
    }

    /**
     * Read a text file into a String, optionally limiting the length.
     * @param file
     *            to read (will not seek, so things like /proc files are OK)
     * @param max
     *            length (positive for head, negative of tail, 0 for no limit)
     * @param ellipsis
     *            to add if the file was truncated (can be null)
     * @return the contents of the file, possibly truncated
     * @throws java.io.IOException
     *             if something goes wrong reading the file
     */
    public static String readTextFile(File file, int max, String ellipsis) throws IOException {
        InputStream input = new FileInputStream(file);
        // wrapping a BufferedInputStream around it because when reading /proc with unbuffered
        // input stream, bytes read not equal to buffer size is not necessarily the correct
        // indication for EOF; but it is true for BufferedInputStream due to its implementation.
        BufferedInputStream bis = new BufferedInputStream(input);
        try {
            long size = file.length();
            if (max > 0 || (size > 0 && max == 0)) {
                // "head" mode: read the first N bytes
                if (size > 0 && (max == 0 || size < max)) {
                    max = (int) size;
                }
                byte[] data = new byte[max + 1];
                int length = bis.read(data);
                if (length <= 0) {
                    return "";
                }
                if (length <= max) {
                    return new String(data, 0, length);
                }
                if (ellipsis == null) {
                    return new String(data, 0, max);
                }
                return new String(data, 0, max) + ellipsis;
            } else if (max < 0) {
                // "tail" mode: keep the last N
                int len;
                boolean rolled = false;
                byte[] last = null;
                byte[] data = null;
                do {
                    if (last != null) {
                        rolled = true;
                    }
                    byte[] tmp = last;
                    last = data;
                    data = tmp;
                    if (data == null) {
                        data = new byte[-max];
                    }
                    len = bis.read(data);
                } while (len == data.length);

                if (last == null && len <= 0) {
                    return "";
                }
                if (last == null) {
                    return new String(data, 0, len);
                }
                if (len > 0) {
                    rolled = true;
                    System.arraycopy(last, len, last, 0, last.length - len);
                    System.arraycopy(data, 0, last, last.length - len, len);
                }
                if (ellipsis == null || !rolled) {
                    return new String(last);
                }
                return ellipsis + new String(last);
            } else {
                // "cat" mode: size unknown, read it all in streaming fashion
                ByteArrayOutputStream contents = new ByteArrayOutputStream();
                int len;
                byte[] data = new byte[1024];
                do {
                    len = bis.read(data);
                    if (len > 0) {
                        contents.write(data, 0, len);
                    }
                } while (len == data.length);
                return contents.toString();
            }
        } finally {
            bis.close();
            input.close();
        }
    }

    /**
     * Writes string to file. Basically same as "echo -n $string > $filename"
     * @throws java.io.IOException
     */
    public static void stringToFile(String filename, String string) throws IOException {
        FileWriter out = new FileWriter(filename);
        try {
            out.write(string);
        } finally {
            out.close();
        }
    }

    public static void closeSilently(Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (Throwable t) {
            // Do nothing
        }
    }


    // ===============================================================

    /**
     * get suffix of a file (string after the last '.')
     * @param file the file
     * @return suffix, or "" if file has no suffix
     */
    public static String getFileSuffix(File file) {
        return getFileSuffix(file.getName());
    }

    /**
     * get suffix of a file (string after the last '.')
     * @param filename the file base name
     * @return suffix, or "" if file has no suffix
     */
    public static String getFileSuffix(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return "";
    }

    /**
     * get md5 string of the file
     * @param file the file
     * @return md5 string
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest;
        FileInputStream in;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

}
