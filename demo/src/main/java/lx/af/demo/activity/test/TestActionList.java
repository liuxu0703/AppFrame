package lx.af.demo.activity.test;

import java.util.Arrays;

import lx.af.demo.base.BaseActivity;
import lx.af.utils.ActivityLauncher.ImageBrowserLauncher;

/**
 * author: lx
 * date: 16-4-1
 */
class TestActionList {

    static TestAction[] ARR = new TestAction[] {

            new TestAction("test image browser", new TestAction.Action() {
                @Override
                public void doAction(BaseActivity activity) {
                    String[] uris = new String[] {
                            "http://imgsrc.baidu.com/baike/pic/item/a1ec08fa513d2697f9e1b7055dfbb2fb4316d8bc.jpg",
                            "http://imgsrc.baidu.com/baike/pic/item/9825bc315c6034a854c607d9c313495409237645.jpg",
                            "http://imgsrc.baidu.com/baike/pic/item/4d086e061d950a7b8d5af19c02d162d9f2d3c97f.jpg",
                            "http://imgsrc.baidu.com/baike/pic/item/f9dcd100baa1cd114118cd78b112c8fcc3ce2d3f.jpg",
                            "http://imgsrc.baidu.com/baike/pic/item/63d9f2d3572c11df3cfe017b6b2762d0f703c2bf.jpg"
                    };
                    ImageBrowserLauncher.of(activity)
                            .uris(Arrays.asList(uris))
                            .currentUri(uris[2])
                            .tapExit(true)
                            .start();
                }
            }),

    };


}
