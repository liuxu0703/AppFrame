package lx.af.utils.ViewUtils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.widget.TextView;

/**
 * author: lx
 * date: 16-4-13
 */
public final class ViewUtils {


    // ===============================================
    // for TextView

    public static void animateTextChangeByFade(final TextView tv, final String newText) {
        tv.animate().alpha(0.0f).setDuration(250).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                tv.setText(newText);
                tv.animate().alpha(1.0f).setDuration(250).setListener(null).start();
            }
        }).start();
    }

    public static void animateTextChangeByWidth(final TextView tv, final String newText) {
        String oldText = tv.getText() == null ? null : tv.getText().toString();
        int width = tv.getWidth();
        final float delta =
                tv.getPaint().measureText(newText) -
                        tv.getPaint().measureText(oldText);
        PropertyValuesHolder widthHolder = PropertyValuesHolder
                .ofInt("width", width, width + (int) delta);
        ObjectAnimator anim = ObjectAnimator
                .ofPropertyValuesHolder(tv, widthHolder).setDuration(250);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (delta < 0) {
                    tv.setText(newText);
                }
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                if (delta >= 0) {
                    tv.setText(newText);
                }
            }
        });
        anim.start();
    }

    // ===============================================

}
