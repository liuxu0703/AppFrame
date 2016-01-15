package lx.af.net.HttpRequest;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lx.af.net.HttpRequest.volley.request.ImageRequest;

/**
 * author: lx
 * date: 15-10-15
 */
public class VolleyImageRequest implements IRequest {

    private static final long TIMEOUT = 10;  // request timeout in 10 seconds

    private String mImageUrl;
    private int mImageWidth;
    private int mImageHeight;
    private long mCacheTime;
    private RequestCallback mCallback;
    private ImageRequest mRequest;

    private RequestListener mRequestListener = new RequestListener() {

        @Override
        public void onResponse(Bitmap bitmap) {
            DataHull datahull = createDataHull(bitmap);
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

    public VolleyImageRequest(String url) {
        this(url, 0, 0, TimeUnit.DAYS.toMillis(7));
    }

    public VolleyImageRequest(String url, int width, int height) {
        this(url, width, height, TimeUnit.DAYS.toMillis(7));
    }

    public VolleyImageRequest(String url, int width, int height, long cacheTime) {
        mImageUrl = url;
        mImageWidth = width;
        mImageHeight = height;
        mCacheTime = cacheTime;
    }

    @Override
    public DataHull request() {
        VolleyManager.throwIfRequestInMainThread();

        int code = initCheck();
        if (code != DataHull.ERR_NONE) {
            return createErrorDataHull(code);
        }

        RequestFuture<Bitmap> future = RequestFuture.newFuture();
        mRequest = new ImageRequest(
                mImageUrl,
                future,
                mImageWidth, mImageHeight, Bitmap.Config.RGB_565,
                future);
        mRequest.setShouldCache(true);
        mRequest.setCacheTime(mCacheTime);
        VolleyManager.addRequest(mRequest);

        Bitmap bitmap;
        try {
            // block for result
            bitmap = future.get(TIMEOUT, TimeUnit.SECONDS);
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

        if (bitmap != null) {
            return createDataHull(bitmap);
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
        mRequest = new ImageRequest(
                mImageUrl,
                mRequestListener,
                mImageWidth, mImageHeight, Bitmap.Config.RGB_565,
                mRequestListener);
        mRequest.setShouldCache(true);
        mRequest.setCacheTime(mCacheTime);
        VolleyManager.addRequest(mRequest);
    }

    private int initCheck() {
        if (TextUtils.isEmpty(mImageUrl)) {
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

    private DataHull createDataHull(Bitmap bitmap) {
        DataHull datahull = new DataHull();
        datahull.mUrl = mImageUrl;
        datahull.mOriginData = bitmap;
        datahull.mParsedData = bitmap;
        datahull.mStatus = DataHull.ERR_NONE;
        datahull.mHttpStatus = 200;
        return datahull;
    }

    protected DataHull createErrorDataHull(int err) {
        DataHull datahull = new DataHull();
        datahull.mUrl = mImageUrl;
        datahull.mStatus = err;
        return datahull;
    }

    private abstract static class RequestListener implements
            Response.Listener<Bitmap>, Response.ErrorListener {
    }

}
