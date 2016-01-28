package lx.af.net.HttpRequest;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.ParseError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lx.af.net.HttpRequest.volley.request.DownloadRequest;
import lx.af.net.HttpRequest.volley.request.StringRequestListener;

/**
 * author: lx
 * date: 16-1-11
 */
public class VolleyDownloadRequest implements IRequest {

    private static final long TIMEOUT = 10;  // request timeout in 10 seconds

    private String mUrl;
    private String mPath;
    private RequestCallback mCallback;
    private DownloadRequest mRequest;

    private StringRequestListener mRequestListener = new StringRequestListener() {

        @Override
        public void onResponse(String path) {
            DataHull datahull = createDataHull(path);
            informCallback(datahull);
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            DataHull datahull;
            if (error instanceof TimeoutError) {
                datahull = createErrorDataHull(DataHull.ERR_REQUEST_TIMEOUT);
            } else if (error instanceof ParseError) {
                datahull = createErrorDataHull(DataHull.ERR_DATA_PARSE_FAIL);
            } else {
                datahull = createErrorDataHull(DataHull.ERR_REQUEST_FAIL);
            }
            if (error.networkResponse != null) {
                datahull.mHttpStatus = error.networkResponse.statusCode;
            }
            informCallback(datahull);
        }
    };

    public VolleyDownloadRequest(String url, String path) {
        this.mUrl = url;
        this.mPath = path;
    }

    @Override
    public DataHull request() {
        VolleyManager.throwIfRequestInMainThread();

        int code = initCheck();
        if (code != DataHull.ERR_NONE) {
            return createErrorDataHull(code);
        }

        RequestFuture<String> future = RequestFuture.newFuture();
        mRequest = new DownloadRequest(mUrl, mPath, future, future);
        VolleyManager.addRequest(mRequest);

        String path;
        try {
            // block for result
            path = future.get(TIMEOUT, TimeUnit.SECONDS);
        } catch (TimeoutException te) {
            Log.e(TAG, "image request timeout", te);
            return createErrorDataHull(DataHull.ERR_REQUEST_TIMEOUT);
        } catch (InterruptedException ie) {
            Log.e(TAG, "image request interrupted", ie);
            return createErrorDataHull(DataHull.ERR_REQUEST_CANCELED);
        } catch (Exception e) {
            Log.e(TAG, "image request fail", e);
            return createErrorDataHull(DataHull.ERR_REQUEST_FAIL);
        }

        if (path != null) {
            return createDataHull(path);
        } else {
            return createErrorDataHull(DataHull.ERR_DATA_PARSE_FAIL);
        }
    }

    @Override
    public void requestAsync(RequestCallback callback) {
        mCallback = callback;
        int code = initCheck();
        if (code != DataHull.ERR_NONE) {
            informCallback(createErrorDataHull(code));
            return;
        }
        mCallback = callback;
        mRequest = new DownloadRequest(mUrl, mPath, mRequestListener, mRequestListener);
        VolleyManager.addRequest(mRequest);
    }

    private int initCheck() {
        if (TextUtils.isEmpty(mUrl)) {
            return DataHull.ERR_URL_NULL;
        }
        if (!VolleyManager.isNetworkAvailable()) {
            return DataHull.ERR_NO_NET_WORK;
        }
        return DataHull.ERR_NONE;
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

    private DataHull createDataHull(String path) {
        DataHull datahull = new DataHull();
        datahull.mUrl = mUrl;
        datahull.mOriginData = path;
        datahull.mParsedData = path;
        datahull.mStatus = DataHull.ERR_NONE;
        datahull.mHttpStatus = 200;
        return datahull;
    }

    protected DataHull createErrorDataHull(int err) {
        DataHull datahull = new DataHull();
        datahull.mUrl = mUrl;
        datahull.mStatus = err;
        return datahull;
    }

}
