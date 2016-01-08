package lx.af.utils.ViewUtils;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.LinkedList;

import lx.af.view.SwipeRefresh.SwipeRefreshListLayout;

/**
 * author: lx
 * date: 16-1-8
 */
public class ActionBarScrollFadeHelper {

    private View mActionBar;
    private View mOffsetView;
    private int mOffsetValue;
    private boolean mAlphaIncrease = true;
    private Drawable mActionBarBackgroundDrawable;
    private LinkedList<FadeViewInfo> mFadeViewList;

    /**
     * set action bar view
     */
    public static ActionBarScrollFadeHelper with(View actionBar) {
        return new ActionBarScrollFadeHelper(actionBar);
    }

    /**
     * set offset view. fade effect will be played according to offset view:
     * fade effect ends when scroll to the bottom of the offset view.
     * @see #offset(int)
     */
    public ActionBarScrollFadeHelper offset(View offsetView) {
        mOffsetView = offsetView;
        return this;
    }

    /**
     * set offset value. fade effect will be played according to offset value:
     * fade effect ends when offsetValue has be scrolled
     * @see #offset(View)
     */
    public ActionBarScrollFadeHelper offset(int offsetValue) {
        mOffsetValue = offsetValue;
        return this;
    }

    /**
     * set action bar to fade in when scroll down.
     */
    public ActionBarScrollFadeHelper fadeIn() {
        mAlphaIncrease = true;
        return this;
    }

    /**
     * set action bar to fade out when scroll down.
     */
    public ActionBarScrollFadeHelper fadeOut() {
        mAlphaIncrease = false;
        return this;
    }

    /**
     * add a view to show the same fade effect with the action bar.
     */
    public ActionBarScrollFadeHelper addFadeWithView(View view) {
        if (mFadeViewList == null) {
            mFadeViewList = new LinkedList<>();
        }
        mFadeViewList.add(new FadeViewInfo(view, false));
        return this;
    }

    /**
     * add a view to show reverse fade effect with the action bar.
     */
    public ActionBarScrollFadeHelper addFadeReverseView(View view) {
        if (mFadeViewList == null) {
            mFadeViewList = new LinkedList<>();
        }
        mFadeViewList.add(new FadeViewInfo(view, true));
        return this;
    }

    /**
     * start to show fade effect.
     * {@link #offset(View)} must be called before calling to this method.
     * @param list fade effect will be set accordingly when the list is scrolled
     */
    public void start(ListView list) {
        init(true);
        list.setOnScrollListener(mOnScrollListener);
    }

    /**
     * start to show fade effect.
     * {@link #offset(View)} must be called before calling to this method.
     * @param list fade effect will be set accordingly when the list is scrolled
     */
    public void start(SwipeRefreshListLayout list) {
        init(true);
        list.setOnScrollListener(mOnScrollListener);
    }

    private ActionBarScrollFadeHelper(View actionBar) {
        mActionBar = actionBar;
        mActionBarBackgroundDrawable = mActionBar.getBackground();
    }

    private void init(boolean acceptOffsetViewOnly) {
        if (acceptOffsetViewOnly && mOffsetView == null) {
            throw new IllegalStateException("no offset view has be set");
        }
        if (mOffsetView == null && mOffsetValue == 0) {
            throw new IllegalStateException("no offset has be set");
        }
        onNewScroll(0);
    }

    private int getOffset() {
        if (mOffsetView != null) {
            return mOffsetView.getHeight();
        }
        return mOffsetValue;
    }

    private void onNewScroll(int scroll) {
        int alpha;
        int actionBarHeight = mActionBar.getHeight();
        int delta = getOffset() - actionBarHeight;
        if (delta <= 0 || scroll <= 0) {
            // set to init state
            alpha = mAlphaIncrease ? 0 : 255;
        } else if (scroll > delta) {
            alpha = mAlphaIncrease ? 255 : 0;
        } else {
            float ratio;
            if (mAlphaIncrease) {
                ratio = (float) scroll / delta;
            } else {
                ratio = (float) (delta - scroll) / delta;
            }
            alpha = (int) (ratio * 255);
        }

        fade(alpha);
    }

    private void fade(int alpha) {
        mActionBarBackgroundDrawable.setAlpha(alpha);
        if (mFadeViewList != null && mFadeViewList.size() != 0) {
            for (FadeViewInfo info : mFadeViewList) {
                info.fade(alpha);
            }
        }
    }

    private boolean isContainView(View container, View view) {
        if (!(container instanceof ViewGroup)) {
            return false;
        }
        ViewParent parent = view.getParent();
        while (parent instanceof ViewGroup) {
            if (parent == container) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            View first = view.getChildAt(0);
            if (first == null) {
                onNewScroll(0);
            } else if (first == mOffsetView) {
                onNewScroll(-mOffsetView.getTop());
            } else if (isContainView(first, mOffsetView)) {
                onNewScroll(-(first.getTop() + mOffsetView.getTop()));
            } else {
                onNewScroll(getOffset());
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    };


    private static class FadeViewInfo {
        View view;
        boolean reverse;

        public FadeViewInfo(View view, boolean reverse) {
            this.view = view;
            this.reverse = reverse;
        }

        public void fade(int alpha) {
            if (view == null) return;
            float a;
            if (reverse) {
                a = (float) (255 - alpha) / 255;
            } else {
                a = (float) alpha / 255;
            }
            view.setAlpha(a);
        }
    }

}
