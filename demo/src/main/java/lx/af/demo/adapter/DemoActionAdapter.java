package lx.af.demo.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.List;

import lx.af.adapter.AbsViewHolder;
import lx.af.adapter.AbsViewHolderAdapter;
import lx.af.demo.R;
import lx.af.demo.consts.ActionModel;
import lx.af.utils.ViewInject.ViewInject;
import lx.af.widget.iconify.widget.IconTextView;

/**
 * author: lx
 * date: 16-3-19
 */
public class DemoActionAdapter extends AbsViewHolderAdapter<ActionModel> implements
        AdapterView.OnItemClickListener {


    public DemoActionAdapter(Context context, List<ActionModel> list) {
        super(context, list);
    }

    public DemoActionAdapter(Context context, ActionModel[] arr) {
        super(context, arr);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ActionModel model = getItem(position);
        Intent intent = new Intent(getContext(), model.activity);
        getContext().startActivity(intent);
    }

    @Override
    public View createItemView(Context context) {
        return View.inflate(context, R.layout.item_main_tab_list, null);
    }

    @Override
    public AbsViewHolder<ActionModel> createViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }


    public static class ViewHolder extends AbsViewHolder<ActionModel> {

        @ViewInject(id = R.id.item_main_tab_list_icon)
        public IconTextView mIconView;
        @ViewInject(id = R.id.item_main_tab_list_title)
        public TextView mTitleView;
        @ViewInject(id = R.id.item_main_tab_list_sub_title)
        public TextView mSubTitleView;

        public ViewHolder(View root) {
            super(root);
        }

        @Override
        public void setData(ActionModel data) {
            mIconView.setText(data.icon);
            mTitleView.setText(data.title);
            mSubTitleView.setText(data.sub);
            mSubTitleView.setVisibility(TextUtils.isEmpty(data.sub) ? View.GONE : View.VISIBLE);
        }

    }

}
