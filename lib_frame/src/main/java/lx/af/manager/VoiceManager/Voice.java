package lx.af.manager.VoiceManager;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import lx.af.manager.VoiceManager.VoiceManager.PlayCallback;
import lx.af.manager.VoiceManager.VoiceManager.PlayOptions;

/**
 * author: liuxu
 * date: 15-8-14.
 */
public class Voice implements Comparable<Voice> {

    /** 播报类型 文字 */
    public static final int TYPE_MESSAGE = 101;
    /** 播报类型 网络音频 */
    public static final int TYPE_URL = 102;
    /** 播报类型 本地音频 */
    public static final int TYPE_FILE = 103;

    /** 音频最大过期时间.当过期时间被设置为超过此值时,会被设置为此值. */
    private static final long MAX_OVERDUE_TIME = TimeUnit.MINUTES.toMillis(10);
    /** 音频最大延后时间.当延后时间被设置为超过此值时,会被设置为此值. */
    private static final long MAX_DELAY_TIME = TimeUnit.MINUTES.toMillis(1);

    private static int sIndex = 0;

    private int id = sIndex ++;
    private long createTime = System.currentTimeMillis();
    private long createFileTime;

    private String path;
    private String content;
    private int type;
    private int priority;
    private long overdue;
    private long delay;
    private boolean playImmediately;
    private PlayCallback callback;

    Voice(int type, String content, PlayCallback callback, PlayOptions options) {
        this.type = type;
        this.content = content;
        this.callback = callback;
        if (options == null) {
            options = new PlayOptions();
        }
        this.playImmediately = options.playImmediately;
        if (this.playImmediately) {
            this.priority = PlayOptions.PRIORITY_FRONT;
        } else {
            this.priority = options.priority;
        }
        setOverdue(options.overdue);
        setDelay(options.delay);
    }

    /**
     * 获取对象创建时间,可认为是播放请求的开始时间
     */
    public long getCreateTime() {
        return createTime;
    }

    /**
     * 获取音频文件创建时间
     */
    public long getCreateFileTime() {
        return createFileTime;
    }

    /**
     * 获取类型.
     * 播报文字: TYPE_MESSAGE = 101;
     * 播报网络音频: TYPE_URL = 102;
     * 播报本地音频: TYPE_FILE = 103;
     */
    public int getType() {
        return type;
    }

    /**
     * 获取优先级.数值越低优先级越高.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * 获取播报过期时间.超过此时间还未开始播报的请求将被放弃.
     */
    public long getOverdue() {
        return overdue;
    }

    /**
     * 获取播报延后时间.
     */
    public long getDelay() {
        return delay;
    }

    /**
     * 获取播报音频文件本地路径
     */
    public String getPath() {
        return path;
    }

    /**
     * 获取播报内容.
     * 类型为文字播报则其为 播报文字;
     * 类型为网络音频则其为 url;
     * 类型为本地音频则其为 本地路径.
     */
    public String getContent() {
        return content;
    }

    boolean isPlayImmediately() {
        return playImmediately;
    }

    void setOverdue(long overdue) {
        if (overdue > MAX_OVERDUE_TIME) {
            this.overdue = MAX_OVERDUE_TIME;
        } else {
            this.overdue = overdue;
        }
    }

    void setDelay(long delay) {
        if (delay > MAX_DELAY_TIME) {
            this.delay = MAX_DELAY_TIME;
        } else {
            this.delay = delay;
        }
    }

    Voice setPath(String path) {
        this.path = path;
        return this;
    }

    PlayCallback getCallback() {
        return callback;
    }

    boolean isOverDue() {
        long age = System.currentTimeMillis() - createTime;
        return (age > overdue);
    }

    void  setCreateFileTime(long createFileTime) {
        this.createFileTime = createFileTime;
    }

    @Override
    public int compareTo(@NonNull Voice that) {
        int p = this.priority - that.priority;
        if (p != 0) {
            return p;
        }
        return this.id - that.id;
    }

    @Override
    public String toString() {
        return "Voice{" +
                "type=" + type +
                ", content='" + content + '\'' +
                ", path='" + path + '\'' +
                ", createTime=" + createTime +
                ", createFileTime=" + createFileTime +
                ", priority=" + priority +
                ", overdue=" + overdue +
                ", delay=" + delay +
                ", id=" + id +
                '}';
    }
}
