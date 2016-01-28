package lx.af.activity.CodeScanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.SurfaceView;

import com.google.zxing.Result;
import com.mining.app.zxing.decoding.Intents;
import com.mining.app.zxing.view.ViewfinderView;

import java.util.concurrent.TimeUnit;

import lx.af.R;
import lx.af.base.AbsBaseActivity;
import lx.af.manager.GlobalThreadManager;
import lx.af.utils.SoundEffectHelper;

/**
 * author: lx
 * date: 16-1-24
 */
public class CodeScannerActivity extends AbsBaseActivity implements ICodeScanner {

    private static final long DISMISS_MAX_DELAY = TimeUnit.MINUTES.toMillis(5);

    private ViewfinderView mViewFinder;
    private SurfaceView mSurfaceView;
    private SoundEffectHelper mSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mipca_code_scanner_activity);
        CodeScannerHandler.start(this, savedInstanceState);
        mSound = SoundEffectHelper.newInstance(this).addSoundFromRaw(R.raw.beep).init();
        startDelayFinish();
    }

    @Override
    protected void onDestroy() {
        stopDelayFinish();
        mSound.release();
        super.onDestroy();
    }

    @Override
    public ViewfinderView getViewFinder() {
        if (mViewFinder == null) {
            mViewFinder = obtainView(R.id.code_capture_view_finder);
        }
        return mViewFinder;
    }

    @Override
    public SurfaceView getSurfaceView() {
        if (mSurfaceView == null) {
            mSurfaceView = obtainView(R.id.code_capture_preview);
        }
        return mSurfaceView;
    }

    @Override
    public boolean handleResult(Result result, Bitmap barcode) {
        playBeepSoundAndVibrate();
        return false;
    }

    private void playBeepSoundAndVibrate() {
        mSound.play();
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200L);
    }

    private void startDelayFinish() {
        GlobalThreadManager.getUiThreadHandler().removeCallbacks(mFinishDelayRunnable);
        GlobalThreadManager.runInUiThreadDelayed(mFinishDelayRunnable, DISMISS_MAX_DELAY);
    }

    private void stopDelayFinish() {
        GlobalThreadManager.getUiThreadHandler().removeCallbacks(mFinishDelayRunnable);
    }

    private Runnable mFinishDelayRunnable  = new Runnable() {

        @Override
        public void run() {
            toastShort(R.string.mipca_toast_finish_activity);
            finish();
        }
    };

}
