package lx.af.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by liuxu on 15-5-19.
 * workaround to for getting soft keyboard show/hide state.
 */
public class SoftKeyboardStateHelper implements ViewTreeObserver.OnGlobalLayoutListener {

    private static final String PREF_LAST_KEYBOARD_HEIGHT = "pref_last_keyboard_height";

    private final List<SoftKeyboardStateListener> mListeners = new LinkedList<SoftKeyboardStateListener>();
    private final View mRootView;
    private final SharedPreferences mPref;
    private int mStatusBarHeight;
    private int mLastKeyboardHeight; // in px
    private boolean mIsKeyboardOpen;

    public SoftKeyboardStateHelper(Activity activity) {
        this(activity, false);
    }

    public SoftKeyboardStateHelper(Activity activity, boolean isSoftKeyboardOpened) {
        mRootView = activity.getWindow().getDecorView();
        mIsKeyboardOpen = isSoftKeyboardOpened;
        mStatusBarHeight = ScreenUtils.getStatusBarHeight(activity);
        mPref = PreferenceManager.getDefaultSharedPreferences(activity);
        mLastKeyboardHeight = mPref.getInt(PREF_LAST_KEYBOARD_HEIGHT, 0);
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
        boolean changed = mLastKeyboardHeight != keyboardHeightInPx;
        if (changed) {
            mLastKeyboardHeight = keyboardHeightInPx;
            mPref.edit().putInt(PREF_LAST_KEYBOARD_HEIGHT, keyboardHeightInPx).apply();
        }

        for (SoftKeyboardStateListener listener : mListeners) {
            if (listener != null) {
                listener.onSoftKeyboardOpened(keyboardHeightInPx, changed);
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

    public interface SoftKeyboardStateListener {
        void onSoftKeyboardOpened(int keyboardHeightInPx, boolean heightChanged);
        void onSoftKeyboardClosed();
    }
}