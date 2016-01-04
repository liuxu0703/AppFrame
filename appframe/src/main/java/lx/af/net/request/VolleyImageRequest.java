package lx.af.net.request;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.RequestFuture;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lx.af.manager.GlobalThreadManager;

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
            GlobalThreadManager.runInUiThread(new Runnable() {
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


    /**
     * A canned request for getting an image at a given URL and calling
     * back with a decoded Bitmap.
     */
    public static class ImageRequest extends Request<Bitmap> {

        /** Socket timeout in milliseconds for image requests */
        private static final int IMAGE_TIMEOUT_MS = 1000;

        /** Default number of retries for image requests */
        private static final int IMAGE_MAX_RETRIES = 2;

        /** Default backoff multiplier for image requests */
        private static final float IMAGE_BACKOFF_MULT = 2f;

        private final Response.Listener<Bitmap> mListener;
        private final Bitmap.Config mDecodeConfig;
        private final int mMaxWidth;
        private final int mMaxHeight;

        private long mCacheTime;

        /** Decoding lock so that we don't decode more than one image at a time (to avoid OOM's) */
        private static final Object sDecodeLock = new Object();

        /**
         * Creates a new image request, decoding to a maximum specified width and
         * height. If both width and height are zero, the image will be decoded to
         * its natural size. If one of the two is nonzero, that dimension will be
         * clamped and the other one will be set to preserve the image's aspect
         * ratio. If both width and height are nonzero, the image will be decoded to
         * be fit in the rectangle of dimensions width x height while keeping its
         * aspect ratio.
         *
         * @param url URL of the image
         * @param listener Listener to receive the decoded bitmap
         * @param maxWidth Maximum width to decode this bitmap to, or zero for none
         * @param maxHeight Maximum height to decode this bitmap to, or zero for
         *            none
         * @param decodeConfig Format to decode the bitmap to
         * @param errorListener Error listener, or null to ignore errors
         */
        public ImageRequest(String url, Response.Listener<Bitmap> listener, int maxWidth, int maxHeight,
                            Bitmap.Config decodeConfig, Response.ErrorListener errorListener) {
            super(Method.GET, url, errorListener);
            setRetryPolicy(
                    new DefaultRetryPolicy(IMAGE_TIMEOUT_MS, IMAGE_MAX_RETRIES, IMAGE_BACKOFF_MULT));
            mListener = listener;
            mDecodeConfig = decodeConfig;
            mMaxWidth = maxWidth;
            mMaxHeight = maxHeight;
        }

        public void setCacheTime(long cacheTime) {
            if (cacheTime < 0) {
                mCacheTime = Long.MAX_VALUE;
            } else {
                mCacheTime = cacheTime;
            }
        }

        @Override
        public Priority getPriority() {
            return Priority.LOW;
        }

        /**
         * Scales one side of a rectangle to fit aspect ratio.
         *
         * @param maxPrimary Maximum size of the primary dimension (i.e. width for
         *        max width), or zero to maintain aspect ratio with secondary
         *        dimension
         * @param maxSecondary Maximum size of the secondary dimension, or zero to
         *        maintain aspect ratio with primary dimension
         * @param actualPrimary Actual size of the primary dimension
         * @param actualSecondary Actual size of the secondary dimension
         */
        private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,
                                               int actualSecondary) {
            // If no dominant value at all, just return the actual.
            if (maxPrimary == 0 && maxSecondary == 0) {
                return actualPrimary;
            }

            // If primary is unspecified, scale primary to match secondary's scaling ratio.
            if (maxPrimary == 0) {
                double ratio = (double) maxSecondary / (double) actualSecondary;
                return (int) (actualPrimary * ratio);
            }

            if (maxSecondary == 0) {
                return maxPrimary;
            }

            double ratio = (double) actualSecondary / (double) actualPrimary;
            int resized = maxPrimary;
            if (resized * ratio > maxSecondary) {
                resized = (int) (maxSecondary / ratio);
            }
            return resized;
        }

        @Override
        protected Response<Bitmap> parseNetworkResponse(NetworkResponse response) {
            // Serialize all decode on a global lock to reduce concurrent heap usage.
            synchronized (sDecodeLock) {
                try {
                    return doParse(response);
                } catch (OutOfMemoryError e) {
                    VolleyLog.e("Caught OOM for %d byte image, url=%s", response.data.length, getUrl());
                    return Response.error(new ParseError(e));
                }
            }
        }

        /**
         * The real guts of parseNetworkResponse. Broken out for readability.
         */
        private Response<Bitmap> doParse(NetworkResponse response) {
            byte[] data = response.data;
            BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
            Bitmap bitmap = null;
            if (mMaxWidth == 0 && mMaxHeight == 0) {
                decodeOptions.inPreferredConfig = mDecodeConfig;
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
            } else {
                // If we have to resize this image, first get the natural bounds.
                decodeOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
                int actualWidth = decodeOptions.outWidth;
                int actualHeight = decodeOptions.outHeight;

                // Then compute the dimensions we would ideally like to decode to.
                int desiredWidth = getResizedDimension(mMaxWidth, mMaxHeight,
                        actualWidth, actualHeight);
                int desiredHeight = getResizedDimension(mMaxHeight, mMaxWidth,
                        actualHeight, actualWidth);

                // Decode to the nearest power of two scaling factor.
                decodeOptions.inJustDecodeBounds = false;
                // TODO(ficus): Do we need this or is it okay since API 8 doesn't support it?
                // decodeOptions.inPreferQualityOverSpeed = PREFER_QUALITY_OVER_SPEED;
                decodeOptions.inSampleSize =
                        findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
                Bitmap tempBitmap =
                        BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);

                // If necessary, scale down to the maximal acceptable size.
                if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth ||
                        tempBitmap.getHeight() > desiredHeight)) {
                    bitmap = Bitmap.createScaledBitmap(tempBitmap,
                            desiredWidth, desiredHeight, true);
                    tempBitmap.recycle();
                } else {
                    bitmap = tempBitmap;
                }
            }

            if (bitmap == null) {
                return Response.error(new ParseError(response));
            } else {
                return Response.success(bitmap, parseCacheEntry(response));
            }
        }

        @Override
        protected void deliverResponse(Bitmap response) {
            mListener.onResponse(response);
        }

        /**
         * Returns the largest power-of-two divisor for use in downscaling a bitmap
         * that will not result in the scaling past the desired dimensions.
         *
         * @param actualWidth Actual width of the bitmap
         * @param actualHeight Actual height of the bitmap
         * @param desiredWidth Desired width of the bitmap
         * @param desiredHeight Desired height of the bitmap
         */
        // Visible for testing.
        static int findBestSampleSize(
                int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
            double wr = (double) actualWidth / desiredWidth;
            double hr = (double) actualHeight / desiredHeight;
            double ratio = Math.min(wr, hr);
            float n = 1.0f;
            while ((n * 2) <= ratio) {
                n *= 2;
            }

            return (int) n;
        }

        /**
         * Extracts a {@link Cache.Entry} from a {@link NetworkResponse}.
         * Cache-control headers are ignored. SoftTtl == 3 mins, ttl == 24 hours.
         * @param response The network response to parse headers from
         * @return a cache entry for the given response, or null if the response is not cacheable.
         */
        Cache.Entry parseCacheEntry(NetworkResponse response) {
            long now = System.currentTimeMillis();

            Map<String, String> headers = response.headers;
            long serverDate = 0;
            String serverEtag = null;
            String headerValue;

            headerValue = headers.get("Date");
            if (headerValue != null) {
                serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
            }

            serverEtag = headers.get("ETag");

            long ttl = 0;
            if (shouldCache()) {
                ttl = now + mCacheTime;
            }

            Cache.Entry entry = new Cache.Entry();
            entry.data = response.data;
            entry.etag = serverEtag;
            entry.softTtl = ttl;
            entry.ttl = ttl;
            entry.serverDate = serverDate;
            entry.responseHeaders = headers;

            return entry;
        }
    }

}
