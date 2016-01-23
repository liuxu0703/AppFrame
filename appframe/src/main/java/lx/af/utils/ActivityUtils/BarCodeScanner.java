package lx.af.utils.ActivityUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.mining.app.zxing.decoding.Intents;

import lx.af.activity.CodeScanner.MipcaActivity;

/**
 * author: lx
 * date: 16-1-22
 */
public class BarCodeScanner extends ActivityLauncherBase<String> {

    private String mScanMode;
    private String mCharacterSet;

    public static BarCodeScanner of(Activity activity) {
        return new BarCodeScanner(activity);
    }

    public static BarCodeScanner of(Fragment fragment) {
        return new BarCodeScanner(fragment);
    }

    protected BarCodeScanner(Activity activity) {
        super(activity);
    }

    protected BarCodeScanner(Fragment fragment) {
        super(fragment);
    }

    public BarCodeScanner characterSet(String characterSet) {
        mCharacterSet = characterSet;
        return this;
    }

    public BarCodeScanner modeQRCode() {
        return scanMode(Intents.Scan.QR_CODE_MODE);
    }

    public BarCodeScanner mode1D() {
        return scanMode(Intents.Scan.ONE_D_MODE);
    }

    public BarCodeScanner modeProduct() {
        return scanMode(Intents.Scan.PRODUCT_MODE);
    }

    public BarCodeScanner modeDataMatrix() {
        return scanMode(Intents.Scan.DATA_MATRIX_MODE);
    }

    public BarCodeScanner scanMode(String scanMode) {
        mScanMode = scanMode;
        return this;
    }

    @Override
    protected String extractResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            return data == null ? null : data.getStringExtra(Intents.Scan.RESULT);
        } else {
            return null;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mScanMode = savedInstanceState.getString(Intents.Scan.MODE);
        mCharacterSet = savedInstanceState.getString(Intents.Scan.CHARACTER_SET);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Intents.Scan.MODE, mScanMode);
        outState.putString(Intents.Scan.CHARACTER_SET, mCharacterSet);
    }

    @Override
    protected int getDefaultRequestCode() {
        return RequestCode.BAR_CODE_SCAN;
    }

    @Override
    public Intent createIntent() {
        Intent intent = newIntent(MipcaActivity.class);
        if (mScanMode != null) {
            intent.putExtra(Intents.Scan.MODE, mScanMode);
        }
        if (mCharacterSet != null) {
            intent.putExtra(Intents.Scan.CHARACTER_SET, mCharacterSet);
        }
        return intent;
    }

}
