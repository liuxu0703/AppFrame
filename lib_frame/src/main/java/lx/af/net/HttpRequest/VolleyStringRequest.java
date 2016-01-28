package lx.af.net.HttpRequest;

import android.os.NetworkOnMainThreadException;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lx.af.net.HttpRequest.volley.request.PostParamsRequest;
import lx.af.net.HttpRequest.volley.request.StringRequestListener;

/**
 * author: lx
 * date: 15-8-26
 *
 * request using volley.
 */
public abstract class VolleyStringRequest<T> implements IRequest {

    private static final long TIMEOUT = 20;  // request timeout, in seconds

    private String mUrl;
    private Map<String, String> mParams;
    private RequestCallback mCallback;

    private PostParamsRequest mRequest;

    private StringRequestListener mListener = new StringRequestListener() {

        @Override
        public void onResponse(String response) {
            DataHull datahull = processData(response);
            informCallback(datahull);
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            DataHull datahull;
            if (error instanceof TimeoutError) {
                datahull = createErrorDataHull(DataHull.ERR_REQUEST_TIMEOUT);
            } else {
                datahull = createErrorDataHull(DataHull.ERR_REQUEST_FAIL);
            }
            if (error.networkResponse != null) {
                datahull.mHttpStatus = error.networkResponse.statusCode;
            }
            informCallback(datahull);
        }
    };

    public VolleyStringRequest(String url, Map<String, String> params) {
        mUrl = url;
        mParams = params;
    }

    @Override
    public DataHull request() {
        VolleyManager.throwIfRequestInMainThread();

        int code = initCheck();
        if (code != DataHull.ERR_NONE) {
            return createErrorDataHull(code);
        }

        RequestFuture<String> future = RequestFuture.newFuture();
        mRequest = new PostParamsRequest(mUrl, mParams, future, future);
        mRequest.setShouldCache(false);
        VolleyManager.addRequest(mRequest);

        String data;
        try {
            // block for result
            data = future.get(TIMEOUT, TimeUnit.SECONDS);
        } catch (TimeoutException te) {
            Log.e(TAG, "request timeout", te);
            return createErrorDataHull(DataHull.ERR_REQUEST_TIMEOUT);
        } catch (InterruptedException ie) {
            Log.e(TAG, "request interrupted", ie);
            return createErrorDataHull(DataHull.ERR_REQUEST_CANCELED);
        } catch (Exception e) {
            Log.e(TAG, "request fail", e);
            DataHull datahull = createErrorDataHull(DataHull.ERR_REQUEST_FAIL);
            datahull.mHttpStatus = mRequest.getHttpStatusCode();
            return datahull;
        }

        return processData(data);
    }

    @Override
    public void requestAsync(RequestCallback callback) {
        mCallback = callback;
        int code = initCheck();
        if (code != DataHull.ERR_NONE) {
            informCallback(createErrorDataHull(code));
            return;
        }

        mRequest = new PostParamsRequest(mUrl, mParams, mListener, mListener);
        mRequest.setShouldCache(false);
        VolleyManager.addRequest(mRequest);
    }

    protected abstract T parseResult(@NonNull String data);

    protected boolean verifyParams(Map<String, String> params) {
        if (params == null || params.size() == 0) {
            return true;
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            //Log.d(TAG, "verifyParams, key=" + entry.getKey() + ", value=" + entry.getValue());
            if (entry.getValue() == null) {
                Log.e(TAG, "request fail due to param invalid: " +
                        "key=" + entry.getKey() + ", value=" + entry.getValue());
                return false;
            }
        }
        return true;
    }

    protected int verifyResult(@NonNull T result) {
        return DataHull.ERR_NONE;
    }

    protected Map<String, String> getParams() {
        return mParams;
    }

    protected void setParams(Map<String, String> params) {
        mParams = params;
    }

    private int initCheck() {
        if (TextUtils.isEmpty(mUrl)) {
            return DataHull.ERR_URL_NULL;
        }

        if (!verifyParams(mParams)) {
            return DataHull.ERR_PARAMS_INVALID;
        }

        if (!VolleyManager.isNetworkAvailable()) {
            return DataHull.ERR_NO_NET_WORK;
        }

        return DataHull.ERR_NONE;
    }

    private DataHull processData(String data) {
        DataHull datahull = new DataHull();
        datahull.mParams = mParams;
        datahull.mUrl = mUrl;

        if (TextUtils.isEmpty(data)) {
            datahull.mHttpStatus =  mRequest.getHttpStatusCode();
            if (datahull.mHttpStatus == 200) {
                datahull.mStatus = DataHull.ERR_DATA_NULL;
            } else {
                datahull.mStatus = DataHull.ERR_REQUEST_FAIL;
            }
            return datahull;
        }
        datahull.mOriginData = data;

        T result = null;
        try {
            result = parseResult(data);
        } catch (Exception e) {
            Log.e(TAG, "parse result fail", e);
        }
        if (result == null) {
            datahull.mStatus = DataHull.ERR_DATA_PARSE_FAIL;
            return datahull;
        }
        datahull.mParsedData = result;

        datahull.mStatus = verifyResult(result);
        return datahull;
    }

    private void informCallback(final DataHull datahull) {
        if (mCallback != null) {
            VolleyManager.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCallback.onRequestComplete(datahull);
                }
            });
        }
    }

    protected DataHull createErrorDataHull(int err) {
        DataHull datahull = new DataHull();
        datahull.mParams = mParams;
        datahull.mUrl = mUrl;
        datahull.mStatus = err;
        return datahull;
    }

}
