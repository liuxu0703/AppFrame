package lx.af.net.HttpRequest.ErrorHandler;

import android.util.Log;

import lx.af.net.HttpRequest.DataHull;
import lx.af.net.HttpRequest.IRequest;
import lx.af.utils.AlertUtils;

/**
 * author: lx
 * date: 15-8-27
 *
 * show toast on request error
 * TODO: localise the strings
 */
public class ToastHandler implements IHandler {

    /**
     * a toast with error code and user readable message will be shown
     * if there is an error contained in DataHull.
     * @param datahull the datahull
     */
    @Override
    public void handleError(DataHull datahull) {
        if(datahull.isRequestSuccess()) {
            return;
        }

        String msg;
        switch (datahull.getStatus()) {
            case DataHull.ERR_NOT_INIT:
            case DataHull.ERR_UNKNOWN:
            case DataHull.ERR_URL_NULL:
            case DataHull.ERR_PARAMS_INVALID:
                msg = "请求失败(" + datahull.getStatus() + ")";
                break;
            case DataHull.ERR_NO_NET_WORK:
                msg = "网络不可用";
                break;
            case DataHull.ERR_REQUEST_FAIL:
            case DataHull.ERR_REQUEST_CANCELED:
                msg = "请求失败(" + datahull.getStatus() + "|" + datahull.getHttpStatus() + ")";
                break;
            case DataHull.ERR_REQUEST_TIMEOUT:
                msg = "请求超时(" + datahull.getStatus() + ")";
                break;
            case DataHull.ERR_DATA_NULL:
            case DataHull.ERR_DATA_PARSE_FAIL:
            case DataHull.ERR_DATA_MALFORMED:
                msg = "请求数据异常(" + datahull.getStatus() + ")";
                break;
            default:
                msg = "请求失败(" + datahull.getStatus() + "|" + datahull.getHttpStatus() + ")";
                break;
        }

        Log.d(IRequest.TAG, "ToastErrorHandler print error info: \n" + datahull);
        toast(msg);
    }

    protected static void toast(final String msg) {
        AlertUtils.toastShort(msg);
    }

}
