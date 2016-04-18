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

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

import java.util.Random;

/**
 * modified by liuxu
 * inspired by https://github.com/tyrantgit/ExplosionField
 */
public class ExplosionAnimator extends ValueAnimator {

    private static final Interpolator DEFAULT_INTERPOLATOR = new AccelerateInterpolator(0.6f);

    private Paint mPaint;
    private View mContainer;
    private Rect mBound;
    private Particle[] mParticles;

    ExplosionAnimator(View container, Rect bound, float endValue, Particle[] particles) {
        mContainer = container;
        mBound = new Rect(bound);
        mPaint = new Paint();
        mParticles = particles;
        Random random = new Random();
        for (Particle p : mParticles) {
            p.init(mBound, endValue, random);
        }
        setInterpolator(DEFAULT_INTERPOLATOR);
        setFloatValues(0f, endValue);
    }

    boolean draw(Canvas canvas) {
        if (!isStarted()) {
            return false;
        }
        for (Particle particle : mParticles) {
            particle.draw((float) getAnimatedValue(), canvas, mPaint);
        }
        mContainer.invalidate();
        return true;
    }

    @Override
    public void start() {
        super.start();
        mContainer.invalidate(mBound);
    }

}
