package lx.af.utils.UIL.displayer;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

/**
 * author: lx
 * date: 16-4-25
 */
public class AnimateDisplayer implements BitmapDisplayer {

    protected Animator animator;
    protected boolean animateFromNetwork = true;
    protected boolean animateFromDisk = true;
    protected boolean animateFromMemory = false;

    public AnimateDisplayer() {
    }

    public AnimateDisplayer(Animator animator) {
        this.animator = animator;
    }

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        imageAware.setImageBitmap(bitmap);
        if ((animateFromNetwork && loadedFrom == LoadedFrom.NETWORK) ||
                (animateFromDisk && loadedFrom == LoadedFrom.DISC_CACHE) ||
                (animateFromMemory && loadedFrom == LoadedFrom.MEMORY_CACHE)) {
            animate(imageAware.getWrappedView());
        }
    }

    protected AnimateDisplayer animator(Animator animator) {
        this.animator = animator;
        return this;
    }

    protected AnimateDisplayer animateFromNetwork(boolean animate) {
        animateFromNetwork = animate;
        return this;
    }

    protected AnimateDisplayer animateFromDisk(boolean animate) {
        animateFromDisk = animate;
        return this;
    }

    protected AnimateDisplayer animateFromMemory(boolean animate) {
        animateFromMemory = animate;
        return this;
    }

    protected void animate(View imageView) {
        if (imageView != null) {
            if (animator != null) {
                animator.setTarget(imageView);
                animator.start();
            } else {
                imageView.setAlpha(0f);
                imageView.animate().alpha(1f).setDuration(600).start();
            }
        }
    }

}
