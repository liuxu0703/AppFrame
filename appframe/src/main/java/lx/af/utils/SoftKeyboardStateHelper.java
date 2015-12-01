package lx.af.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by liuxu on 15-5-19.
 * workaround to for getting soft keyboard show/hide state.
 */
public class SoftKeyboardStateHelper implements ViewTreeObserver.OnGlobalLayoutListener {

    private final List<SoftKeyboardStateListener> mListeners = new LinkedList<SoftKeyboardStateListener>();
    private final View mRootView;
    private int mStatusBarHeight;
    private int mLastKeyboardHeight; // in px
    private boolean mIsKeyboardOpen;

    public SoftKeyboardStateHelper(Activity activity) {
        this(activity, false);
    }

    public SoftKeyboardStateHelper(Activity activity, boolean isSoftKeyboardOpened) {
        mRootView = activity.getWindow().getDecorView();
        mIsKeyboardOpen = isSoftKeyboardOpened;
        mStatusBarHeight = getStatusBarHeight(activity);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        final Rect r = new Rect();
        //r will be populated with the coordinates of your view that area still visible.
        mRootView.getWindowVisibleDisplayFrame(r);
        final int heightDiff = mRootView.getHeight() - (r.bottom - r.top) - mStatusBarHeight;
        if (!mIsKeyboardOpen && heightDiff > 100) {
            // if more than 100 pixels, its probably a keyboard...
            mIsKeyboardOpen = true;
            notifyOnSoftKeyboardOpened(heightDiff);
        } else if (mIsKeyboardOpen && heightDiff < 100) {
            mIsKeyboardOpen = false;
            notifyOnSoftKeyboardClosed();
        }
    }

    public boolean isKeyboardOpened() {
        return mIsKeyboardOpen;
    }

    /**
     * Default value is zero
     * @return last saved keyboard height in px
     */
    public int getLastKeyboardHeight() {
        return mLastKeyboardHeight;
    }

    public void addStateListener(SoftKeyboardStateListener listener) {
        mListeners.add(listener);
    }

    public void removeStateListener(SoftKeyboardStateListener listener) {
        mListeners.remove(listener);
    }

    private void notifyOnSoftKeyboardOpened(int keyboardHeightInPx) {
        this.mLastKeyboardHeight = keyboardHeightInPx;

        for (SoftKeyboardStateListener listener : mListeners) {
            if (listener != null) {
                listener.onSoftKeyboardOpened(keyboardHeightInPx);
            }
        }
    }

    private void notifyOnSoftKeyboardClosed() {
        for (SoftKeyboardStateListener listener : mListeners) {
            if (listener != null) {
                listener.onSoftKeyboardClosed();
            }
        }
    }

    /**
     * get status bar height
     * @param activity must be instance of activity
     * @return status bar height
     */
    private static int getStatusBarHeight(Activity activity){
        int height;
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        height = rect.top;
        if (height == 0) {
            Class<?> cls;
            try {
                cls = Class.forName("com.android.internal.R$dimen");
                Object localObject = cls.newInstance();
                String sbh = cls.getField("status_bar_height").get(localObject).toString();
                int i5 = Integer.parseInt(sbh);
                height = activity.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return height;
    }

    public interface SoftKeyboardStateListener {
        void onSoftKeyboardOpened(int keyboardHeightInPx);
        void onSoftKeyboardClosed();
    }
}