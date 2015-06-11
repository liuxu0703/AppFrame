package lx.af.view.CropImage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import lx.af.utils.ScreenUtils;

/**
 * Created by sam on 14-10-17.
 *
 */
public class CropImageLayout extends RelativeLayout {

    private CropScaleImageView mZoomImageView;
    private CropImageBorderView mClipImageView;

    public final static int MAX_WIDTH = 2048;

    public CropImageLayout(Context context) {
        this(context, null);
    }

    public CropImageLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mZoomImageView = new CropScaleImageView(context);
        mClipImageView = new CropImageBorderView(context);
        // set size of the clip rect to half the screen width
        int clipSize = ScreenUtils.getScreenWidth() / 2;
        mZoomImageView.setClipSize(clipSize, clipSize);
        mClipImageView.setClipSize(clipSize, clipSize);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mZoomImageView, params);
        addView(mClipImageView, params);
    }

    public Bitmap clip() {
        return mZoomImageView.clip();
    }

    public void setImageBitmap(Bitmap bitmap) {
        mZoomImageView.setImageBitmap(bitmap);
    }

    public void setImagePath(String filePath) {
        Bitmap b = BitmapFactory.decodeFile(filePath);
        setImageBitmap(b);
    }

}
