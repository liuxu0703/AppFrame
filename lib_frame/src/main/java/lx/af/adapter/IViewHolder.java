package lx.af.adapter;

import android.view.View;

/**
 * author: lx
 * date: 16-1-6
 */
public interface IViewHolder<T> {

    void setData(T data);

    View getRootView();

}
