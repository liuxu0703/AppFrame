package lx.af.utils.ViewPagerTransformer;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * import from https://github.com/hongyangAndroid/MagicViewPager
 */
public class NonPageTransformer implements ViewPager.PageTransformer
{
    @Override
    public void transformPage(View page, float position)
    {
        page.setScaleX(0.999f);//hack
    }

    public static final ViewPager.PageTransformer INSTANCE = new NonPageTransformer();
}
