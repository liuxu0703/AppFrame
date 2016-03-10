package lx.af.utils.ActivityLauncher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * author: lx
 * date: 16-2-23
 *
 */
public class SimpleActivityLauncher {

    private Activity mActivity;
    private Fragment mFragment;
    private Intent mIntent;
    private int mInAnimResId;
    private int mOutAnimResId;

    protected SimpleActivityLauncher(Activity activity, Class<? extends Activity> target) {
        mActivity = activity;
        mIntent = new Intent(activity, target);
    }

    protected SimpleActivityLauncher(Fragment fragment, Class<? extends Activity> target) {
        mActivity = mFragment.getActivity();
        mFragment = fragment;
        mIntent = new Intent(fragment.getActivity(), target);
    }

    public static SimpleActivityLauncher of(Activity activity, Class<? extends Activity> target) {
        return new SimpleActivityLauncher(activity, target);
    }

    public static SimpleActivityLauncher of(Fragment fragment, Class<? extends Activity> target) {
        return new SimpleActivityLauncher(fragment, target);
    }

    public SimpleActivityLauncher overridePendingTransition(int inAnim, int outAnim) {
        mInAnimResId = inAnim;
        mOutAnimResId = outAnim;
        return this;
    }

    public void start() {
        if (mFragment != null) {
            mFragment.startActivity(mIntent);
        } else {
            mActivity.startActivity(mIntent);
        }
        if (mInAnimResId != 0 || mOutAnimResId != 0) {
            mActivity.overridePendingTransition(mInAnimResId, mOutAnimResId);
        }
    }

    public SimpleActivityLauncher putExtra(String name, boolean value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtra(String name, byte value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtra(String name, char value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtra(String name, short value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtra(String name, int value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtra(String name, long value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtra(String name, float value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtra(String name, double value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtra(String name, String value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtra(String name, CharSequence value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtra(String name, Parcelable value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtra(String name, Parcelable[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putParcelableArrayListExtra(String name, ArrayList<? extends Parcelable> value) {
        mIntent.putParcelableArrayListExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putIntegerArrayListExtra(String name, ArrayList<Integer> value) {
        mIntent.putIntegerArrayListExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putStringArrayListExtra(String name, ArrayList<String> value) {
        mIntent.putStringArrayListExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putCharSequenceArrayListExtra(String name, ArrayList<CharSequence> value) {
        mIntent.putCharSequenceArrayListExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtra(String name, Serializable value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtra(String name, boolean[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtra(String name, byte[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtra(String name, short[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtra(String name, char[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtra(String name, int[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtra(String name, long[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtra(String name, float[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtra(String name, double[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtra(String name, String[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtra(String name, CharSequence[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtra(String name, Bundle value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleActivityLauncher putExtras(Intent src) {
        mIntent.putExtras(src);
        return this;
    }

    public SimpleActivityLauncher putExtras(Bundle extras) {
        mIntent.putExtras(extras);
        return this;
    }

}
