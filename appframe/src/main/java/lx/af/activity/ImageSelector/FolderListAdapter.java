package lx.af.activity.ImageSelector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import lx.af.R;

class FolderListAdapter extends BaseAdapter {

    private Context mContext;
    private FolderListView mFolderListView;
    private LayoutInflater mInflater;

    private List<FolderModel> mFolders = new ArrayList<>();
    private int mLastSelected = 0;

    public FolderListAdapter(FolderListView folderListView) {
        mContext = folderListView.getContext();
        mFolderListView = folderListView;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<FolderModel> folders) {
        if (folders != null && folders.size() > 0) {
            mFolders = folders;
        } else {
            mFolders.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFolders.size() + 1;
    }

    @Override
    public FolderModel getItem(int i) {
        if (i == 0) return null;
        return mFolders.get(i - 1);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.mis_item_folder, viewGroup, false);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (holder != null) {
            if (i == 0) {
                holder.name.setText(R.string.mis_folder_all);
                holder.size.setText(mContext.getString(
                        R.string.mis_folder_img_amount, getTotalImageSize()));
                if (mFolders.size() > 0) {
                    FolderModel f = mFolders.get(0);
                    displayImage(holder.cover, f.cover);
                }
            } else {
                holder.bindData(getItem(i));
            }
            if (mLastSelected == i) {
                holder.indicator.setVisibility(View.VISIBLE);
            } else {
                holder.indicator.setVisibility(View.GONE);
            }
        }
        return view;
    }

    private int getTotalImageSize() {
        int result = 0;
        if (mFolders != null && mFolders.size()>0) {
            for (FolderModel f: mFolders){
                result += f.images.size();
            }
        }
        return result;
    }

    public void setSelectIndex(int i) {
        if (mLastSelected == i) return;
        mLastSelected = i;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return mLastSelected;
    }

    private void displayImage(ImageView imageView, ImageModel image) {
        if (mFolderListView.isScrolling()) {
            // avoid decode bitmap when scrolling
            imageView.setImageResource(R.drawable.img_gallery_default);
        } else {
            String imgUri = image.getDisplayUri();
            ImageLoader.getInstance().displayImage(
                    imgUri, imageView, ImageOptions.getDisplayImageOptions());
        }
    }

    class ViewHolder {
        ImageView cover;
        TextView name;
        TextView size;
        ImageView indicator;
        ViewHolder(View view) {
            cover = (ImageView)view.findViewById(R.id.mis_item_folder_cover);
            name = (TextView) view.findViewById(R.id.mis_item_folder_name);
            size = (TextView) view.findViewById(R.id.mis_item_folder_img_amount);
            indicator = (ImageView) view.findViewById(R.id.mis_item_folder_check);
            view.setTag(this);
        }

        void bindData(FolderModel data) {
            name.setText(data.name);
            size.setText(mContext.getString(
                    R.string.mis_folder_img_amount, data.images.size()));
            displayImage(cover, data.cover);
        }
    }

}
