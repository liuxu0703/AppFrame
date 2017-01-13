package lx.af.demo.activity.DemoWidget;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import lx.af.adapter.AbsListAdapter;
import lx.af.demo.R;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseActivity;

/**
 * author: lx
 * date: 16-10-12
 */
public class FolderDialogDemo extends BaseActivity implements
        AdapterView.OnItemClickListener,
        ActionBar.Default {

    @InjectView(R.id.icon_grid)
    GridView mIconGridView;

    private IconAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_dialog);
        ButterKnife.inject(this);
        mAdapter = new IconAdapter(this);
        mIconGridView.setAdapter(mAdapter);
        mIconGridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        IconModel model = mAdapter.getItem(position);

    }


    private static class IconAdapter extends AbsListAdapter<IconModel> {

        public IconAdapter(Context context) {
            super(context, IconModel.createRandomList());
        }

        @Override
        public View getView(Context context, int position, View convertView, ViewGroup parent) {
            ImageView image;
            if (convertView == null) {
                image = new ImageView(context);
                image.setScaleType(ImageView.ScaleType.CENTER);
                convertView = image;
            } else {
                image = (ImageView) convertView;
            }
            IconModel icon = getItem(position);
            image.setImageResource(icon.mResId);
            image.setTag(icon.mMenuCount);
            return convertView;
        }
    }


    private static class IconModel {

        private static Random sRandom = new Random();

        int mResId;
        int mMenuCount;

        IconModel() {}

        static IconModel createRandom() {
            IconModel model = new IconModel();
            model.mResId = R.drawable.ic_star_on;
            model.mMenuCount = sRandom.nextInt(15) + 1;
            return model;
        }

        static List<IconModel> createRandomList() {
            ArrayList<IconModel> list = new ArrayList<>(12);
            for (int i = 0; i < 12; i ++) {
                list.add(createRandom());
            }
            return list;
        }

    }

}
