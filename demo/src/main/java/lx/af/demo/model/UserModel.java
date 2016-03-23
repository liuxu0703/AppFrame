package lx.af.demo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import lx.af.demo.utils.TestData.TestDataHelper;
import lx.af.demo.utils.TestData.TestImageHelper;
import lx.af.utils.StringUtils;

/**
 * author: lx
 * date: 16-3-22
 */
public class UserModel {

    public String id;
    public String name;
    public String avatarUri;
    public boolean isMale;

    public static UserModel createRandom() {
        UserModel user = new UserModel();
        user.name = TestDataHelper.getRandomName();
        user.avatarUri = TestImageHelper.randomAvatarL();
        user.isMale = user.name.length() % 2 == 1;
        user.id = StringUtils.toMd5(user.name + System.currentTimeMillis());
        return user;
    }

    public static ArrayList<UserModel> createRandomList(int length) {
        return createRandomList(length, false);
    }

    public static ArrayList<UserModel> createRandomList(int length, boolean allowFail) {
        if (allowFail) {
            int random = new Random().nextInt(10);
            if (random == 1) {
                return null;
            }
            if (random == 2) {
                return new ArrayList<>();
            }
        }

        ArrayList<UserModel> list = new ArrayList<>(length);
        for (int i = 0; i < length; i ++) {
            list.add(UserModel.createRandom());
        }
        return list;
    }

}
