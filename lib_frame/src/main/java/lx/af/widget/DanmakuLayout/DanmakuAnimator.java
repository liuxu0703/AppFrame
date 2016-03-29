package lx.af.widget.DanmakuLayout;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

/**
 * author: lx
 * date: 16-3-23
 */
class DanmakuAnimator {

    private static final long DEFAULT_DURATION = 4000;
    private static final Interpolator DEFAULT_INTERPOLATOR = new LinearInterpolator();

    private View view;
    private int type;
    private long duration;
    private Interpolator interpolator;

    private int width;
    private int height;
    private int parentWidth;
    private float speed;
    private long startTime;

    private Callback callback;

    DanmakuAnimator(View v, int type, long duration, Interpolator interpolator) {
        this.view = v;
        this.type = type;
        this.duration = duration <= 0 ? DEFAULT_DURATION : duration;
        this.view.setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.interpolator = interpolator != null ? interpolator : DEFAULT_INTERPOLATOR;
    }

    DanmakuAnimator reset(View v, int type, long duration, Interpolator interpolator) {
        this.view = v;
        this.type = type;
        this.duration = duration <= 0 ? DEFAULT_DURATION : duration;
        this.view.setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.interpolator = interpolator != null ? interpolator : DEFAULT_INTERPOLATOR;
        return this;
    }

    DanmakuAnimator init(int width, int height, int parentWidth) {
        this.width = width;
        this.height = height;
        this.parentWidth = parentWidth;
        this.startTime = 0;
        if (interpolator instanceof LinearInterpolator) {
            this.speed = (float) (parentWidth + width) / duration;
        } else {
            this.speed = -1;
        }
        return this;
    }

    View getView() {
        return view;
    }

    int getType() {
        return type;
    }

    long getDuration() {
        return duration;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isAvailable(DanmakuAnimator pendingDa) {
        if (pendingDa.speed == -1 || speed == -1) {
            // interpolator is not LinearInterpolator, mark as unavailable
            return false;
        }
        if (pendingDa.speed > speed) {
            return false;
        }
        long time = System.currentTimeMillis() - startTime;
        int distance = (int) (time * speed);
        return distance > Math.min(width * 2, parentWidth / 2 + width);
    }

    void startAnimation(int y, Callback c) {
        callback = c;
        Animation anim = new TranslateAnimation(
                TranslateAnimation.ABSOLUTE, parentWidth,
                TranslateAnimation.ABSOLUTE, 0 - width,
                TranslateAnimation.ABSOLUTE, y,
                TranslateAnimation.ABSOLUTE, y);
        anim.setFillAfter(true);
        anim.setDuration(duration);
        anim.setInterpolator(interpolator);
        anim.setAnimationListener(mListener);
        view.startAnimation(anim);
        view.setVisibility(View.VISIBLE);
        startTime = System.currentTimeMillis();
    }

    private Animation.AnimationListener mListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            callback.onAnimationStart(DanmakuAnimator.this);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            callback.onAnimationEnd(DanmakuAnimator.this);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

    @Override
    public String toString() {
        return "DanmakuAnimator{" +
                "type=" + type +
                ", duration=" + duration +
                ", width=" + width +
                ", height=" + height +
                ", speed=" + speed +
                '}';
    }


    // ============================================


    interface Callback {

        void onAnimationStart(DanmakuAnimator da);

        void onAnimationEnd(DanmakuAnimator da);

    }

}
