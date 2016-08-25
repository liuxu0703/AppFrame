package lx.af.utils;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import lx.af.base.AbsBaseActivity;

/**
 * author: lx
 * date: 16-6-30
 */
public class PhoneStateHelper {

    private AbsBaseActivity mActivity;
    private TelephonyManager mTelephonyManager;
    private StateIdleCallback mIdleCallback;
    private StateRingCallback mRingCallback;
    private int mSavedState;

    private PhoneStateHelper(AbsBaseActivity activity) {
        mActivity = activity;
    }

    public static PhoneStateHelper with(AbsBaseActivity activity) {
        return new PhoneStateHelper(activity);
    }

    public PhoneStateHelper setSavedState(int state) {
        mSavedState = state;
        return this;
    }

    public PhoneStateHelper setIdleCallback(StateIdleCallback c) {
        mIdleCallback = c;
        return this;
    }

    public PhoneStateHelper setRingCallback(StateRingCallback c) {
        mRingCallback = c;
        return this;
    }

    public void start() {
        mTelephonyManager = (TelephonyManager) mActivity.getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        mActivity.addLifeCycleListener(new AbsBaseActivity.LifeCycleAdapter() {
            @Override
            public void onActivityDestroyed(AbsBaseActivity activity) {
                mTelephonyManager.listen(mPhoneListener, PhoneStateListener.LISTEN_NONE);
            }
        });
    }

    public int getSavedState() {
        return mSavedState;
    }

    private PhoneStateListener mPhoneListener = new PhoneStateListener() {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            Log.d("PhoneStateHelper", "phone state changed: " + state);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE: {
                    if (mIdleCallback != null) {
                        mSavedState = mIdleCallback.onStateIdle(mSavedState);
                    }
                    break;
                }
                case TelephonyManager.CALL_STATE_RINGING:
                case TelephonyManager.CALL_STATE_OFFHOOK: {
                    if (mRingCallback != null) {
                        mSavedState = mRingCallback.onStateRing(mSavedState);
                    }
                    break;
                }
            }
        }
    };


    public interface StateIdleCallback {
        int onStateIdle(int savedState);
    }

    public interface StateRingCallback {
        int onStateRing(int savedState);
    }

}
