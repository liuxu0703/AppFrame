package lx.af.widget.AutoSlidePager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Scroller;

/**
 * author: lx
 * date: 15-10-23
 */
class AutoSlideLine extends View {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private AutoSlidePager mPager;
    private int mAnimDuration = 600;

    private int mCurrentPage;
    private int mCurrentViewWidth;
    private int mFromViewWidth;
    private Scroller mScroller;

    public AutoSlideLine(Context context, AutoSlidePager pager) {
        super(context);
        mPager = pager;
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context, new AccelerateDecelerateInterpolator());
    }

    public void setLineColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    public void setAnimDuration(int duration) {
        mAnimDuration = duration;
    }

    public void setCurrentPage(int page, boolean anim) {
        if (mCurrentPage == page) {
            return;
        }
        mScroller.abortAnimation();

        int fromPage = mCurrentPage;
        mCurrentPage = page;
        View fromView = mPager.getPage(fromPage);
        View toView = mPager.getPage(mCurrentPage);
        mFromViewWidth = fromView.getWidth();
        mCurrentViewWidth = toView.getWidth();
        if (anim) {
            int fromX = fromView.getLeft() - mPager.getPaddingLeft();
            int toX = toView.getLeft() - mPager.getPaddingLeft();
            mScroller.startScroll(fromX, 0, toX - fromX, 0, mAnimDuration);
        }
        invalidate();
    }

    @Override
    public void computeScroll() {
        mScroller.computeScrollOffset();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int count = mPager.getPageCount();
        if (count == 0) {
            return;
        }

        if (mCurrentViewWidth == 0) {
            mCurrentViewWidth = mPager.getPage(mCurrentPage).getWidth();
        }

        final float rectWidth;
        if (!mScroller.isFinished()) {
            final int widthDelta = mCurrentViewWidth - mFromViewWidth;
            final float percent = (float) mScroller.timePassed() / mScroller.getDuration();
            rectWidth = mFromViewWidth + widthDelta * percent;
        } else {
            rectWidth = mCurrentViewWidth;
        }

        final float left = mScroller.getCurrX();
        final float right = left + rectWidth;
        canvas.drawRect(left, 0, right, getHeight(), mPaint);

        if (!mScroller.isFinished()) {
            invalidate();
        }
    }

}
