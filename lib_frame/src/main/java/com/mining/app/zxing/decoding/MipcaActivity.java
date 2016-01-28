package com.mining.app.zxing.decoding;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.mining.app.zxing.camera.CameraManager;
import com.mining.app.zxing.decoding.CaptureActivityHandler;
import com.mining.app.zxing.decoding.InactivityTimer;
import com.mining.app.zxing.decoding.Intents;
import com.mining.app.zxing.view.ViewfinderView;

import java.util.Vector;

import lx.af.R;
import lx.af.base.AbsBaseActivity;
import lx.af.utils.SoundEffectHelper;

/**
 * code scanner activity
 */
public class MipcaActivity extends AbsBaseActivity implements
        SurfaceHolder.Callback {

    private ViewfinderView viewfinderView;

    private CaptureActivityHandler handler;
    private InactivityTimer inactivityTimer;
    private SoundEffectHelper sound;
    private boolean hasSurface;

    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private String scanMode;

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
        if (state != null) {
            characterSet = state.getString(Intents.Scan.CHARACTER_SET);
            scanMode = state.getString(Intents.Scan.MODE);
        }

		setContentView(R.layout.mipca_activity);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
        sound = SoundEffectHelper.newInstance(this).addSoundFromRaw(R.raw.beep).init();
	}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Intents.Scan.CHARACTER_SET, characterSet);
        outState.putString(Intents.Scan.MODE, scanMode);
    }

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
		}
        characterSet = getIntent().getStringExtra(Intents.Scan.CHARACTER_SET);
        scanMode = getIntent().getStringExtra(Intents.Scan.MODE);
        decodeFormats = DecodeFormatManager.parseDecodeFormats(getIntent());
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        Intent intent = new Intent();
        intent.putExtra(Intents.Scan.RESULT, result.getText());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (Exception e) {
            Log.e("mipca", "init camera fail", e);
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

	private void playBeepSoundAndVibrate() {
		sound.play();
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200L);
	}

}