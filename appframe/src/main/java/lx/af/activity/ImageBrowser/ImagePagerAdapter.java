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
import lx.af.view.ProgressWheel;
import lx.af.view.photoview.PhotoView;
import lx.af.view.photoview.PhotoViewAttacher.OnViewTapListener;

/**
 * author: lx
 * date: 15-12-19
 */
class ImagePagerAdapter extends PagerAdapter implements
        OnViewTapListener,
        View.OnLongClickListener {

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
        View itemView = View.inflate(container.getContext(), R.layout.image_browser_item, null);
        ProgressWheel progress = (ProgressWheel)
                itemView.findViewById(R.id.image_browser_item_loading);
        PhotoView photoView = (PhotoView)
                itemView.findViewById(R.id.image_browser_item_image);
        photoView.setOnViewTapListener(this);
        photoView.setOnLongClickListener(this);
        photoView.setTag(uri);
        LoadListener listener = new LoadListener(progress, uri, mLoadImageCallback);
        ImageLoader.getInstance().displayImage(
                uri, photoView, getDisplayImageOptions(), listener, listener);
        container.addView(itemView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return itemView;
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

    private static class LoadListener implements ImageLoadingListener, ImageLoadingProgressListener {

        ProgressWheel progress;
        String uri;
        LoadImageCallback callback;

        public LoadListener(ProgressWheel progress, String uri, LoadImageCallback c) {
            this.progress = progress;
            this.uri = uri;
            this.callback = c;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            if (uri.equals(imageUri)) {
                progress.setVisibility(View.VISIBLE);
                progress.spin();
            }
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            if (uri.equals(imageUri)) {
                progress.stopSpinning();
                progress.setVisibility(View.GONE);
            }
            if (callback != null) {
                callback.onImageLoadComplete(imageUri, false);
            }
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (uri.equals(imageUri)) {
                progress.stopSpinning();
                progress.setVisibility(View.GONE);
            }
            if (callback != null) {
                callback.onImageLoadComplete(imageUri, true);
            }
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            if (uri.equals(imageUri)) {
                progress.stopSpinning();
                progress.setVisibility(View.GONE);
            }
            if (callback != null) {
                callback.onImageLoadComplete(imageUri, false);
            }
        }

        @Override
        public void onProgressUpdate(String imageUri, View view, int current, int total) {
            if (uri.equals(imageUri)) {
                int pro = current * 360 / total;
                int percentage = current * 100 / total;
                progress.setProgress(pro);
                progress.setText(percentage + "%");
            }
        }
    }

    private static DisplayImageOptions getDisplayImageOptions() {
        if (sDisplayImageOptions == null) {
            sDisplayImageOptions = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.img_gallery_default)
                    .showImageOnFail(R.drawable.img_gallery_default)
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
        void onImageLoadComplete(String imgUri, boolean loadSuccess);
    }

    public interface ClickImageCallback {
        void onImageClicked(String imageUri, View view);
        boolean onImageLongClicked(String imageUri, View view);
    }

}
