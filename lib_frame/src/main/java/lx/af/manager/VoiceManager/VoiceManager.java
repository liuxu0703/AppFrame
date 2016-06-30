package lx.af.manager.VoiceManager;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import lx.af.manager.GlobalThreadManager;

/**
 * author: liuxu
 * date: 2015-08-14
 *
 */
public class VoiceManager {

    static final String TAG = "VoiceManager";

    private static VoiceManager sInstance;

    private volatile boolean mIsInit = false;
    private volatile boolean mIsPause = false;

    private VoicePlayer mVoicePlayer;
    private VoiceHandler mHandler;
    private VoiceProvider mVoiceProvider;

    private Executor mExecutor;

    private HashMap<Context, Context> mContextMap = new HashMap<>(4);

    private PhoneStateListener mPhoneListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            handlePhoneStateChange(state);
        }
    };

    /**
     * 获取 VoiceManager 实例.
     * 注意: 获取后的实例需要调用 VoiceManager.init() 才可以使用.
     */
    public static synchronized VoiceManager getInstance() {
        if (sInstance == null) {
            sInstance = new VoiceManager();
        }
        return sInstance;
    }

    private VoiceManager() {
        mHandler = new VoiceHandler();
        mVoicePlayer = new VoicePlayer();
    }

    // =======================================
    // public methods

    /**
     * 初始化 VoiceManager.
     * @param context 组件上下文.注意此参数尽量使用当前组件上下文,而不要用 Application Context,
     *                因为在 {@link #release(Context)} 的时候会根据此 context 进行反注册.
     */
    public synchronized void init(Context context) {
        mContextMap.put(context, context);
        if (!mIsInit) {
            mExecutor = GlobalThreadManager.getThreadPoolInstance();
            mVoicePlayer.init();
            mHandler.start(mVoicePlayer);
            mVoiceProvider = new VoiceProvider(context.getApplicationContext(), mExecutor);
            mVoiceProvider.init();
            TelephonyManager tpm = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            tpm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
            mIsInit = true;
        }
    }

    /**
     * 释放 VoiceManager.
     */
    public synchronized void release(Context context) {
        mContextMap.remove(context);

        if (mContextMap.size() == 0 && mIsInit) {
            Log.d(TAG, "manager, no impl, release");
            TelephonyManager tpm = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            tpm.listen(mPhoneListener, PhoneStateListener.LISTEN_NONE);
            mHandler.stop();
            mVoicePlayer.release();
            mVoiceProvider.release();
            mIsInit = false;
        }
    }

    /**
     * 检查 VoiceManager 是否可用. 即是否已调用过 init() .
     */
    public boolean isReady() {
        return mIsInit;
    }

    /**
     * 检查 VoiceManager 是否正在播放.
     */
    public boolean isPlaying() {
        return mVoicePlayer.isPlaying();
    }

    /**
     * 检查 VoiceManager 是否正在播放指定内容. 内容可以是 本地文件路径, 网络url, 或者 一段播报文本 .
     */
    public boolean isPlayingContent(String content) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }
        String currentContent = getCurrentPlayContent();
        return currentContent != null && currentContent.equals(content);
    }

    /**
     * 获取当前正在播放的内容. 这可能是 本地文件路径, 网络url, 或者 一段播报文本 .
     * 如果不在播放状态,则返回 null.
     */
    public String getCurrentPlayContent() {
        Voice voice = mVoicePlayer.getCurrentVoice();
        return voice == null ? null : voice.getContent();
    }

    /**
     * 暂停播报.调用此方法后调用播报函数会回调错误.
     */
    public void pause() {
        mIsPause = true;
        mHandler.clearQueue();
    }

    /**
     * 重启播报.
     */
    public void resume() {
        mIsPause = false;
    }

    /**
     * 播报文字内容.
     * @param message 播报文字.
     * @param callback 播报回调.
     */
    public void playText(String message, PlayCallback callback) {
        play(Voice.TYPE_MESSAGE, message, callback, null);
    }

    /**
     * 播报本地音频.
     * @param path 本地音频路径.
     * @param callback 播报回调.
     */
    public void playFile(String path, PlayCallback callback) {
        play(Voice.TYPE_FILE, path, callback, null);
    }

    /**
     * 播报网络音频.
     * @param url 网络音频 url.
     * @param callback 播报回调.
     */
    public void playUrl(String url, PlayCallback callback) {
        play(Voice.TYPE_URL, url, callback, null);
    }

    /**
     * 播报文字内容.
     * @param message 播报文字.
     * @param callback 播报回调.
     * @param options 播报选项.
     */
    public void playText(String message, PlayCallback callback, PlayOptions options) {
        play(Voice.TYPE_MESSAGE, message, callback, options);
    }

    /**
     * 播报本地音频.
     * @param path 本地音频路径.
     * @param callback 播报回调.
     * @param options 播报选项.
     */
    public void playFile(String path, PlayCallback callback, PlayOptions options) {
        play(Voice.TYPE_FILE, path, callback, options);
    }

    /**
     * 播报网络音频.
     * @param url 网络音频 url.
     * @param callback 播报回调.
     * @param options 播报选项.
     */
    public void playUrl(String url, PlayCallback callback, PlayOptions options) {
        play(Voice.TYPE_URL, url, callback, options);
    }

    /**
     * 停止播放当前音频. 不会影响下一个音频的播放.
     */
    public void stopPlay() {
        mVoicePlayer.stopPlayer();
    }

    /**
     * 开始录音.
     * @param callback 录音回调.
     */
    public void startRecord(RecordCallback callback) {
        startRecord(callback, null);
    }

    /**
     * 开始录音.
     * @param callback 录音回调.
     * @param options 录音选项.
     */
    public void startRecord(RecordCallback callback, RecordOptions options) {
        checkInit();
        Record record = new Record(callback, options);
        mVoicePlayer.startRecord(record);
    }

    /**
     * 结束录音.
     */
    public void stopRecord() {
        mVoicePlayer.stopRecord();
    }

    // =======================================

    private void play(int type, final String content, PlayCallback callback, PlayOptions options) {
        checkInit();
        Voice voice = new Voice(type, content, callback, options);

        if (TextUtils.isEmpty(content)) {
            if (callback != null) {
                callback.onPlayError(voice, PlayCallback.ERR_DATA_NULL);
            }
            return;
        }

        if (mIsPause) {
            if (callback != null) {
                callback.onPlayError(voice, PlayCallback.ERR_PLAY_PAUSED);
            }
            return;
        }

        mVoiceProvider.fillVoice(voice, new VoiceProvider.Callback() {
            @Override
            public void onResult(Voice voice) {
                voice.setCreateFileTime(System.currentTimeMillis());
                if (voice.getCallback() != null) {
                    voice.getCallback().onPlayFileLoaded(voice);
                }
                mHandler.add(voice);
            }

            @Override
            public void onError(Voice voice) {
                Log.w(TAG, "manager, fail to load file, content: " + voice.getContent());
                if (voice.getCallback() != null) {
                    voice.getCallback().onPlayError(voice, PlayCallback.ERR_FILE_NOT_LOADED);
                }
            }
        });
    }

    private void handlePhoneStateChange(int state) {
        Log.d(TAG, "manager, phone state change: " + state);
        switch(state) {
            case TelephonyManager.CALL_STATE_IDLE: {
                mVoicePlayer.resumePlayer();
                break;
            }
            case TelephonyManager.CALL_STATE_RINGING:
            case TelephonyManager.CALL_STATE_OFFHOOK: {
                mVoicePlayer.interruptRecord();
                break;
            }
        }
    }

    private void checkInit() {
        if (!mIsInit) {
            throw new IllegalStateException("VoiceManager not init");
        }
    }

    // =======================================

    /**
     * 播报请求选项
     */
    public static class PlayOptions {

        /** 播报请求优先级 默认 */
        public static final int PRIORITY_DEFAULT = 10;
        /** 播报请求优先级 优先播放 */
        public static final int PRIORITY_FRONT = 5;
        /** 播报请求优先级 最后播放 */
        public static final int PRIORITY_END = 15;

        /**
         * 是否立刻播放.
         * 如果设置为 true 则任何正在播放的音频会被停止,并立刻播放该音频.
         * 如果设置为 true 则 priority 会被自动设置为 PRIORITY_FRONT.
         * 默认为 false .
         */
        public boolean playImmediately = false;

        /**
         * 播报请求优先级.越小的数值会越早播报.默认为10.
         */
        public int priority = PRIORITY_DEFAULT;

        /**
         * 播报请求过期时间.超过此时间播报还未开始则放弃. in millisecond.
         * 默认为 1 分钟.
         */
        public long overdue = TimeUnit.MINUTES.toMillis(1);

        /**
         * 播报延后时间. in millisecond.
         * 默认为 600 毫秒.
         */
        public long delay = 600;

    }

    /**
     * 录音选项
     */
    public static class RecordOptions {

        /**
         * 设置最长录音时间.超过此时长则自动结束录音,并回调录音完成. in millisecond.
         * 默认为 45 秒.
         */
        public long maxTime = TimeUnit.SECONDS.toMillis(45);

        /**
         * 设置最短录音时间.未到此时长则自动删除录音,并回调录音错误. in millisecond.
         * 默认为 1 秒.
         */
        public long minTime = TimeUnit.SECONDS.toMillis(1);

        /**
         * 录音进度返回间隔, in millisecond.
         * 此选项决定了 RecordCallback.onRecordProgress() 的回调间隔.
         * 默认为 500 毫秒.
         */
        public long reportProgressInterval = 500;

        /**
         * 录音文件输出路径.
         */
        public String filePath;

    }

    /**
     * 播放请求的回调
     */
    public interface PlayCallback {

        /** 传入数据为空 */
        int ERR_DATA_NULL = 1;
        /** 音频文件加载失败 */
        int ERR_FILE_NOT_LOADED = 2;
        /** 音频文件播放失败 */
        int ERR_PLAY_FAIL = 3;
        /** 播报功能暂停,原因是调用了 VoiceManager.pause() */
        int ERR_PLAY_PAUSED = 4;
        /** 音频文件播放停止. 可能是其他播放请求立刻播放造成的,也可能是调用了 VoiceManager.stopPlay() */
        int ERR_PLAY_STOPPED = 5;

        /**
         * 在音频文件被成功加载时回调.非主线程回调.
         * @param voice 包含请求信息的结构体
         */
        void onPlayFileLoaded(Voice voice);

        /**
         * 在音频文件开始播放时回调.非主线程回调.
         * @param voice 包含请求信息的结构体
         */
        void onPlayStart(Voice voice);

        /**
         * 在音频文件播放完成时回调.非主线程回调.
         * @param voice 包含请求信息的结构体
         */
        void onPlayComplete(Voice voice);

        /**
         * 在请求过程出错时回调.非主线程回调.
         * @param voice 包含请求信息的结构体
         * @param err 错误代码
         */
        void onPlayError(Voice voice, int err);

    }

    /**
     * 录音请求的回调
     */
    public interface RecordCallback {

        /** 录音失败 */
        int ERR_RECORD_FAIL = 1;
        /** 录音失败,录音存放路径不可用 */
        int ERR_RECORD_PATH_UNAVAIL = 2;
        /** 录音时长过短 */
        int ERR_RECORD_TOO_SHORT = 3;
        /** 录音中断 (因来电等) */
        int ERR_RECORD_INTERRUPTED = 4;

        /**
         * 在录音完成时回调.非主线程回调.
         * @param record 包含请求信息的结构体
         * @param isMaxTime 是否已达到最大录音时长
         */
        void onRecordComplete(Record record, boolean isMaxTime);

        /**
         * 录音进行中回调.非主线程回调.
         * 此方法回调间隔由 RecordOptions.reportProgressInterval 决定.
         * @param record 包含请求信息的结构体
         * @param amplitude 当前录音音量. 值同 MediaRecord.getMaxAmplitude()
         * @see VoiceManager.RecordOptions
         * @see android.media.MediaRecorder
         */
        void onRecordProgress(Record record, int amplitude);

        /**
         * 在录音出错时回调.非主线程回调.
         * @param record 包含请求信息的结构体
         * @param err 错误代码
         */
        void onRecordError(Record record, int err);

    }

}
