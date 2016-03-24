package lx.af.demo.utils.TestData;

import java.util.ArrayList;
import java.util.Random;

/**
 * author: lx
 * date: 16-3-22
 *
 * methods end with "L" means get a local uri (from assets folder)
 * methods end with "N" means get a network uri
 */
public class TestImageHelper {

    private static final ImageGroup AVATAR = ImageGroup.create("assets://pics/avatar/a%d.jpg", 21);
    private static final ImageGroup GIRL = ImageGroup.create("assets://pics/girl/a%d.jpg", 31);
    private static final ImageGroup PET = ImageGroup.create("assets://pics/pet/%d.jpg", 17);
    private static final ImageGroup SCENE = ImageGroup.create("assets://pics/scene/a%d.jpg", 17);
    private static final ImageGroup CARTOON = ImageGroup.create("assets://pics/cartoon/%d.jpg", 10);

    private static final ImageGroup[] IMAGE_GROUP_ARRAY = new ImageGroup[] {
            AVATAR, GIRL, SCENE, PET, CARTOON,
    };

    private static Random sRandom = new Random();

    public static String randomAvatarL() {
        return AVATAR.random();
    }

    public static String randomImageL() {
        int groupIdx = random(IMAGE_GROUP_ARRAY.length);
        return IMAGE_GROUP_ARRAY[groupIdx].random();
    }

    public static ArrayList<String> randomImageListL() {
        int idx = random(IMAGE_GROUP_ARRAY.length);
        return IMAGE_GROUP_ARRAY[idx].randomList();
    }

    public static ArrayList<String> randomImageListL(int minSize, int maxSize) {
        minSize = minSize < 1 ? 1 : minSize;
        int size = minSize + random(maxSize);
        ArrayList<ImageGroup> validList = new ArrayList<>();
        for (ImageGroup g : IMAGE_GROUP_ARRAY) {
            if (g.size >= size) {
                validList.add(g);
            }
        }
        if (validList.size() > 0) {
            int idx = random(validList.size());
            return validList.get(idx).randomList(size);
        } else {
            return null;
        }
    }

    private static int random(int n) {
        return sRandom.nextInt(n);
    }


    private static class ImageGroup {

        String format;
        int size;

        ImageGroup(String format, int size) {
            this.format = format;
            this.size = size;
        }

        String random() {
            int idx = (new Random()).nextInt(size);
            return String.format(format, idx);
        }

        ArrayList<String> randomList() {
            return randomList(size);
        }

        ArrayList<String> randomList(int length) {
            if (size < length) {
                return null;
            }
            ArrayList<String> list = new ArrayList<>(length);
            int start = sRandom.nextInt(size);
            for (int i = 0; i < length; i ++) {
                int idx = start + i;
                if (idx > size - 1) {
                    idx -= size;
                }
                list.add(String.format(format, idx));
            }
            return list;
        }

        static ImageGroup create(String format, int index) {
            return new ImageGroup(format, index);
        }

    }

}
