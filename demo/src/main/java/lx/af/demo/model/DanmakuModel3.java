package lx.af.demo.model;

import com.google.gson.annotations.Expose;

import lx.af.test.TestDataHelper;

/**
 * author: lx
 * date: 16-3-23
 */
public class DanmakuModel3 {

    @Expose
    public String content;

    public static DanmakuModel3 createRandom() {
        DanmakuModel3 model = new DanmakuModel3();
        model.content = TestDataHelper.getRandomAddress();
        return model;
    }

}
