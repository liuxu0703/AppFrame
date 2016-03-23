package lx.af.widget.NineGrid;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * author: lx
 * date: 15-12-25
 */
public class NineImageUILAdapter implements NineGridLayout.NineGridAdapter {

    private NineGridLayout mGrid;
    private List<String> mImageUris;

    private OnItemClickListener mOnItemClickListener;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                int position = mGrid.getPositionByView(v);
                mOnItemClickListener.onItemClicked(v, NineImageUILAdapter.this, position);
            }
        }
    };

    public NineImageUILAdapter(NineGridLayout grid) {
        this(grid, null);
    }

    public NineImageUILAdapter(NineGridLayout grid, List<String> imageUris) {
        mGrid = grid;
        setImageUris(imageUris);
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

    public void setImageUris(List<String> imageUris) {
        mImageUris = imageUris;
        if (mImageUris != null && mImageUris.size() != 0) {
            mGrid.setAdapter(this);
        }
    }

    public List<String> getImageUris() {
        return mImageUris;
    }

    public void refreshForAdapterView(List<String> picList) {
        if (picList != null && picList.size() > 0) {
            mGrid.setVisibility(View.VISIBLE);
            setImageUris(picList);
        } else {
            mGrid.setVisibility(View.GONE);
        }
    }

    @Override
    public int getCount() {
        return mImageUris.size();
    }

    @Override
    public String getData(int position) {
        return mImageUris.get(position);
    }

    @Override
    public View initItemView(Context context) {
        ImageView img = new ImageView(context);
        img.setOnClickListener(mOnClickListener);
        return img;
    }

    @Override
    public void displayItemView(View view, int position, int total) {
        ImageView img = (ImageView) view;
        if (total == 1) {
            img.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        ImageLoader.getInstance().displayImage(getData(position), img);
    }


    public interface OnItemClickListener {

        void onItemClicked(View view, NineImageUILAdapter adapter, int position);

    }

}
