package lx.af.demo.activity;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import lx.af.demo.base.BaseDemoActivity;

/**
 * author: lx
 * date: 15-12-16
 */
public class ActivityViewPager extends BaseDemoActivity implements
        BaseDemoActivity.ActionBarImpl {



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
