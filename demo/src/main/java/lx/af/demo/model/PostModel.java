package lx.af.demo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import lx.af.test.TestDataHelper;
import lx.af.test.TestImageHelper;

/**
 * author: lx
 * date: 16-3-22
 */
public class PostModel {

    public UserModel user;
    public String content;
    public ArrayList<String> picList;
    public String address;
    public long createTime;

    public static PostModel createRandom() {
        PostModel post = new PostModel();
        post.user = UserModel.createRandom();
        post.address = TestDataHelper.getRandomAddress();
        post.createTime = TestDataHelper.getRandomTime();
        int random = new Random().nextInt(3);
        if (random == 1) {
            post.content = null;
        } else {
            post.content = TestDataHelper.getRandomLongString();
        }
        if (post.content == null || random == 2) {
            post.picList = TestImageHelper.randomImageListL(1, 9);
        } else {
            post.picList = null;
        }
        return post;
    }

    public static ArrayList<PostModel> createRandomList(int length, boolean allowFail) {
        if (allowFail) {
            int random = new Random().nextInt(10);
            if (random == 0) {
                return null;
            }
            if (random == 1) {
                return new ArrayList<>();
            }
        }

        ArrayList<PostModel> list = new ArrayList<>(length);
        for (int i = 0; i < length; i ++) {
            list.add(PostModel.createRandom());
        }
        Collections.sort(list, new Comparator<PostModel>() {
            @Override
            public int compare(PostModel lhs, PostModel rhs) {
                return (int) (rhs.createTime - lhs.createTime);
            }
        });
        return list;
    }

}
