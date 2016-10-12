package lx.af.activity.ImageBrowser;

import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.util.List;
import java.util.Map;

import lx.af.R;
import lx.af.activity.ImageBrowser.ImageBrowserActivity.ImageInfo;
import lx.af.utils.UIL.UILLoader;
import lx.af.view.photoview.PhotoView;
import lx.af.view.photoview.PhotoViewAttacher.OnViewTapListener;
import lx.af.widget.ProgressPieView;

/**
 * author: lx
 * date: 15-12-19
 */
class ImagePagerAdapter extends PagerAdapter implements
        OnViewTapListener,
        View.OnLongClickListener {

    private final List<String> mUriList;
    private Map<String, ImageInfo> mImgInfoMap;
    private LoadImageCallback mLoadImageCallback;
    private ClickImageCallback mClickImageCallback;

    public ImagePagerAdapter(List<String> resources, Map<String, ImageInfo> infoMap) {
        this.mUriList = resources;
        this.mImgInfoMap = infoMap;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        String uri = mUriList.get(position);
        View itemView = View.inflate(container.getContext(), R.layout.image_browser_item, null);
        ProgressPieView progress = (ProgressPieView)
                itemView.findViewById(R.id.image_browser_item_loading);
        PhotoView photoView = (PhotoView)
                itemView.findViewById(R.id.image_browser_item_image);
        photoView.setOnViewTapListener(this);
        photoView.setOnLongClickListener(this);
        photoView.setTag(uri);
        LoadListener listener = new LoadListener(progress, uri, mLoadImageCallback);
        UILLoader.of(photoView, uri)
                .preloadImageUri(mImgInfoMap.get(uri).preloadUri)
                .imageOnFail(R.drawable.img_gallery_default)
                .imageForEmptyUri(R.drawable.img_gallery_default)
                .setProgressListener(listener)
                .setLoadListener(listener)
                .display();
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

    private class LoadListener implements ImageLoadingListener, ImageLoadingProgressListener {

        ProgressPieView progress;
        String uri;
        LoadImageCallback callback;

        public LoadListener(ProgressPieView progress, String uri, LoadImageCallback c) {
            this.progress = progress;
            this.uri = uri;
            this.callback = c;
        }

        @Override
        public void onLoadingStarted(final String imageUri, View view) {
            progress.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (uri.equals(imageUri)) {
                        ImageInfo info = mImgInfoMap.get(imageUri);
                        if (info.valid == ImageBrowserActivity.ImageValidation.UNKNOWN) {
                            progress.setVisibility(View.VISIBLE);
                        } else {
                            progress.setVisibility(View.GONE);
                        }
                    }
                }
            }, 300);
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            if (uri.equals(imageUri)) {
                progress.setVisibility(View.GONE);
            }
            if (callback != null) {
                callback.onImageLoadComplete(imageUri, false);
            }
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (uri.equals(imageUri)) {
                progress.setVisibility(View.GONE);
            }
            if (callback != null) {
                callback.onImageLoadComplete(imageUri, true);
            }
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            if (uri.equals(imageUri)) {
                progress.setVisibility(View.GONE);
            }
            if (callback != null) {
                callback.onImageLoadComplete(imageUri, false);
            }
        }

        @Override
        public void onProgressUpdate(String imageUri, View view, int current, int total) {
            if (uri.equals(imageUri)) {
                int percentage = current * 100 / total;
                //Log.d("liuxu", "111 image browser progress" +
                //        ", percentage=" + percentage +
                //        ", current=" + current + ", total=" + total);
                if (percentage > 100) {
                    percentage = 100;
                }
                progress.setProgress(percentage);
                //progress.setText(percentage + "%");
            }
        }
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
