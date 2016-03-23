package lx.af.demo.fragment;

import lx.af.demo.consts.DemoConfig;
import lx.af.demo.consts.ActionModel;

/**
 * author: lx
 * date: 16-1-5
 */
public class MainTab3 extends MainTabList {

    @Override
    protected ActionModel[] getActionModelArray() {
        return DemoConfig.UTILS_DEMO;
    }

}
