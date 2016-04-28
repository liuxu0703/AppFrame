package lx.af.demo.activity.DemoFrame;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import lx.af.demo.R;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseActivity;
import lx.af.utils.ScreenUtils;
import lx.af.utils.ViewUtils.ActionBarScrollFadeHelper;
import lx.af.view.ObservableScrollView;
import lx.af.widget.iconify.widget.IconTextView;

/**
 * author: lx
 * date: 16-3-14
 */
public class ScrollViewFadeDemo extends BaseActivity implements
        ActionBar.Default.Callback.Overlay {


    @InjectView(R.id.activity_scroll_view_fade_hint)
    TextView mTvHint;
    @InjectView(R.id.activity_scroll_view_fade_text)
    TextView mTvText;
    @InjectView(R.id.activity_scroll_view_fade_scrollview)
    ObservableScrollView mScrollView;

    private View mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_view_fade);
        ButterKnife.inject(this);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 45; i++) {
            sb.append("Test Scroll Fade, text line ").append(i + 1).append("\n");
        }
        mTvText.setText(sb.toString());

        ActionBarScrollFadeHelper
                .with(getActionBarView())
                .startOffset(ScreenUtils.dip2px(120))
                .endOffset(ScreenUtils.dip2px(200))
                .addFadeWithView(mTvTitle)
                .addFadeReverseDrawable(mTvHint.getBackground())
                .start(mScrollView);
    }

    @Override
    public void onActionBarCreated(View actionBar, IconTextView left, TextView title, IconTextView right) {
        mTvTitle = title;
    }

}
