package lx.af.net.HttpRequest;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

/**
 * author: lx
 * date: 15-8-27
 *
 * request using volley, with returned data parsed into a JsonHolder object.
 */
public class VolleyJsonRequest<T> extends VolleyStringRequest<T> {

    private static Gson sGson = new Gson();

    private TypeToken<T> mTypeToken;

    public VolleyJsonRequest(String url, Map<String, String> params, TypeToken<T> typeToken) {
        super(url, params);
        mTypeToken = typeToken;
    }

    @Override
    protected T parseResult(@NonNull String data) {
        return sGson.fromJson(data, mTypeToken.getType());
    }

}
