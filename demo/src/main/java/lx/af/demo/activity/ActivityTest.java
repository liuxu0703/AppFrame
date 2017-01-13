package lx.af.demo.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import butterknife.OnClick;
import lx.af.demo.R;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseActivity;
import lx.af.utils.ScreenUtils;
import lx.af.utils.UIL.UILLoader;

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

        {
            ImageView imageView = (ImageView) findViewById(R.id.test_round_drawable);
            UILLoader.of(imageView, "https://tse2-mm.cn.bing.net/th?id=OIP.M6b7bea5bfdb6ea886e1a3bc298de78a5o0&pid=15.1")
                    .border(ScreenUtils.dip2px(10), Color.parseColor("#60000000"))
                    .animateFadeIn().display();
        }

//        {
//            ImageView imageView = (ImageView) findViewById(R.id.test_round_drawable2);
//            UILLoader.of(imageView, "http://www.liuhaihua.cn/wp-content/uploads/2015/12/2YVJJn.png")
//                    .border(ScreenUtils.dip2px(10), Color.parseColor("#80000000"))
//                    .animateFadeIn().display();
//        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    @OnClick({
            R.id.test_btn_1,
            R.id.test_btn_2,
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test_btn_1: {
                break;
            }
            case R.id.test_btn_2: {
                break;
            }
        }
    }

}
