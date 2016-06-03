package lx.af.utils.UIL.displayer;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * author: lx
 * date: 16-5-31
 */
public abstract class BaseDrawableDisplayer extends BaseDisplayer {

    public abstract Drawable createDisplayDrawable(Bitmap bitmap);

}
