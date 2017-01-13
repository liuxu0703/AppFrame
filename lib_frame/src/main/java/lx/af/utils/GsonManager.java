package lx.af.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * author: lx
 * date: 16-5-25
 */
public class GsonManager {

    public static Gson getGson() {
        return sGson;
    }

    private GsonManager() {}

    private static JsonDeserializer mIntegerDeserializer = new JsonDeserializer<Integer>() {
        @Override
        public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsInt();
            } catch (Exception e) {
                return 0;
            }
        }
    };

    private static JsonDeserializer mLongDeserializer = new JsonDeserializer<Long>() {
        @Override
        public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsLong();
            } catch (Exception e) {
                return 0L;
            }
        }
    };

    private static JsonDeserializer mFloatDeserializer = new JsonDeserializer<Float>() {
        @Override
        public Float deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsFloat();
            } catch (Exception e) {
                return 0f;
            }
        }
    };

    private static JsonDeserializer mDoubleDeserializer = new JsonDeserializer<Double>() {
        @Override
        public Double deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsDouble();
            } catch (Exception e) {
                return 0d;
            }
        }
    };

    private static JsonDeserializer mStringDeserializer = new JsonDeserializer<String>() {
        @Override
        public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsString();
            } catch (Exception e) {
                return null;
            }
        }
    };

    private static Gson sGson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(int.class, mIntegerDeserializer)
            .registerTypeAdapter(long.class, mLongDeserializer)
            .registerTypeAdapter(float.class, mFloatDeserializer)
            .registerTypeAdapter(double.class, mDoubleDeserializer)
            .registerTypeAdapter(String.class, mStringDeserializer)
            .create();

}
