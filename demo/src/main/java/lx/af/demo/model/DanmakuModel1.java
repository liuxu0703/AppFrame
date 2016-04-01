package lx.af.demo.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

import lx.af.test.TestDataHelper;
import lx.af.test.TestImageHelper;

/**
 * author: lx
 * date: 16-3-23
 */
public class DanmakuModel1 {

    @Expose
    public String avatar;

    @Expose
    public String content;

    public static DanmakuModel1 createRandom() {
        DanmakuModel1 model = new DanmakuModel1();
        model.avatar = TestImageHelper.randomAvatarL();
        model.content = TestDataHelper.getRandomShortString();
        return model;
    }

    public static ArrayList<DanmakuModel1> createRandomList(int length) {
        ArrayList<DanmakuModel1> list = new ArrayList<>(length);
        for (int i = 0; i < length; i ++) {
            list.add(DanmakuModel1.createRandom());
        }
        return list;
    }

}
