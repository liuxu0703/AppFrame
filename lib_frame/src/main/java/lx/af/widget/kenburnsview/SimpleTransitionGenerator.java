package lx.af.widget.kenburnsview;

import android.graphics.RectF;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * author: lx
 * date: 15-12-10
 */
public class SimpleTransitionGenerator implements TransitionGenerator {

    /** Default value for the transition duration in milliseconds. */
    public static final int DEFAULT_TRANSITION_DURATION = 10000;

    /** The duration, in milliseconds, of each transition. */
    private long mTransitionDuration;

    /** The {@link Interpolator} to be used to create transitions. */
    private Interpolator mTransitionInterpolator;

    /** The last generated transition. */
    private Transition mLastGenTrans;

    /** The bounds of the drawable when the last transition was generated. */
    private RectF mLastDrawableBounds = new RectF();

    private RectF mHeadRect = new RectF();
    private RectF mTailRest = new RectF();

    public SimpleTransitionGenerator() {
        this(DEFAULT_TRANSITION_DURATION, new AccelerateDecelerateInterpolator());
    }

    public SimpleTransitionGenerator(long transitionDuration, Interpolator transitionInterpolator) {
        setTransitionDuration(transitionDuration);
        setTransitionInterpolator(transitionInterpolator);
    }

    @Override
    public Transition generateNextTransition(RectF drawableBounds, RectF viewport) {
        boolean drawableBoundsChanged = true;
        boolean viewportRatioChanged = true;
        RectF oldDstRect = null;
        if (mLastGenTrans != null) {
            oldDstRect = mLastGenTrans.getDestinyRect();
            drawableBoundsChanged = !drawableBounds.equals(mLastDrawableBounds);
            viewportRatioChanged = !MathUtils.haveSameAspectRatio(oldDstRect, viewport);
        }

        if (drawableBoundsChanged || viewportRatioChanged) {
            oldDstRect = null;

            float dw = drawableBounds.width();
            float dh = drawableBounds.height();
            float dr = dh == 0 ? 0 : dw / dh;
            float vw = viewport.width();
            float vh = viewport.height();
            float vr = vh == 0 ? 1 : vw / vh;

            if (dr < vr) {
                // vertical
                float delta = dw / 20;
                float w = dw - 2 * delta;
                float h = w / vr;
                mHeadRect.set(delta, 0, dw - delta, h);
                mTailRest.set(mHeadRect);
                mTailRest.offset(0f, dh - h);
            } else {
                // horizontal
                float delta = dh / 10;
                float h = dh - 2 * delta;
                float w = h * vr;
                mHeadRect.set(0, delta, w, dh - delta);
                mTailRest.set(mHeadRect);
                mTailRest.offset(dw - w, 0f);
            }
        }

        if (oldDstRect == null || mHeadRect.equals(oldDstRect)) {
            mLastGenTrans = new Transition(mHeadRect, mTailRest,
                    mTransitionDuration, mTransitionInterpolator);
        } else {
            mLastGenTrans = new Transition(mTailRest, mHeadRect,
                    mTransitionDuration, mTransitionInterpolator);
        }
        mLastDrawableBounds.set(drawableBounds);

        return mLastGenTrans;
    }

    /**
     * Sets the duration, in milliseconds, for each transition generated.
     * @param transitionDuration the transition duration.
     */
    public void setTransitionDuration(long transitionDuration) {
        mTransitionDuration = transitionDuration;
    }


    /**
     * Sets the {@link Interpolator} for each transition generated.
     * @param interpolator the transition interpolator.
     */
    public void setTransitionInterpolator(Interpolator interpolator) {
        mTransitionInterpolator = interpolator;
    }

}
