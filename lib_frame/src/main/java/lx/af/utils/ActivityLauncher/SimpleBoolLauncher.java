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
public class SimpleBoolLauncher extends ActivityLauncherBase<Boolean> {

    private Intent mIntent;
    private int mDefaultRequestCode = (new Random()).nextInt(5000);

    protected SimpleBoolLauncher(Activity activity, Class<? extends Activity> target) {
        super(activity);
        mIntent = new Intent(activity, target);
    }

    protected SimpleBoolLauncher(Fragment fragment, Class<? extends Activity> target) {
        super(fragment);
        mIntent = new Intent(fragment.getActivity(), target);
    }

    public static SimpleBoolLauncher of(Activity activity, Class<? extends Activity> target) {
        return new SimpleBoolLauncher(activity, target);
    }

    public static SimpleBoolLauncher of(Fragment fragment, Class<? extends Activity> target) {
        return new SimpleBoolLauncher(fragment, target);
    }

    public SimpleBoolLauncher overridePendingTransition(int inAnim, int outAnim) {
        mInAnimResId = inAnim;
        mOutAnimResId = outAnim;
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, boolean value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, byte value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, char value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, short value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, int value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, long value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, float value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, double value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, String value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, CharSequence value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, Parcelable value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, Parcelable[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putParcelableArrayListExtra(String name, ArrayList<? extends Parcelable> value) {
        mIntent.putParcelableArrayListExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putIntegerArrayListExtra(String name, ArrayList<Integer> value) {
        mIntent.putIntegerArrayListExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putStringArrayListExtra(String name, ArrayList<String> value) {
        mIntent.putStringArrayListExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putCharSequenceArrayListExtra(String name, ArrayList<CharSequence> value) {
        mIntent.putCharSequenceArrayListExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, Serializable value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, boolean[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, byte[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, short[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, char[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, int[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, long[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, float[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, double[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, String[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, CharSequence[] value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtra(String name, Bundle value) {
        mIntent.putExtra(name, value);
        return this;
    }

    public SimpleBoolLauncher putExtras(Intent src) {
        mIntent.putExtras(src);
        return this;
    }

    public SimpleBoolLauncher putExtras(Bundle extras) {
        mIntent.putExtras(extras);
        return this;
    }

    @Override
    protected Boolean extractResult(int resultCode, Intent data) {
        return resultCode == Activity.RESULT_OK;
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
