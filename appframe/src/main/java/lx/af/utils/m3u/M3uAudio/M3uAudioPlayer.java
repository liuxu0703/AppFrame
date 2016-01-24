package lx.af.utils.m3u.M3uAudio;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.util.ArrayList;

/**
 * author: lx
 * date: 16-1-12
 */
public class M3uAudioPlayer {

    public static final String TAG = "M3uPlayer";

    private static final int PLAYER_COUNT = 2;

    private HandlerThread mThread;
    private Handler mHandler;
    private ArrayList<AudioPlayer> mPlayerList = new ArrayList<>(PLAYER_COUNT);
    private M3uProvider mProvider = new M3uProvider();
    private AudioPlayer mNextPlayer;

    public M3uAudioPlayer() {
        mThread = new HandlerThread("M3uAudioPlayerThread");
        mThread.start();
        mHandler = new Handler(mThread.getLooper());
        for (int i = 0; i < PLAYER_COUNT; i ++) {
            mPlayerList.add(new AudioPlayer(mPlayListener));
        }
    }

    public void release() {
        mProvider.release();
        mHandler.removeCallbacks(null);
        mThread.quit();
        for (AudioPlayer player : mPlayerList) {
            player.release();
        }
    }

    public void start(final String m3u_url) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mProvider.prepare(m3u_url);
                M3uElement first = mProvider.getNext(null);
                Log.d(TAG, "play start, first element: " + first);
                mNextPlayer = getPlayer();
                mNextPlayer.prepare(first);
                mNextPlayer.start();
            }
        });
    }

    private void playNext() {
        Log.d(TAG, "play next, for " + mNextPlayer.getCurrentElement());
        mNextPlayer.start();
    }

    private void prepareNext(final M3uElement element) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                M3uElement next = mProvider.getNext(element);
                Log.d(TAG, "prepare next, for " + next);
                mNextPlayer = getPlayer();
                mNextPlayer.prepare(next);
            }
        });
    }

    private AudioPlayer getPlayer() {
        for (AudioPlayer player : mPlayerList) {
            if (!player.isBusy()) {
                return player;
            }
        }

        return null;
    }

    private AudioPlayer.PlayListener mPlayListener = new AudioPlayer.PlayListener() {

        @Override
        public void onPlayStart(M3uElement element) {
            prepareNext(element);
        }

        @Override
        public void onPlayEnd(M3uElement element) {
            playNext();
        }
    };

}
