package lx.af.utils.m3u.M3uAudio;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import lx.af.utils.m3u.M3uParser.Element;
import lx.af.utils.m3u.M3uParser.Playlist;

import static lx.af.utils.m3u.M3uAudio.M3uAudioPlayer.TAG;

/**
 * author: lx
 * date: 16-1-12
 */
class M3uProvider {

    private static final int MAX_RETRY_COUNT = 10;

    private String mHlsUrl;
    private String mStreamUrl;
    private Playlist mStreamList;
    private Playlist mCurrentPlayList;
    private Playlist mLastPlayList;
    private LinkedList<M3uElement> mElementList = new LinkedList<>();
    private HashMap<URI, M3uElement> mUrlMap = new HashMap<>();

    private HandlerThread mThread;
    private Handler mHandler;
    private long mLastRequestTime;

    public M3uProvider() {
        mThread = new HandlerThread("M3uProviderThread");
        mThread.start();
        mHandler = new Handler(mThread.getLooper());
    }

    public void release() {
        mHandler.removeCallbacks(null);
        mThread.quit();
    }

    public void prepare(String m3u_url) {
        mHlsUrl = m3u_url;
        refresh();
    }

    public synchronized M3uElement getNext(M3uElement current) {
        if (current != null && current.isFirst()) {
            mHandler.removeCallbacks(mNextPlaylistRunnable);
            mHandler.post(mNextPlaylistRunnable);
        }
        if (mElementList.size() == 0) {
            for (int i = 0; i < MAX_RETRY_COUNT; i ++) {
                refreshRetry(i);
                if (mElementList.size() != 0) {
                    break;
                }
            }
        }
        if (mElementList.size() == 0) {
            return null;
        }

        int idx = mElementList.indexOf(current);
        if (idx == mElementList.size() - 1) {
            for (int i = 0; i < MAX_RETRY_COUNT; i ++) {
                refreshRetry(i);
                if (idx != mElementList.size() - 1) {
                    break;
                }
            }
        }
        if (idx == mElementList.size() - 1) {
            return null;
        } else {
            M3uElement next = mElementList.get(idx + 1);
            next.setUsed(true);
            mElementList.remove(current);
            return next;
        }
    }

    private void refresh() {
        long interval = System.currentTimeMillis() - mLastRequestTime;
        long minReloadDelay = mCurrentPlayList == null ? 0 : mCurrentPlayList.getMinimumReloadDelay();
        long delta = minReloadDelay - interval;
        if (delta > 0) {
            try {
                Thread.sleep(delta);
            } catch (InterruptedException ignore) {
            }
        }
        refreshPlaylist();
    }

    private void refreshRetry(int retryCount) {
        long interval = System.currentTimeMillis() - mLastRequestTime;
        long delta = mCurrentPlayList.getRetryReloadDelay(retryCount) - interval;
        if (delta > 0) {
            try {
                Thread.sleep(delta);
            } catch (InterruptedException ignore) {
            }
        }
        refreshPlaylist();
    }

    private synchronized void refreshPlaylist() {
        Playlist playlist = M3uFileDownloader.downloadAsPlaylist(mHlsUrl);
        Log.d(TAG, "parse playlist result: " + (playlist != null ? playlist.dump() : null));
        mLastRequestTime = System.currentTimeMillis();
        if (playlist == null) {
            return;
        }

        List<Element> list = playlist.getElements();
        for (int i = 0; i < list.size(); i ++) {
            Element element = list.get(i);
            if (element.isPlayList()) {
                // #EXT-X-STREAM-INF
                mStreamUrl = mHlsUrl;
                mStreamList = playlist;
                mHlsUrl = element.getURI().toString();
                refreshPlaylist();
                return;
            } else {
                // #EXTINF
                URI uri = element.getURI();
                M3uElement e = mUrlMap.get(uri);
                if (e == null) {
                    e = new M3uElement(element, playlist, i == 0);
                    mUrlMap.put(uri, e);
                    mElementList.add(e);
                }
            }
        }

        mLastPlayList = mCurrentPlayList;
        mCurrentPlayList = playlist;
        Log.d(TAG, "refresh playlist done: " + playlist);
    }

    private Runnable mNextPlaylistRunnable = new Runnable() {
        @Override
        public void run() {
            refresh();
        }
    };
}
