package lx.af.activity.CodeScanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.mining.app.zxing.camera.CameraManager;
import com.mining.app.zxing.decoding.Intents;
import com.mining.app.zxing.view.ViewfinderResultPointCallback;

import java.util.Vector;

import lx.af.R;
import lx.af.base.AbsBaseActivity;
import lx.af.base.AbsBaseActivity.LifeCycleAdapter;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 */
public final class CodeScannerHandler extends Handler implements
        SurfaceHolder.Callback {

    private static final String TAG = CodeScannerHandler.class.getSimpleName();

    static final int MSG_AUTO_FOCUS = 101;
    static final int MSG_RESTART_PREVIEW = 102;
    static final int MSG_DECODE_SUCCEED = 103;
    static final int MSG_DECODE_FAIL = 104;
    static final int MSG_RETURN_SCAN_RESULT = 105;
    static final int MSG_LAUNCH_PRODUCT_QUERY = 106;

    private final AbsBaseActivity mActivity;
    private final ICodeScanner mCodeCapture;
    private DecodeThread mDecodeThread;

    private Vector<BarcodeFormat> mDecodeFormats;
    private String mCharacterSet;
    private String mScanMode;

    private boolean mSurfaceCreated = false;
    private boolean mFinishOnPause = true;
    private State mState;

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    private LifeCycleAdapter mLifeCycleCallbacks = new LifeCycleAdapter() {
        @Override
        public void onActivityResumed(AbsBaseActivity activity) {
            SurfaceView surfaceView = mCodeCapture.getSurfaceView();
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            if (mSurfaceCreated) {
                initCamera(surfaceHolder);
            } else {
                surfaceHolder.addCallback(CodeScannerHandler.this);
            }
        }

        @Override
        public void onActivityPaused(AbsBaseActivity activity) {
            quitSynchronously();
            CameraManager.get().closeDriver();
            if (mFinishOnPause) {
                mActivity.finish();
            }
        }

        @Override
        public void onActivitySaveInstanceState(AbsBaseActivity activity, Bundle outState) {
            if (mCharacterSet != null) {
                outState.putString(Intents.Scan.CHARACTER_SET, mCharacterSet);
            }
            if (mScanMode != null) {
                outState.putString(Intents.Scan.MODE, mScanMode);
            }
        }
    };

    public static CodeScannerHandler start(Activity activity, Bundle savedInstanceState) {
        return new CodeScannerHandler(activity, savedInstanceState);
    }

    private CodeScannerHandler(Activity activity, Bundle savedInstanceState) {
        if (!(activity instanceof AbsBaseActivity) ||
                !(activity instanceof ICodeScanner)) {
            throw new IllegalArgumentException(
                    "must be sub class of AbsBaseActivity and ICodeScanner");
        }
        if (savedInstanceState != null) {
            mCharacterSet = savedInstanceState.getString(Intents.Scan.CHARACTER_SET);
            mScanMode = savedInstanceState.getString(Intents.Scan.MODE);
        } else {
            Intent intent = activity.getIntent();
            mCharacterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);
            mScanMode = intent.getStringExtra(Intents.Scan.MODE);
        }
        mDecodeFormats = DecodeFormatManager.parseDecodeFormats(mScanMode);

        CameraManager.init(activity.getApplication());
        mCodeCapture = (ICodeScanner) activity;
        mActivity = (AbsBaseActivity) activity;
        mActivity.addLifeCycleListener(mLifeCycleCallbacks);
        mState = State.SUCCESS;
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case MSG_AUTO_FOCUS: {
                // When one auto focus pass finishes, start another. This is the closest thing to
                // continuous AF. It does seem to hunt a bit, but I'm not sure what else to do.
                if (mState == State.PREVIEW) {
                    CameraManager.get().requestAutoFocus(this, MSG_AUTO_FOCUS);
                }
                break;
            }
            case MSG_RESTART_PREVIEW: {
                Log.i(TAG, "Got restart preview message");
                restartPreviewAndDecode();
                break;
            }
            case MSG_DECODE_SUCCEED: {
                Log.d(TAG, "Got decode succeeded message");
                mState = State.SUCCESS;
                Bundle bundle = message.getData();
                Bitmap barcode = bundle == null ? null :
                        (Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);
                Intent intent = mCodeCapture.handleResult((Result) message.obj, barcode);
                if (intent != null) {
                    sendMessage(obtainMessage(MSG_RETURN_SCAN_RESULT, intent));
                }
                break;
            }
            case MSG_DECODE_FAIL: {
                // We're decoding as fast as possible, so when one decode fails, start another.
                mState = State.PREVIEW;
                CameraManager.get().requestPreviewFrame(
                        mDecodeThread.getHandler(), DecodeHandler.MSG_DECODE);
                break;
            }
            case MSG_RETURN_SCAN_RESULT: {
                Log.i(TAG, "Got return scan result message");
                mActivity.setResult(Activity.RESULT_OK, (Intent) message.obj);
                mActivity.finish();
                break;
            }
            case MSG_LAUNCH_PRODUCT_QUERY: {
                Log.i(TAG, "Got product query message");
                String url = (String) message.obj;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                mActivity.startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!mSurfaceCreated) {
            mSurfaceCreated = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceCreated = false;
    }

    public void setFinishActivityOnPause(boolean finishOnPause) {
        mFinishOnPause = finishOnPause;
    }

    public void quitSynchronously() {
        mState = State.DONE;
        CameraManager.get().stopPreview();
        Message quit = Message.obtain(mDecodeThread.getHandler(), DecodeHandler.MSG_QUIT);
        quit.sendToTarget();
        try {
            mDecodeThread.join();
        } catch (InterruptedException e) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(MSG_DECODE_SUCCEED);
        removeMessages(MSG_DECODE_FAIL);
    }

    public void restartPreviewAndDecode() {
        if (mState == State.SUCCESS) {
            mState = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(
                    mDecodeThread.getHandler(), DecodeHandler.MSG_DECODE);
            CameraManager.get().requestAutoFocus(this, MSG_AUTO_FOCUS);
            mCodeCapture.getViewFinder().drawViewfinder();
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (Exception e) {
            Log.e("mipca", "init camera fail", e);
            mActivity.toastShort(R.string.mipca_toast_init_camera_fail);
            mActivity.finish();
            return;
        }

        if (mDecodeThread == null) {
            mDecodeThread = new DecodeThread(this, mDecodeFormats, mCharacterSet,
                    new ViewfinderResultPointCallback(mCodeCapture.getViewFinder()));
            mDecodeThread.start();
        }

        CameraManager.get().startPreview();
        restartPreviewAndDecode();
    }

}
