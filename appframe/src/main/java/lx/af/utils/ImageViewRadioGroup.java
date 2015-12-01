package lx.af.utils;

import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * author: lx
 * date: 15-11-30
 *
 * display select state for a group of ImageView.
 * for example, a tab-style ui.
 */
public class ImageViewRadioGroup implements View.OnClickListener {

    private ArrayList<ImageSelector> mSelectors = new ArrayList<>();
    private ImageSelector mCurrentSelector;

    /**
     * add an ImageView as selector to display select state.
     * @param v the ImageView
     * @param l OnClickListener for the ImageView
     * @param selectId image to display when selected
     * @param unSelectId image to display when not selected
     */
    public void addSelector(ImageView v, View.OnClickListener l, int selectId, int unSelectId) {
        ImageSelector selector = new ImageSelector(v, selectId, unSelectId, l);
        mSelectors.add(selector);
        v.setOnClickListener(this);
    }

    /**
     * set selected ImageView. will trigger its OnClickListener.
     * will do nothing if the ImageView is not in this group.
     */
    public void setSelected(View v) {
        if (mCurrentSelector != null && mCurrentSelector.view == v) {
            return;
        }
        for (ImageSelector selector : mSelectors) {
            if (selector.view == v) {
                selector.displaySelectState(true);
                if (selector.listener != null) {
                    selector.listener.onClick(v);
                }
                mCurrentSelector = selector;
            } else {
                selector.displaySelectState(false);
            }
        }
    }

    /**
     * get the selected ImageView. maybe null.
     */
    public ImageView getSelected() {
        return mCurrentSelector != null ? mCurrentSelector.view : null;
    }

    /**
     * set all ImageView under this group as un-selected
     */
    public void clearSelected() {
        if (mCurrentSelector != null) {
            mCurrentSelector.displaySelectState(false);
            mCurrentSelector = null;
        }
    }

    @Override
    public void onClick(View v) {
        setSelected(v);
    }

    private static class ImageSelector {

        ImageView view;
        int srcSelectId;
        int srcUnSelectId;
        View.OnClickListener listener;

        ImageSelector(ImageView v, int srcSelectId, int srcUnSelectId, View.OnClickListener l) {
            this.view = v;
            this.srcSelectId = srcSelectId;
            this.srcUnSelectId = srcUnSelectId;
            this.listener = l;
        }

        void displaySelectState(boolean selected) {
            view.setImageResource(selected ? srcSelectId : srcUnSelectId);
        }
    }

}
