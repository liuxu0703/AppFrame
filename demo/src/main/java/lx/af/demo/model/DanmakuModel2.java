package lx.af.demo.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

import lx.af.demo.utils.TestData.TestDataHelper;
import lx.af.demo.utils.TestData.TestImageHelper;

/**
 * author: lx
 * date: 16-3-23
 */
public class DanmakuModel2 {

    @Expose
    public String avatar1;

    @Expose
    public String avatar2;

    @Expose
    public String avatar3;

    public static DanmakuModel2 createRandom() {
        DanmakuModel2 model = new DanmakuModel2();
        model.avatar1 = TestImageHelper.randomAvatarL();
        model.avatar2 = TestImageHelper.randomAvatarL();
        model.avatar3 = TestImageHelper.randomAvatarL();
        return model;
    }

}
