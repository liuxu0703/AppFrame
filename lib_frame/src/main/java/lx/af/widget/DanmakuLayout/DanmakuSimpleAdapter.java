package lx.af.widget.DanmakuLayout;

/**
 * author: lx
 * date: 16-3-23
 */
public abstract class DanmakuSimpleAdapter<T> extends DanmakuBaseAdapter<T> {

    @Override
    public int getViewType(T data) {
        return 1;
    }

    @Override
    public void onDataEmpty() {

    }

}
