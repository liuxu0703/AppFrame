package lx.af.widget.Danmaku;

import android.graphics.Point;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

import java.util.Random;

import lx.af.manager.GlobalThreadManager;

/**
 * author: lx
 * date: 16-3-1
 */
class ViewWrapper implements Animation.AnimationListener {

    private static final int TIME_MIN = 4; // in seconds
    private static final int TIME_MAX = 6; // in seconds

    private static final Interpolator DEFAULT_INTERPOLATOR = new LinearInterpolator();

    View view;
    Object data;
    int width;
    int height;
    int top;

    private ViewPool pool;
    private TranslateAnimation anim;
    private Random random = new Random();
    private long startTime;
    private boolean start = false;

    public ViewWrapper(View view, ViewPool pool) {
        this.view = view;
        this.pool = pool;
    }

    void reset() {
        width = 0;
        height = 0;
        top = 0;
        start = false;
        final ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                width = view.getMeasuredWidth();
                height = view.getMeasuredHeight();
                if (width != 0 && height != 0) {
                    vto.removeOnPreDrawListener(this);
                    if (start) {
                        startAnim();
                    }
                }
                return false;
            }
        });
    }

    void startAnimation() {
        if (width != 0 && height != 0) {
            startAnim();
        } else {
            start = true;
        }
    }

    private void startAnim() {
        Point start = pool.getNextStartPoint(this);
        if (start == null) {
            GlobalThreadManager.runInUiThreadDelayed(new Runnable() {
                @Override
                public void run() {
                    startAnim();
                }
            }, 1500);
        } else {
            top = start.y;
            anim = new TranslateAnimation(
                    TranslateAnimation.ABSOLUTE, start.x,
                    TranslateAnimation.ABSOLUTE, 0 - width,
                    TranslateAnimation.ABSOLUTE, start.y,
                    TranslateAnimation.ABSOLUTE, start.y);
            anim.setAnimationListener(this);
            anim.setFillAfter(true);
            anim.setInterpolator(DEFAULT_INTERPOLATOR);
            int second = TIME_MIN + random.nextInt(TIME_MAX);
            view.setVisibility(View.VISIBLE);
            anim.setDuration(second * 1000);
            view.startAnimation(anim);
            startTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
        pool.addRunningView(this);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        pool.recycleView(this);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

}
