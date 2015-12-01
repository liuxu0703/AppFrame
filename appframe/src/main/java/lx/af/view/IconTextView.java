package lx.af.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import lx.af.R;

/**
 * author: lx
 * date: 15-11-24
 * enhanced TextView to easy set its four icon.
 */
public class IconTextView extends TextView {

    private static final int IDX_LEFT = 0;
    private static final int IDX_TOP = 1;
    private static final int IDX_RIGHT = 2;
    private static final int IDX_BOTTOM = 3;

    public IconTextView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public IconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public IconTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconTextView, defStyle, 0);
            int bottomHeight = a.getDimensionPixelSize(R.styleable.IconTextView_bottom_icon_height, -1);
            int bottomWidth = a.getDimensionPixelSize(R.styleable.IconTextView_bottom_icon_width, -1);
            int leftHeight = a.getDimensionPixelSize(R.styleable.IconTextView_left_icon_height, -1);
            int leftWidth = a.getDimensionPixelSize(R.styleable.IconTextView_left_icon_width, -1);
            int rightHeight = a.getDimensionPixelSize(R.styleable.IconTextView_right_icon_height, -1);
            int rightWidth = a.getDimensionPixelSize(R.styleable.IconTextView_right_icon_width, -1);
            int topHeight = a.getDimensionPixelSize(R.styleable.IconTextView_top_icon_height, -1);
            int topWidth = a.getDimensionPixelSize(R.styleable.IconTextView_top_icon_width, -1);
            a.recycle();

            Drawable[] drawables = getCompoundDrawables();
            setDrawableBounds(drawables[IDX_LEFT], leftWidth, leftHeight);
            setDrawableBounds(drawables[IDX_TOP], topWidth, topHeight);
            setDrawableBounds(drawables[IDX_RIGHT], rightWidth, rightHeight);
            setDrawableBounds(drawables[IDX_BOTTOM], bottomWidth, bottomHeight);

            setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
        }
    }

    private void setDrawableBounds(Drawable drawable, int width, int height) {
        if (drawable == null) {
            return;
        }
        if (width > 0 && height > 0) {
            drawable.setBounds(0, 0, width, height);
        }
    }

    private void setIcon(int idx, Drawable drawable, int width, int height) {
        setDrawableBounds(drawable, width, height);
        Drawable[] drawables = getCompoundDrawables();
        drawables[idx] = drawable;
        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

    public void setLeftIcon(Drawable drawable, int width, int height) {
        setIcon(IDX_LEFT, drawable, width, height);
    }

    public void setLeftIcon(Drawable drawable) {
        setIcon(IDX_LEFT, drawable, 0, 0);
    }

    public void setTopIcon(Drawable drawable, int width, int height) {
        setIcon(IDX_TOP, drawable, width, height);
    }

    public void setTopIcon(Drawable drawable) {
        setIcon(IDX_TOP, drawable, 0, 0);
    }

    public void setRightIcon(Drawable drawable, int width, int height) {
        setIcon(IDX_RIGHT, drawable, width, height);
    }

    public void setRightIcon(Drawable drawable) {
        setIcon(IDX_RIGHT, drawable, 0, 0);
    }

    public void setBottomIcon(Drawable drawable, int width, int height) {
        setIcon(IDX_BOTTOM, drawable, width, height);
    }

    public void setBottomIcon(Drawable drawable) {
        setIcon(IDX_BOTTOM, drawable, 0, 0);
    }

}
