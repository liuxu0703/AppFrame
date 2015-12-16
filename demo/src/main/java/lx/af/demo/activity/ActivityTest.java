package lx.af.demo.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.Arrays;

import lx.af.demo.R;
import lx.af.demo.adapter.ImagePagerAdapter;
import lx.af.demo.base.BaseDemoActivity;
import lx.af.demo.consts.TestRes;
import lx.af.utils.view.ViewPagerAutoFlipper;
import lx.af.view.kenburnsview.KenBurnsView;

/**
 * author: lx
 * date: 15-12-8
 */
public class ActivityTest extends BaseDemoActivity implements
        View.OnClickListener,
        BaseDemoActivity.ActionBarImpl {

    private static final String L = "http://i.k1982.com/design_img/201008/20100806201117702.jpg";
    private static final String T = "http://img5.duitang.com/uploads/item/201405/03/20140503222852_aNXJL.thumb.700_0.jpeg";

    KenBurnsView kbv;
    ViewPager pager;
    String current = L;

    ViewPagerAutoFlipper autoSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        findViewById(R.id.test_btn_1).setOnClickListener(this);
        kbv = obtainView(R.id.test_kbv);
        pager = obtainView(R.id.test_pager);
        pager.setAdapter(new ImagePagerAdapter(Arrays.asList(TestRes.TEST_IMG_AVATAR1)));

        autoSlider = new ViewPagerAutoFlipper(pager);
        autoSlider.startAutoFlip();
    }

    @Override
    public void onClick(View v) {
//        current = current.equals(L) ? T : L;
//        Log.d("liuxu", "11111 activity test, load url: " + current);
//        ImageLoader.getInstance().displayImage(current, kbv);

        if (autoSlider.isAutoFlip()) {
            autoSlider.stopAutoFlip();
        } else {
            autoSlider.startAutoFlip();
        }
    }

}
