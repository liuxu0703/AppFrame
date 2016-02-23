package lx.af.utils.ActivityLauncher;

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
public class CodeScannerLauncher extends ActivityLauncherBase<String> {

    private String mScanMode;
    private String mCharacterSet;
    private String mTitle;
    private Class<?> mScannerClass;

    public static CodeScannerLauncher of(Activity activity) {
        return new CodeScannerLauncher(activity);
    }

    public static CodeScannerLauncher of(Fragment fragment) {
        return new CodeScannerLauncher(fragment);
    }

    protected CodeScannerLauncher(Activity activity) {
        super(activity);
    }

    protected CodeScannerLauncher(Fragment fragment) {
        super(fragment);
    }

    public CodeScannerLauncher scanner(Class<?> scannerClazz) {
        this.mScannerClass = scannerClazz;
        return this;
    }

    public CodeScannerLauncher title(String title) {
        mTitle = title;
        return this;
    }

    public CodeScannerLauncher characterSet(String characterSet) {
        mCharacterSet = characterSet;
        return this;
    }

    public CodeScannerLauncher modeQRCode() {
        return scanMode(Intents.Scan.QR_CODE_MODE);
    }

    public CodeScannerLauncher mode1D() {
        return scanMode(Intents.Scan.ONE_D_MODE);
    }

    public CodeScannerLauncher modeProduct() {
        return scanMode(Intents.Scan.PRODUCT_MODE);
    }

    public CodeScannerLauncher modeDataMatrix() {
        return scanMode(Intents.Scan.DATA_MATRIX_MODE);
    }

    public CodeScannerLauncher scanMode(String scanMode) {
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
        if (mScannerClass == null) {
            mScannerClass = CodeScannerActivity.class;
        }
        Intent intent = newIntent(mScannerClass);
        if (mScanMode != null) {
            intent.putExtra(Intents.Scan.MODE, mScanMode);
        }
        if (mCharacterSet != null) {
            intent.putExtra(Intents.Scan.CHARACTER_SET, mCharacterSet);
        }
        if (mTitle != null) {
            intent.putExtra(CodeScannerActivity.EXTRA_TITLE, mTitle);
        }
        return intent;
    }

}
