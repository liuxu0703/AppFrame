package lx.af.net.request;

import lx.af.net.request.ErrorHandler.ErrorHandler;

/**
 * author: lx
 * date: 15-8-26
 *
 * 请求回调
 */
public interface RequestCallback {

    /**
     * 当请求完成时回调.此方法会保证在主线程运行.
     * 请求成功与失败都会回调此方法,进一步获取参数 {@link DataHull} 中的信息以确定请求是否成功.
     * 请求结果 {@link DataHull} 可由 {@link ErrorHandler}
     * 进行简单错误处理.
     * @see DataHull
     * @param d 一个代表此请求的数据壳
     */
    void onRequestComplete(DataHull d);

}
