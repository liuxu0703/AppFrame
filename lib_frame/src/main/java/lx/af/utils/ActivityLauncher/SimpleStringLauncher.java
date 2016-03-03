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
public class SimpleStringLauncher extends ActivityLauncherBase<String> {

    private Intent mIntent;
    private String mIntentResultKey;
    private int mDefaultRequestCode = (new Random()).nextInt(5000);

    protected SimpleStringLauncher(Activity activity, Class<? extends Activity> target, String key) {
        super(activity);
        mIntent = new Intent(activity, target);
        mIntentResultKey = key;
    }

    protected SimpleStringLauncher(Fragment fragment, Class<? extends Activity> target, String key) {
        super(fragment);
        mIntent = new Intent(fragment.getActivity(), target);
        mIntentResultKey = key;
    }

    public static SimpleStringLauncher of(Activity activity, Class<? extends Activity> target, String key) {
        return new SimpleStringLauncher(activity, target, key);
    }

    public static SimpleStringLauncher of(Fragment fragment, Class<? extends Activity> target, String key) {
        return new SimpleStringLauncher(fragment, target, key);
    }

    public SimpleStringLauncher overridePendingTransition(int inAnim, int outAnim) {
        mInAnimResId = inAnim;
        mOutAnimResId = outAnim;
        return this;
    }

    public SimpleStringLauncher putExtra(String name, boolean value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtra(String name, byte value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtra(String name, char value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtra(String name, short value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtra(String name, int value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtra(String name, long value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtra(String name, float value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtra(String name, double value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtra(String name, String value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtra(String name, CharSequence value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtra(String name, Parcelable value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtra(String name, Parcelable[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putParcelableArrayListExtra(String name, ArrayList<? extends Parcelable> value) {
        mIntent.putParcelableArrayListExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putIntegerArrayListExtra(String name, ArrayList<Integer> value) {
        mIntent.putIntegerArrayListExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putStringArrayListExtra(String name, ArrayList<String> value) {
        mIntent.putStringArrayListExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putCharSequenceArrayListExtra(String name, ArrayList<CharSequence> value) {
        mIntent.putCharSequenceArrayListExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtra(String name, Serializable value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtra(String name, boolean[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtra(String name, byte[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtra(String name, short[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtra(String name, char[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtra(String name, int[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtra(String name, long[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtra(String name, float[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtra(String name, double[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtra(String name, String[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtra(String name, CharSequence[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtra(String name, Bundle value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleStringLauncher putExtras(Intent src) {
        mIntent.putExtras(src);
        return this;
    }

    public SimpleStringLauncher putExtras(Bundle extras) {
        mIntent.putExtras(extras);
        return this;
    }

    @Override
    protected String extractResult(int resultCode, Intent data) {
        return (data == null ? null : data.getStringExtra(mIntentResultKey));
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
