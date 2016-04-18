package lx.af.widget.ExplosionField;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Random;

import lx.af.utils.ScreenUtils;

/**
 * author: lx
 * date: 16-4-6
 */
public class ParticleDot extends ParticleDrop {

    private static final float X = ScreenUtils.dip2px(5);
    private static final float V = ScreenUtils.dip2px(2);
    private static final float W = ScreenUtils.dip2px(1);

    private static final int[] COLOR_ARR = new int[] {
            Color.parseColor("#02a8f3"),
            Color.parseColor("#9acd34"),
            Color.parseColor("#e81d62"),
            Color.parseColor("#feea3a"),
            Color.parseColor("#fe9700"),
    };

    private float alpha;
    private int color;
    private float radius;
    private float baseRadius;

    public ParticleDot() {
    }

    public ParticleDot(int color) {
        this.color = color;
    }

    @Override
    public void init(Rect bound, float endValue, Random random) {
        super.init(bound, endValue, random);
        if (color == 0) {
            this.color = COLOR_ARR[random.nextInt(COLOR_ARR.length)];
        }
        if (random.nextFloat() < 0.2f) {
            baseRadius = V + ((X - V) * random.nextFloat());
        } else {
            baseRadius = W + ((V - W) * random.nextFloat());
        }
        radius = V;
        alpha = 1f;
    }

    public void advance(float factor) {
        float f = 0f;
        float normalization = factor / endValue;
        if (normalization < life || normalization > 1f - overflow) {
            alpha = 0f;
            return;
        }
        normalization = (normalization - life) / (1f - life - overflow);
        float f2 = normalization * endValue;
        if (normalization >= 0.7f) {
            f = (normalization - 0.7f) / 0.3f;
        }
        alpha = 1f - f;
        f = bottom * f2;
        cx = baseCx + f * xFactor;
        cy = (float) (baseCy - this.neg * Math.pow(f, 2.0)) - f * mag;
        radius = V + (baseRadius - V) * f2;
    }

    @Override
    public void draw(float factor, Canvas canvas, Paint paint) {
        advance(factor);
        if (alpha > 0f && radius > 0f) {
            paint.setColor(color);
            paint.setAlpha((int) (Color.alpha(color) * alpha));
            canvas.drawCircle(cx, cy, radius, paint);
        }
    }

}
