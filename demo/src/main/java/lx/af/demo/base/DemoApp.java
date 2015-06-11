package lx.af.demo.base;

import lx.af.app.BaseApp;
import lx.af.utils.PathUtils;

/**
 * Created by liuxu on 15-6-11.
 *
 */
public class DemoApp extends BaseApp {

    @Override
    public void onCreate() {
        super.onCreate();

        PathUtils.setSdRoot("AppFrameDemo");
    }
}
