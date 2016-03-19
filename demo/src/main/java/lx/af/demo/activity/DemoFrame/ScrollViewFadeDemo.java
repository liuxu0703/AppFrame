package lx.af.demo.activity.DemoFrame;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import lx.af.demo.R;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseActivity;
import lx.af.utils.ScreenUtils;
import lx.af.utils.ViewInject.ViewInject;
import lx.af.utils.ViewUtils.ActionBarScrollFadeHelper;
import lx.af.view.ObservableScrollView;
import lx.af.widget.iconify.widget.IconTextView;

/**
 * author: lx
 * date: 16-3-14
 */
public class ScrollViewFadeDemo extends BaseActivity implements
        ActionBar.Default.Callback.Overlay {

    @ViewInject(id = R.id.activity_scroll_view_fade_text)
    private TextView mTvText;
    @ViewInject(id = R.id.activity_scroll_view_fade_hint)
    private TextView mTvHint;
    @ViewInject(id = R.id.activity_scroll_view_fade_scrollview)
    private ObservableScrollView mScrollView;

    private View mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_view_fade);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 45; i ++) {
            sb.append("Test Scroll Fade, text line ").append(i + 1).append("\n");
        }
        mTvText.setText(sb.toString());

        ActionBarScrollFadeHelper
                .with(getActionBarView())
                .startOffset(ScreenUtils.dip2px(80))
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
