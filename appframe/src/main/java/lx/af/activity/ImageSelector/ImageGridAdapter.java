package lx.af.activity.ImageSelector;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import lx.af.R;
import lx.af.utils.ScreenUtils;
import lx.af.activity.ImageSelector.ImageItemView.OnItemViewClickListener;

/**
 * 图片Adapter
 * Created by Nereo on 2015/4/7.
 *
 * import and modified by liuxu on 2015.04.22
 */
class ImageGridAdapter extends BaseAdapter {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;

    private ImageGridView mGridView;

    private LayoutInflater mInflater;
    private boolean mShowCamera = true;

    private List<ImageModel> mImages = new ArrayList<>();
    private List<ImageModel> mSelectedImages = new ArrayList<>();

    private int mItemSize;
    private GridView.LayoutParams mItemLayoutParams;

    private OnItemClickListener mItemClickListener;

    public ImageGridAdapter(ImageGridView gridView, OnItemClickListener l, boolean showCamera) {
        mGridView = gridView;
        mItemClickListener = l;
        Context context = gridView.getContext();
        mInflater = LayoutInflater.from(context);
        this.mShowCamera = showCamera;
        int gridItemSpace = context.getResources().getDimensionPixelOffset(R.dimen.mis_grid_spacing);
        mItemSize = (ScreenUtils.getScreenWidth() - 2 * gridItemSpace) / 3;
        mItemLayoutParams = new GridView.LayoutParams(mItemSize, mItemSize);
    }

    public void setShowCamera(boolean b) {
        if (mShowCamera == b) return;
        mShowCamera = b;
        notifyDataSetChanged();
    }

    public boolean isShowCamera() {
        return mShowCamera;
    }

    /**
     * 选择某个图片，改变选择状态
     */
    public void select(ImageModel image) {
        if (mSelectedImages.contains(image)) {
            mSelectedImages.remove(image);
        } else {
            mSelectedImages.add(image);
        }
    }

    /**
     * 通过图片路径设置默认选择
     */
    public void setDefaultSelected(ArrayList<String> resultList) {
        mSelectedImages.clear();
        for (String path : resultList) {
            ImageModel image = getImageByPath(path);
            if (image != null) {
                image.selected = true;
                mSelectedImages.add(image);
            }
        }
        if (mSelectedImages.size() > 0) {
            notifyDataSetChanged();
        }
    }

    private ImageModel getImageByPath(String path) {
        if (mImages != null && mImages.size()>0) {
            for (ImageModel image : mImages) {
                if (image.path.equalsIgnoreCase(path)) {
                    return image;
                }
            }
        }
        return null;
    }

    public ArrayList<String> getImageUriList() {
        ArrayList<String> list = new ArrayList<>(mImages.size());
        for (ImageModel image : mImages) {
            list.add(Uri.parse("file://" + image.path).toString());
        }
        return list;
    }

    public ArrayList<String> getSelectedImageUriList() {
        ArrayList<String> list = new ArrayList<>(mSelectedImages.size());
        for (ImageModel image : mSelectedImages) {
            list.add(Uri.parse("file://" + image.path).toString());
        }
        return list;
    }

    /**
     * 设置数据集
     */
    public void setData(List<ImageModel> images) {
        mSelectedImages.clear();
        if (images != null && images.size()>0) {
            mImages = images;
        } else {
            mImages.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (mShowCamera) {
            return position == 0 ? TYPE_CAMERA : TYPE_NORMAL;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        return mShowCamera ? mImages.size() + 1 : mImages.size();
    }

    @Override
    public ImageModel getItem(int i) {
        if (mShowCamera) {
            if (i == 0) {
                return null;
            }
            return mImages.get(i - 1);
        } else {
            return mImages.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        int type = getItemViewType(i);
        if (type == TYPE_CAMERA) {
            view = mInflater.inflate(R.layout.mis_item_camera, viewGroup, false);
            view.setTag(null);
            view.setOnClickListener(mCameraClickListener);
        } else if (type == TYPE_NORMAL) {
            if (view == null || !(view instanceof ImageItemView)) {
                view = new ImageItemView(mGridView, mItemClickListener);
            }
            ImageItemView itemView = (ImageItemView) view;
            itemView.setData(getItem(i));
        }

        /** Fixed View Size */
        GridView.LayoutParams lp = (GridView.LayoutParams) view.getLayoutParams();
        if (lp == null || lp.height != mItemSize) {
            view.setLayoutParams(mItemLayoutParams);
        }

        return view;
    }

    private View.OnClickListener mCameraClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mItemClickListener.onItemCameraClicked();
        }
    };

    public interface OnItemClickListener extends OnItemViewClickListener {
        void onItemCameraClicked();
    }

}
