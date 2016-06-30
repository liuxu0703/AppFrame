package lx.af.manager.VoiceManager;

import android.content.Context;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import lx.af.utils.NetStateUtils;
import lx.af.utils.PathUtils;
import lx.af.utils.cache.LocalDiskCache;

import static lx.af.manager.VoiceManager.VoiceManager.TAG;

/**
 * author: liuxu
 * date: 15-8-17.
 *
 * generate audio file for later played by MediaPlayer.
 */
class VoiceProvider {

    private LocalDiskCache mCache;
    private Executor mExecutor;

    public VoiceProvider(Context context, Executor executor) {
        mExecutor = executor;
        mCache = LocalDiskCache.getInstance(PathUtils.getCacheDir("voice_cache").getAbsolutePath());
        mCache.setExecutor(mExecutor);
        mCache.setMaxCount(60);
    }

    void init() {
        mCache.open();
    }

    void release() {
        mCache.close();
    }

    void fillVoice(final Voice voice, final Callback callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                // try cache first
                if (fillVoiceFromCache(voice, callback)) {
                    return;
                }

                switch (voice.getType()) {
                    case Voice.TYPE_MESSAGE: {
                        // generate file with baidu speech
                        fillVoiceForText(voice, callback);
                        break;
                    }
                    case Voice.TYPE_URL: {
                        // download file from net
                        fillVoiceForUrl(voice, callback);
                        break;
                    }
                    case Voice.TYPE_FILE: {
                        // use file itself
                        File file = new File(voice.getContent());
                        if (file.exists()) {
                            voice.setPath(file.getAbsolutePath());
                            callback.onResult(voice);
                        } else {
                            Log.e(TAG, "provider, local file not exists: " + file);
                            callback.onError(voice);
                        }
                        break;
                    }
                    default: {
                        // wrong type
                        Log.e(TAG, "provider, type wrong: " + voice.getType());
                        callback.onError(voice);
                        break;
                    }
                }
            }
        });
    }

    // ========================================

    private boolean fillVoiceFromCache(Voice voice, final Callback callback) {
        String path = mCache.get(voice.getContent());
        if (path != null) {
            voice.setPath(path);
            callback.onResult(voice);
            return true;
        } else {
            return false;
        }
    }

    private void fillVoiceForText(final Voice voice, final Callback callback) {
        callback.onError(voice);
    }

    private void fillVoiceForUrl(final Voice voice, final Callback callback) {
        String path = null;
        int retry = 0;
        while (path == null && retry < 3 && NetStateUtils.isNetConnected()) {
            if (retry != 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException ignore) {
                }
            }
            path = downloadFromUrl(voice.getContent());
            retry ++;
        }

        if (path == null) {
            Log.e(TAG, "provider, download file fail: " + voice.getContent());
            callback.onError(voice);
        } else {
            voice.setPath(path);
            callback.onResult(voice);
        }
    }

    private String downloadFromUrl(String url) {
        String path = mCache.add(url);
        if (path == null) {
            return null;
        }

        InputStream is = null;
        OutputStream os = null;

        try {
            URL u = new URL(url);
            URLConnection con = u.openConnection();
            is = con.getInputStream();
            os = new FileOutputStream(path);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) >= 0) {
                os.write(buffer, 0, bytesRead);
            }

            os.flush();
            return path;
        } catch (Exception e) {
            Log.w(TAG, "provider, fail to get from url", e);
            return null;
        } finally {
            closeSilently(is);
            closeSilently(os);
        }
    }

    private static void closeSilently(Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (Throwable t) {
            // Do nothing
        }
    }

    // ========================================

    /**
     * callback for VoiceProvider
     */
    interface Callback {

        /**
         * called when Voice object is successfully generated
         * @param voice the result
         */
        void onResult(Voice voice);

        /**
         * called on error
         */
        void onError(Voice voice);

    }

}
