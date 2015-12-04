package lx.af.view.ImageBrowser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import lx.af.R;
import lx.af.view.ProgressWheel;

public class IBImageViewContainer extends RelativeLayout {

    private static DisplayImageOptions sDisplayImageOptions;

    private IBTouchImageView mImageView;
    private ProgressWheel mProgressWheel;
    private LoadImageCallback mLoadImageCallback;

    private ImageLoadingListener mLoadingListener = new ImageLoadingListener() {
        @Override
        public void onLoadingStarted(String imageUri, View view) {}

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            // use ImageView.setImageResource() here will bring some weired problem:
            // TouchImageView needs the origin bitmap width and height, but the calculation is
            // only done in ImageView.setImageBitmap(). call ImageView.setImageResource() and
            // ImageView.setImageUri() will miss the call to ImageView.setImageBitmap().
            // so just use ImageView.setImageBitmap() here.
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_gallery_default);
            mImageView.setImageBitmap(bitmap);
            mImageView.setScaleType(ScaleType.FIT_CENTER);
            mImageView.setVisibility(View.VISIBLE);
            mProgressWheel.setVisibility(View.GONE);

            if (mLoadImageCallback != null) {
                mLoadImageCallback.onImageLoadComplete(imageUri, false);
            }
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            mImageView.setScaleType(ScaleType.MATRIX);
            mImageView.setVisibility(View.VISIBLE);
            mProgressWheel.setVisibility(View.GONE);
            if (mLoadImageCallback != null) {
                mLoadImageCallback.onImageLoadComplete(imageUri, true);
            }
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            if (mLoadImageCallback != null) {
                mLoadImageCallback.onImageLoadComplete(imageUri, false);
            }
        }
    };

    private ImageLoadingProgressListener mProgressListener = new ImageLoadingProgressListener() {
        @Override
        public void onProgressUpdate(String imageUri, View view, int current, int total) {
            int progress = current * 360 / total;
            int percentage = current * 100 / total;
            mProgressWheel.setProgress(progress);
            mProgressWheel.setText(percentage + "%");
        }
    };

	public IBImageViewContainer(Context ctx) {
		super(ctx);
        initView();
	}

	public IBImageViewContainer(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
        initView();
	}

	public IBTouchImageView getImageView() {
		return mImageView;
	}

	protected void initView() {
        this.setBackgroundColor(getResources().getColor(android.R.color.black));
		mImageView = new IBTouchImageView(getContext());
		LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mImageView.setLayoutParams(params);
        mImageView.setBackgroundColor(getResources().getColor(android.R.color.black));
        mImageView.setVisibility(View.GONE);
		this.addView(mImageView);

        int progressSize = getScreenWidth() / 5;
        mProgressWheel = (ProgressWheel) inflate(getContext(), R.layout.image_browser_load_progress, null);
        mProgressWheel.setProgress(0);
        mProgressWheel.setVisibility(View.VISIBLE);
        mProgressWheel.spin();
        LayoutParams rp = new LayoutParams(progressSize, progressSize);
        rp.addRule(CENTER_IN_PARENT);
        this.addView(mProgressWheel, rp);
	}

    public void setLoadImageCallback(LoadImageCallback c) {
        mLoadImageCallback = c;
    }

	public void setUri(String uri) {
        ImageLoader.getInstance().displayImage(
                uri, mImageView, getDisplayImageOptions(),
                mLoadingListener, mProgressListener);
	}

    @Override
    public void setOnClickListener(OnClickListener l) {
        if (mImageView != null) {
            mImageView.setOnClickListener(l);
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

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    public interface LoadImageCallback {
        void onImageLoadComplete(String imgUri, boolean loadSuccess);
    }

}


