package lx.af.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import lx.af.R;

/**
 * author: lx
 * date: 16-1-6
 */
public abstract class AbsViewHolderAdapter<T> extends AbsListAdapter<T> {

    public AbsViewHolderAdapter(Context context, List<T> list) {
        super(context, list);
    }

    public AbsViewHolderAdapter(Context context, T[] arr) {
        super(context, arr);
    }

    public AbsViewHolderAdapter(Context context) {
        super(context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(Context context, int position, View convertView, ViewGroup parent) {
        IViewHolder<T> vh;
        if (convertView == null) {
            vh = createViewHolder(context);
            convertView = vh.getRootView();
            convertView.setTag(R.id.tag_id_view_holder, vh);
        } else {
            vh = (IViewHolder<T>) convertView.getTag(R.id.tag_id_view_holder);
        }
        vh.setData(getItem(position));
        return convertView;
    }

    /**
     * create view holder
     */
    public abstract IViewHolder<T> createViewHolder(Context context);

}
