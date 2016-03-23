package lx.af.utils.ViewUtils;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.concurrent.TimeUnit;

/**
 * author: lx
 * date: 15-12-15
 *
 * helper to make ViewPager auto scroll
 */
public class ViewPagerAutoFlipper implements
        ViewPager.OnPageChangeListener {

    private static final long DEFAULT_FLIP_INTERVAL = TimeUnit.SECONDS.toMillis(3);

    private ViewPager mViewPager;
    private boolean mAutoFlip = false;
    private long mFlipInterval = DEFAULT_FLIP_INTERVAL;
    private long mLastFlipTime;
    private int mScrollState = ViewPager.SCROLL_STATE_IDLE;
    private int mPosition;

    public static ViewPagerAutoFlipper newInstance(ViewPager pager) {
        return new ViewPagerAutoFlipper(pager);
    }

    public ViewPagerAutoFlipper(ViewPager pager) {
        mViewPager = pager;
        mViewPager.addOnPageChangeListener(this);
        mPosition = mViewPager.getCurrentItem();
    }

    /**
     * @param interval auto flip interval, in millis
     */
    public ViewPagerAutoFlipper setInterval(long interval) {
        if (interval > 0) {
            mFlipInterval = interval;
        } else {
            throw new IllegalArgumentException("interval should be positive");
        }
        return this;
    }

    /**
     * @return true if ViewPager is auto flipping; false otherwise
     */
    public boolean isAutoFlip() {
        return mAutoFlip;
    }

    /**
     * start ViewPager auto flip
     */
    public void start() {
        if (mViewPager != null) {
            mAutoFlip = true;
            mViewPager.removeCallbacks(mFlipRunnable);
            mViewPager.removeOnPageChangeListener(this);
            mViewPager.addOnPageChangeListener(this);
            mPosition = mViewPager.getCurrentItem();
            mLastFlipTime = 0;
            mScrollState = ViewPager.SCROLL_STATE_IDLE;
            mViewPager.postDelayed(mFlipRunnable, mFlipInterval);
        }
    }

    /**
     * stop ViewPager auto flip.
     * should always call this in Activity.onDestroy to release handler callbacks,
     * or mem leak may occur.
     */
    public void stop() {
        mAutoFlip = false;
        if (mViewPager != null) {
            mViewPager.removeCallbacks(mFlipRunnable);
        }
    }

    /**
     * reset to init state.
     * should be called after {@link ViewPager#setAdapter(PagerAdapter)}
     */
    public void reset() {
        if (mViewPager != null) {
            mViewPager.removeCallbacks(mFlipRunnable);
            mViewPager.removeOnPageChangeListener(this);
            mViewPager.addOnPageChangeListener(this);
            mPosition = mViewPager.getCurrentItem();
            mLastFlipTime = 0;
            mScrollState = ViewPager.SCROLL_STATE_IDLE;
            mViewPager.postDelayed(mFlipRunnable, mFlipInterval);
        }
    }

    @Override
    public void onPageSelected(int position) {
        mPosition = position;
        mLastFlipTime = System.currentTimeMillis();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mScrollState = state;
    }

    private boolean canAutoFlip() {
        if (mViewPager == null || mViewPager.getAdapter() == null) {
            return false;
        }
        long interval = System.currentTimeMillis() - mLastFlipTime;
        if (interval < mFlipInterval - 300) {
            // postDelayed() is not so accurate about delay time,
            // we minus 300 to avoid post too soon caused by the deviation
            mViewPager.removeCallbacks(mFlipRunnable);
            mViewPager.postDelayed(mFlipRunnable, mFlipInterval - interval);
            return false;
        }
        return mAutoFlip && mScrollState == ViewPager.SCROLL_STATE_IDLE;
    }

    private Runnable mFlipRunnable = new Runnable() {
        @Override
        public void run() {
            if (mViewPager == null) {
                // unlikely
                return;
            }
            if (mViewPager.getWindowToken() == null) {
                // window token no longer valid, exit loop
                mViewPager.removeCallbacks(mFlipRunnable);
                mViewPager.removeOnPageChangeListener(ViewPagerAutoFlipper.this);
                return;
            }

            if (canAutoFlip()) {
                int count = mViewPager.getAdapter().getCount();
                int next = (mPosition + 1 == count) ? 0 : mPosition + 1;
                mViewPager.setCurrentItem(next);
            }
            if (mAutoFlip) {
                mViewPager.removeCallbacks(mFlipRunnable);
                mViewPager.postDelayed(mFlipRunnable, mFlipInterval);
            }
        }
    };

}
