package lx.af.demo.utils.TestData;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import lx.af.demo.base.DemoApp;

/**
 * author: lx
 * date: 16-3-22
 */
public class TestDataHelper {

    private static ArrayList<String> mNameList;
    private static ArrayList<String> mLongStringList;
    private static ArrayList<String> mAddressList;

    public static String getRandomName() {
        if (mNameList == null) {
            PackageManager pm = DemoApp.getInstance().getPackageManager();
            List<ApplicationInfo> appList = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            mNameList = new ArrayList<>(appList.size());
            for (ApplicationInfo ai : appList) {
                mNameList.add(ai.loadLabel(pm).toString());
            }
        }
        if (mNameList == null || mNameList.size() == 0) {
            return "Default Name";
        }
        int idx = random(mNameList.size());
        return mNameList.get(idx);
    }

    public static String getRandomLongString() {
        if (mLongStringList == null) {
            initLongStringList();
        }
        if (mLongStringList == null) {
            return "Laziness is a feature of a programmer.";
        }
        int idx = random(mLongStringList.size());
        return mLongStringList.get(idx);
    }

    public static String getRandomShortString() {
        return getRandomName();
    }

    public static String getRandomAddress() {
        if (mAddressList == null) {
            initAddressList();
        }
        int idx = random(mAddressList.size());
        return mAddressList.get(idx);
    }

    public static long getRandomTime() {
        Calendar time = Calendar.getInstance();
        int day = 1 + random(time.get(Calendar.DAY_OF_MONTH));
        int hour = 1 + random(22);
        int minute = 1 + random(56);
        time.set(Calendar.DAY_OF_MONTH, day);
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
        return time.getTimeInMillis();
    }

    public static int random(int n) {
        return (new Random()).nextInt(n);
    }


    private static void initLongStringList() {
        PackageManager pm = DemoApp.getInstance().getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(DemoApp.getInstance().getPackageName(), 0);
            String pkgName = pi.packageName;
            PackageInfo pkgInfo = pm.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS);
            String sharedPkgList[] = pkgInfo.requestedPermissions;
            mLongStringList = new ArrayList<>();
            for (String name : sharedPkgList) {
                PermissionInfo perm = pm.getPermissionInfo(name, 0);
                mLongStringList.add(name + ": " + perm.loadDescription(pm).toString());
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void initAddressList() {
        mAddressList = new ArrayList<>();
        mAddressList.add("Jining, home town, China.");
        mAddressList.add("Yantai, university, China.");
        mAddressList.add("Qinhuangdao, university, China.");
        mAddressList.add("Beijing, first job, China.");
        mAddressList.add("Jinan, work place, China.");
        mAddressList.add("Landon, England.");
        mAddressList.add("Paris, French.");
        mAddressList.add("Moscow, Russia.");
        mAddressList.add("Tokyo, Japan.");
        mAddressList.add("Washington, USA.");
        mAddressList.add("Rio, Brazil.");
    }

}
