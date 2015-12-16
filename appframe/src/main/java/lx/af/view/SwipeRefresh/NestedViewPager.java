package lx.af.view.SwipeRefresh;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * author: lx
 * date: 15-12-9
 */
public class NestedViewPager extends ViewPager {

    private static final int THRESHOLD_X = 10;
    private static final int THRESHOLD_Y = 20;

    private float mDownX;
    private float mDownY;
    private float mMoveX;
    private float mMoveY;
    private boolean mHandleScrollSelf = false;

    public NestedViewPager(Context context) {
        super(context);
    }

    public NestedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mDownX = ev.getRawX();
            mDownY = ev.getRawY();
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            mMoveX = ev.getRawX();
            mMoveY = ev.getRawY();

            if (mHandleScrollSelf) {
                //Log.d("liuxu", "111 dispatchTouchEvent, handle event by self");
                getParent().requestDisallowInterceptTouchEvent(true);
                return super.dispatchTouchEvent(ev);
            }

            //Log.d("liuxu", "111 dispatchTouchEvent" +
            //       ", deltaX="+Math.abs(mMoveY - mDownY) +
            //        ", deltaY="+Math.abs(mMoveX - mDownX));

            if (Math.abs(mMoveY - mDownY) < THRESHOLD_Y && Math.abs(mMoveX - mDownX) > THRESHOLD_X) {
                mHandleScrollSelf = true;
            } else {
                return false;
            }

        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            mHandleScrollSelf = false;
        }

        return super.dispatchTouchEvent(ev);
    }

}
