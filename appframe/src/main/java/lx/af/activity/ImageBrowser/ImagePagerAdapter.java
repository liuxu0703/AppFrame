package lx.af.activity.ImageBrowser;

import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.util.List;

import lx.af.R;
import lx.af.view.photoview.PhotoView;
import lx.af.view.photoview.PhotoViewAttacher.OnViewTapListener;

/**
 * author: lx
 * date: 15-12-19
 */
class ImagePagerAdapter extends PagerAdapter implements
        OnViewTapListener,
        View.OnLongClickListener,
        ImageLoadingListener,
        ImageLoadingProgressListener {

    private static DisplayImageOptions sDisplayImageOptions;

    private final List<String> mUriList;
    private LoadImageCallback mLoadImageCallback;
    private ClickImageCallback mClickImageCallback;

    public ImagePagerAdapter(List<String> resources) {
        this.mUriList = resources;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        String uri = mUriList.get(position);
        PhotoView photoView = new PhotoView(container.getContext());
        photoView.setOnViewTapListener(this);
        photoView.setOnLongClickListener(this);
        photoView.setTag(uri);
        ImageLoader.getInstance().displayImage(
                uri, photoView, getDisplayImageOptions(), this, this);
        container.addView(photoView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return photoView;
    }

    @Override
    public int getCount() {
        return mUriList.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void onProgressUpdate(String imageUri, View view, int current, int total) {
        if (mLoadImageCallback != null) {
            mLoadImageCallback.onImageLoadProgress(imageUri, current, total);
        }
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {
        if (mLoadImageCallback != null) {
            mLoadImageCallback.onImageLoadStart(imageUri);
        }
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        if (mLoadImageCallback != null) {
            mLoadImageCallback.onImageLoadComplete(imageUri, true);
        }
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        PhotoView p = (PhotoView) view;
        p.setImageResource(R.drawable.img_gallery_default);
        if (mLoadImageCallback != null) {
            mLoadImageCallback.onImageLoadComplete(imageUri, false);
        }
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {
        if (view != null) {
            PhotoView p = (PhotoView) view;
            p.setImageResource(R.drawable.img_gallery_default);
        }
        if (mLoadImageCallback != null) {
            mLoadImageCallback.onImageLoadComplete(imageUri, false);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mClickImageCallback != null) {
            String uri = (String) v.getTag();
            return mClickImageCallback.onImageLongClicked(uri, v);
        } else {
            return false;
        }
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        if (mClickImageCallback != null) {
            String uri = (String) view.getTag();
            mClickImageCallback.onImageClicked(uri, view);
        }
    }

    private static DisplayImageOptions getDisplayImageOptions() {
        if (sDisplayImageOptions == null) {
            sDisplayImageOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
        }
        return sDisplayImageOptions;
    }

    public void setLoadImageCallback(LoadImageCallback c) {
        mLoadImageCallback = c;
    }

    public void setClickImageCallback(ClickImageCallback c) {
        mClickImageCallback = c;
    }

    public interface LoadImageCallback {
        void onImageLoadStart(String imgUri);
        void onImageLoadProgress(String imgUri, int current, int total);
        void onImageLoadComplete(String imgUri, boolean loadSuccess);
    }

    public interface ClickImageCallback {
        void onImageClicked(String imageUri, View view);
        boolean onImageLongClicked(String imageUri, View view);
    }

}
