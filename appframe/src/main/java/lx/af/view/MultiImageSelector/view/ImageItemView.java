package lx.af.view.MultiImageSelector.view;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import lx.af.R;
import lx.af.view.MultiImageSelector.bean.Image;

/**
 * Created by liuxu on 15-5-13.
 * adapter item view for ImageGridView.
 */
public class ImageItemView extends FrameLayout {

    private ImageGridView mGridView;
    private ImageView mImage;
    private ImageView mCheck;
    private View mWrapper;

    private Image mData;

    public ImageItemView(ImageGridView gridView) {
        super(gridView.getContext());
        mGridView = gridView;
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.mis_item_image, this);
        mImage = (ImageView) findViewById(R.id.mis_item_img_image);
        mCheck = (ImageView) findViewById(R.id.mis_item_img_check);
        mWrapper = findViewById(R.id.mis_item_img_wrapper);
    }

    public void setData(Image data) {
        if (data == null) {
            return;
        }
        mData = data;
        updateChecked();
        String imgUri = mData.getDisplayUri();
        DisplayImageOptions options = mGridView.isScrolling() ?
                ImageOptions.getScrollImageOptions() : ImageOptions.getDisplayImageOptions();
        ImageLoader.getInstance().displayImage(imgUri, mImage, options);
    }

    public void setShowCheck(boolean showCheck) {
        if (showCheck) {
            mCheck.setVisibility(View.VISIBLE);
        } else {
            mCheck.setVisibility(View.GONE);
        }
    }

    public void toggleCheck() {
        if (mData == null) {
            return;
        }
        mData.selected = !mData.selected;
        updateChecked();
    }

    private void updateChecked() {
        mCheck.setImageResource(mData.selected ?
                R.drawable.mis_ic_selected : R.drawable.mis_ic_unselected);
        mWrapper.setVisibility(mData.selected ? View.VISIBLE : View.GONE);
    }

}
