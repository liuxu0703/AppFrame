package lx.af.demo.activity.DemoWidget;

import android.os.Bundle;
import android.view.View;

import lx.af.demo.R;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseActivity;
import lx.af.utils.ScreenUtils;
import lx.af.widget.divider.QuoteDivider;

/**
 * author: lx
 * date: 16-3-28
 */
public class QuoteDividerDemo extends BaseActivity implements
        View.OnClickListener,
        ActionBar.Default {

    private QuoteDivider mQuoteDivider;

    private int mPosition1;
    private int mPosition2;
    private int mPosition3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_divider);
        findViewById(R.id.quote_divider_btn_1).setOnClickListener(this);
        findViewById(R.id.quote_divider_btn_2).setOnClickListener(this);
        findViewById(R.id.quote_divider_btn_3).setOnClickListener(this);
        mQuoteDivider = (QuoteDivider) findViewById(R.id.quote_divider_1);

        int width = ScreenUtils.getScreenWidth();
        mPosition1 = width / 6;
        mPosition2 = width / 2;
        mPosition3 = width * 5 / 6;

        mQuoteDivider.setQuoteLeft(mPosition1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.quote_divider_btn_1: {
                mQuoteDivider.setQuoteLeftWithAnim(mPosition1);
                break;
            }
            case R.id.quote_divider_btn_2: {
                mQuoteDivider.setQuoteLeftWithAnim(mPosition2);
                break;
            }
            case R.id.quote_divider_btn_3: {
                mQuoteDivider.setQuoteLeftWithAnim(mPosition3);
                break;
            }
        }
    }
}
