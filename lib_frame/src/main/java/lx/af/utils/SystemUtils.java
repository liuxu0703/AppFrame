package lx.af.utils;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.util.List;

/**
 * created by liuxu. many methods here are collected from various site.
 *
 * methods about package.
 */
public final class SystemUtils {

    private static Application sApp;

    private SystemUtils() {
    }

    public static void init(Application app) {
        sApp = app;
    }


    // =======================================================
    // about device info

    /**
     * get Android OS version, like 4.2.2
     * @return OS version
     */
    public static String getOSVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * get api level:
     *
     * Android 5.0      ->  21  LOLLIPOP
     * Android 4.4W     ->  20  KITKAT_WATCH
     * Android 4.4      ->  19  KITKAT
     * Android 4.3      ->  18  JELLY_BEAN_MR2
     * Android 4.2      ->  17  JELLY_BEAN_MR1
     * Android 4.1      ->  16  JELLY_BEAN
     * Android 4.0.3    ->  15  ICE_CREAM_SANDWICH_MR1
     * Android 4.0      ->  14  ICE_CREAM_SANDWICH
     * Android 3.2      ->  13  HONEYCOMB_MR2
     * Android 3.1      ->  12  HONEYCOMB_MR1
     * Android 3.0      ->  11  HONEYCOMB
     * Android 2.3.3    ->  10  GINGERBREAD_MR1
     * Android 2.3      ->  9   GINGERBREAD
     * Android 2.2      ->  8   FROYO
     * Android 2.1      ->  7   ECLAIR_MR1
     * Android 2.0.1    ->  6   ECLAIR_0_1
     * Android 2.0      ->  5   ECLAIR
     * Android 1.6      ->  4   DONUT
     * Android 1.5      ->  3   CUPCAKE
     * Android 1.1      ->  2   BASE_1_1
     * Android 1.0      ->  1   BASE
     *
     * @return SDK version
     */
    public static int getAndroidSDKVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * get device model
     * @return android.os.build.MODEL
     */
    public static String getDeviceModel() {
        return android.os.Build.MODEL;
    }

    /**
     * get device brand
     * @return android.os.Build.BRAND
     */
    public static String getTerminalBrand() {
        String brand;
        try {
            brand = android.os.Build.BRAND;
            if (TextUtils.isEmpty(brand)) {
                Class<android.os.Build> build_class = android.os.Build.class;
                java.lang.reflect.Field field;
                field = build_class.getField("BRAND");
                brand = (String) field.get(new android.os.Build());
            }
        } catch (Exception e) {
            e.printStackTrace();
            brand = "";
        }
        return brand;
    }

    /**
     * returns the unique device ID:
     * IMEI for GSM, MEID or ESN for CDMA.
     * return null if device ID is not available.
     * Requires Permission: READ_PHONE_STATE
     */
    public static String getIMEI(){
        android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager)
                sApp.getSystemService(Context.TELEPHONY_SERVICE);
        // Requires Permission: READ_PHONE_STATE
        String device_id = tm.getDeviceId();
        if (TextUtils.isEmpty(device_id)) {
            device_id = android.provider.Settings.Secure.getString(
                    sApp.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
        }
        return device_id;
    }

    /**
     * returns a unique id for the device:
     * return IMEI if available. if not, return ANDROID_ID from settings provider
     * @return a unique id
     */
    public static String getDeviceId() {
        String id = getIMEI();
        if (TextUtils.isEmpty(id)) {
            id = android.provider.Settings.Secure.getString(
                    sApp.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
        }
        return id;
    }

    /**
     * get mac address
     */
    public static String getMacAddress() {
        // TODO
        android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager)
                sApp.getSystemService(Context.WIFI_SERVICE);
        String mac = wifi.getConnectionInfo().getMacAddress();
        if(mac == null){
            mac = "";
        }
        return  mac;
    }


    // =======================================================
    // about application short cut

    /**
     * 删除对应启动界面名对应名称的快捷方式。
     * @param startClass
     *            快捷方式启动的界面。
     * @param shortCutName
     *            快捷方式名称。
     */
    public static void delShortCut(Class<?> startClass,
                                   String shortCutName) {
        Intent shortcut = new Intent(
                "com.android.launcher.action.UNINSTALL_SHORTCUT");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortCutName);
        // 不能用intent.setAction("xxx").setComponent来设置intent。这样会有手机不能启动。
        Intent startIntent = new Intent(sApp, startClass).setAction(
                "android.intent.action.MAIN").addCategory(
                "android.intent.category.LAUNCHER");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, startIntent);
        sApp.sendBroadcast(shortcut);
    }

    /**
     * @param startClass
     *            要开启的界面的class
     * @param shortCutName
     *            快捷方式名称
     * @param shortCutResId
     *            快捷方式图标的资源id。
     * @param coverOld
     *            有快捷方式的情况下，coverOld == true，覆盖原快捷方式；coverOld == false，取消创建；
     */
    public static void addShortcut(Class<?> startClass,
                                   String shortCutName, int shortCutResId, boolean coverOld) {
        if (hasShortCut(shortCutName)) {
            if (!coverOld) {
                return;
            } else {
                delShortCut(startClass, shortCutName);
            }
        }

        Intent shortcut = new Intent(
                "com.android.launcher.action.INSTALL_SHORTCUT");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortCutName);
        shortcut.putExtra("duplicate", false);

        Intent startIntent = new Intent(sApp, startClass).setAction(
                "android.intent.action.MAIN").addCategory(
                "android.intent.category.LAUNCHER");
        // 不能用intent.setAction("xxx").setComponent来设置intent。这样会有手机不能启动。
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, startIntent);
        Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(
                sApp, shortCutResId);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

        sApp.sendBroadcast(shortcut);
    }

    /**
     * see if the short cut already exists
     * @param shortCutName the application name
     * @return true if exists
     */
    public static boolean hasShortCut(String shortCutName) {
        String url = "content://com.android.launcher.settings/favorites?notify=true";
        ContentResolver resolver = sApp.getContentResolver();
        Cursor cursor = resolver.query(Uri.parse(url),
                null,
                "title=? and iconPackage=?",
                new String[] { shortCutName, sApp.getPackageName() },
                null);

        if (null == cursor || !cursor.moveToFirst()) {
            url = "content://com.android.launcher2.settings/favorites?notify=true";
            cursor = resolver.query(Uri.parse(url),
                    null,
                    "title=? and iconPackage=?",
                    new String[] { shortCutName, sApp.getPackageName() },
                    null);
        }

        if (cursor != null) {
            boolean has = cursor.moveToFirst();
            cursor.close();
            return has;
        }
        return false;
    }


    // =======================================================
    // about package

    /**
     * get package name, for example, "com.google.map"
     * @return packageName the package name
     */
    public static String getPackageName() {
        return sApp.getPackageName();
    }

    /**
     * get app version code
     */
    public static int getAppVersionCode() {
        int versionCode = 0;
        PackageManager pm = sApp.getPackageManager();
        try {
            PackageInfo pi;
            pi = pm.getPackageInfo(sApp.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            versionCode = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * get app version name
     */
    public static String getAppVersionName() {
        String versionName = "1.0.0";
        PackageManager pm = sApp.getPackageManager();
        try {
            PackageInfo pi;
            pi = pm.getPackageInfo(sApp.getPackageName(), PackageManager.GET_ACTIVITIES);
            versionName = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * install package
     * @param apkFile the apk file
     */
    public static void installApk(File apkFile) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("application/vnd.android.package-archive");
        intent.setData(Uri.fromFile(apkFile));
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sApp.startActivity(intent);
    }

    // =======================================================
    // system runtime operations

    /**
     * clear background services and processes.
     * @return process count being cleared
     */
    public static int clearBackgroundProcess() {
        long memory = getDeviceAvailableMemory();
        // process count being cleared
        int count = 0;
        ActivityManager am = (ActivityManager) sApp.getSystemService(Context.ACTIVITY_SERVICE);
        // running service list
        List<ActivityManager.RunningServiceInfo> serviceList = am.getRunningServices(100);
        if (serviceList != null)
            for (ActivityManager.RunningServiceInfo service : serviceList) {
                if (service.pid == android.os.Process.myPid())
                    continue;
                try {
                    android.os.Process.killProcess(service.pid);
                    count++;
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }

        // running process list
        List<ActivityManager.RunningAppProcessInfo> processList = am.getRunningAppProcesses();
        if (processList != null)
            for (ActivityManager.RunningAppProcessInfo process : processList) {
                if (process.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    String[] pkgList = process.pkgList;
                    for (String pkgName : pkgList) {
                        //Log.d("liuxu", "kill package: " + pkgName);
                        try {
                            am.killBackgroundProcesses(pkgName);
                            count++;
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                    }
                }
            }
        //Log.d("liuxu", "memory cleared: " + (getDeviceAvailableMemory() -i));
        return count;
    }

    /**
     * back to home screen.
     */
    public static void back2home() {
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        sApp.startActivity(mHomeIntent);
    }

    /**
     * return device available memory
     */
    public static int getDeviceAvailableMemory() {
        ActivityManager am = (ActivityManager) sApp.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return (int) (mi.availMem / (1024 * 1024));
    }

    public static String getCurrentProcessName() {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager)
                sApp.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess :
                activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    // =======================================================
    // other

}
