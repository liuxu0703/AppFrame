package lx.af.demo.adapter;

import android.content.Context;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lx.af.demo.R;
import lx.af.demo.model.DanmakuModel1;
import lx.af.demo.model.DanmakuModel2;
import lx.af.demo.model.DanmakuModel3;
import lx.af.demo.utils.ImageLoaderHelper;
import lx.af.manager.GlobalThreadManager;
import lx.af.utils.Animation.interpolator.FlashHoverInterpolator;
import lx.af.widget.DanmakuLayout.DanmakuBaseAdapter;
import lx.af.widget.DanmakuLayout.DanmakuLayout;

/**
 * author: lx
 * date: 16-3-23
 */
public class DanmakuDemoAdapter1 extends DanmakuBaseAdapter<DanmakuDemoAdapter1.ItemWrapper> {

    private static Random sRandom = new Random();
    private Context mContext;

    public DanmakuDemoAdapter1(Context context) {
        mContext = context;
    }

    @Override
    public View getView(ItemWrapper data, View convertView, DanmakuLayout parent) {
        int type = getViewType(data);
        if (type == 1) {
            ViewHolder1 vh;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_danmaku_1, null);
                vh = new ViewHolder1(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder1) convertView.getTag();
            }
            vh.setData(data.d1);
        } else if (type == 2) {
            ViewHolder2 vh;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_danmaku_2, null);
                vh = new ViewHolder2(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder2) convertView.getTag();
            }
            vh.setData(data.d2);
        } else if (type == 3) {
            ViewHolder3 vh;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_danmaku_3, null);
                vh = new ViewHolder3(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder3) convertView.getTag();
            }
            vh.setData(data.d3);
        } else if (type == 4) {
            ViewHolder4 vh;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_danmaku_4, null);
                vh = new ViewHolder4(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder4) convertView.getTag();
            }
            vh.setData(data.d4);
        }

        return convertView;
    }

    @Override
    public long getDuration(ItemWrapper data) {
        if (data.d4 != null) {
            return 8000;
        } else {
            return sRandom.nextInt(8000) + 2000;
        }
    }

    @Override
    public Interpolator getInterpolator(ItemWrapper data) {
        if (data.d4 != null) {
            return new FlashHoverInterpolator(0.7f);
        } else {
            return null;
        }
    }

    @Override
    public int getViewType(ItemWrapper data) {
        if (data.d1 != null) {
            return 1;
        } else if (data.d2 != null) {
            return 2;
        } else if (data.d3 != null) {
            return 3;
        } else if (data.d4 != null) {
            return 4;
        }
        return 0;
    }

    @Override
    public void onDataEmpty() {
        loadData();
    }

    public void loadData() {
        GlobalThreadManager.runInThreadPool(new Runnable() {
            @Override
            public void run() {
                final List<ItemWrapper> list = ItemWrapper.createRandomList(16);
                GlobalThreadManager.runInUiThreadDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addData(list);
                    }
                }, 500);
            }
        });
    }


    private static class ViewHolder1 {

        ImageView avatar;
        TextView text;

        public ViewHolder1(View root) {
            avatar = (ImageView) root.findViewById(R.id.item_danmaku_avatar);
            text = (TextView) root.findViewById(R.id.item_danmaku_text);
        }

        public void setData(DanmakuModel1 data) {
            ImageLoaderHelper.displayAvatar(avatar, data.avatar);
            text.setText(data.content);
        }
    }

    private static class ViewHolder2 {

        ImageView avatar1;
        ImageView avatar2;
        ImageView avatar3;

        public ViewHolder2(View root) {
            avatar1 = (ImageView) root.findViewById(R.id.item_danmaku_avatar1);
            avatar2 = (ImageView) root.findViewById(R.id.item_danmaku_avatar2);
            avatar3 = (ImageView) root.findViewById(R.id.item_danmaku_avatar3);
        }

        public void setData(DanmakuModel2 data) {
            ImageLoaderHelper.displayAvatar(avatar1, data.avatar1);
            ImageLoaderHelper.displayAvatar(avatar2, data.avatar2);
            ImageLoaderHelper.displayAvatar(avatar3, data.avatar3);
        }
    }

    private static class ViewHolder4 {

        ImageView image;

        public ViewHolder4(View root) {
            image = (ImageView) root.findViewById(R.id.item_danmaku_image);
        }

        public void setData(DanmakuModel1 data) {
            ImageLoaderHelper.displayImage(image, data.avatar);
        }
    }

    private static class ViewHolder3 {

        TextView text;

        public ViewHolder3(View root) {
            text = (TextView) root.findViewById(R.id.item_danmaku_text);
        }

        public void setData(DanmakuModel3 data) {
            text.setText(data.content);
        }
    }


    public static class ItemWrapper {

        public DanmakuModel1 d1;
        public DanmakuModel2 d2;
        public DanmakuModel3 d3;
        public DanmakuModel1 d4;

        public static ItemWrapper createRandom() {
            ItemWrapper item = new ItemWrapper();
            int type = sRandom.nextInt(3);
            if (type == 0) {
                item.d1 = DanmakuModel1.createRandom();
            } else if (type == 1) {
                item.d2 = DanmakuModel2.createRandom();
            } else {
                item.d3 = DanmakuModel3.createRandom();
            }
            return item;
        }

        public static List<ItemWrapper> createRandomList(int length) {
            ArrayList<ItemWrapper> list = new ArrayList<>(length);
            for (int i = 0; i < length; i ++) {
                list.add(createRandom());
            }
            return list;
        }

    }

}
