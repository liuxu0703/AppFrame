package lx.af.demo.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import lx.af.demo.R;
import lx.af.demo.model.DanmakuModel1;
import lx.af.demo.utils.ImageLoaderHelper;
import lx.af.manager.GlobalThreadManager;
import lx.af.widget.DanmakuLayout.DanmakuLayout;
import lx.af.widget.DanmakuLayout.DanmakuSimpleAdapter;

/**
 * author: lx
 * date: 16-3-23
 */
public class DanmakuDemoAdapter2 extends DanmakuSimpleAdapter<DanmakuModel1> {

    private Random mRandom = new Random();
    private Context mContext;

    public DanmakuDemoAdapter2(Context context) {
        mContext = context;
    }

    @Override
    public View getView(DanmakuModel1 data, View convertView, DanmakuLayout parent) {
        DanmakuViewHolder vh;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_danmaku_1, null);
            vh = new DanmakuViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (DanmakuViewHolder) convertView.getTag();
        }
        vh.setData(data);
        return convertView;
    }

    @Override
    public long getDuration(DanmakuModel1 data) {
        return mRandom.nextInt(2500) + 2500; // 2500 - 5500 millis
    }

    @Override
    public void onDataEmpty() {
        GlobalThreadManager.runInThreadPool(new Runnable() {
            @Override
            public void run() {
                final List<DanmakuModel1> list = DanmakuModel1.createRandomList(16);
                GlobalThreadManager.runInUiThreadDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addData(list);
                    }
                }, 500);
            }
        });
    }


    private static class DanmakuViewHolder {

        ImageView avatar;
        TextView text;

        public DanmakuViewHolder(View root) {
            avatar = (ImageView) root.findViewById(R.id.item_danmaku_avatar);
            text = (TextView) root.findViewById(R.id.item_danmaku_text);
        }

        public void setData(DanmakuModel1 data) {
            ImageLoaderHelper.displayAvatar(avatar, data.avatar);
            text.setText(data.content);
        }
    }

}
