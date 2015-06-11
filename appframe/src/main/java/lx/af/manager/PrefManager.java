package lx.af.manager;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * author: liuxu
 * date: 2015-02-12
 * to easy access SharedPreferences.
 */
public final class PrefManager {

    private static Application sApp;
    private static PrefManager sInstance;

    public synchronized static void getInstance() {
        if (sInstance == null) {
            sInstance = new PrefManager();
        }
    }

    private PrefManager() {}

    public static void init(Application app) {
        sApp = app;
    }

    /**
     * get saved serializable object
     * @param key key
     * @param defValue default value
     * @return value according to key, or default value if no entry is found.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T getSerializable(String key, T defValue) {
        String valueString = PrefManager.getString(key, key, defValue.toString());
        if (valueString == null || valueString.length() == 0) {
            saveSerializable(key, defValue);
            return defValue;
        }
        try {
            Object resultObject = fromString(valueString);
            return (T) resultObject;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    /**
     * save serializable object
     * @param key key
     * @param value value
     * @return true if save success
     */
    public static <T extends Serializable> boolean saveSerializable(String key, T value) {
        if (value == null) {
            return false;
        }
        try {
            String valueString = toString(value);
            PrefManager.putString(key, key, valueString);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * get boolean from default SharedPreferences
     */
    public static boolean getBoolean(String key, boolean defValue) {
        return getDefaultPref().getBoolean(key, defValue);
    }

    /**
     * get string from default SharedPreferences
     */
    public static String getString(String key, String defValue) {
        return getDefaultPref().getString(key, defValue);
    }

    /**
     * get int from default SharedPreferences
     */
    public static int getInt(String key, int defValue) {
        return getDefaultPref().getInt(key, defValue);
    }

    /**
     * get long from default SharedPreferences
     */
    public static long getLong(String key, long defValue) {
        return getDefaultPref().getLong(key, defValue);
    }

    /**
     * put int to default SharedPreferences
     */
    public static void putInt(String key, int value) {
        getDefaultPref().edit().putInt(key, value).apply();
    }

    /**
     * put long to default SharedPreferences
     */
    public static void putLong(String key, long value) {
        getDefaultPref().edit().putLong(key, value).apply();
    }

    /**
     * put boolean to default SharedPreferences
     */
    public static void putBoolean(String key, boolean value) {
        getDefaultPref().edit().putBoolean(key, value).apply();
    }

    /**
     * put string to default SharedPreferences
     */
    public static void putString(String key, String value) {
        getDefaultPref().edit().putString(key, value).apply();
    }

    /**
     * get boolean from SharedPreferences with a specified name
     * @param name the SharedPreferences name
     */
    public static boolean getBoolean(String name, String key, boolean defValue) {
        //Log.d("getBoolean() : name = " + name + " : key = " + key + " : defValue = " + defValue);
        return getPref(name).getBoolean(key, defValue);
    }

    /**
     * get string from SharedPreferences with a specified name
     * @param name the SharedPreferences name
     */
    public static String getString(String name, String key, String defValue) {
        //Log.d("getString() : name = " + name + " : key = " + key + " : defValue = " + defValue);
        return getPref(name).getString(key, defValue);
    }

    /**
     * get int from SharedPreferences with a specified name
     * @param name the SharedPreferences name
     */
    public static int getInt(String name, String key, int defValue) {
        //Log.d("getInt() : name = " + name + " : key = " + key + " : defValue = " + defValue);
        return getPref(name).getInt(key, defValue);
    }

    /**
     * get long from SharedPreferences with a specified name
     * @param name the SharedPreferences name
     */
    public static long getLong(String name, String key, long defValue) {
        //Log.d("getLong() : name = " + name + " : key = " + key + " : defValue = " + defValue);
        return getPref(name).getLong(key, defValue);
    }

    /**
     * put int to SharedPreferences with a specified name
     * @param name the SharedPreferences name
     */
    public static void putInt(String name, String key, int value) {
        //Log.d("putInt() : name = " + name + " : key = " + key + " : value = " + value);
        getPref(name).edit().putInt(key, value).apply();
    }

    /**
     * put long to SharedPreferences with a specified name
     * @param name the SharedPreferences name
     */
    public static void putLong(String name, String key, long value) {
        //Log.d("putLong() : name = " + name + " : key = " + key + " : value = " + value);
        getPref(name).edit().putLong(key, value);
    }

    /**
     * put boolean to SharedPreferences with a specified name
     * @param name the SharedPreferences name
     */
    public static void putBoolean(String name, String key, boolean value) {
        //Log.d("putBoolean() : name = " + name + " : key = " + key + " : defValue = " + value);
        getPref(name).edit().putBoolean(key, value).apply();
    }

    /**
     * put string to SharedPreferences with a specified name
     * @param name the SharedPreferences name
     */
    public static void putString(String name, String key, String value) {
        //Log.d("putString() : name = " + name + " : key = " + key + " : defValue = " + value);
        getPref(name).edit().putString(key, value).apply();
    }

    /**
     * read the object from Base64 string.
     */
    private static Object fromString(String s) throws IOException,
            ClassNotFoundException {
        byte[] data = Base64.decode(s, Base64.DEFAULT);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }

    /**
     * write the object to a Base64 string.
     */
    private static String toString(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
    }

    /**
     * get default SharedPreferences
     */
    public static SharedPreferences getDefaultPref() {
        return PreferenceManager.getDefaultSharedPreferences(sApp);
    }

    /**
     * get SharedPreferences with a specified name
     */
    public static SharedPreferences getPref(String name) {
        return sApp.getSharedPreferences(name, Context.MODE_PRIVATE);
    }
}