package lx.af.view.SwipeRefresh;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lx.af.R;
import lx.af.view.ProgressWheel;

/**
 * author: lx
 * date: 16-3-2
 */
public class DefaultLoadMoreFooter extends RelativeLayout implements ILoadMoreFooter {

    private TextView mMessage;
    private ProgressWheel mProgress;

    public DefaultLoadMoreFooter(Context context) {
        super(context);
        initView(context);
    }

    public DefaultLoadMoreFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.swipe_refresh_default_footer, this);
        setBackgroundColor(Color.parseColor("#f0f0f0"));
        mMessage = (TextView) findViewById(R.id.default_refresh_footer_load_more_text);
        mProgress = (ProgressWheel) findViewById(R.id.default_refresh_footer_load_more_progress);
    }

    @Override
    public void init(SwipeRefreshListLayout refreshLayout, ListView parent) {
        parent.addFooterView(this);
    }

    @Override
    public void refreshLoadState(ListView parent, SwipeRefreshListLayout.LoadState state) {
        switch (state) {
            case LOADING:
                mProgress.setVisibility(View.VISIBLE);
                mProgress.spin();
                mMessage.setText(R.string.swipe_refresh_default_footer_loading);
                break;
            case IDLE:
            case NO_MORE:
                mProgress.setVisibility(View.GONE);
                mProgress.stopSpinning();
                mMessage.setText(R.string.swipe_refresh_default_footer_done);
                break;
        }
    }
}
