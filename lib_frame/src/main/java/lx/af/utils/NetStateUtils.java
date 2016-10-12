package lx.af.utils;

import android.app.Application;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/**
 * created by liuxu. many methods here are collected from various source.
 *
 * methods about network state.
 */
public final class NetStateUtils {

    private static Application sApp;

    public static final int NET_TYPE_NO = 0;
    public static final int NET_TYPE_WIFI = 1;
    public static final int NET_TYPE_2G = 2;
    public static final int NET_TYPE_3G = 3;

    private NetStateUtils() {
    }

    public static void init(Application app) {
        sApp = app;
    }

    public static boolean isNetConnected() {
        return getAvailableNetWorkInfo() != null;
    }

    public static boolean isWifiConnected() {
        NetworkInfo networkInfo = getAvailableNetWorkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWifiEnable() {
        WifiManager wifiManager = (WifiManager) sApp.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    public static boolean isGpsEnable() {
        LocationManager locationManager =
                ((LocationManager) sApp.getSystemService(Context.LOCATION_SERVICE));
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean isThirdGeneration() {
        TelephonyManager telephonyManager =
                (TelephonyManager) sApp.getSystemService(Context.TELEPHONY_SERVICE);
        int netWorkType = telephonyManager.getNetworkType();
        switch (netWorkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false;
            default:
                return true;
        }
    }

    public static NetworkInfo getAvailableNetWorkInfo() {
        NetworkInfo activeNetInfo;
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) sApp.getSystemService(Context.CONNECTIVITY_SERVICE);
            activeNetInfo = connectivityManager.getActiveNetworkInfo();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (activeNetInfo != null && activeNetInfo.isAvailable()
                && activeNetInfo.isConnected()) {
            return activeNetInfo;
        } else {
            return null;
        }
    }

    public static String getNetWorkType() {

        String netWorkType = "";
        NetworkInfo netWorkInfo = getAvailableNetWorkInfo();

        if (netWorkInfo != null) {
            if (netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                netWorkType = "1";
            } else if (netWorkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {

                TelephonyManager telephonyManager = (TelephonyManager) sApp
                        .getSystemService(Context.TELEPHONY_SERVICE);

                switch (telephonyManager.getNetworkType()) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                        netWorkType = "2";
                        break;
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        netWorkType = "3";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                        netWorkType = "4";
                        break;
                    // case TelephonyManager.NETWORK_TYPE_HSDPA:
                    // netWorkType = "5";
                    // break;
                    // case TelephonyManager.NETWORK_TYPE_HSUPA:
                    // netWorkType = "6";
                    // break;
                    // case TelephonyManager.NETWORK_TYPE_HSPA:
                    // netWorkType = "7";
                    // break;
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                        netWorkType = "8";
                        break;
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        netWorkType = "9";
                        break;
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        netWorkType = "10";
                        break;
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                        netWorkType = "11";
                        break;
                    default:
                        netWorkType = "-1";
                        break;
                }

            }

        }
        return netWorkType;
    }

    public static int getNetType() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) sApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isAvailable()
                && networkInfo.isConnected()) {
            if (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) {
                return NET_TYPE_WIFI;
            } else {
                TelephonyManager telephonyManager = (TelephonyManager) sApp
                        .getSystemService(Context.TELEPHONY_SERVICE);

                switch (telephonyManager.getNetworkType()) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        return NET_TYPE_2G;
                    default:
                        return NET_TYPE_3G;
                }
            }
        } else {
            return NET_TYPE_NO;
        }
    }
}
