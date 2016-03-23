package lx.af.widget;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import lx.af.R;
import lx.af.activity.ImageEditor.ImageEditorActivity;
import lx.af.base.AbsBaseActivity;
import lx.af.utils.ActivityLauncher.ActivityResultCallback;
import lx.af.utils.ActivityLauncher.ImageBrowserLauncher;
import lx.af.utils.ActivityLauncher.ImageSelectorLauncher;
import lx.af.utils.ActivityLauncher.SimpleStringLauncher;
import lx.af.utils.ScreenUtils;
import lx.af.widget.FlowLayout.FlowLayout;

/**
 * author: lx
 * date: 16-3-19
 */
public class SelectImageWidget extends FlowLayout {

    private static final int DEFAULT_IMAGE_MARGIN = ScreenUtils.dip2px(3);
    private static final int DEFAULT_MAX_COUNT = 9;

    private View mAddView;

    private int mMaxCount;
    private int mImageSize;
    private int mImageMargin;

    private ArrayList<String> mPathList = new ArrayList<>();
    private HashMap<String, ImageView> mImageViewMap = new HashMap<>();
    private LinkedList<ImageView> mImageViewRecycler = new LinkedList<>();

    private ImageClickListener mImageClickListener = new ImageClickListener();
    private ImageListChangeListener mChangeListener;

    public SelectImageWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public SelectImageWidget(Context context) {
        super(context);
        initView(context, null);
    }

    public void setMaxImageCount(int count) {
        mMaxCount = count;
    }

    public int getMaxImageCount() {
        return mMaxCount;
    }

    public void setAddView(View addView) {
        if (mAddView != null) {
            addView.setVisibility(mAddView.getVisibility());
        }
        if (mAddView != null && mAddView.getParent() == this) {
            addView.setLayoutParams(createLayoutParams());
            removeView(mAddView);
            addView(addView);
        } else if (mImageSize != 0) {
            addView.setLayoutParams(createLayoutParams());
            addView(addView);
        }
        mAddView = addView;
    }

    public void setImageListChangeListener(ImageListChangeListener l) {
        mChangeListener = l;
    }

    public void setItemClickListener(ItemClickListener l) {
        mItemClickListener = l;
    }

    @NonNull
    public ArrayList<String> getImagePathList() {
        return mPathList;
    }

    public void resetImagePathList(List<String> pathList) {
        boolean changed = false;

        // remove views representing path that has been removed
        Iterator<String> it = mPathList.iterator();
        while (it.hasNext()) {
            String path = it.next();
            if (!pathList.contains(path)) {
                // the path has been removed, remove the view
                ImageView img = mImageViewMap.get(path);
                if (img == null) {
                    continue;
                }
                removeView(img);
                it.remove();
                mImageViewMap.remove(path);
                mImageViewRecycler.add(img);
                changed = true;
            }
        }

        // add views representing path that has currently been added
        it = pathList.iterator();
        while (it.hasNext()) {
            String path = it.next();
            if (!mPathList.contains(path)) {
                // the path has been currently added, add view for it
                ImageView img = getImageView();
                img.setTag(path);
                addView(img, mPathList.size());
                ImageLoader.getInstance().displayImage("file://" + path, img);
                mPathList.add(path);
                mImageViewMap.put(path, img);
                changed = true;
            }
        }

        mAddView.setVisibility(mPathList.size() < mMaxCount ? View.VISIBLE : View.GONE);

        if (changed) {
            notifyImageListChanged();
        }
    }

    public void addImagePathList(List<String> pathList) {
        if (pathList == null || pathList.size() == 0) {
            return;
        }

        boolean changed = false;
        for (int i = 0; i < pathList.size(); i ++) {
            if (mPathList.size() >= mMaxCount) {
                break;
            }

            String path = pathList.get(i);
            ImageView img = getImageView();
            img.setTag(path);
            addView(img, mPathList.size());
            ImageLoader.getInstance().displayImage("file://" + path, img);
            mPathList.add(path);
            mImageViewMap.put(path, img);
            changed = true;
        }

        mAddView.setVisibility(mPathList.size() < mMaxCount ? View.VISIBLE : View.GONE);
        if (changed) {
            notifyImageListChanged();
        }
    }

    public void addImagePath(String path) {
        if (mPathList.contains(path)) {
            return;
        }
        ImageView img = getImageView();
        img.setTag(path);
        addView(img, mPathList.size());
        ImageLoader.getInstance().displayImage("file://" + path, img);
        mPathList.add(path);
        mImageViewMap.put(path, img);

        mAddView.setVisibility(mPathList.size() < mMaxCount ? View.VISIBLE : View.GONE);
        notifyImageListChanged();
    }

    public void replaceImagePath(String originPath, String newPath) {
        ImageView img = mImageViewMap.get(originPath);
        if (img == null) {
            return;
        }
        int idx = mPathList.indexOf(originPath);
        mPathList.add(idx, newPath);
        mPathList.remove(originPath);
        mImageViewMap.remove(originPath);
        mImageViewMap.put(newPath, img);
        img.setTag(newPath);
        ImageLoader.getInstance().displayImage("file://" + newPath, img);
        notifyImageListChanged();
    }

    public void removeImagePath(String path) {
        ImageView img = mImageViewMap.get(path);
        if (img == null) {
            return;
        }

        removeView(img);
        mPathList.remove(path);
        mImageViewMap.remove(path);
        mImageViewRecycler.add(img);

        mAddView.setVisibility(mPathList.size() < mMaxCount ? View.VISIBLE : View.GONE);
        notifyImageListChanged();
    }

    public void startSelectImage() {
        startImageSelector();
    }

    // ==============================================

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != 0 && mImageSize == 0) {
            int screenWidth = ScreenUtils.getScreenWidth();
            int paddingHorizontal = getPaddingLeft() + getPaddingRight();
            if (w > (screenWidth * 4 / 5)) {
                int count = mMaxCount < 5 ? 4 : 5;
                mImageSize = (w - mImageMargin * count * 2 - paddingHorizontal) / count;
            } else {
                mImageSize = (screenWidth - mImageMargin * 10 - paddingHorizontal) / 5;
            }
            if (mAddView == null) {
                mAddView = createDefaultAddView();
                addView(mAddView);
            } else if (mAddView.getParent() != this) {
                addView(mAddView);
            }
        }
    }

    private void initView(Context context, AttributeSet attrs) {
        setLayoutTransition(new LayoutTransition());

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SelectImageWidget);
            mImageSize = a.getDimensionPixelSize(R.styleable.SelectImageWidget_selectImageItemSize, 0);
            mImageMargin = a.getDimensionPixelSize(R.styleable.SelectImageWidget_selectImageItemMargin, DEFAULT_IMAGE_MARGIN);
            mMaxCount = a.getInteger(R.styleable.SelectImageWidget_selectImageMaxCount, DEFAULT_MAX_COUNT);
            a.recycle();
        } else {
            mImageMargin = DEFAULT_IMAGE_MARGIN;
            mMaxCount = DEFAULT_MAX_COUNT;
        }
    }

    private ImageView getImageView() {
        if (mImageViewRecycler.size() != 0) {
            return mImageViewRecycler.pop();
        }
        if (mImageMargin == 0) {
            mImageMargin = ScreenUtils.dip2px(4);
        }
        if (mImageSize == 0) {
            mImageSize = (ScreenUtils.getScreenWidth() - 6 * mImageMargin) / 5;
        }
        ImageView img = new ImageView(getContext());
        img.setLayoutParams(createLayoutParams());
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        img.setOnClickListener(mImageClickListener);
        img.setOnLongClickListener(mImageClickListener);
        return img;
    }

    private View createDefaultAddView() {
        if (mImageMargin == 0) {
            mImageMargin = ScreenUtils.dip2px(4);
        }
        if (mImageSize == 0) {
            mImageSize = (ScreenUtils.getScreenWidth() - 6 * mImageMargin) / 5;
        }
        ImageView img = new ImageView(getContext());
        img.setLayoutParams(createLayoutParams());
        img.setImageResource(R.drawable.ic_image_add);
        img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onImageAddClicked(SelectImageWidget.this, mAddView);
            }
        });
        return img;
    }

    private MarginLayoutParams createLayoutParams() {
        MarginLayoutParams params = new MarginLayoutParams(mImageSize, mImageSize);
        params.setMargins(mImageMargin, mImageMargin, mImageMargin, mImageMargin);
        return params;
    }

    private void notifyImageListChanged() {
        if (mChangeListener != null) {
            ArrayList<String> list = new ArrayList<>(mPathList.size());
            list.addAll(mPathList);
            mChangeListener.onImageListChanged(list);
        }
    }

    private void showMenuDialog(final String imagePath) {
        String[] menu = new String[] {
                getResources().getString(R.string.add_image_widget_menu_doodle),
                getResources().getString(R.string.add_image_widget_menu_delete),
        };

        new AlertDialog.Builder(getContext())
                .setItems(menu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            startImageEditor(imagePath);
                        } else if (which == 1) {
                            removeImagePath(imagePath);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create().show();
    }

    private void startImageEditor(final String imagePath) {
        AbsBaseActivity activity = (AbsBaseActivity) getContext();
        SimpleStringLauncher
                .of(activity, ImageEditorActivity.class, ImageEditorActivity.EXTRA_RESULT)
                .putExtra(ImageEditorActivity.EXTRA_PATH, imagePath)
                .start(new ActivityResultCallback<String>() {
                    @Override
                    public void onActivityResult(int resultCode, @NonNull String result) {
                        replaceImagePath(imagePath, result);
                    }
                });
    }

    private void startImageSelector() {
        AbsBaseActivity activity = (AbsBaseActivity) getContext();
        ImageSelectorLauncher.of(activity)
                .count(mMaxCount)
                .preSelect(mPathList)
                .start(new ActivityResultCallback<ArrayList<String>>() {
                    @Override
                    public void onActivityResult(int resultCode, @NonNull ArrayList<String> result) {
                        resetImagePathList(result);
                    }
                });
    }


    private ItemClickListener mItemClickListener = new ItemClickListener() {

        @Override
        public void onImageAddClicked(SelectImageWidget container, View addView) {
            startImageSelector();
        }

        @Override
        public void onImageClicked(SelectImageWidget container, ImageView imageView, @NonNull String path) {
            ImageBrowserLauncher.of(getContext())
                    .tapExit(true)
                    .paths(container.getImagePathList())
                    .currentPath(path)
                    .currentView(imageView)
                    .start();
        }

        @Override
        public boolean onImageLongClicked(SelectImageWidget container, ImageView imageView, @NonNull String path) {
            showMenuDialog(path);
            return true;
        }
    };


    private class ImageClickListener implements OnClickListener, OnLongClickListener {

        @Override
        public void onClick(View v) {
            String path = (String) v.getTag();
            mItemClickListener.onImageClicked(SelectImageWidget.this, (ImageView) v, path);
        }

        @Override
        public boolean onLongClick(View v) {
            String path = (String) v.getTag();
            return mItemClickListener.onImageLongClicked(SelectImageWidget.this, (ImageView) v, path);
        }
    }


    public interface ItemClickListener {
        void onImageAddClicked(SelectImageWidget container, View addView);
        void onImageClicked(SelectImageWidget container, ImageView imageView, @NonNull String path);
        boolean onImageLongClicked(SelectImageWidget container, ImageView imageView, @NonNull String path);
    }

    public interface ImageListChangeListener {
        void onImageListChanged(@NonNull ArrayList<String> imageList);
    }

}
