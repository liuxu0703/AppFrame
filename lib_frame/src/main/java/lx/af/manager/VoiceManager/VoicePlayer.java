package lx.af.manager.VoiceManager;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import lx.af.utils.PathUtils;

import static lx.af.manager.VoiceManager.VoiceManager.TAG;

/**
 * author: liuxu
 * date: 2015-08-14
 *
 * play/record voice.
 */
class VoicePlayer implements Handler.Callback {

    private MediaPlayer mMediaPlayer;
    private MediaRecorder mMediaRecorder;
    private MediaRecorderListener mMediaRecorderListener;
    private Handler mHandler = new Handler(this);

    private volatile boolean mIsPlayPaused = false;
    private volatile boolean mIsPlaying = false;
    private volatile boolean mIsRecording = false;
    private Voice mCurrentVoice;
    private Record mCurrentRecord;

    VoicePlayer() {
    }

    void init() {
        initMediaPlayer();
        initMediaRecorder();
    }

    void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder.setOnErrorListener(null);
        }
        stopUpdateRecordProgress();
    }

    synchronized boolean isPlayerReady() {
        if (mMediaPlayer == null) {
            return false;
        }
        if (mIsPlayPaused || mIsPlaying || mIsRecording) {
            return false;
        }
        return true;
    }

    synchronized boolean isPlaying() {
        if (mMediaPlayer == null) {
            return false;
        } else {
            return mIsPlaying;
        }
    }

    synchronized void play(Voice voice) {
        Log.d(TAG, "player, prepare to play: " + voice.getContent());
        if (mMediaPlayer == null) {
            initMediaPlayer();
            if (mMediaPlayer == null) {
                if (voice.getCallback() != null) {
                    voice.getCallback().onPlayError(voice, VoiceManager.PlayCallback.ERR_PLAY_FAIL);
                }
                return;
            }
        }

        mIsPlaying = true;
        String path = voice.getPath();

        try {
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepareAsync();
            mCurrentVoice = voice;
        } catch (Exception e) {
            Log.e(TAG, "player, fail to prepare play, " + voice.getContent(), e);
            if (voice.getCallback() != null) {
                voice.getCallback().onPlayError(voice, VoiceManager.PlayCallback.ERR_PLAY_FAIL);
            }
            resetMediaPlayer();
        }
    }

    void startRecord(Record record) {
        // buffer record request. too much record request in a short time would crash.
        mHandler.removeMessages(MSG_START_RECORD);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_START_RECORD, 0, 0, record), 300);
    }

    void stopRecord() {
        stopRecord(false);
    }

    void stopRecord(boolean maxTime) {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.setOnErrorListener(null);
        }
        if (mCurrentRecord != null) {
            stopUpdateRecordProgress();
            mCurrentRecord.stop();
            if (mCurrentRecord.getDurance() < mCurrentRecord.getMinTime()) {
                // length too short, delete file and report error
                if (mCurrentRecord.getCallback() != null) {
                    mCurrentRecord.getCallback().onRecordError(
                            mCurrentRecord, VoiceManager.RecordCallback.ERR_RECORD_TOO_SHORT);
                }
                mCurrentRecord.deleteFile();
            } else {
                if (mCurrentRecord.getCallback() != null) {
                    mCurrentRecord.getCallback().onRecordComplete(mCurrentRecord, maxTime);
                }
            }
            mCurrentRecord = null;
        }
        mIsRecording = false;
    }

    void interruptRecord() {
        stopUpdateRecordProgress();
        if (mMediaRecorder != null && mCurrentRecord != null) {
            if (mCurrentRecord.getCallback() != null) {
                mCurrentRecord.getCallback().onRecordError(
                        mCurrentRecord, VoiceManager.RecordCallback.ERR_RECORD_INTERRUPTED);
            }
            mMediaRecorder.reset();
            mCurrentRecord = null;
        }
        pausePlayer();
    }

    void resumePlayer() {
        if (mMediaPlayer != null && mIsPlayPaused) {
            mMediaPlayer.start();
        }
        mIsPlayPaused = false;
    }

    void pausePlayer() {
        if (mMediaPlayer != null && mIsPlaying) {
            mMediaPlayer.pause();
            mIsPlayPaused = true;
        }
    }

    void stopPlayer() {
        if (mMediaPlayer != null && mIsPlaying) {
            mMediaPlayer.stop();
        }
        if (mCurrentVoice != null && mCurrentVoice.getCallback() != null) {
            mCurrentVoice.getCallback().onPlayError(
                    mCurrentVoice, VoiceManager.PlayCallback.ERR_PLAY_STOPPED);
            mCurrentVoice = null;
        }
        resetMediaPlayer();
    }

    int getCurrentRecordAmplitude() {
        if (mMediaRecorder == null) {
            return 0;
        } else {
            return mMediaRecorder.getMaxAmplitude();
        }
    }

    Voice getCurrentVoice() {
        return mCurrentVoice;
    }

    private void startRecordInner(Record record) {
        String path = getOutputFilePath(record);
        if (path == null) {
            Log.w(TAG, "player, fail to record, path null");
            if (record.getCallback() != null) {
                record.getCallback().onRecordError(
                        record, VoiceManager.RecordCallback.ERR_RECORD_PATH_UNAVAIL);
            }
            return;
        }

        if (mMediaRecorder == null) {
            initMediaRecorder();
            if (mMediaRecorder == null) {
                if (record.getCallback() != null) {
                    record.getCallback().onRecordError(
                            record, VoiceManager.RecordCallback.ERR_RECORD_FAIL);
                }
                return;
            }
        } else {
            mMediaRecorder.reset();
        }

        try {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setOnErrorListener(mMediaRecorderListener);
            mMediaRecorder.setOutputFile(path);

            stopPlayer();

            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mIsRecording = true;
            record.start();
            mCurrentRecord = record;
            scheduleUpdateRecordProgress(record);
        } catch (Exception e) {
            Log.w(TAG, "player, fail to record, " + record.getFilePath(), e);
            if (record.getCallback() != null) {
                record.getCallback().onRecordError(
                        record, VoiceManager.RecordCallback.ERR_RECORD_FAIL);
            }
            resetMediaRecorder();
        }
    }

    private static String getOutputFilePath(Record record) {
        if (record.getFilePath() != null) {
            return record.getFilePath();
        } else {
            String path = PathUtils.generateTmpPath("_record").getAbsolutePath();
            record.setFilePath(path);
            return path;
        }
    }

    private MediaPlayer initMediaPlayer() {
        try {
            mMediaPlayer = new MediaPlayer();
            MediaPlayerListener mpListener = new MediaPlayerListener();
            mMediaPlayer.setOnCompletionListener(mpListener);
            mMediaPlayer.setOnErrorListener(mpListener);
            mMediaPlayer.setOnPreparedListener(mpListener);
        } catch (Exception e) {
            Log.e(TAG, "player, fail to init MediaPlayer", e);
        }
        mIsPlaying = false;
        return mMediaPlayer;
    }

    private MediaRecorder initMediaRecorder() {
        if (mMediaRecorderListener == null) {
            mMediaRecorderListener = new MediaRecorderListener();
        }
        try {
            mMediaRecorder = new MediaRecorder();
        } catch (Exception e) {
            Log.e(TAG, "player, fail to init MediaRecorder", e);
        }
        return mMediaRecorder;
    }

    private void resetMediaPlayer() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.reset();
            } catch (IllegalStateException e) {
                Log.w(TAG, "player, reset MediaPlayer exception", e);
            }
        }
        mIsPlaying = false;
    }

    private void resetMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
        }
    }

    private class MediaPlayerListener implements
            MediaPlayer.OnCompletionListener,
            MediaPlayer.OnPreparedListener,
            MediaPlayer.OnErrorListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mCurrentVoice != null && mCurrentVoice.getCallback() != null) {
                mCurrentVoice.getCallback().onPlayComplete(mCurrentVoice);
                mCurrentVoice = null;
            }
            resetMediaPlayer();
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            String content = mCurrentVoice != null ? mCurrentVoice.getContent() : null;
            Log.w(TAG, "player, play error: " + what + " | " + extra + ", for " + content);
            if (mCurrentVoice != null && mCurrentVoice.getCallback() != null) {
                mCurrentVoice.getCallback().onPlayError(
                        mCurrentVoice, VoiceManager.PlayCallback.ERR_PLAY_FAIL);
                mCurrentVoice = null;
            }
            resetMediaPlayer();
            return false;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mMediaPlayer.start();
            if (mCurrentVoice != null && mCurrentVoice.getCallback() != null) {
                mCurrentVoice.getCallback().onPlayStart(mCurrentVoice);
            }
        }
    }

    private class MediaRecorderListener implements
            MediaRecorder.OnErrorListener {

        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Log.w(TAG, "player, record error: " + what + " | " + extra);
            if (mCurrentRecord != null && mCurrentRecord.getCallback() != null) {
                mCurrentRecord.getCallback().onRecordError(
                        mCurrentRecord, VoiceManager.RecordCallback.ERR_RECORD_FAIL);
                stopUpdateRecordProgress();
                mCurrentRecord = null;
            }
            mIsRecording = false;
        }
    }

    private void scheduleUpdateRecordProgress(Record record) {
        mHandler.removeMessages(MSG_UPDATE_RECORD_PROGRESS);
        Message message = mHandler.obtainMessage(MSG_UPDATE_RECORD_PROGRESS, record);
        mHandler.sendMessageDelayed(message, record.getReportInterval());
    }

    private void stopUpdateRecordProgress() {
        mHandler.removeMessages(MSG_UPDATE_RECORD_PROGRESS);
    }

    private static final int MSG_UPDATE_RECORD_PROGRESS = 101;
    private static final int MSG_START_RECORD = 102;

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_UPDATE_RECORD_PROGRESS: {
                Record record = (Record) msg.obj;
                if (record.getCallback() != null) {
                    int amp = getCurrentRecordAmplitude();
                    record.getCallback().onRecordProgress(record, amp);
                }
                if (record.getCurrentDurance() >= record.getMaxTime()) {
                    // max time reached
                    stopRecord(true);
                    mHandler.removeMessages(MSG_UPDATE_RECORD_PROGRESS);
                } else {
                    scheduleUpdateRecordProgress(record);
                }
                break;
            }
            case MSG_START_RECORD: {
                Record record = (Record) msg.obj;
                startRecordInner(record);
                break;
            }
        }
        return true;
    }

}
