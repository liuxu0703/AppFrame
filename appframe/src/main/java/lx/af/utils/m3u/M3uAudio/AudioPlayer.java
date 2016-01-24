package lx.af.utils.m3u.M3uAudio;

import android.media.MediaPlayer;
import android.util.Log;

import static lx.af.utils.m3u.M3uAudio.M3uAudioPlayer.TAG;

/**
 * author: lx
 * date: 16-1-12
 */
class AudioPlayer {

    private MediaPlayer mMediaPlayer;
    private M3uElement mElement;
    private volatile boolean mIsBusy;
    private volatile boolean mIsPrepared;
    private volatile boolean mStartOnPrepared;

    private PlayListener mPlayListener;

    public AudioPlayer() {
        initMediaPlayer();
    }

    public AudioPlayer(PlayListener listener) {
        this();
        mPlayListener = listener;
    }

    public void release() {
        mMediaPlayer.release();
    }

    public void prepare(M3uElement element) {
        try {
            mElement = element;
            mMediaPlayer.setDataSource(element.getURI().toString());
            mMediaPlayer.prepareAsync();
            mIsBusy = true;
        } catch (Exception e) {
            Log.e(TAG, "player, fail to prepare play " + element, e);
            resetMediaPlayer();
        }
    }

    public void start() {
        if (mIsPrepared) {
            startPlay();
        } else {
            mStartOnPrepared = true;
        }
    }

    public M3uElement getCurrentElement() {
        return mElement;
    }

    public boolean isBusy() {
        return mIsBusy;
    }

    private void startPlay() {
        Log.d(TAG, "player, startPlay(), for " + mElement);
        mMediaPlayer.start();
        mElement.setPlayed(true);
        if (mPlayListener != null) {
            mPlayListener.onPlayStart(mElement);
        }
    }

    private MediaPlayer initMediaPlayer() {
        MediaPlayerListener mpListener = new MediaPlayerListener();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(mpListener);
        mMediaPlayer.setOnErrorListener(mpListener);
        mMediaPlayer.setOnPreparedListener(mpListener);
        mIsBusy = false;
        mIsPrepared = false;
        mStartOnPrepared = false;
        return mMediaPlayer;
    }

    private void resetMediaPlayer() {
        if (mMediaPlayer == null) {
            initMediaPlayer();
        }
        mMediaPlayer.reset();
        mIsBusy = false;
        mIsPrepared = false;
        mStartOnPrepared = false;
    }

    private class MediaPlayerListener implements
            MediaPlayer.OnCompletionListener,
            MediaPlayer.OnPreparedListener,
            MediaPlayer.OnErrorListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.d(TAG, "player, play complete, for " + mElement);
            resetMediaPlayer();
            if (mPlayListener != null) {
                mPlayListener.onPlayEnd(mElement);
            }
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.w(TAG, "player, play error: " + what + " | " + extra + ", for " + mElement);
            resetMediaPlayer();
            if (mPlayListener != null) {
                mPlayListener.onPlayEnd(mElement);
            }
            return false;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            Log.d(TAG, "player, play prepared, for " + mElement);
            mIsPrepared = true;
            if (mStartOnPrepared) {
                startPlay();
            }
        }
    }

    interface PlayListener {
        void onPlayStart(M3uElement element);
        void onPlayEnd(M3uElement element);
    }

}
