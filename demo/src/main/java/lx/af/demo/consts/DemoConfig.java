package lx.af.demo.consts;

import android.app.Activity;

import lx.af.demo.activity.DemoWidget.SelectImageViewDemo;
import lx.af.demo.activity.DemoWidget.AutoSlidePagerDemo;
import lx.af.demo.activity.DemoWidget.DanmakuLayoutDemo;
import lx.af.demo.activity.DemoWidget.DotCircleDemo;
import lx.af.demo.activity.DemoWidget.FlowLayoutDemo;
import lx.af.demo.activity.DemoUtils.HomeKeyWatcherDemo;
import lx.af.demo.activity.DemoWidget.VideoPlayViewDemo;
import lx.af.demo.activity.DemoUtils.ViewPagerAutoFlipDemo;
import lx.af.demo.activity.DemoFrame.CodeScannerDemo;
import lx.af.demo.activity.DemoFrame.ImageOperateDemo;
import lx.af.demo.activity.DemoFrame.ScrollViewFadeDemo;
import lx.af.demo.activity.DemoFrame.SwipeRefreshDemo;
import lx.af.demo.model.ActionModel;

/**
 * author: lx
 * date: 16-3-19
 */
public class DemoConfig {

    public static final ActionModel[] FRAME_DEMO = new ActionModel[] {
            demo("Image Operate", ImageOperateDemo.class),
            demo("Swipe Refresh Frame", SwipeRefreshDemo.class),
            demo("ScrollView Fade", ScrollViewFadeDemo.class),
            demo("Bar Code Scanner", CodeScannerDemo.class),
    };


    public static final ActionModel[] WIDGET_DEMO = new ActionModel[] {
            demo("Auto Slide Pager", AutoSlidePagerDemo.class),
            demo("Dot Circle", DotCircleDemo.class),
            demo("Video Play", VideoPlayViewDemo.class),
            demo("Danmaku", DanmakuLayoutDemo.class),
            demo("Flow Layout", FlowLayoutDemo.class),
            demo("Select Image Widget", SelectImageViewDemo.class),
    };


    public static final ActionModel[] UTILS_DEMO = new ActionModel[] {
            demo("ActionBar Fade - ListView", SwipeRefreshDemo.class),
            demo("ActionBar Fade - ScrollView", ScrollViewFadeDemo.class),
            demo("ViewPager Auto Flip", ViewPagerAutoFlipDemo.class),
            demo("Home Key Watcher", HomeKeyWatcherDemo.class),
    };


    public static final ActionModel[] MISC_DEMO = new ActionModel[] {
            demo("What Ever", null),
    };


    public static ActionModel demo(String title, Class<? extends Activity> activity) {
        return new ActionModel(title, activity);
    }

}
