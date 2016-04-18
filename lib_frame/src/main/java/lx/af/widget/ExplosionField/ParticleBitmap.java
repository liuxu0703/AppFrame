package lx.af.widget.ExplosionField;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Random;

import lx.af.utils.ResourceUtils;

/**
 * author: lx
 * date: 16-4-6
 */
public class ParticleBitmap extends ParticleDrop {

    private int alpha;
    private Bitmap bitmap;

    public ParticleBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public ParticleBitmap(int resId) {
        this.bitmap = BitmapFactory.decodeResource(ResourceUtils.getRes(), resId);
    }

    @Override
    public void init(Rect bound, float endValue, Random random) {
        super.init(bound, endValue, random);
        alpha = 0;
    }

    public void advance(float factor) {
        float f;
        float normalization = factor / endValue;
        if (normalization < life || normalization > 1f - overflow) {
            alpha = 0;
            return;
        }
        normalization = (normalization - life) / (1f - life - overflow);
        float f2 = normalization * endValue;
        alpha = (int) (normalization * 2.6f * 255);
        if (alpha > 255) {
            alpha = 255;
        }
        f = bottom * f2;
        cx = baseCx + f * xFactor;
        cy = (float) (baseCy - this.neg * Math.pow(f, 2.0)) - f * mag;
    }

    @Override
    public void draw(float factor, Canvas canvas, Paint paint) {
        advance(factor);
        if (alpha > 0) {
            paint.setAlpha(alpha);
            canvas.drawBitmap(bitmap, cx, cy, paint);
        }
    }

}
