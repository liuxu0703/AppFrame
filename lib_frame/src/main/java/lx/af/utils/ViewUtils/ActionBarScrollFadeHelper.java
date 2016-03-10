package lx.af.utils.ViewUtils;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
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
    private ViewParent mViewRoot;
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
        return new ActionBarScrollFadeHelper(actionBar);
    }

    /**
     * set offset view. fade effect will be played according to offset view:
     * fade effect ends when scroll to the bottom of the offset view.
     * @see #endOffset(int)
     */
    public ActionBarScrollFadeHelper endOffset(View offsetView) {
        mEndOffsetView = offsetView;
        return this;
    }

    /**
     * set offset value. fade effect will be played according to offset value:
     * fade effect ends when offsetValue has be scrolled
     * @see #endOffset(View)
     */
    public ActionBarScrollFadeHelper endOffset(int offsetValue) {
        mEndOffsetValue = offsetValue;
        return this;
    }

    public ActionBarScrollFadeHelper startOffset(View offsetView) {
        mStartOffsetView = offsetView;
        return this;
    }

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
        list.setOnScrollListener(mOnScrollListener);
    }

    /**
     * start to show fade effect.
     * {@link #endOffset(View)} must be called before calling to this method.
     * @param list fade effect will be set accordingly when the list is scrolled
     */
    public void start(SwipeRefreshListLayout list) {
        init(list, true);
        list.setOnScrollListener(mOnScrollListener);
    }

    private ActionBarScrollFadeHelper(View actionBar) {
        mActionBar = actionBar;
        mViewRoot = mActionBar.getParent();
        mFadeDrawableList.add(new FadeDrawableInfo(mActionBar.getBackground(), false));
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
        ViewTreeObserver vto = scrollableView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mEndOffsetValue = getViewBottom(mEndOffsetView);
                mStartOffsetValue = getViewBottom(mStartOffsetView);
                if (mEndOffsetValue != 0) {
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
        if (scroll <= start - acHeight || scroll <= 0) {
            // set to init state
            alpha = mAlphaIncrease ? 0 : 255;
        } else if (scroll > end - acHeight) {
            // set to final state
            alpha = mAlphaIncrease ? 255 : 0;
        } else {
            float ratio;
            int delta = Math.abs(end - start);
            if (mAlphaIncrease) {
                ratio = (float) Math.abs(scroll + acHeight - start) / delta;
            } else {
                ratio = (float) Math.abs(end - (scroll + acHeight)) / delta;
            }
            alpha = (int) (ratio * 255);
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
    private int getViewBottom(View view) {
        if (view == null) {
            return 0;
        }
        int top = 0;
        ViewParent parent = view.getParent();
        while (parent != mViewRoot && parent instanceof View) {
            top += ((View) parent).getTop();
            parent = parent.getParent();
        }
        int ret = view.getHeight() + top;
        return ret;
    }

    private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            View first = view.getChildAt(0);
            if (first == null) {
                onNewScroll(0);
            } else if (first == mEndOffsetView) {
                onNewScroll(-mEndOffsetView.getTop());
            } else if (isContainView(first, mEndOffsetView)) {
                onNewScroll(-(first.getTop() + mEndOffsetView.getTop()));
            } else {
                onNewScroll(mEndOffsetValue);
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
        void onFadeChanged(int alpha);
    }

}
