package lx.af.utils.UIL.displayer.animator;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * author: lx
 * date: 16-4-29
 */
public class FloatInAnimator extends BaseAnimator {

    private int duration;

    public FloatInAnimator() {
        this(500);
    }

    public FloatInAnimator(int duration) {
        this.duration = duration;
    }

    @Override
    public void animate(View view) {
        AlphaAnimation fadeAnim = new AlphaAnimation(0f, 1f);
        TranslateAnimation tranAnim = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.1f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.1f,
                Animation.RELATIVE_TO_SELF, 0.0f
        );
        AnimationSet anim = new AnimationSet(true);
        anim.addAnimation(fadeAnim);
        anim.addAnimation(tranAnim);
        anim.setDuration(duration);
        anim.setInterpolator(new DecelerateInterpolator());
        view.startAnimation(anim);
    }
}
