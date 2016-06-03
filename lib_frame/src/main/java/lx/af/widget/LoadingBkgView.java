package lx.af.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import lx.af.R;
import lx.af.manager.GlobalThreadManager;
import lx.af.utils.ResourceUtils;
import lx.af.view.ProgressWheel;

/**
 * author: lx
 * date: 16-3-7
 */
public class LoadingBkgView extends LinearLayout {

    private static final int ERROR_TYPE_FAIL = 11;
    private static final int ERROR_TYPE_EMPTY = 12;

    private TextView mViewMessage;
    private ProgressWheel mViewProgress;
    private ImageView mViewIcon;

    private String mMessageLoad;
    private String mMessageFail;
    private String mMessageEmpty;
    private int mIconLoadingResId;
    private int mIconFailResId;
    private int mIconEmptyResId;

    private boolean mIsLoading = false;
    private int mErrorType = ERROR_TYPE_FAIL;
    private boolean mRetryOnEmpty = false;
    private OnClickListener mRetryClickListener;

    private Animation mAnimGone = ResourceUtils.loadAnimation(R.anim.fade_out);
    private Animation mAnimShow = ResourceUtils.loadAnimation(R.anim.fade_in);

    public LoadingBkgView(Context context) {
        super(context);
        initView(context, null);
    }

    public LoadingBkgView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        int textColor = -1;
        int progressColor = -1;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadingBkgView);
            mMessageLoad = a.getString(R.styleable.LoadingBkgView_loading_msg_load);
            mMessageFail = a.getString(R.styleable.LoadingBkgView_loading_msg_fail);
            mMessageEmpty = a.getString(R.styleable.LoadingBkgView_loading_msg_empty);
            mIconLoadingResId = a.getResourceId(R.styleable.LoadingBkgView_loading_icon,
                    R.drawable.default_loading_bkg_view_load_icon);
            mIconFailResId = a.getResourceId(R.styleable.LoadingBkgView_loading_icon_fail,
                    R.drawable.default_loading_bkg_view_fail_icon);
            mIconEmptyResId = a.getResourceId(R.styleable.LoadingBkgView_loading_icon_empty,
                    R.drawable.default_loading_bkg_view_empty_icon);
            mRetryOnEmpty = a.getBoolean(R.styleable.LoadingBkgView_loading_retry_on_empty, false);
            textColor = a.getColor(R.styleable.LoadingBkgView_loading_text_color,
                    getResources().getColor(R.color.default_loading_bkg_view_text_color));
            progressColor = a.getColor(R.styleable.LoadingBkgView_loading_text_color,
                    getResources().getColor(R.color.default_loading_bkg_view_progress_color));
            a.recycle();
        }

        if (TextUtils.isEmpty(mMessageLoad)) {
            mMessageLoad = getResources().getString(R.string.loading_bkg_view_message_loading);
        }
        if (TextUtils.isEmpty(mMessageFail)) {
            mMessageFail = getResources().getString(R.string.loading_bkg_view_message_fail);
        }
        if (TextUtils.isEmpty(mMessageEmpty)) {
            mMessageEmpty = getResources().getString(R.string.loading_bkg_view_message_empty);
        }

        inflate(context, R.layout.loading_bkg_view, this);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        setClickable(true);
        mViewProgress = (ProgressWheel) findViewById(R.id.loading_bkg_view_progress);
        mViewMessage = (TextView) findViewById(R.id.loading_bkg_view_message);
        mViewIcon = (ImageView) findViewById(R.id.loading_bkg_view_icon);
        mViewIcon.setOnClickListener(mRetryClickListenerInner);
        mViewMessage.setOnClickListener(mRetryClickListenerInner);

        if (textColor != -1) {
            mViewMessage.setTextColor(textColor);
        }
        if (progressColor != -1) {
            mViewProgress.setBarColor(progressColor);
        }
    }

    public void loading() {
        GlobalThreadManager.runInUiThread(new Runnable() {
            @Override
            public void run() {
                mIsLoading = true;
                setVisibility(View.VISIBLE);
                mViewProgress.setVisibility(View.VISIBLE);
                mViewProgress.spin();
                mViewIcon.setImageResource(mIconLoadingResId);
                mViewMessage.setText(R.string.loading_bkg_view_message_loading);
                mViewMessage.setVisibility(View.VISIBLE);
                mViewMessage.startAnimation(mAnimShow);
            }
        });
    }

    public void done(long delay) {
        GlobalThreadManager.runInUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                mIsLoading = false;
                mViewProgress.stopSpinning();
                setVisibility(View.GONE);
                startAnimation(mAnimGone);
            }
        }, delay);
    }

    public void done() {
        done(0);
    }

    public void empty() {
        empty(mMessageEmpty);
    }

    public void empty(int resId) {
        empty(getResources().getString(resId));
    }

    public void empty(final String message) {
        GlobalThreadManager.runInUiThread(new Runnable() {
            @Override
            public void run() {
                mIsLoading = false;
                mErrorType = ERROR_TYPE_EMPTY;
                setVisibility(View.VISIBLE);
                mMessageEmpty = message;
                mViewProgress.stopSpinning();
                mViewProgress.setVisibility(View.INVISIBLE);
                mViewProgress.startAnimation(mAnimGone);
                mViewIcon.setImageResource(mIconEmptyResId);
                mViewMessage.setText(mMessageEmpty);
                mViewMessage.setVisibility(View.VISIBLE);
                mViewMessage.startAnimation(mAnimShow);
            }
        });
    }

    public void fail() {
        fail(mMessageFail);
    }

    public void fail(int strResId) {
        fail(getResources().getString(strResId));
    }

    public void fail(final String message) {
        GlobalThreadManager.runInUiThread(new Runnable() {
            @Override
            public void run() {
                mIsLoading = false;
                mErrorType = ERROR_TYPE_FAIL;
                setVisibility(View.VISIBLE);
                mMessageFail = message;
                mViewProgress.stopSpinning();
                mViewProgress.setVisibility(View.INVISIBLE);
                mViewProgress.startAnimation(mAnimGone);
                mViewIcon.setImageResource(mIconFailResId);
                mViewMessage.setText(mMessageFail);
                mViewMessage.setVisibility(View.VISIBLE);
                mViewMessage.startAnimation(mAnimShow);
            }
        });
    }

    public void setFailIcon(int iconResId) {
        mIconFailResId = iconResId;
    }

    public void setEmptyIcon(int iconResId) {
        mIconEmptyResId = iconResId;
    }

    public void setRetryOnEmpty(boolean retryOnEmpty) {
        mRetryOnEmpty = retryOnEmpty;
    }

    public void setRetryClickCallback(OnClickListener l) {
        mRetryClickListener = l;
        mViewMessage.setOnClickListener(l);
    }

    public boolean isLoading() {
        return mIsLoading;
    }

    private OnClickListener mRetryClickListenerInner = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mRetryClickListener != null) {
                if (mErrorType == ERROR_TYPE_FAIL) {
                    mRetryClickListener.onClick(v);
                }
                if (mErrorType == ERROR_TYPE_EMPTY && mRetryOnEmpty) {
                    mRetryClickListener.onClick(v);
                }
            }
        }
    };

}
