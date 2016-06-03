package lx.af.utils.ViewUtils;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * author: lx
 * date: 16-2-29
 */
public class SelectorRadioGroup {


    public interface SelectListener {
        void onSelectChanged(View oldView, View view);
    }


    private ArrayList<SelectorGroup> mSelectors = new ArrayList<>();
    private SelectorGroup mCurrent;
    private SelectListener mSelectListener;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setSelected(v);
        }
    };

    public static SelectorRadioGroup newInstance() {
        return new SelectorRadioGroup();
    }

    public SelectorRadioGroup addSelector(View... views) {
        mSelectors.add(new SelectorGroup(null, views));
        views[0].setOnClickListener(mOnClickListener);
        return this;
    }

    public SelectorRadioGroup addSelector(View.OnClickListener listener, View... views) {
        mSelectors.add(new SelectorGroup(listener, views));
        views[0].setOnClickListener(mOnClickListener);
        return this;
    }

    public SelectorRadioGroup setSelectListener(SelectListener l) {
        mSelectListener = l;
        return this;
    }

    public List<View> getSelectors() {
        ArrayList<View> list = new ArrayList<>(mSelectors.size());
        for (SelectorGroup s : mSelectors) {
            list.add(s.views[0]);
        }
        return list;
    }

    public void setSelected(View v) {
        if (mCurrent != null && mCurrent.containsView(v)) {
            // already selected
            return;
        }

        View oldView = getSelected();
        for (SelectorGroup selector : mSelectors) {
            if (selector.containsView(v)) {
                selector.setSelected(true);
                if (selector.listener != null) {
                    selector.listener.onClick(v);
                }
                mCurrent = selector;
            } else {
                selector.setSelected(false);
            }
        }

        if (mSelectListener != null) {
            mSelectListener.onSelectChanged(oldView, getSelected());
        }
    }

    public View getSelected() {
        return mCurrent != null ? mCurrent.views[0] : null;
    }

    public void clearSelected() {
        if (mCurrent != null) {
            mCurrent.setSelected(false);
            mCurrent = null;
        }
    }

    private static class SelectorGroup {

        View[] views;
        View.OnClickListener listener;

        SelectorGroup(View.OnClickListener listener, View[] views) {
            this.views = views;
            this.listener = listener;
        }

        void setSelected(boolean selected) {
            for (View view : views) {
                view.setSelected(selected);
            }
        }

        boolean containsView(View view) {
            for (View v : views) {
                if (view == v) {
                    return true;
                }
            }
            return false;
        }

    }

}
