package lx.af.activity.CodeScanner;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.google.zxing.Result;
import com.mining.app.zxing.view.ViewfinderView;

import java.util.concurrent.TimeUnit;

import lx.af.R;
import lx.af.base.AbsBaseActivity;
import lx.af.manager.GlobalThreadManager;
import lx.af.utils.SoundEffectHelper;

/**
 * author: lx
 * date: 16-02-02
 */
public class CodeScannerActivity extends AbsBaseActivity implements ICodeScanner {

    public static final String EXTRA_TITLE = "activity_title";

    private static final long DISMISS_MAX_DELAY = TimeUnit.MINUTES.toMillis(5);

    private ViewfinderView mViewFinder;
    private SurfaceView mSurfaceView;
    private SoundEffectHelper mSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mipca_code_scanner_activity);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (!TextUtils.isEmpty(title)) {
            TextView tvTitle = (TextView) findViewById(R.id.code_capture_title);
            tvTitle.setText(title);
        }
        findViewById(R.id.code_capture_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
