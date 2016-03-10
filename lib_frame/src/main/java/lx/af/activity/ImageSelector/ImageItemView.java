package lx.af.activity.ImageSelector;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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
import lx.af.utils.ScreenUtils;

/**
 * Created by liuxu on 15-5-13.
 * adapter item view for ImageGridView.
 */
@SuppressLint("ViewConstructor")
class ImageItemView extends FrameLayout implements View.OnClickListener {

    private static final ImageSize IMAGE_SIZE =
            new ImageSize(ScreenUtils.dip2px(64), ScreenUtils.dip2px(64));

    private ImageGridView mGridView;
    private ImageView mImage;
    private ImageView mCheck;
    private View mWrapper;
    private Animation mShowAnim;

    private ImageModel mData;
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
            if (mShowAnim == null) {
                mShowAnim = new AlphaAnimation(0.2f, 1.0f);
                mShowAnim.setDuration(200);
            }
            view.startAnimation(mShowAnim);
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

    public void setData(ImageModel data) {
        if (data == null) {
            return;
        }
        mData = data;
        updateChecked();
        String imgUri = mData.getDisplayUri();
        DisplayImageOptions options = mGridView.isScrolling() ?
                ImageOptions.getScrollImageOptions() : ImageOptions.getDisplayImageOptions();
        ImageLoader.getInstance().displayImage(
                imgUri, new ImageViewAware(mImage, false), options, IMAGE_SIZE,
                mImageLoadListener, null);
    }

    public void toggleCheck() {
        Log.d("liuxu", "111 toggleCheck(), " + mData);
        if (mData == null) {
            return;
        }
        mData.selected = !mData.selected;
        updateChecked();
    }

    private void updateChecked() {
        Log.d("liuxu", "111 updateChecked(), " + mData);
        mCheck.setImageResource(mData.selected ?
                R.drawable.mis_ic_selected : R.drawable.mis_ic_unselected);
        mWrapper.setVisibility(mData.selected ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        Log.d("liuxu", "111 onClick(), " + mData + ", " + v);
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
        void onItemCheckClicked(ImageItemView view, ImageModel data);
        void onItemImageClicked(ImageItemView view, ImageModel data);
    }
}
