package lx.af.adapter;

import android.view.View;

import lx.af.utils.ViewInject.ViewInjectUtils;

/**
 * author: lx
 * date: 16-1-6
 */
public abstract class AbsViewHolder<T> implements IViewHolder<T> {

    private View mRoot;

    public AbsViewHolder(View root) {
        mRoot = root;
        ViewInjectUtils.inject(AbsViewHolder.class, this, mRoot);
    }

    @Override
    public View getRootView() {
        return mRoot;
    }

}
