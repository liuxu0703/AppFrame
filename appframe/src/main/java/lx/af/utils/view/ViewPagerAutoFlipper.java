package lx.af.utils.view;

import android.os.Handler;
import android.os.Message;
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
        Handler.Callback,
        ViewPager.OnPageChangeListener {

    private static final long DEFAULT_FLIP_INTERVAL = TimeUnit.SECONDS.toMillis(3);

    private static final int MSG_AUTO_FLIP = 101;

    private Handler mHandler;
    private ViewPager mViewPager;
    private boolean mAutoFlip = false;
    private long mFlipInterval = DEFAULT_FLIP_INTERVAL;
    private long mLastFlipTime;
    private int mScrollState = ViewPager.SCROLL_STATE_IDLE;
    private int mPosition;

    public ViewPagerAutoFlipper(ViewPager pager) {
        mHandler = new Handler(this);
        mViewPager = pager;
        mViewPager.addOnPageChangeListener(this);
        mPosition = mViewPager.getCurrentItem();
    }

    /**
     * start ViewPager auto flip
     */
    public void startAutoFlip() {
        mAutoFlip = true;
        mHandler.removeMessages(MSG_AUTO_FLIP);
        mHandler.sendEmptyMessageDelayed(MSG_AUTO_FLIP, mFlipInterval);
    }

    /**
     * stop ViewPager auto flip.
     * should always call this in Activity.onDestroy to release handler callbacks,
     * or mem leak may occur.
     */
    public void stopAutoFlip() {
        mAutoFlip = false;
        mHandler.removeMessages(MSG_AUTO_FLIP);
    }

    /**
     * reset to init state.
     * should be called after {@link ViewPager#setAdapter(PagerAdapter)}
     */
    public void resetAutoFlip() {
        mHandler.removeMessages(MSG_AUTO_FLIP);
        mViewPager.removeOnPageChangeListener(this);
        mViewPager.addOnPageChangeListener(this);
        mPosition = mViewPager.getCurrentItem();
        mLastFlipTime = 0;
        mScrollState = ViewPager.SCROLL_STATE_IDLE;
        mHandler.sendEmptyMessageDelayed(MSG_AUTO_FLIP, mFlipInterval);
    }

    public boolean isAutoFlip() {
        return mAutoFlip;
    }

    public void setAutoFlipInterval(long interval) {
        if (interval != 0) {
            mFlipInterval = interval;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_AUTO_FLIP: {
                if (canAutoFlip()) {
                    mViewPager.post(mFlipRunnable);
                }
                mHandler.sendEmptyMessageDelayed(MSG_AUTO_FLIP, mFlipInterval);
                break;
            }
        }
        return true;
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
            mHandler.removeMessages(MSG_AUTO_FLIP);
            mHandler.sendEmptyMessageDelayed(MSG_AUTO_FLIP, mFlipInterval - interval);
            return false;
        }
        return mAutoFlip && mScrollState == ViewPager.SCROLL_STATE_IDLE;
    }

    private Runnable mFlipRunnable = new Runnable() {
        @Override
        public void run() {
            int count = mViewPager.getAdapter().getCount();
            int next = (mPosition + 1 == count) ? 0 : mPosition + 1;
            mViewPager.setCurrentItem(next);
        }
    };


}
