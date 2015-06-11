package lx.af.view.MultiImageSelector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import lx.af.R;
import lx.af.utils.ScreenUtils;
import lx.af.view.MultiImageSelector.bean.Image;
import lx.af.view.MultiImageSelector.view.ImageGridView;
import lx.af.view.MultiImageSelector.view.ImageItemView;

/**
 * 图片Adapter
 * Created by Nereo on 2015/4/7.
 *
 * import and modified by liuxu on 2015.04.22
 */
public class ImageGridAdapter extends BaseAdapter {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;

    private ImageGridView mGridView;

    private LayoutInflater mInflater;
    private boolean showCamera = true;
    private boolean showSelectIndicator = true;

    private List<Image> mImages = new ArrayList<>();
    private List<Image> mSelectedImages = new ArrayList<>();

    private int mItemSize;
    private GridView.LayoutParams mItemLayoutParams;

    public ImageGridAdapter(ImageGridView gridView, boolean showCamera) {
        mGridView = gridView;
        Context context = gridView.getContext();
        mInflater = LayoutInflater.from(context);
        this.showCamera = showCamera;
        int gridItemSpace = context.getResources().getDimensionPixelOffset(R.dimen.mis_grid_spacing);
        mItemSize = (ScreenUtils.getScreenWidth() - 2 * gridItemSpace) / 3;
        mItemLayoutParams = new GridView.LayoutParams(mItemSize, mItemSize);
    }

    /**
     * 显示选择指示器
     */
    public void showSelectIndicator(boolean b) {
        showSelectIndicator = b;
    }

    public void setShowCamera(boolean b) {
        if (showCamera == b) return;
        showCamera = b;
        notifyDataSetChanged();
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    /**
     * 选择某个图片，改变选择状态
     */
    public void select(Image image) {
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
        for (String path : resultList) {
            Image image = getImageByPath(path);
            if (image != null) {
                mSelectedImages.add(image);
            }
        }
        if (mSelectedImages.size() > 0) {
            notifyDataSetChanged();
        }
    }

    private Image getImageByPath(String path) {
        if (mImages != null && mImages.size()>0) {
            for (Image image : mImages) {
                if (image.path.equalsIgnoreCase(path)) {
                    return image;
                }
            }
        }
        return null;
    }

    /**
     * 设置数据集
     */
    public void setData(List<Image> images) {
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
        if (showCamera) {
            return position==0 ? TYPE_CAMERA : TYPE_NORMAL;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        return showCamera ? mImages.size()+1 : mImages.size();
    }

    @Override
    public Image getItem(int i) {
        if (showCamera) {
            if (i == 0) {
                return null;
            }
            return mImages.get(i-1);
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
        } else if (type == TYPE_NORMAL) {
            if (view == null || !(view instanceof ImageItemView)) {
                view = new ImageItemView(mGridView);
            }
            ImageItemView itemView = (ImageItemView) view;
            itemView.setData(getItem(i));
            itemView.setShowCheck(showSelectIndicator);
        }

        /** Fixed View Size */
        GridView.LayoutParams lp = (GridView.LayoutParams) view.getLayoutParams();
        if (lp == null || lp.height != mItemSize) {
            view.setLayoutParams(mItemLayoutParams);
        }

        return view;
    }

}
