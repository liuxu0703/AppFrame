package com.mining.app.zxing.camera;

/**
 * author: lx
 * date: 16-1-22
 */
class ScreenUtils {

    public static ScreenTypes sScreenType;

    public enum ScreenTypes {
        LEVEL_NONE,
        LEVEL_ONE, //(320-480)x(*)
        LEVEL_TWO, //(480-640)x(800-854) ex:800*480
        LEVEL_THREE, //(480-640)x(854+) ex:854*480
        LEVEL_FOUR, //(640-700)x(*) ex:960*640
        LEVEL_FIVE, //(700+)X(*) ex:1280x720
        LEVEL_SIX, //(700+)X(*) ex:1280x1920
        LEVEL_SEVEN,
    }

    public static ScreenTypes getLevel() {
        if (sScreenType != null) {
            return sScreenType;
        }

        final int W = lx.af.utils.ScreenUtils.getScreenWidth();
        final int H = lx.af.utils.ScreenUtils.getScreenHeight();

        if (W >= 1080) {
            return ScreenTypes.LEVEL_SIX;
        } else if (W >= 700) {
            return ScreenTypes.LEVEL_FIVE;
        } else if (W >= 540) {
            return ScreenTypes.LEVEL_FOUR;
        } else if (W >= 480 && H >= 800) {
            if (H >= 854) {
                return ScreenTypes.LEVEL_THREE;
            }
            return ScreenTypes.LEVEL_TWO;
        } else if (W >= 320) {
            return ScreenTypes.LEVEL_ONE;
        } else {
            return ScreenTypes.LEVEL_TWO;
        }
    }

    public static int[] getEWM() {
        ScreenTypes type = getLevel();
        int[] iconInfo = new int[2];
        switch (type) {
            case LEVEL_ONE:
                iconInfo[0] = 266;
                iconInfo[1] = 200;
                break;
            case LEVEL_TWO:
            case LEVEL_THREE:
                iconInfo[0] = 400;
                iconInfo[1] = 300;
                break;
            case LEVEL_FOUR:
                iconInfo[0] = 456;
                iconInfo[1] = 342;
                break;
            case LEVEL_FIVE:
                iconInfo[0] = 620;
                iconInfo[1] = 450;
                break;
            case LEVEL_SIX:
                iconInfo[0] = 900;
                iconInfo[1] = 675;
                break;
            default:
                iconInfo[0] = 480;
                iconInfo[1] = 360;
                break;
        }
        return iconInfo;
    }

}
