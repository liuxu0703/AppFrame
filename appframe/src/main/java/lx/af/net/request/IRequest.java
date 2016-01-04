package lx.af.net.request;

import lx.af.net.request.ErrorHandler.ErrorHandler;

/**
 * author: lx
 * date: 15-8-26
 *
 * http 请求接口.
 */
public interface IRequest {

    String TAG = "Request";

    /**
     * 同步发起请求.会阻塞当前线程.
     * 请求结果 {@link DataHull} 可由 {@link ErrorHandler}
     * 进行简单错误处理.
     * @see DataHull
     * @return 一个代表此请求的数据壳
     */
    DataHull request();

    /**
     * 异步发起请求.不会阻塞当前线程.回调方法会在主线程运行.
     * 请求成功与失败都会进行回调,进一步获取回调函数参数 {@link DataHull} 中的信息以确定请求是否成功.
     * 请求结果 {@link DataHull} 可由 {@link ErrorHandler}
     * 进行简单错误处理.
     * @see DataHull
     * @param callback 回调
     */
    void requestAsync(RequestCallback callback);

}
