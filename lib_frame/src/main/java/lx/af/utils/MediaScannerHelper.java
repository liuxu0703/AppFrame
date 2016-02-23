package lx.af.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.util.ArrayList;

/**
 * author: lx
 * date: 16-2-20
 */
public class MediaScannerHelper {

    /**
     * author: liuxu
     * callback when scan is done.
     */
    public interface ScannerListener {

        /**
         * author: liuxu
         * callback when scan is done.
         * when scanning a folder, this will be invoked when all
         * files and folders under the given folder is scanned.
         * Note: this method will be invoked in main thread (UI thread).
         * @param path
         *            the file or folder being scanned
         * @param success
         *            whether the scan operation is success. when scanning a
         *            folder, success will be set to true only when all files
         *            and folders under the given folder is successfully
         *            scanned.
         */
        void onScanCompleted(String path, boolean success);

    }


    // =========================================================


    private int mScanCount = 0;
    private boolean mSuccess = true;
    private Context mContext;
    private File mScanPath;
    private ArrayList<File> mScanList;
    private MediaScannerConnection mConnection;
    private ScannerListener mListener;

    private MediaScannerConnection.MediaScannerConnectionClient mClient =
            new MediaScannerConnection.MediaScannerConnectionClient() {

                @Override
                public void onMediaScannerConnected() {
                    doScan();
                }

                @Override
                public void onScanCompleted(String path, Uri uri) {
                    mSuccess = mSuccess && (uri != null);
                    mScanCount++;
                    if (mScanCount == mScanList.size()) {
                        // all files has been scanned.
                        // Note:
                        // MediaScannerConnectionClient.onScanCompleted()
                        // will be invoked in media thread by default. so
                        // here we callback to the UI thread.
                        callbackToUiThreadOnComplete();
                        mContext.unbindService(mConnection);
                    }
                }
            };

    public static MediaScannerHelper newInstance(Context context, String path, ScannerListener listener) {
        return new MediaScannerHelper(context, path, listener);
    }

    public MediaScannerHelper(Context context, String path, ScannerListener listener) {
        mContext = context;
        mScanPath = new File(path);
        mListener = listener;
    }

    public void scan() {
        if (!mScanPath.exists()) {
            if (mListener != null) {
                mListener.onScanCompleted(mScanPath.getAbsolutePath(), false);
            }
            return;
        }
        prepare();
        mConnection = new MediaScannerConnection(mContext, mClient);
        mConnection.connect();
    }

    private void prepare() {
        mScanCount = 0;
        mScanList = new ArrayList<>();
        prepareScanList(mScanPath);
    }

    // put all files and folders that need to be scanned into a list.
    // we do this before scan really happens so that the file count
    // is known beforehand, thus we can know whether all files has
    // been scanned in MediaScannerConnectionClient.onScanCompleted()
    private void prepareScanList(File path) {
        mScanList.add(path);
        if (path.isDirectory()) {
            File[] subPaths = path.listFiles();
            for (File p : subPaths) {
                prepareScanList(p);
            }
        }
    }

    private void doScan() {
        for (File f : mScanList) {
            mConnection.scanFile(f.getAbsolutePath(), null);
        }
    }

    private void callbackToUiThreadOnComplete() {
        if (mListener == null) {
            return;
        }
        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onScanCompleted(
                        mScanPath.getAbsolutePath(), mSuccess);
            }
        });
    }

}
