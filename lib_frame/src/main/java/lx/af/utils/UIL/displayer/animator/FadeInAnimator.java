package lx.af.utils.UIL.displayer.animator;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;

/**
 * author: lx
 * date: 16-4-28
 */
public class FadeInAnimator extends BaseAnimator {

    private int duration;

    public FadeInAnimator() {
        this(500);
    }

    public FadeInAnimator(int duration) {
        this.duration = duration;
    }

    @Override
    public void animate(View view) {
        AlphaAnimation fadeImage = new AlphaAnimation(0f, 1f);
        fadeImage.setDuration(duration);
        fadeImage.setInterpolator(new DecelerateInterpolator());
        view.startAnimation(fadeImage);
    }

}
