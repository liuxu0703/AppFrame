package lx.af.utils.ViewUtils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.text.TextPaint;
import android.widget.TextView;

/**
 * author: lx
 * date: 16-4-13
 */
public final class TextViewUtils {


    // ===============================================
    // for TextView

    public static void animateTextChangeByFade(final TextView tv, final String newText) {
        tv.animate().alpha(0.0f).setDuration(160).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                tv.setText(newText);
                tv.animate().alpha(1.0f).setDuration(160).setListener(null).start();
            }
        }).start();
    }

    public static void animateTextChangeByWidth(final TextView tv, final String newText) {
        String oldText = tv.getText() == null ? null : tv.getText().toString();
        TextPaint paint = tv.getPaint();
        final int delta = (int) (paint.measureText(newText) - paint.measureText(oldText));
        if (delta == 0) {
            // text size not changed, show fade animation
            animateTextChangeByFade(tv, newText);
            return;
        }

        int width = tv.getWidth();
        ObjectAnimator anim = ObjectAnimator.ofInt(tv, "width", width, width + delta).setDuration(300);
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
