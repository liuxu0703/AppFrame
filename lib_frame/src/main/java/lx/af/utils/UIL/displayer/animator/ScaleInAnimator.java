package lx.af.utils.UIL.displayer.animator;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

/**
 * author: lx
 * date: 16-4-28
 */
public class ScaleInAnimator extends BaseAnimator {

    private int duration;

    public ScaleInAnimator() {
        this(300);
    }

    public ScaleInAnimator(int duration) {
        this.duration = duration;
    }

    @Override
    public void animate(View view) {
        ScaleAnimation anim = new ScaleAnimation(
                0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(duration);
        anim.setInterpolator(new DecelerateInterpolator());
        view.startAnimation(anim);
    }

}
