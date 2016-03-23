package lx.af.demo.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import lx.af.adapter.AbsViewHolder;
import lx.af.demo.R;
import lx.af.demo.model.DanmakuModel;
import lx.af.demo.utils.ImageLoaderHelper;
import lx.af.manager.GlobalThreadManager;
import lx.af.utils.ViewInject.ViewInject;
import lx.af.widget.DanmakuLayout.DanmakuLayout;
import lx.af.widget.DanmakuLayout.DanmakuSimpleAdapter;

/**
 * author: lx
 * date: 16-3-23
 */
public class DanmakuAdapter extends DanmakuSimpleAdapter<DanmakuModel> {

    private Random mRandom = new Random();
    private Context mContext;

    public DanmakuAdapter(Context context) {
        mContext = context;
    }

    @Override
    public View getView(DanmakuModel data, View convertView, DanmakuLayout parent) {
        DanmakuViewHolder vh;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_post_list_danmaku, null);
            vh = new DanmakuViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (DanmakuViewHolder) convertView.getTag();
        }
        vh.setData(data);
        return convertView;
    }

    @Override
    public long getDuration(DanmakuModel data) {
        return mRandom.nextInt(2500) + 2500; // 2500 - 5500 millis
    }

    @Override
    public void onDataEmpty() {
        GlobalThreadManager.runInThreadPool(new Runnable() {
            @Override
            public void run() {
                final List<DanmakuModel> list = DanmakuModel.createRandomList(16);
                GlobalThreadManager.runInUiThreadDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addData(list);
                    }
                }, 500);
            }
        });
    }


    private static class DanmakuViewHolder extends AbsViewHolder<DanmakuModel> {

        @ViewInject(id = R.id.item_program_topic_danmaku_avatar)
        ImageView avatar;
        @ViewInject(id = R.id.item_program_topic_danmaku_text)
        TextView text;

        public DanmakuViewHolder(View root) {
            super(root);
        }

        @Override
        public void setData(DanmakuModel data) {
            ImageLoaderHelper.displayAvatar(avatar, data.avatar);
            text.setText(data.content);
        }
    }

}
