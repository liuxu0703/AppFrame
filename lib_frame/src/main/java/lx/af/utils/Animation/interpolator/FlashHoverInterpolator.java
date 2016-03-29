package lx.af.utils.Animation.interpolator;

import android.view.animation.Interpolator;

/**
 * author: lx
 * date: 16-3-29
 *
 * move-in --> hover -> move-out
 */
public class FlashHoverInterpolator implements Interpolator {

    private float hover;
    private float acceleratePart;
    private float accelerateFactor;

    /**
     * @param hover determines hover time, should be (0.0f, 1.0f)
     */
    public FlashHoverInterpolator(float hover) {
        this.hover = hover;
        this.acceleratePart = (1.0f - hover) / 2;
        this.accelerateFactor = 1.0f - hover;
    }

    @Override
    public float getInterpolation(float input) {
        if (input <= acceleratePart) {
            float x = input / accelerateFactor;
            return (float) (Math.sin(x * Math.PI)) / 2f;
        } else if (input >= 1 - acceleratePart) {
            float x = (input - hover) / accelerateFactor;
            return (float) (1f - (Math.sin(x * Math.PI) / 2f ));
        } else {
            return 0.5f;
        }
    }
}
