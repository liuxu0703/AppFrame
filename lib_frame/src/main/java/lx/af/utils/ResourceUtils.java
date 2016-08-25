package lx.af.utils;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Application;
import android.content.res.AssetFileDescriptor;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.io.InputStream;

/**
 * author: lx
 * date: 16-1-27
 */
public final class ResourceUtils {

    private static Application sApp;
    private static Resources sRes;

    private ResourceUtils() {
    }

    public static void init(Application app) {
        sApp = app;
        sRes = app.getResources();
    }

    public static Resources getRes() {
        return sRes;
    }

    public static String getString(int resId) {
        return sRes.getString(resId);
    }

    public static String getString(int resId, Object... formatArgs) {
        return sRes.getString(resId, formatArgs);
    }

    public static String[] getStrings(int resId){
        return sRes.getStringArray(resId);
    }

    public static int getColor(int resId) {
        return sRes.getColor(resId);
    }

    public static int getInteger(int id) throws Resources.NotFoundException {
        return sRes.getInteger(id);
    }

    public static boolean getBoolean(int id) throws Resources.NotFoundException {
        return sRes.getBoolean(id);
    }

    public static Drawable getDrawable(int id) throws Resources.NotFoundException {
        return sRes.getDrawable(id);
    }

    public static ColorStateList getColorStateList(int id) throws Resources.NotFoundException {
        return sRes.getColorStateList(id);
    }

    public static float getDimension(int id) throws Resources.NotFoundException {
        return sRes.getDimension(id);
    }

    public static int getDimensionPixelOffset(int id) throws Resources.NotFoundException {
        return sRes.getDimensionPixelOffset(id);
    }

    public static int getDimensionPixelSize(int id) throws Resources.NotFoundException {
        return sRes.getDimensionPixelSize(id);
    }

    public static Configuration getConfiguration() {
        return sRes.getConfiguration();
    }

    public static InputStream openRawResource(int id) throws Resources.NotFoundException {
        return sRes.openRawResource(id);
    }

    public static AssetFileDescriptor openRawResourceFd(int id) throws Resources.NotFoundException {
        return sRes.openRawResourceFd(id);
    }

    public static InputStream openRawResource(int id, TypedValue value) throws Resources.NotFoundException {
        return sRes.openRawResource(id, value);
    }

    // ==========================================

    public static Animation loadAnimation(int animId) {
        return AnimationUtils.loadAnimation(sApp, animId);
    }

    public static Animation loadAnimation(int animId, long duration) {
        Animation anim = AnimationUtils.loadAnimation(sApp, animId);
        anim.setDuration(duration);
        return anim;
    }

    public static Animator loadAnimator(int animId) {
        return AnimatorInflater.loadAnimator(sApp, animId);
    }

    public static Animator loadAnimator(int animId, long duration) {
        Animator anim = AnimatorInflater.loadAnimator(sApp, animId);
        anim.setDuration(duration);
        return anim;
    }

}
