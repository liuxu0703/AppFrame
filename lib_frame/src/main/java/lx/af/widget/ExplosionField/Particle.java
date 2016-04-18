package lx.af.widget.ExplosionField;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Random;

/**
 * author: lx
 * date: 16-4-6
 */
public interface Particle {

    void init(Rect bound, float endValue, Random random);

    void draw(float factor, Canvas canvas, Paint paint);

}
