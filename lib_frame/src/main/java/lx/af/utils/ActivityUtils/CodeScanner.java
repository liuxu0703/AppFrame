package lx.af.utils.ActivityUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.mining.app.zxing.decoding.Intents;

import lx.af.activity.CodeScanner.CodeScannerActivity;

/**
 * author: lx
 * date: 16-1-22
 */
public class CodeScanner extends ActivityLauncherBase<String> {

    private String mScanMode;
    private String mCharacterSet;
    private Class<?> mScannerClazz;

    public static CodeScanner of(Activity activity) {
        return new CodeScanner(activity);
    }

    public static CodeScanner of(Fragment fragment) {
        return new CodeScanner(fragment);
    }

    protected CodeScanner(Activity activity) {
        super(activity);
    }

    protected CodeScanner(Fragment fragment) {
        super(fragment);
    }

    public CodeScanner scanner(Class<?> scannerClazz) {
        this.mScannerClazz = scannerClazz;
        return this;
    }

    public CodeScanner characterSet(String characterSet) {
        mCharacterSet = characterSet;
        return this;
    }

    public CodeScanner modeQRCode() {
        return scanMode(Intents.Scan.QR_CODE_MODE);
    }

    public CodeScanner mode1D() {
        return scanMode(Intents.Scan.ONE_D_MODE);
    }

    public CodeScanner modeProduct() {
        return scanMode(Intents.Scan.PRODUCT_MODE);
    }

    public CodeScanner modeDataMatrix() {
        return scanMode(Intents.Scan.DATA_MATRIX_MODE);
    }

    public CodeScanner scanMode(String scanMode) {
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
        if (mScannerClazz == null) {
            mScannerClazz = CodeScannerActivity.class;
        }
        Intent intent = newIntent(mScannerClazz);
        if (mScanMode != null) {
            intent.putExtra(Intents.Scan.MODE, mScanMode);
        }
        if (mCharacterSet != null) {
            intent.putExtra(Intents.Scan.CHARACTER_SET, mCharacterSet);
        }
        return intent;
    }

}
