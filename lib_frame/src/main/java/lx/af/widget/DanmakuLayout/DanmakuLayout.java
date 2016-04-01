package lx.af.widget.DanmakuLayout;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import lx.af.utils.ScreenUtils;

/**
 * author: lx
 * date: 16-3-1
 *
 * a relative layout to display danmaku.
 */
public class DanmakuLayout extends RelativeLayout implements
        Handler.Callback {

    private static final boolean DEBUG = false;
    private static final int TRACK_DIS = ScreenUtils.dip2px(6);
    private static final long DEFAULT_INTERVAL = 1000;

    private DanmakuBaseAdapter mAdapter;
    private DanmakuAnimator mPendingAnimator;

    private Handler mHandler = new Handler(this);
    private Random mRandom = new Random();

    private final Object mTrackLock = new Object();
    private SparseArray<Stack<DanmakuAnimator>> mViewRecycler = new SparseArray<>();
    private HashMap<DanmakuAnimator, Track> mRunningTrackMap = new HashMap<>();
    private LinkedList<Track> mTrackRecycler = new LinkedList<>();
    private LinkedList<Track> mRunningTrackList = new LinkedList<>();
    private LinkedList<Track> mUnAvailTrackList = new LinkedList<>();

    private long mMinInterval = DEFAULT_INTERVAL;
    private boolean mIsRunning = false;
    private boolean mIsDataEmpty = false;

    private OnDataEmptyListener mDataEmptyListener;

    public DanmakuLayout(Context context) {
        super(context);
        initView();
    }

    public DanmakuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
    }

    /**
     * set a data set adapter. data should be added by the adapter.
     */
    public void setAdapter(DanmakuBaseAdapter adapter) {
        mAdapter = adapter;
        mAdapter.setDanmakuLayout(this);
        if (mIsRunning) {
            mHandler.sendEmptyMessage(MSG_NEXT);
        }
    }

    /**
     * get the adapter.
     */
    public DanmakuBaseAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * start play danmaku.
     * if data set is not null, danmaku will show immediately.
     * if data set is null, we will wait for new data.
     * data null event can be monitored by either {@link OnDataEmptyListener}
     * or {@link DanmakuBaseAdapter#onDataEmpty()} in the adapter.
     * @see #toggleDanmaku()
     */
    public void startDanmaku() {
        if (!mIsRunning) {
            mIsRunning = true;
            mHandler.sendEmptyMessage(MSG_NEXT);
        }
    }

    /**
     * stop play danmaku.
     */
    public void stopDanmaku() {
        mIsRunning = false;
        mHandler.removeMessages(MSG_NEXT);
    }

    /**
     * start play if stopped; stop play if started.
     * @see #startDanmaku()
     * @see #stopDanmaku()
     */
    public void toggleDanmaku() {
        if (mIsRunning) {
            stopDanmaku();
        } else {
            startDanmaku();
        }
    }

    /**
     * set a minimum interval for a danmaku to be displayed after another.
     * this will only affect the speed of retrieving danmaku data.
     * to set the animation speed, see {@link DanmakuBaseAdapter#getDuration(Object)}
     */
    public void setDanmakuMinInterval(long interval) {
        if (interval < 100) {
            mMinInterval = DEFAULT_INTERVAL;
        } else {
            mMinInterval = interval;
        }
    }

    /**
     * get minimum interval for a danmaku to be displayed after another.
     * this will only affect the speed of retrieving danmaku data.
     * to set the animation speed, see {@link DanmakuBaseAdapter#getDuration(Object)}
     */
    public long getDanmakuMinInterval() {
        return mMinInterval;
    }

    /**
     * set a callback to be invoked when there is no data in the adapter.
     * this can also be monitored in the adapter
     * with {@link DanmakuBaseAdapter#onDataEmpty()}
     */
    public void setDanmakuDataEmptyListener(OnDataEmptyListener l) {
        mDataEmptyListener = l;
    }

    /**
     * @return true if danmaku displaying has be started; false otherwise
     */
    public boolean isDanmakuRunning() {
        return mIsRunning;
    }

    /**
     *
     * @return true on no data; false otherwise
     */
    public boolean isDataEmpty() {
        return mIsDataEmpty;
    }


    public interface OnDataEmptyListener {

        /**
         * callback to be invoked when there is no data in the adapter.
         * this can also be monitored in the adapter
         * with {@link DanmakuBaseAdapter#onDataEmpty()}
         */
        void onDanmakuDataEmpty(DanmakuLayout danmaku);
    }


    // ===================================================


    private static final int MSG_NEXT = 101;

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_NEXT: {
                if (!mIsRunning || mIsDataEmpty) {
                    mHandler.removeMessages(MSG_NEXT);
                    log("get next, stop, running=" + mIsRunning + ", empty=" + mIsDataEmpty);
                    break;
                }
                if (getHeight() == 0) {
                    log("get next, height 0, wait for layout");
                    mHandler.sendEmptyMessageDelayed(MSG_NEXT, mMinInterval);
                    break;
                }

                DanmakuAnimator da;
                if (mPendingAnimator != null) {
                    da = mPendingAnimator;
                } else {
                    da = retrieveDanmakuAnimator();
                }
                if (da == null || !mAdapter.hasData()) {
                    mIsDataEmpty = true;
                    mHandler.removeMessages(MSG_NEXT);
                    log("get next, stop, data empty");
                    if (mDataEmptyListener != null) {
                        mDataEmptyListener.onDanmakuDataEmpty(this);
                    }
                }
                if (da == null) {
                    break;
                }

                int y = findStartY(da);
                if (y < 0) {
                    mPendingAnimator = da;
                    // check for available track for the next turn
                    mHandler.sendEmptyMessageDelayed(MSG_NEXT, mMinInterval);
                } else {
                    synchronized (mTrackLock) {
                        Track track = obtainTrack();
                        track.start = y;
                        track.end = y + da.getHeight();
                        track.da = da;
                        mRunningTrackMap.put(da, track);
                        mRunningTrackList.add(track);
                    }
                    addDanmakuView(da);
                    da.startAnimation(y, mAnimatorCallback);
                    mPendingAnimator = null;
                    log("get next, y=" + y + ", " + da);
                    // start next when this animation starts
                }
                break;
            }
        }
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // set this to true to stop handler loop
        mIsDataEmpty = true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAdapter != null && mAdapter.hasData()) {
            mIsDataEmpty = false;
        }
    }

    void onNewDataArrived() {
        if (mIsDataEmpty) {
            mIsDataEmpty = false;
            if (mIsRunning) {
                mHandler.sendEmptyMessageDelayed(MSG_NEXT, mMinInterval);
            }
        }
    }

    void addToRecycler(DanmakuAnimator da) {
        removeView(da.getView());
        Stack<DanmakuAnimator> stack = mViewRecycler.get(da.getType());
        if (stack == null) {
            stack = new Stack<>();
            stack.push(da);
            mViewRecycler.put(da.getType(), stack);
        } else {
            stack.push(da);
        }
    }

    DanmakuAnimator getRecycledAnimator(int type) {
        if (mViewRecycler.size() == 0) {
            return null;
        }
        Stack<DanmakuAnimator> stack = mViewRecycler.get(type);
        if (stack == null || stack.size() == 0) {
            return null;
        }
        return stack.pop();
    }

    void log(String msg) {
        if (DEBUG) {
            Log.d("Danmaku", msg + "  (" + getChildCount() + "|" + mViewRecycler.size() + ")");
        }
    }

    private void addDanmakuView(DanmakuAnimator da) {
        da.getView().setVisibility(View.INVISIBLE);
        addView(da.getView());
    }

    private DanmakuAnimator retrieveDanmakuAnimator() {
        DanmakuAnimator da = mAdapter.getDanmakuAnimator();
        if (da == null) {
            return null;
        }
        View view = da.getView();
        measureChild(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();
        return da.init(width, height, getWidth());
    }

    private int findStartY(DanmakuAnimator da) {
        synchronized (mTrackLock) {
            if (mRunningTrackList.size() == 0) {
                // no running danmaku in the layout, show the first one in the middle.
                return (getHeight() - da.getHeight()) / 2;
            }

            Track track = findNewTrack(mRunningTrackList, da);
            if (track == null) {
                mUnAvailTrackList.clear();
                for (Track t : mRunningTrackList) {
                    if (!t.da.isAvailable(da)) {
                        mUnAvailTrackList.add(t);
                    }
                }
                if (mUnAvailTrackList.size() < mRunningTrackList.size()) {
                    track = findNewTrack(mUnAvailTrackList, da);
                }
            }
            if (track != null) {
                // pick a random start point on the track
                return track.start + mRandom.nextInt(track.end - track.start - da.getHeight());
            } else {
                return -1;
            }
        }
    }

    private Track findNewTrack(List<Track> runningTracks, DanmakuAnimator da) {
        synchronized (mTrackLock) {
            Collections.sort(runningTracks, mTrackComparator);
            int runningCount = runningTracks.size();
            ArrayList<Track> tracks = new ArrayList<>(runningCount + 1);
            for (int i = 0; i < runningCount + 1; i ++) {
                Track track = obtainTrack();
                if (i == 0) {
                    track.start = TRACK_DIS;
                } else {
                    Track t = runningTracks.get(i - 1);
                    track.start = t.end + TRACK_DIS;
                }
                if (i == runningCount) {
                    track.end = getHeight() - TRACK_DIS;
                } else {
                    Track t = runningTracks.get(i);
                    track.end = t.start - TRACK_DIS;
                }
                if (track.end - track.start > da.getHeight()) {
                    // valid track
                    tracks.add(track);
                }
            }

            if (tracks.size() == 0) {
                return null;
            } else {
                int idx = mRandom.nextInt(tracks.size());
                return tracks.get(idx);
            }
        }
    }

    private Track obtainTrack() {
        if (mTrackRecycler.size() == 0) {
            return new Track();
        }
        Track track = mTrackRecycler.pop();
        track.start = 0;
        track.end = 0;
        track.da = null;
        return track;
    }


    private DanmakuAnimator.Callback mAnimatorCallback = new DanmakuAnimator.Callback() {
        @Override
        public void onAnimationStart(DanmakuAnimator da) {
            mHandler.sendEmptyMessageDelayed(MSG_NEXT, mMinInterval);
        }

        @Override
        public void onAnimationEnd(DanmakuAnimator da) {
            synchronized (mTrackLock) {
                Track track = mRunningTrackMap.get(da);
                if (track != null) {
                    mRunningTrackList.remove(track);
                    mRunningTrackMap.remove(da);
                    mTrackRecycler.add(track);
                }
            }
            addToRecycler(da);
        }
    };

    private static class Track {

        int start;
        int end;
        DanmakuAnimator da;

        @Override
        public String toString() {
            return "Track{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }
    }

    private Comparator<Track> mTrackComparator = new Comparator<Track>() {
        @Override
        public int compare(Track lhs, Track rhs) {
            return lhs.start - rhs.start;
        }
    };

}
