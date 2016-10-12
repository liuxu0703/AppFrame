package lx.af.utils.UIL;

import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * author: lx
 * date: 16-8-25
 */
public abstract class LoadFinishListener implements ImageLoadingListener {

    @Override
    public void onLoadingStarted(String imageUri, View view) {
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        onLoadingFinished(imageUri, view, null, false);
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        onLoadingFinished(imageUri, view, loadedImage, true);
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {
        onLoadingFinished(imageUri, view, null, false);
    }

    abstract void onLoadingFinished(String imageUri, View view, Bitmap loadedImage, boolean success);

}
