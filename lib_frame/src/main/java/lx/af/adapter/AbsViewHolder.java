package lx.af.adapter;

import android.view.View;

import lx.af.utils.ViewInject.ViewInject;
import lx.af.utils.ViewInject.ViewInjectUtils;

/**
 * author: lx
 * date: 16-1-6
 *
 * view holder base, support view inject {@link ViewInject}
 */
public abstract class AbsViewHolder<T> {

    private View mRoot;

    public AbsViewHolder(View root) {
        mRoot = root;
        ViewInjectUtils.inject(AbsViewHolder.class, this, mRoot);
    }

    public abstract void setData(T data);

    public View getRootView() {
        return mRoot;
    }

    @SuppressWarnings("unchecked")
    public  <E extends View> E obtainView(int id) {
        return (E) mRoot.findViewById(id);
    }
}
