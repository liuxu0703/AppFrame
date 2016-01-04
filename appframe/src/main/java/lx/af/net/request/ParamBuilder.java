package lx.af.net.request;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * author: lx
 * date: 15-8-27
 *
 * 请求参数创建器
 */
public class ParamBuilder {

    private Map<String, String> mParams;
    private static Gson sGson = new Gson();

    private ParamBuilder(Map<String, String> params) {
        if (params == null) {
            mParams = new HashMap<>();
        } else {
            mParams = params;
        }
    }

    // ========================================
    // public methods

    public static ParamBuilder newInstance() {
        return new ParamBuilder(null);
    }

    public static ParamBuilder newInstance(Map<String, String> params) {
        return new ParamBuilder(params);
    }

    public Map<String, String> build() {
        return mParams;
    }

    public String buildJson() {
        return sGson.toJson(mParams);
    }

    public ParamBuilder put(String key, String value) {
        if (value != null) {
            mParams.put(key, value);
        }
        return this;
    }

    public ParamBuilder put(String key, int value) {
        mParams.put(key, Integer.toString(value));
        return this;
    }

    public ParamBuilder put(String key, long value) {
        mParams.put(key, Long.toString(value));
        return this;
    }

    public ParamBuilder put(String key, boolean value) {
        mParams.put(key, Boolean.toString(value));
        return this;
    }

    public ParamBuilder put(Map<String, String> params) {
        if (params != null) {
            mParams.putAll(params);
        }
        return this;
    }

    public ParamBuilder putJson(String key, Object obj) {
        mParams.put(key, sGson.toJson(obj));
        return this;
    }

}
