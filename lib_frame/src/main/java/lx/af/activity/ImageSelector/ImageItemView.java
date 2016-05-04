package lx.af.activity.ImageSelector;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.FailReason;

import lx.af.R;
import lx.af.utils.ScreenUtils;
import lx.af.utils.UIL.ListenerAdapter;
import lx.af.utils.UIL.UILLoader;
import lx.af.utils.log.Log;

/**
 * Created by liuxu on 15-5-13.
 * adapter item view for ImageGridView.
 */
@SuppressLint("ViewConstructor")
class ImageItemView extends FrameLayout implements View.OnClickListener {

    private static final int MAX_SIZE = ScreenUtils.dip2px(64);

    private ImageGridView mGridView;
    private ImageView mImage;
    private ImageView mCheck;

    private ImageModel mData;
    private OnItemImageClickListener mClickListener;

    public ImageItemView(ImageGridView gridView, OnItemImageClickListener listener) {
        super(gridView.getContext());
        mGridView = gridView;
        mClickListener = listener;
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.mis_item_image, this);
        mImage = (ImageView) findViewById(R.id.mis_item_img_image);
        mCheck = (ImageView) findViewById(R.id.mis_item_img_check);
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
        UILLoader.of(mImage, imgUri)
                .imageDefault(R.drawable.img_gallery_default)
                .maxSize(MAX_SIZE, MAX_SIZE)
                .delayBeforeLoading(mGridView.isScrolling() ? 200 : 0)
                .animateFloatIn()
                .setLoadListener(new ListenerAdapter() {
                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        Log.w("liuxu", "image selector load image fail " + failReason);
                        if (mData != null && mData.getDisplayUri().equals(imageUri)) {
                            mData.invalid = true;
                        }
                    }
                })
                .display();
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

    interface OnItemImageClickListener {
        void onItemCheckClicked(ImageItemView view, ImageModel data);
        void onItemImageClicked(ImageItemView view, ImageModel data);
    }
}
