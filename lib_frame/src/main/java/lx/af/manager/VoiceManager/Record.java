package lx.af.manager.VoiceManager;

import java.io.File;

/**
 * author: liuxu
 * date: 15-8-17.
 */
public class Record {

    private long maxTime;
    private long minTime;
    private long reportInterval;
    private long startTime;
    private long durance;
    private String filePath;
    private VoiceManager.RecordCallback callback;

    Record(VoiceManager.RecordCallback callback, VoiceManager.RecordOptions options) {
        this.callback = callback;
        if (options == null) {
            options = new VoiceManager.RecordOptions();
        }
        maxTime = options.maxTime;
        minTime = options.minTime;
        filePath = options.filePath;
        reportInterval = options.reportProgressInterval;
    }

    /**
     * 获取录音允许的最大时长, in milliseconds.
     */
    public long getMaxTime() {
        return maxTime;
    }

    /**
     * 获取录音允许的最小时长, in milliseconds.
     */
    public long getMinTime() {
        return minTime;
    }

    /**
     * 获取最终录音时长, in millisecond.
     * 在录音未结束时调用该方法会返回 0 .
     */
    public long getDurance() {
        return durance;
    }

    /**
     * 获取当前已录制时长, in millisecond.
     */
    public long getCurrentDurance() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * 获取录音文件路径
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * 删除录音文件.
     * @return  true 如果调用此方法后文件已不存在;
     *          false 如果调用此方法后文件仍然存在.
     */
    public boolean deleteFile() {
        if (filePath != null) {
            File file = new File(filePath);
            return file.delete();
        } else {
            return true;
        }
    }

    VoiceManager.RecordCallback getCallback() {
        return callback;
    }

    void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    void start() {
        startTime = System.currentTimeMillis();
    }

    void stop() {
        durance = System.currentTimeMillis() - startTime;
    }

    long getReportInterval() {
        return reportInterval;
    }

    @Override
    public String toString() {
        return "Record{" +
                "filePath='" + filePath + '\'' +
                ", durance=" + durance +
                ", startTime=" + startTime +
                ", minTime=" + minTime +
                ", maxTime=" + maxTime +
                '}';
    }
}
