package lx.af.net.request;

import java.util.Map;

/**
 * author: lx
 * date: 15-8-26
 *
 * http 请求数据壳.包含了 请求信息 和 服务器返回信息.
 */
public class DataHull {

    /** 请求成功 */
    public static final int ERR_NONE = 0;
    /** 未执行.这属于编程错误,不应该返回这个值 */
    public static final int ERR_NOT_INIT = 100;
    /** 请求失败,未知错误 */
    public static final int ERR_UNKNOWN = 101;
    /** 请求失败,请求URL为空 */
    public static final int ERR_URL_NULL = 102;
    /** 请求失败,请求参数错误 */
    public static final int ERR_PARAMS_INVALID = 103;
    /** 请求失败,网络不可用 */
    public static final int ERR_NO_NET_WORK = 104;
    /** 请求失败,http 请求失败 */
    public static final int ERR_REQUEST_FAIL = 601;
    /** 请求失败,http 请求超时 */
    public static final int ERR_REQUEST_TIMEOUT = 602;
    /** 请求失败,http 请求时中断 */
    public static final int ERR_REQUEST_CANCELED = 603;
    /** 请求失败,返回数据为空 */
    public static final int ERR_DATA_NULL = 701;
    /** 请求失败,返回数据解析错误 */
    public static final int ERR_DATA_PARSE_FAIL = 702;
    /** 请求失败,返回数据内容异常(比如关键字段为空) */
    public static final int ERR_DATA_MALFORMED = 703;


    /** 数据来源未知 */
    public static final int SOURCE_UNKNOWN = 100;
    /** 数据来自服务器 */
    public static final int SOURCE_SERVER = 101;
    /** 数据来自内存缓存 */
    public static final int SOURCE_MEMORY_CACHE = 102;
    /** 数据来自本地缓存 */
    public static final int SOURCE_SDCARD_CACHE = 103;


    /** 状态码 */
    int mStatus = ERR_NOT_INIT;

    /** http 状态码 */
    int mHttpStatus = 0;

    /** 数据来源 */
    int mDataSource = SOURCE_UNKNOWN;

    /** 请求 URL */
    String mUrl;

    /** 请求参数 (如果有的话) */
    Map<String, String> mParams;

    /** 服务器返回的原始数据 */
    Object mOriginData;

    /** 由原始数据解析得到的目标数据 */
    Object mParsedData;

    /**
     * 获取状态码.
     * 此状态码为整个请求周期的汇总,参数错误,网络错误,http错误,数据解析错误等都已包括在内.
     * 如果此状态吗为 {@link #ERR_NONE}, 则可以保证最终数据是可用的,不必再进行额外验证(比如
     * 空指针验证等)
     */
    public int getStatus() {
        return mStatus;
    }

    /**
     * 获取 http 状态码.
     * 如果 http 请求失败,可以检查此状态码以确定原因.
     * 如果请求还未递交给 http 处理就已经有错误发生,则此状态码为0.
     * NOTE: 在检查 http 状态码前,先检查总的状态码
     * @see #getStatus()
     */
    public int getHttpStatus() {
        return mHttpStatus;
    }

    /**
     * 获取数据来源.
     */
    public int getDataSource() {
        return mDataSource;
    }

    /**
     * 获取请求 URL.
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * 获取请求参数.
     */
    public Map<String, String> getParams() {
        return mParams;
    }

    /**
     * 获取服务器返回的原始数据.
     */
    @SuppressWarnings("unchecked")
    public <E> E getOriginData() {
        return (E) mOriginData;
    }

    /**
     * 获取解析过的目标数据.
     */
    @SuppressWarnings("unchecked")
    public <T> T getParsedData() {
        return (T) mParsedData;
    }

    /**
     * 检查请求是否成功
     * 参数错误,网络错误,http错误,数据解析错误等都已包括在内.
     * 如果此方法返回 true,则可以保证最终数据是可用的,不必再进行额外验证(比如空指针验证等)
     * 如果此方法返回 false,检查状态码以获取失败原因.
     * @see #getStatus()
     * @return true 请求成功 ; false 请求失败.
     */
    public boolean isRequestSuccess() {
        return mStatus == ERR_NONE;
    }

    @Override
    public String toString() {
        return "DataHull{" +
                "mStatus=" + mStatus +
                ", mHttpStatus=" + mHttpStatus +
                ", mUrl='" + mUrl + '\'' +
                ", mParams={"+mParams+"}\n"+
                ", mOriginData=" + mOriginData +
                '}';
    }

}
