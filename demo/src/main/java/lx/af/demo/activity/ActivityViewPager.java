package lx.af.demo.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lx.af.demo.R;
import lx.af.demo.base.BaseDemoActivity;
import lx.af.utils.ActivityUtils.ActivityResultCallback;
import lx.af.utils.ActivityUtils.ImageSelector;
import lx.af.utils.view.ViewPagerAutoFlipper;

/**
 * author: lx
 * date: 15-12-16
 */
public class ActivityViewPager extends BaseDemoActivity implements
        View.OnClickListener,
        BaseDemoActivity.ActionBarImpl {

    public static final String[] TEST_IMG_SCENE = new String[]{
            "http://www.bz55.com/uploads/allimg/150309/139-150309101A0.jpg",
            "http://www.bz55.com/uploads/allimg/150309/139-150309101A3.jpg",
            "http://www.bz55.com/uploads/allimg/150309/139-150309101A7.jpg",
            "http://www.bz55.com/uploads/allimg/150309/139-150309101F2.jpg",
    };

    private Button mBtnFlip;
    private ViewPager mViewPager;
    private ImagePagerAdapter mAdapter;
    private ViewPagerAutoFlipper mFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        mViewPager = obtainView(R.id.avp_pager);
        mBtnFlip = obtainView(R.id.avp_pager_btn_flip);
        mBtnFlip.setOnClickListener(this);
        findViewById(R.id.avp_pager_btn_activity).setOnClickListener(this);

        mAdapter = new ImagePagerAdapter(Arrays.asList(TEST_IMG_SCENE));
        mViewPager.setAdapter(mAdapter);
        mFlipper = ViewPagerAutoFlipper.newInstance(mViewPager).setInterval(2000);
        mFlipper.startAutoFlip();

        mBtnFlip.setText(mFlipper.isAutoFlip() ? "Stop Auto Flip" : "Start Auto Flip");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.avp_pager_btn_flip: {
                if (mFlipper.isAutoFlip()) {
                    mFlipper.stopAutoFlip();
                    mBtnFlip.setText("Start Auto Flip");
                } else {
                    mFlipper.startAutoFlip();
                    mBtnFlip.setText("Stop Auto Flip");
                }
                break;
            }
            case R.id.avp_pager_btn_activity: {
                ImageSelector.of(this).count(9).showCamera(false).start(new ActivityResultCallback<ArrayList<String>>() {
                    @Override
                    public void onActivityResult(int requestCode, ArrayList<String> result) {
                        List<String> uris = new ArrayList<>(result.size());
                        for (String path : result) {
                            uris.add(Uri.parse("file://" + path).toString());
                        }
                        mAdapter = new ImagePagerAdapter(uris);
                        mViewPager.setAdapter(mAdapter);
                        mFlipper.resetAutoFlip();
                    }
                });
                break;
            }
        }
    }

    public class ImagePagerAdapter extends PagerAdapter {

        private List<String> mList;
        private ArrayList<ImageView> mImageViews;

        public ImagePagerAdapter(List<String> list) {
            mList = list;
            mImageViews = new ArrayList<>(mList.size());
        }

        @Override
        public int getCount() {
            return mList != null ? mList.size() : 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mImageViews.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView imageView = new ImageView(container.getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImageViews.add(imageView);
            container.addView(imageView, 0);

            String imgUri = mList.get(position);
            ImageLoader.getInstance().displayImage(imgUri, imageView);

            return imageView;
        }
    }

}
