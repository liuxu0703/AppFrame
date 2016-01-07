package lx.af.demo.base;

import lx.af.app.AbsBaseApp;
import lx.af.utils.PathUtils;

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
