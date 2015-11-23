package lx.af.utils;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * author: lx
 * date: 15-10-29
 */
public class KV {


    private static SharedPreferences sPreference;

    private KV() {}

    public static void init(Application app) {
        sPreference = PreferenceManager.getDefaultSharedPreferences(app);
    }

    /**
     * get int from default SharedPreferences
     */
    public static int getInt(String key, int defValue) {
        return sPreference.getInt(key, defValue);
    }

    /**
     * get long from default SharedPreferences
     */
    public static long getLong(String key, long defValue) {
        return sPreference.getLong(key, defValue);
    }

    /**
     * get float from default SharedPreferences
     */
    public static float getFloat(String key, float defValue) {
        return sPreference.getFloat(key, defValue);
    }

    /**
     * get boolean from default SharedPreferences
     */
    public static boolean getBoolean(String key, boolean defValue) {
        return sPreference.getBoolean(key, defValue);
    }

    /**
     * get string from default SharedPreferences
     */
    public static String getString(String key, String defValue) {
        return sPreference.getString(key, defValue);
    }

    /**
     * put int to default SharedPreferences
     */
    public static void put(String key, int value) {
        sPreference.edit().putInt(key, value).apply();
    }

    /**
     * put long to default SharedPreferences
     */
    public static void put(String key, long value) {
        sPreference.edit().putLong(key, value).apply();
    }

    /**
     * put float to default SharedPreferences
     */
    public static void put(String key, float value) {
        sPreference.edit().putFloat(key, value).apply();
    }

    /**
     * put boolean to default SharedPreferences
     */
    public static void put(String key, boolean value) {
        sPreference.edit().putBoolean(key, value).apply();
    }

    /**
     * put string to default SharedPreferences
     */
    public static void put(String key, String value) {
        sPreference.edit().putString(key, value).apply();
    }
    
}
