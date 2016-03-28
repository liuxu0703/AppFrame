package lx.af.demo.base;

import lx.af.base.AbsBaseApp;
import lx.af.demo.R;
import lx.af.utils.PathUtils;
import lx.af.utils.WatermarkHelper;

/**
 * Created by liuxu on 15-6-11.
 *
 */
public class DemoApp extends AbsBaseApp {

    @Override
    public void onCreate() {
        super.onCreate();

        PathUtils.setSdDir("AppFrameDemo");
        initWatermark();
    }

    private void initWatermark() {
        WatermarkHelper.WatermarkOptions watermarkOptions = new WatermarkHelper.WatermarkOptions();
        watermarkOptions.position = WatermarkHelper.WatermarkPosition.BOTTOM_RIGHT;
        watermarkOptions.setMargin(8);
        WatermarkHelper.init(R.drawable.ic_launcher, watermarkOptions);
    }

}
