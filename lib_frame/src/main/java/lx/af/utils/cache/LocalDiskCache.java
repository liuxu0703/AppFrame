package lx.af.utils.cache;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * author: liuxu
 * date: 15-8-18
 *
 * a simple last-recent-used disk cache.
 *
 * try prune cache dir every 'x' times the method 'add()' is called:
 * if cache file total size or total count is greater than threshold, prune will
 * fire. the prune rule is based on LRU.
 *
 * to manage the cache dir, it is essential to call 'add()' to get cache file name,
 * and 'get()' to generate a name for a new cache file.
 * fail to do this will leave the file un-managed by this class.
 *
 * this class will only manage files with prefix 'cache_', other files in the dir
 * will not be touched.
 *
 */
public class LocalDiskCache {

    private static final String TAG = LocalDiskCache.class.getSimpleName();
    private static final String PREFIX = "cache_";
    private static final String INDEX_FILE = "SimpleDiskCacheIndex";

    private static final int DEFAULT_PRUNE_INTERVAL = 10;
    private static final int DEFAULT_MAX_COUNT = 100;
    private static final long DEFAULT_MAX_SIZE = 5 * 1024 * 1024;

    private static HashMap<String, LocalDiskCache> sCacheMap = new HashMap<>();

    private CacheIndexManager mIndexManager;
    private Executor mExecutor;
    private final Object mPruneLock = new Object();
    private volatile boolean mIsPruning = false;

    private File mCacheDir;
    private long mMaxSize = DEFAULT_MAX_SIZE;
    private int mMaxCount = DEFAULT_MAX_COUNT;
    private int mPruneInterval = DEFAULT_PRUNE_INTERVAL;
    private int mPutCount = 0;


    private FileFilter mFileFilter = new FileFilter() {
        @Override
        public boolean accept(File f) {
            return (!f.isDirectory()) && f.getName().startsWith(PREFIX);
        }
    };

    private Comparator<File> mFileComparator = new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
            return mIndexManager.compare(f1.getName(), f2.getName());
        }
    };

    private LocalDiskCache(String cacheDir) {
        if (TextUtils.isEmpty(cacheDir)) {
            Log.e(TAG, "cache dir not exists !");
        } else {
            mCacheDir = new File(cacheDir);
            if (!mCacheDir.exists()) {
                if (!mCacheDir.mkdirs()) {
                    Log.e(TAG, "cache dir not exists !");
                }
            }
        }
        mIndexManager = new CacheIndexManager(cacheDir);
    }

    /**
     * get a LocalDiskCache instance.
     * if an instance for the given dir already exists, it will be returned.
     * @param cacheDir the cache dir
     * @return instance
     */
    public static LocalDiskCache getInstance(String cacheDir) {
        LocalDiskCache cache = sCacheMap.get(cacheDir);
        if (cache == null) {
            cache = new LocalDiskCache(cacheDir);
            sCacheMap.put(cacheDir, cache);
        }
        return cache;
    }

    public void open() {
        mIndexManager.open();
    }

    public void close() {
        mIndexManager.close();
    }

    /**
     * set max cache size (the prune threshold).
     * better to call this when initialized.
     * @param maxSize the max size
     */
    public void setMaxSize(long maxSize) {
        mMaxSize = maxSize;
    }

    /**
     * set max count (the prune threshold).
     * better to call this when initialized.
     * @param maxCount the max count
     */
    public void setMaxCount(int maxCount) {
        mMaxCount = maxCount;
    }

    /**
     * set prune interval. prune will begin every 'interval' time when 'add()' is called.
     * better to call this when initialized.
     * @param interval the interval
     */
    public void setPruneInterval(int interval) {
        mPruneInterval = interval;
    }

    public void setExecutor(Executor executor) {
        mExecutor = executor;
    }

    /**
     * get dir managed by this instance.
     * @return the cache dir
     */
    public File getCacheDir() {
        return mCacheDir;
    }

    /**
     * get cache file name according to key.
     * cache file name is a concatenation of 'cache_' and MD5 string of 'key'.
     * @param key the key string
     * @return cache file name
     */
    public String get(String key) {
        if (mCacheDir == null) {
            return null;
        }

        // lock the block in case prune is ongoing when get cache.
        // once gained access to the lock, last modify time of the file will be
        // set to current. thus as the last recent used file, it will not be
        // deleted if later prune begins.
        synchronized (mPruneLock) {
            String fileName = PREFIX + toMd5(key);
            File file = new File(mCacheDir, fileName);
            if (file.exists()) {
                mIndexManager.addImpl(fileName);
                return file.getAbsolutePath();
            } else {
                mIndexManager.deleteImpl(fileName);
                return null;
            }
        }
    }

    /**
     * generate a cache file name according to key.
     * cache file name is a concatenation of 'cache_' and MD5 string of 'key'.
     * @param key the key string
     * @return cache file name
     */
    public String add(String key) {
        if (mCacheDir == null) {
            return null;
        }

        // mPutCount starts with 0, so that a prune will be fired on the first
        // time this method is called,
        if (mPutCount % mPruneInterval == 0) {
            prune();
        }
        mPutCount ++;
        String fileName = PREFIX + toMd5(key);
        if (!mIndexManager.isOpen()) {
            // TODO: why is it not open?
            mIndexManager.open();
        }
        mIndexManager.addImpl(fileName);
        return mCacheDir.getAbsolutePath() + "/" + fileName;
    }

    /**
     * remove a cache according to key.
     * @param key the key string
     */
    public void remove(String key) {
        String fileName = PREFIX + toMd5(key);
        mIndexManager.deleteImpl(fileName);
    }

    private void prune() {
        if (mIsPruning) {
            return;
        }

        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadExecutor();
        }

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (mPruneLock) {
                    pruneSync();
                }
            }
        });
    }

    private void pruneSync() {
        mIsPruning = true;
        Log.d(TAG, "prune for " + mCacheDir);
        if (mCacheDir == null) {
            return;
        }

        File[] files = mCacheDir.listFiles(mFileFilter);

        if (files != null && files.length != 0) {
            ArrayList<File> list = new ArrayList<>(files.length);
            Collections.addAll(list, files);
            // sort according to last modify time
            Collections.sort(list, mFileComparator);

            if (list.size() > mMaxCount) {
                // prune for total cache file count.
                // if file count is greater than max, prune the dir to 2/3 of max.
                int pruneBegin = mMaxCount * 2 / 3;  // where to begin prune
                for (int i = pruneBegin; i < list.size(); i ++) {
                    File delFile = list.get(i);
                    if (delFile.delete()) {
                        Log.d(TAG, "reach max count, prune file: " + delFile.getName() + ", idx=" + i);
                        mIndexManager.deleteImpl(delFile.getName());
                    } else {
                        Log.w(TAG, "reach max count, prune file fail: " + delFile + ", idx=" + i);
                    }
                }
            } else {
                // prune for total cache size.
                // if size is greater than max, prune the dir to 2/3 of max.
                long size = 0;  // total cache size
                long sizeForPrune = mMaxSize * 2 / 3;
                int pruneBegin = 0;  // where to begin prune if needed
                for (int i = 0; i < list.size(); i ++) {
                    File f = list.get(i);
                    size += f.length();
                    if (size > mMaxSize) {
                        break;
                    }
                    if (pruneBegin == 0) {
                        if (size > sizeForPrune) {
                            // mark array index for prune begin
                            pruneBegin = i;
                        }
                    }
                }

                if (size > mMaxSize) {
                    for (int i = pruneBegin; i < list.size(); i ++) {
                        File delFile = list.get(i);
                        if (delFile.delete()) {
                            Log.d(TAG, "reach max size, prune file: " + delFile.getName() + ", idx=" + i);
                            mIndexManager.deleteImpl(delFile.getName());
                        } else {
                            Log.w(TAG, "reach max size, prune file fail: " + delFile + ", idx=" + i);
                        }
                    }
                }
            }

            // get this chance to flush
            mIndexManager.flush();
        }

        mIsPruning = false;
    }


    /**
     * manage an index for cache file.
     */
    private static class CacheIndexManager {

        private static class CacheIndexModel {
            @Expose
            public String name;  // file name
            @Expose
            public int impl;  // implement count
            @Expose
            public long lastUsed;  // last used time
        }

        private List<CacheIndexModel> mIndex;
        private HashMap<String, CacheIndexModel> mMap;
        private File mIndexFile;
        private Gson mGson;
        private boolean mIsOpen = false;
        private boolean mIsNeedFlush = false;

        CacheIndexManager(String cacheDir) {
            mIndexFile = new File(cacheDir, INDEX_FILE);
            mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            mMap = new HashMap<>();
        }

        synchronized boolean isOpen() {
            return mIsOpen;
        }

        synchronized void open() {
            if (mIsOpen) {
                return;
            }

            if (mIndexFile.exists()) {
                try {
                    String json = file2String(mIndexFile);
                    Type listType = new TypeToken<List<CacheIndexModel>>(){}.getType();
                    mIndex = mGson.fromJson(json, listType);
                } catch (Exception e) {
                    Log.w(TAG, "read from index file fail", e);
                }
            }

            if (mIndex == null) {
                // read index fail for some reason, start anew.
                mIndex = new ArrayList<>();
            }

            if (mIndex.size() != 0) {
                for (CacheIndexModel model : mIndex) {
                    mMap.put(model.name, model);
                }
            }

            mIsOpen = true;
        }

        synchronized void close() {
            // do not clear mIndex and mMap here, since a prune may be
            // running on another thread. just let GC do its work.
            if (mIsOpen) {
                flush();
                mIsOpen = false;
            }
        }

        synchronized void addImpl(String fileName) {
            checkOpen();
            CacheIndexModel model = mMap.get(fileName);
            if (model != null) {
                model.impl ++;
                model.lastUsed = System.currentTimeMillis();
            } else {
                model = new CacheIndexModel();
                model.name = fileName;
                model.lastUsed = System.currentTimeMillis();
                mMap.put(fileName, model);
                mIndex.add(model);
            }
            mIsNeedFlush = true;
        }

        synchronized void deleteImpl(String fileName) {
            if (mIndex != null && mMap != null) {
                CacheIndexModel model = mMap.get(fileName);
                if (model != null) {
                    mMap.remove(fileName);
                    mIndex.remove(model);
                }
                mIsNeedFlush = true;
            }
        }

        synchronized int getImpl(String fileName) {
            checkOpen();
            CacheIndexModel model = mMap.get(fileName);
            return model != null ? model.impl : 0;
        }

        synchronized long getLastUsed(String fileName) {
            checkOpen();
            CacheIndexModel model = mMap.get(fileName);
            return model != null ? model.lastUsed : 0;
        }

        synchronized int compare(String f1, String f2) {
            checkOpen();
            CacheIndexModel m1 = mMap.get(f1);
            CacheIndexModel m2 = mMap.get(f2);

            int impl1 = m1 != null ? m1.impl : 0;
            int impl2 = m2 != null ? m2.impl : 0;
            int result = impl2 - impl1;

            if (result != 0) {
                return result;
            } else {
                long lastUsed1 = m1 != null ? m1.lastUsed : 0l;
                long lastUsed2 = m2 != null ? m2.lastUsed : 0l;
                return (int) (lastUsed2 - lastUsed1);
            }
        }

        synchronized void flush() {
            if (mIndex != null && mIsNeedFlush) {
                Type listType = new TypeToken<List<CacheIndexModel>>(){}.getType();
                String json = mGson.toJson(mIndex, listType);
                try {
                    string2File(mIndexFile.getAbsolutePath(), json);
                    mIsNeedFlush = false;
                } catch (IOException e) {
                    Log.w(TAG, "write to index file fail", e);
                }
            }
        }

        private void checkOpen() {
            if (!mIsOpen) {
                throw new IllegalStateException("should call open() first");
            }
        }

        /**
         * read string from file. basically same as "cat $filename"
         *
         * @param fileName file path
         * @return the content
         * @throws IOException
         */
        private static String file2String(File fileName) throws IOException {
            InputStreamReader reader = null;
            StringWriter writer = new StringWriter();
            try {
                reader = new InputStreamReader(new FileInputStream(fileName));
                char[] buffer = new char[4 * 1024];
                int n;
                while (-1 != (n = reader.read(buffer))) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                if (reader != null)
                    try {
                        reader.close();
                    } catch (IOException ignore) {
                    }
            }
            return writer.toString();
        }

        /**
         * writes string to file. basically same as "echo -n $string > $filename"
         * @param filename file path
         * @param string string to write
         * @throws IOException
         */
        private static void string2File(String filename, String string) throws IOException {
            FileWriter out = new FileWriter(filename);
            try {
                out.write(string);
            } finally {
                out.close();
            }
        }
    }

    /**
     * get md5 string according to source
     * @param str the source string
     * @return the md5 string
     */
    private static String toMd5(String str) {
        String result = "";
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(str.getBytes("utf-8"));
            result = toHexString(algorithm.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (int b : bytes) {
            if (b < 0) {
                b += 256;
            }
            if (b < 16) {
                hexString.append("0");
            }
            hexString.append(Integer.toHexString(b));
        }
        return hexString.toString();
    }

}
