package lx.af.utils.ActivityLauncher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * author: lx
 * date: 16-2-23
 *
 */
public class SimpleParcelableLauncher extends ActivityLauncherBase<Parcelable> {

    private Intent mIntent;
    private String mIntentResultKey;
    private int mDefaultRequestCode = (new Random()).nextInt(5000);

    public SimpleParcelableLauncher(Activity activity, Class<? extends Activity> target, String key) {
        super(activity);
        mIntent = new Intent(activity, target);
        mIntentResultKey = key;
    }

    public SimpleParcelableLauncher(Fragment fragment, Class<? extends Activity> target, String key) {
        super(fragment);
        mIntent = new Intent(fragment.getActivity(), target);
        mIntentResultKey = key;
    }

    public static SimpleParcelableLauncher of(Activity activity, Class<? extends Activity> target, String key) {
        return new SimpleParcelableLauncher(activity, target, key);
    }

    public static SimpleParcelableLauncher of(Fragment fragment, Class<? extends Activity> target, String key) {
        return new SimpleParcelableLauncher(fragment, target, key);
    }

    public SimpleParcelableLauncher overridePendingTransition(int inAnim, int outAnim) {
        mInAnimResId = inAnim;
        mOutAnimResId = outAnim;
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, boolean value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, byte value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, char value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, short value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, int value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, long value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, float value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, double value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, String value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, CharSequence value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, Parcelable value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, Parcelable[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putParcelableArrayListExtra(String name, ArrayList<? extends Parcelable> value) {
        mIntent.putParcelableArrayListExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putIntegerArrayListExtra(String name, ArrayList<Integer> value) {
        mIntent.putIntegerArrayListExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putStringArrayListExtra(String name, ArrayList<String> value) {
        mIntent.putStringArrayListExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putCharSequenceArrayListExtra(String name, ArrayList<CharSequence> value) {
        mIntent.putCharSequenceArrayListExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, Serializable value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, boolean[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, byte[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, short[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, char[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, int[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, long[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, float[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, double[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, String[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, CharSequence[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtra(String name, Bundle value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleParcelableLauncher putExtras(Intent src) {
        mIntent.putExtras(src);
        return this;
    }

    public SimpleParcelableLauncher putExtras(Bundle extras) {
        mIntent.putExtras(extras);
        return this;
    }

    @Override
    protected Parcelable extractResult(int resultCode, Intent data) {
        return (data == null ? null : data.getParcelableExtra(mIntentResultKey));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    protected int getDefaultRequestCode() {
        return mDefaultRequestCode;
    }

    @Override
    public Intent createIntent() {
        return mIntent;
    }

}
