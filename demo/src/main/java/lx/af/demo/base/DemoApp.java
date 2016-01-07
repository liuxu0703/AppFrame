package lx.af.demo.base;

import lx.af.base.AbsBaseApp;
import lx.af.utils.PathUtils;
import lx.af.utils.log.LogUtils;

/**
 * Created by liuxu on 15-6-11.
 *
 */
public class DemoApp extends AbsBaseApp {

    @Override
    public void onCreate() {
        super.onCreate();

        PathUtils.setSdRoot("AppFrameDemo");
    }
}
