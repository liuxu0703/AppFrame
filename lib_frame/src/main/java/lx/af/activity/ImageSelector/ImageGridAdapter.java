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
import lx.af.activity.ImageSelector.ImageItemView.OnItemImageClickListener;
import lx.af.utils.ScreenUtils;

/**
 * author: lx
 * date: 15-04-22
 *
 * inspired by https://github.com/lovetuzitong/MultiImageSelector
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

    private OnItemViewClickListener mItemClickListener;

    public ImageGridAdapter(ImageGridView gridView, OnItemViewClickListener l, boolean showCamera) {
        mGridView = gridView;
        mItemClickListener = l;
        Context context = gridView.getContext();
        mInflater = LayoutInflater.from(context);
        this.mShowCamera = showCamera;
        int gridItemSpace = context.getResources().getDimensionPixelOffset(R.dimen.mis_grid_spacing);
        mItemSize = (ScreenUtils.getScreenWidth() - 2 * gridItemSpace) / 3;
    }

    public void setShowCamera(boolean b) {
        if (mShowCamera == b) return;
        mShowCamera = b;
        notifyDataSetChanged();
    }

    public void select(ImageModel image) {
        if (mSelectedImages.contains(image)) {
            mSelectedImages.remove(image);
        } else {
            mSelectedImages.add(image);
        }
    }

    public void setDefaultSelected(ArrayList<String> resultList) {
        mSelectedImages.clear();
        for (ImageModel image : mImages) {
            image.selected = resultList != null && resultList.contains(image.path);
            if (image.selected) {
                mSelectedImages.add(image);
            }
        }
        notifyDataSetChanged();
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        int type = getItemViewType(position);
        if (type == TYPE_CAMERA) {
            if (view == null) {
                view = mInflater.inflate(R.layout.mis_item_camera, viewGroup, false);
                view.setTag(null);
                view.setOnClickListener(mCameraClickListener);
            }
        } else if (type == TYPE_NORMAL) {
            if (view == null) {
                view = new ImageItemView(mGridView, mItemClickListener);
            }
            ImageItemView itemView = (ImageItemView) view;
            itemView.setData(getItem(position));
        }

        /** Fixed View Size */
        GridView.LayoutParams lp = (GridView.LayoutParams) view.getLayoutParams();
        if (lp == null || lp.height != mItemSize) {
            lp = new GridView.LayoutParams(mItemSize, mItemSize);
            view.setLayoutParams(lp);
        }

        return view;
    }

    private View.OnClickListener mCameraClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mItemClickListener.onItemCameraClicked();
        }
    };

    interface OnItemViewClickListener extends OnItemImageClickListener {
        void onItemCameraClicked();
    }

}
