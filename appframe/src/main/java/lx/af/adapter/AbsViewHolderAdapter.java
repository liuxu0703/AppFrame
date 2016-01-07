package lx.af.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

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
        AbsViewHolder<T> vh;
        if (convertView == null) {
            convertView = createItemView(context);
            vh = createViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (AbsViewHolder<T>) convertView.getTag();
        }
        vh.setData(getItem(position));
        return convertView;
    }

    /**
     * create item view for {@link BaseAdapter#getView(int, View, ViewGroup)}
     */
    public abstract View createItemView(Context context);

    /**
     * create view holder for item view returned by {@link #createItemView(Context)}
     */
    public abstract AbsViewHolder<T> createViewHolder(View itemView);

}
