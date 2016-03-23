package lx.af.demo.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

import lx.af.demo.utils.TestData.TestDataHelper;
import lx.af.demo.utils.TestData.TestImageHelper;
import lx.af.utils.StringUtils;

/**
 * author: lx
 * date: 16-3-23
 */
public class DanmakuModel {

    @Expose
    public String id;

    @Expose
    public String avatar;

    @Expose
    public String content;

    public static DanmakuModel createRandom() {
        DanmakuModel model = new DanmakuModel();
        model.avatar = TestImageHelper.randomAvatarL();
        model.content = TestDataHelper.getRandomShortString();
        model.id = StringUtils.toMd5(model.avatar + model.content);
        return model;
    }

    public static ArrayList<DanmakuModel> createRandomList(int length) {
        ArrayList<DanmakuModel> list = new ArrayList<>(length);
        for (int i = 0; i < length; i ++) {
            list.add(DanmakuModel.createRandom());
        }
        return list;
    }

}
