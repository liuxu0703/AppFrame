package lx.af.adapter;

import android.view.View;

/**
 * author: lx
 * date: 16-1-6
 */
public abstract class AbsViewHolder<T> {

    private View mRoot;

    public AbsViewHolder(View root) {
        mRoot = root;
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
