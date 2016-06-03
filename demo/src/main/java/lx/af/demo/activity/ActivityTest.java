package lx.af.demo.activity;

import android.os.Bundle;
import android.view.View;

import butterknife.OnClick;
import lx.af.demo.R;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseActivity;

/**
 * author: lx
 * date: 15-12-8
 */
public class ActivityTest extends BaseActivity implements
        View.OnClickListener,
        ActionBar.Default {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableFeature(FEATURE_DOUBLE_BACK_EXIT);
        setContentView(R.layout.activity_test);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    @OnClick({
            R.id.test_btn_1,
            R.id.test_btn_2,
            R.id.test_btn_3,
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test_btn_1: {
                break;
            }
            case R.id.test_btn_2: {
                break;
            }
            case R.id.test_btn_3: {
                break;
            }
        }
    }

}
