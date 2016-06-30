package lx.af.manager.VoiceManager;

import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import static lx.af.manager.VoiceManager.VoiceManager.TAG;

/**
 * author: liuxu
 * date: 15-8-13.
 *
 * work queue for MediaPlayer audio play.
 *
 * TODO:
 * combine VoiceHandler and BaiduSpeechHandler. use ReentrantLock to start
 * one thread (instead of two) to handle the two queue.
 */
class VoiceHandler {

    private VoicePlayer mVoicePlayer;
    private BlockingQueue<Voice> mQueue = new PriorityBlockingQueue<>();
    private QueueThread mThread;

    private volatile boolean mIsQuit = false;

    VoiceHandler() {
    }

    void start(VoicePlayer audioPlayer) {
        mIsQuit = false;
        mVoicePlayer = audioPlayer;
        mThread = new QueueThread();
        mThread.start();
    }

    void stop() {
        mIsQuit = true;
        mThread.interrupt();
    }

    void clearQueue() {
        mQueue.clear();
    }

    void add(Voice voice) {
        if (voice.isPlayImmediately()) {
            mVoicePlayer.stopPlayer();
        }
        mQueue.add(voice);
    }

    private void release() {
        Log.d(TAG, "handler, release");
        mQueue.clear();
    }

    /**
     * thread sleep.
     * @param millis sleep time, in millisecond.
     * @return true if sleep end normally; false if being interrupted.
     */
    private static boolean sleepSilently(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
            return true;
        } catch (InterruptedException ignore) {
            return false;
        }
    }


    private class QueueThread extends Thread {
        @Override
        public void run() {
            Voice voice;
            long delay;

            while (!mIsQuit) {
                // check ready before dequeue:
                // if we dequeue for Voice before check for ready, the 'priority'
                // may have a good chance to lost its use.
                if (!mVoicePlayer.isPlayerReady()) {
                    if (!sleepSilently(2000)) {
                        if (mIsQuit) break;
                    }
                    continue;
                }

                try {
                    // block until we have something
                    voice = mQueue.take();
                } catch (InterruptedException ignore) {
                    if (mIsQuit) {
                        break;
                    } else {
                        continue;
                    }
                }

                while (!mIsQuit) {
                    if (voice.isOverDue()) {
                        Log.w(TAG, "handler, overdue, drop voice: " + voice.getContent());
                        break;
                    }

                    delay = voice.getDelay();
                    if (delay > 0) {
                        if (!sleepSilently(delay)) {
                            if (mIsQuit) break;
                        }
                    }

                    // it may have been a while for us to wait for new data to come,
                    // so we need to check ready again.
                    if (!mVoicePlayer.isPlayerReady()) {
                        if (!sleepSilently(2000)) {
                            if (mIsQuit) break;
                        }
                    } else {
                        // finally ready to play
                        mVoicePlayer.play(voice);
                        break;
                    }
                }
            }

            // thread stopped, release resources
            release();
        }
    }

}
