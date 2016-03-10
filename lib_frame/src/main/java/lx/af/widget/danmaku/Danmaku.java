package lx.af.widget.danmaku;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import java.util.LinkedList;
import java.util.List;

/**
 * author: lx
 * date: 16-3-1
 */
public class Danmaku extends RelativeLayout implements Handler.Callback {

    private static final long INTERVAL = 1500;

    private final Object mDataLock = new Object();

    private ViewPool mViewPool;
    private LinkedList<Object> mDataList = new LinkedList<>();
    private Handler mHandler = new Handler(this);
    private Callback mCallback;
    private boolean mIsRunning = false;

    public Danmaku(Context context) {
        super(context);
        initView();
    }

    public Danmaku(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mViewPool = new ViewPool(this);
    }

    public void setViewAdapter(ViewAdapter adapter) {
        mViewPool.setViewAdapter(adapter);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void addData(List<Object> data) {
        synchronized (mDataLock) {
            mDataList.addAll(0, data);
        }
        mHandler.removeMessages(MSG_START_NEXT);
        mHandler.sendEmptyMessageDelayed(MSG_START_NEXT, INTERVAL);
    }

    public void addData(Object ... data) {
        synchronized (mDataLock) {
            if (data != null && data.length != 0) {
                for (Object d : data) {
                    mDataList.add(0, d);
                }
            }
        }
        mHandler.removeMessages(MSG_START_NEXT);
        mHandler.sendEmptyMessageDelayed(MSG_START_NEXT, INTERVAL);
    }

    public void startDanmaku() {
        mIsRunning = true;
    }

    public void stopDanmaku() {
        mIsRunning = false;
    }

    public void toggleDanmaku() {
        mIsRunning = !mIsRunning;
    }

    public boolean isDanmakuRunning() {
        return mIsRunning;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mViewPool.onLayout(changed, l, t, r, b);
    }

    private static final int MSG_START_NEXT = 101;

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_START_NEXT: {
                if (!mIsRunning) {
                    mHandler.removeMessages(MSG_START_NEXT);
                    break;
                }

                Object data = null;
                synchronized (mDataLock) {
                    if (mDataList.size() != 0) {
                        data = mDataList.get(0);
                        mDataList.remove(0);
                    }
                    if (mDataList.size() != 0) {
                        mHandler.sendEmptyMessageDelayed(MSG_START_NEXT, INTERVAL);
                    } else {
                        // no more data, stop loop
                        mHandler.removeMessages(MSG_START_NEXT);
                        if (mCallback != null) {
                            mCallback.onDataEmpty();
                        }
                    }
                }
                if (data != null) {
                    ViewWrapper wrapper = mViewPool.obtainView(data);
                    wrapper.startAnimation();
                }
                break;
            }
        }
        return true;
    }


    public interface Callback {

        void onDataEmpty();

    }

}
