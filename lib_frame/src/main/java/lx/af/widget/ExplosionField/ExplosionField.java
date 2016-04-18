/*
 * Copyright (C) 2015 tyrantgit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package lx.af.widget.ExplosionField;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

/**
 * modified by liuxu
 * inspired by https://github.com/tyrantgit/ExplosionField
 */
public class ExplosionField extends View {

    private List<ExplosionAnimator> mExplosions = new ArrayList<>();

    private int mDuration;
    private int mStartDelay;
    private float mEndValue;
    private Rect mBound;
    private Particle[] mParticles;

    public ExplosionField(Context context) {
        super(context);
        init();
    }

    public ExplosionField(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExplosionField(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }

    private boolean check() {
        if (mDuration == 0) {
            mDuration = 2000;
        }
        if (mStartDelay < 0) {
            mStartDelay = 0;
        }
        if (mEndValue == 0) {
            mEndValue = 2.0f;
        }
        if (mBound == null) {
            return false;
        }
        if (mParticles == null || mParticles.length == 0) {
            return false;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (ExplosionAnimator explosion : mExplosions) {
            explosion.draw(canvas);
        }
    }

    public ExplosionField setBound(Rect bound) {
        mBound = bound;
        return this;
    }

    public ExplosionField setBound(View view) {
        return setBound(view, 0);
    }

    public ExplosionField setBound(View view, int expand) {
        return setBound(view, expand, expand);
    }

    public ExplosionField setBound(View view, int expandX, int expandY) {
        mBound = new Rect();
        view.getGlobalVisibleRect(mBound);
        int[] location = new int[2];
        getLocationOnScreen(location);
        mBound.offset(-location[0], -location[1]);
        if (expandX != 0 || expandY != 0) {
            mBound.inset(-expandX, -expandY);
        }
        return this;
    }

    public ExplosionField setDuration(int duration) {
        mDuration = duration;
        return this;
    }

    public ExplosionField setStartDelay(int delay) {
        mStartDelay = delay;
        return this;
    }

    public ExplosionField setEndValue(float endValue) {
        mEndValue = endValue;
        return this;
    }

    public ExplosionField setParticles(Particle[] particles) {
        mParticles = particles;
        return this;
    }

    public void explode() {
        if (!check()) {
            return;
        }
        final ExplosionAnimator explosion = new ExplosionAnimator(
                this, mBound, mEndValue, mParticles);
        explosion.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mExplosions.remove(animation);
            }
        });
        mExplosions.add(explosion);
        explosion.setDuration(mDuration);
        explosion.setStartDelay(mStartDelay);
        explosion.start();
    }

    public void clear() {
        mExplosions.clear();
        invalidate();
    }

    public static ExplosionField attach2Window(Activity activity) {
        ViewGroup rootView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        ExplosionField explosionField = new ExplosionField(activity);
        rootView.addView(explosionField, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return explosionField;
    }

}
