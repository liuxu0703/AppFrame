package lx.af.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by liuxu on 15-5-26.
 *
 */
public class NonScrollGridView extends GridView {

	public NonScrollGridView(Context context) {
		super(context);
	}

	public NonScrollGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

    public NonScrollGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
