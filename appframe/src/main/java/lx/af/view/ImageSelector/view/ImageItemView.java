package lx.af.view.ImageSelector.view;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import lx.af.R;
import lx.af.view.ImageSelector.bean.Image;

/**
 * Created by liuxu on 15-5-13.
 * adapter item view for ImageGridView.
 */
public class ImageItemView extends FrameLayout implements View.OnClickListener {

    private static final ImageSize IMAGE_SIZE = new ImageSize(100, 100);

    private ImageGridView mGridView;
    private ImageView mImage;
    private ImageView mCheck;
    private View mWrapper;

    private Image mData;
    private OnItemViewClickListener mClickListener;

    private ImageLoadingListener mImageLoadListener = new ImageLoadingListener() {
        @Override
        public void onLoadingStarted(String imageUri, View view) {
        }
        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            if (mData != null && mData.getDisplayUri().equals(imageUri)) {
                mData.invalid = true;
            }
        }
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        }
        @Override
        public void onLoadingCancelled(String imageUri, View view) {
        }
    };

    public ImageItemView(ImageGridView gridView, OnItemViewClickListener listener) {
        super(gridView.getContext());
        mGridView = gridView;
        mClickListener = listener;
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.mis_item_image, this);
        mImage = (ImageView) findViewById(R.id.mis_item_img_image);
        mCheck = (ImageView) findViewById(R.id.mis_item_img_check);
        mWrapper = findViewById(R.id.mis_item_img_wrapper);

        mImage.setOnClickListener(this);
        mCheck.setOnClickListener(this);
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
        ImageLoader.getInstance().displayImage(
                imgUri, new ImageViewAware(mImage), options, IMAGE_SIZE, mImageLoadListener, null);
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

    @Override
    public void onClick(View v) {
        if (mData.invalid) {
            Toast.makeText(getContext(),
                    R.string.mis_toast_invalid_image, Toast.LENGTH_SHORT).show();
            return;
        }

        int id = v.getId();
        if (id == R.id.mis_item_img_image) {
            mClickListener.onItemImageClicked(this, mData);
        } else if (id == R.id.mis_item_img_check) {
            mClickListener.onItemCheckClicked(this, mData);
        }
    }

    public interface OnItemViewClickListener {
        void onItemCheckClicked(ImageItemView view, Image data);
        void onItemImageClicked(ImageItemView view, Image data);
    }
}
