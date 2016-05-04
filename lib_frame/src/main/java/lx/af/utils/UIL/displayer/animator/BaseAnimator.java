package lx.af.utils.UIL.displayer.animator;

import android.view.View;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;

/**
 * author: lx
 * date: 16-4-28
 */
public abstract class BaseAnimator {

    private boolean animateFromNetwork = true;
    private boolean animateFromDisk = true;
    private boolean animateFromMemory = false;

    public BaseAnimator setAnimateFromNetwork(boolean animate) {
        animateFromNetwork = animate;
        return this;
    }

    public BaseAnimator setAnimateFromDisk(boolean animate) {
        animateFromDisk = animate;
        return this;
    }

    public BaseAnimator setAnimateFromMemory(boolean animate) {
        animateFromMemory = animate;
        return this;
    }

    public void animate(View view, LoadedFrom loadedFrom) {
        if ((animateFromNetwork && loadedFrom == LoadedFrom.NETWORK) ||
                (animateFromDisk && loadedFrom == LoadedFrom.DISC_CACHE) ||
                (animateFromMemory && loadedFrom == LoadedFrom.MEMORY_CACHE)) {
            animate(view);
        }
    }

    public abstract void animate(View view);

}
