package lx.af.net.HttpRequest.volley.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * author: lx
 * date: 16-1-4
 *
 * post params to server using volley, with return data parsed as raw string.
 */
public class PostParamsRequest extends Request<String> {

    private Map<String, String> mParams;
    private Response.Listener<String> mListener;
    private VolleyError mError;

    public PostParamsRequest(String url, Map<String, String> params,
                             Response.Listener<String> listener,
                             Response.ErrorListener errListener) {
        super(Method.POST, url, errListener);
        mParams = params;
        mListener = listener;
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mError = error;
        super.deliverError(error);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }

    public int getHttpStatusCode() {
        if (mError != null && mError.networkResponse != null) {
            return mError.networkResponse.statusCode;
        } else {
            return 200;
        }
    }

}
