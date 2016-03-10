package lx.af.widget.danmaku;

import android.graphics.Point;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

import lx.af.utils.ScreenUtils;

/**
 * author: lx
 * date: 16-3-1
 */
class ViewPool {

    private static final int TRACK_DIS = ScreenUtils.dip2px(6);

    private Danmaku mDanmaku;
    private ViewAdapter mViewAdapter;
    private Stack<ViewWrapper> mRecycleStack = new Stack<>();
    private LinkedList<ViewWrapper> mRunningStack = new LinkedList<>();

    private int mWidth;
    private int mHeight;
    private Random mRandom = new Random();

    public ViewPool(Danmaku danmaku) {
        this.mDanmaku = danmaku;
    }

    void setViewAdapter(ViewAdapter viewAdapter) {
        mViewAdapter = viewAdapter;
    }

    synchronized ViewWrapper obtainView(Object data) {
        ViewWrapper wrapper = null;
        View convertView = null;
        if (!mRecycleStack.isEmpty()) {
            wrapper = mRecycleStack.pop();
        }
        if (wrapper != null) {
            convertView = wrapper.view;
        }
        View view = mViewAdapter.getView(data, convertView, mDanmaku);
        view.setVisibility(View.INVISIBLE);
        mDanmaku.addView(view);
        if (wrapper == null) {
            wrapper = new ViewWrapper(view, this);
        }
        wrapper.reset();
        wrapper.data = data;
        return wrapper;
    }

    synchronized void recycleView(ViewWrapper viewWrapper) {
        mDanmaku.removeView(viewWrapper.view);
        mRunningStack.remove(viewWrapper);
        mRecycleStack.push(viewWrapper);
    }

    synchronized void addRunningView(ViewWrapper wrapper) {
        mRunningStack.add(wrapper);
        Collections.sort(mRunningStack, mTrackComparator);
    }

    void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed || mWidth == 0 || mHeight == 0) {
            mWidth = r - l;
            mHeight = b - t;
        }
    }

    synchronized Point getNextStartPoint(ViewWrapper wrapper) {
        int x = mWidth;
        int y;
        if (mRunningStack.size() == 0) {
            y = mHeight / 2 - wrapper.height / 2;
        } else {
            y = getNextStartY(wrapper);
        }
        if (y == 0) {
            return null;
        } else {
            return new Point(x, y);
        }
    }

    private synchronized int getNextStartY(ViewWrapper wrapper) {
        int runningCount = mRunningStack.size();
        ArrayList<Track> tracks = new ArrayList<>(runningCount + 1);
        for (int i = 0; i < runningCount + 1; i ++) {
            Track track = new Track();
            if (i == 0) {
                track.start = TRACK_DIS;
            } else {
                ViewWrapper vw = mRunningStack.get(i - 1);
                track.start = vw.top + vw.height + TRACK_DIS;
            }
            if (i == runningCount) {
                track.end = mHeight - TRACK_DIS;
            } else {
                ViewWrapper vw = mRunningStack.get(i);
                track.end = vw.top - TRACK_DIS;
            }
            if (track.end - track.start > wrapper.height) {
                // valid track
                tracks.add(track);
            }
        }

        if (tracks.size() == 0) {
            return 0;
        } else {
            int idx = mRandom.nextInt(tracks.size());
            Track track = tracks.get(idx);
            int y = track.start + mRandom.nextInt(track.end - track.start - wrapper.height);
            return y;
        }
    }

    private static class Track {
        int start;
        int end;

        @Override
        public String toString() {
            return "[" + start + "," + end + "]";
        }
    }

    private Comparator<ViewWrapper> mTrackComparator = new Comparator<ViewWrapper>() {
        @Override
        public int compare(ViewWrapper lhs, ViewWrapper rhs) {
            return lhs.top - rhs.top;
        }
    };

}
