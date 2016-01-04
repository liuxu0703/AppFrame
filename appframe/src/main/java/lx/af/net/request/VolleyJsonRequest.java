package lx.af.net.request;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lx.af.utils.log.Log;

/**
 * author: lx
 * date: 15-8-27
 *
 * request using volley, with returned data parsed into a JsonHolder object.
 */
public class VolleyJsonRequest<T> extends VolleyStringRequest<T> {

    private static Gson sGson = new Gson();
    private static Pattern sPattern = Pattern.compile("\\s*|\t|\r|\n");

    private TypeToken<T> mTypeToken;

    public VolleyJsonRequest(String url, Map<String, String> params, TypeToken<T> typeToken) {
        super(url, params);
        mTypeToken = typeToken;
    }

    @Override
    protected T parseResult(@NonNull String data) {
        String replaced = replaceBlank(data);
        Log.d("liuxu", "parse data, origin   : " + data);
        Log.d("liuxu", "parse data, replaced : " + replaced);
        return sGson.fromJson(replaceBlank(data), mTypeToken.getType());
    }

    private static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Matcher m = sPattern.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
}
