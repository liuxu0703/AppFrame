package lx.af.utils.UIL.displayer;

import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lx.af.utils.UIL.ImageInfo;
import lx.af.utils.UIL.ImageInfoCallback;
import lx.af.utils.UIL.displayer.animator.BaseAnimator;

/**
 * author: lx
 * date: 16-4-25
 */
public abstract class BaseDisplayer implements BitmapDisplayer {

    private static ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    private BaseAnimator mDisplayerAnimator;
    private int mBlurRadius = -1;

    private ImageInfoCallback mImageInfoCallback;

    public BaseDisplayer setDisplayerAnimator(BaseAnimator animator) {
        mDisplayerAnimator = animator;
        return this;
    }

    public BaseDisplayer setBlur(int radius) {
        mBlurRadius = radius;
        return this;
    }

    public BaseDisplayer setImageInfoCallback(ImageInfoCallback c) {
        mImageInfoCallback = c;
        return this;
    }

    @Override
    public void display(final Bitmap bitmap, final ImageAware imageAware, final LoadedFrom loadedFrom) {
        if (mImageInfoCallback != null) {
            ImageInfo info = new ImageInfo();
            info.width = bitmap.getWidth();
            info.height = bitmap.getHeight();
            info.size = bitmap.getByteCount();
            mImageInfoCallback.onGetImageInfo(info);
        }

        if (mBlurRadius > 0) {
            THREAD_POOL.execute(new Runnable() {
                @Override
                public void run() {
                    View view = imageAware.getWrappedView();
                    Blur.BlurFactor factor = new Blur.BlurFactor();
                    factor.width = bitmap.getWidth();
                    factor.height = bitmap.getHeight();
                    factor.radius = mBlurRadius;
                    factor.sampling = 2;
                    final Bitmap blur = Blur.of(view.getContext(), bitmap, factor);
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            if (blur != null) {
                                displayInner(blur, imageAware, loadedFrom);
                            } else {
                                displayInner(bitmap, imageAware, loadedFrom);
                            }
                        }
                    });
                }
            });
        } else {
            displayInner(bitmap, imageAware, loadedFrom);
        }
    }

    private void displayInner(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        display(bitmap, imageAware);
        if (mDisplayerAnimator != null) {
            mDisplayerAnimator.animate(imageAware.getWrappedView(), loadedFrom);
        }
    }

    public abstract void display(Bitmap bitmap, ImageAware imageAware);

}
