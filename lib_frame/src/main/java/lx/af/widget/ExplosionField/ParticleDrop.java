package lx.af.widget.ExplosionField;

import android.graphics.Rect;

import java.util.Random;

import lx.af.utils.ScreenUtils;

/**
 * author: lx
 * date: 16-4-7
 */
public abstract class ParticleDrop implements Particle {

    public static final int DIRECTION_RIGHT = 1;
    public static final int DIRECTION_RANDOM = 0;
    public static final int DIRECTION_LEFT = -1;

    protected static final float Y = ScreenUtils.dip2px(20);

    protected float cx;
    protected float cy;
    protected float baseCx;
    protected float baseCy;
    protected float top;
    protected float bottom;
    protected float mag;
    protected float neg;
    protected float life;
    protected float overflow;
    protected float endValue;

    protected float xFactor = 1.0f;
    protected int direction = DIRECTION_RANDOM;


    public ParticleDrop setXFactor(float xFactor) {
        this.xFactor = xFactor;
        return this;
    }

    public ParticleDrop setDirection(int direction) {
        if (direction > 0) {
            this.direction = DIRECTION_RIGHT;
        } else if (direction < 0) {
            this.direction = DIRECTION_LEFT;
        } else {
            this.direction = DIRECTION_RANDOM;
        }
        return this;
    }

    @Override
    public void init(Rect bound, float endValue, Random random) {
        this.endValue = endValue;
        float nextFloat = random.nextFloat();
        top = bound.height() * ((0.18f * random.nextFloat()) + 0.2f);
        top = nextFloat < 0.2f ? top : top + ((top * 0.2f) * random.nextFloat());
        bottom = (bound.height() * (random.nextFloat() - 0.5f)) * 1.8f;
        float f = nextFloat < 0.2f ? bottom : nextFloat < 0.8f ? bottom * 0.6f : bottom * 0.3f;
        if (direction == DIRECTION_LEFT) {
            bottom = - Math.abs(f);
        } else if (direction == DIRECTION_RIGHT) {
            bottom = Math.abs(f);
        } else {
            bottom = f;
        }
        mag = 4.0f * top / bottom;
        neg = (-mag) / bottom;
        f = bound.centerX() + (Y * (random.nextFloat() - 0.5f));
        baseCx = f;
        cx = f;
        f = bound.centerY() + (Y * (random.nextFloat() - 0.5f));
        baseCy = f;
        cy = f;
        life = endValue / 10 * random.nextFloat();
        overflow = 0.4f * random.nextFloat();
    }

}
