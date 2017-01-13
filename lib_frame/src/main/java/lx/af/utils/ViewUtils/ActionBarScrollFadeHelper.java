package lx.af.utils.ViewUtils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.LinkedList;

import lx.af.utils.ScreenUtils;
import lx.af.view.ObservableScrollView;
import lx.af.view.SwipeRefresh.SwipeRefreshGridLayout;
import lx.af.view.SwipeRefresh.SwipeRefreshListLayout;

/**
 * author: lx
 * date: 16-1-8
 */
public class ActionBarScrollFadeHelper {

    private static final boolean DEBUG = false;

    private View mActionBar;
    private View mEndOffsetView;
    private View mStartOffsetView;
    private FadeListener mFadeListener;

    private int mEndOffsetValue;
    private int mStartOffsetValue = 0;
    private boolean mAlphaIncrease = true;
    private LinkedList<FadeViewInfo> mFadeViewList;
    private LinkedList<FadeDrawableInfo> mFadeDrawableList = new LinkedList<>();

    /**
     * set action bar view
     */
    public static ActionBarScrollFadeHelper with(View actionBar) {
        return new ActionBarScrollFadeHelper(actionBar, true);
    }

    public static ActionBarScrollFadeHelper with(View actionBar, boolean actionBarFade) {
        return new ActionBarScrollFadeHelper(actionBar, actionBarFade);
    }

    /**
     * set offset view to indicate when to end fade effect.
     * @param offsetView fade effect will end when scroll reaches bottom of this view.
     * @see #endOffset(int)
     */
    public ActionBarScrollFadeHelper endOffset(View offsetView) {
        mEndOffsetView = offsetView;
        return this;
    }

    /**
     * set offset value to indicate when to end fade effect.
     * some scrollable view ({@link ListView}, {@link SwipeRefreshListLayout})
     * does not support setting a value as end offset.
     * in such case, use {@link #endOffset(View)} instead.
     * @param offsetValue fade effect will end when offsetValue has been scrolled.
     * @see #endOffset(View)
     */
    public ActionBarScrollFadeHelper endOffset(int offsetValue) {
        mEndOffsetValue = offsetValue;
        return this;
    }

    /**
     * set offset view to indicate when to start fade effect.
     * @param offsetView fade effect will begin when scroll reaches bottom of this view.
     */
    public ActionBarScrollFadeHelper startOffset(View offsetView) {
        mStartOffsetView = offsetView;
        return this;
    }

    /**
     * set offset value to indicate when to start fade effect.
     * @param offsetValue fade effect will begin when offsetValue has been scrolled.
     */
    public ActionBarScrollFadeHelper startOffset(int offsetValue) {
        mStartOffsetValue = offsetValue;
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
     * set a callback to be informed when action bar fade changes.
     */
    public ActionBarScrollFadeHelper setFadeListener(FadeListener l) {
        mFadeListener = l;
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
     * add a drawable to show the same fade effect with the action bar.
     */
    public ActionBarScrollFadeHelper addFadeWithDrawable(Drawable drawable) {
        mFadeDrawableList.add(new FadeDrawableInfo(drawable, false));
        return this;
    }

    /**
     * add a drawable to show the same fade effect with the action bar.
     */
    public ActionBarScrollFadeHelper addFadeReverseDrawable(Drawable drawable) {
        mFadeDrawableList.add(new FadeDrawableInfo(drawable, true));
        return this;
    }

    /**
     * start to show fade effect.
     * {@link #endOffset(View)} must be called before calling to this method.
     * @param list fade effect will be set accordingly when the list is scrolled
     */
    public void start(ListView list) {
        init(list, true);
        list.setOnScrollListener(mListViewScrollListener);
    }

    /**
     * start to show fade effect.
     * {@link #endOffset(View)} must be called before calling to this method.
     * @param list fade effect will be set accordingly when the list is scrolled
     */
    public void start(SwipeRefreshListLayout list) {
        init(list, true);
        list.setOnScrollListener(mListViewScrollListener);
    }

    /**
     * start to show fade effect.
     * {@link #endOffset(View)} must be called before calling to this method.
     * @param list fade effect will be set accordingly when the list is scrolled
     */
    public void start(SwipeRefreshGridLayout list) {
        init(list, true);
        list.setOnScrollListener(mListViewScrollListener);
    }

    /**
     * start to show fade effect.
     * {@link #endOffset(View)} or {@link #endOffset(int)} must be called
     * before calling to this method.
     * @param scrollView fade effect will be set accordingly when the ScrollView is scrolled
     */
    public void start(ObservableScrollView scrollView) {
        init(scrollView, false);
        scrollView.setOnScrollListener(mScrollViewScrollListener);
    }

    private ActionBarScrollFadeHelper(View actionBar, boolean actionBarFade) {
        mActionBar = actionBar;
        if (actionBarFade) {
            mFadeDrawableList.add(new FadeDrawableInfo(mActionBar.getBackground(), false));
        }
    }

    private void init(View scrollableView, boolean acceptOffsetViewOnly) {
        if (acceptOffsetViewOnly && mEndOffsetView == null) {
            throw new IllegalStateException("no offset view has be set");
        }
        if (mEndOffsetView == null && mEndOffsetValue == 0) {
            throw new IllegalStateException("no offset has be set");
        }
        initOffsetValue(scrollableView);
        onNewScroll(0);
    }

    private void initOffsetValue(final View scrollableView) {
        if (mEndOffsetValue != 0 && (mStartOffsetValue != 0 || mStartOffsetView == null)) {
            return;
        }
        ViewTreeObserver vto = scrollableView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mEndOffsetValue == 0) {
                    mEndOffsetValue = getViewBottom(mEndOffsetView);
                }
                if (mStartOffsetValue == 0 && mStartOffsetView != null) {
                    mStartOffsetValue = getViewBottom(mStartOffsetView);
                }
                if (mEndOffsetValue != 0 && (mStartOffsetValue != 0 || mStartOffsetView == null)) {
                    if (android.os.Build.VERSION.SDK_INT >= 16) {
                        scrollableView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        scrollableView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            }
        });
    }

    private void onNewScroll(int scroll) {
        int alpha;
        int acHeight = mActionBar.getHeight();
        int start = mStartOffsetValue;
        int end = mEndOffsetValue;
        float ratio = 0.0f;
        if (scroll <= start - acHeight || scroll <= 0) {
            // set to init state
            alpha = mAlphaIncrease ? 0 : 255;
        } else if (scroll > end - acHeight) {
            // set to final state
            alpha = mAlphaIncrease ? 255 : 0;
        } else {
            int delta = Math.abs(end - start);
            if (mAlphaIncrease) {
                ratio = (float) Math.abs(scroll + acHeight - start) / delta;
            } else {
                ratio = (float) Math.abs(end - (scroll + acHeight)) / delta;
            }
            alpha = (int) (ratio * 255);
        }

        if (DEBUG) {
            Log.d("liuxu", "ActionBarFadeHelper, onNewScroll" +
                    ", acHeight=" + acHeight + ", start=" + start + ", end=" + end +
                    ", scroll=" + scroll + ", ratio=" + ratio + ", alpha=" + alpha);
        }
        fade(alpha);
    }

    private void fade(int alpha) {
        for (FadeDrawableInfo info : mFadeDrawableList) {
            info.fade(alpha);
        }
        if (mFadeViewList != null && mFadeViewList.size() != 0) {
            for (FadeViewInfo info : mFadeViewList) {
                info.fade(alpha);
            }
        }
        if (mFadeListener != null) {
            mFadeListener.onFadeChanged(alpha);
        }
    }

    private boolean isContainView(View container, View view) {
        if (container == null || view == null) {
            return false;
        }
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

    // get distance between (0,0) and view bottom line
    private static int getViewBottom(View view) {
        if (view == null) {
            return 0;
        }
        if (view.getHeight() == 0) {
            return 0;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return location[1] + view.getHeight()
                - ScreenUtils.getStatusBarHeight((Activity) view.getContext());
    }

    private AbsListView.OnScrollListener mListViewScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            View first = view.getChildAt(0);
            if (first == null) {
                onNewScroll(0);
            } else if (first == mEndOffsetView) {
                onNewScroll(-mEndOffsetView.getTop());
            } else if (isContainView(first, mEndOffsetView)) {
                onNewScroll(-(first.getTop()));
            } else {
                onNewScroll(mEndOffsetValue);
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    };

    private ObservableScrollView.OnScrollListener mScrollViewScrollListener = new ObservableScrollView.OnScrollListener() {
        @Override
        public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
            onNewScroll(y);
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


    private static class FadeDrawableInfo {
        Drawable drawable;
        boolean reverse;

        public FadeDrawableInfo(Drawable drawable, boolean reverse) {
            this.drawable = drawable;
            this.reverse = reverse;
        }

        public void fade(int alpha) {
            if (drawable == null) return;
            if (reverse) {
                drawable.setAlpha(255 - alpha);
            } else {
                drawable.setAlpha(alpha);
            }
        }
    }


    public interface FadeListener {

        /**
         * callback to be informed when action bar fade changes
         * @param alpha action bar background alpha value
         */
        void onFadeChanged(int alpha);
    }

}
