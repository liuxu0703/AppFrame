package lx.af.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.Log;

/**
 * author: lx
 * date: 16-3-25
 */
public class WatermarkHelper {

    public static final String TAG = "watermark";

    public enum WatermarkPosition {
        TOP_LEFT,
        TOP_CENTER,
        TOP_RIGHT,
        CENTER_LEFT,
        CENTER,
        CENTER_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_CENTER,
        BOTTOM_RIGHT,
    }

    public static class WatermarkOptions {

        private static final int DEFAULT_MARGIN = 10;

        public WatermarkPosition position = WatermarkPosition.TOP_RIGHT;
        public int marginLeft = DEFAULT_MARGIN;
        public int marginTop = DEFAULT_MARGIN;
        public int marginRight = DEFAULT_MARGIN;
        public int marginBottom = DEFAULT_MARGIN;

        public int maxWatermarkWidthScale = 8;
        public int maxWatermarkHeightScale = 8;

        public void setMargin(int margin) {
            marginLeft = marginRight = marginTop = marginBottom = margin;
        }

        @Override
        public String toString() {
            return "WatermarkOptions{" +
                    "position=" + position +
                    ", marginLeft=" + marginLeft +
                    ", marginTop=" + marginTop +
                    ", marginRight=" + marginRight +
                    ", marginBottom=" + marginBottom +
                    ", maxWatermarkWidthScale=" + maxWatermarkWidthScale +
                    ", maxWatermarkHeightScale=" + maxWatermarkHeightScale +
                    '}';
        }
    }


    // ===================================================


    private static int sDefaultWaterMarkIconId = -1;
    private static WatermarkOptions sDefaultOptions;

    public static void init(int defaultWaterMarkIconId, WatermarkOptions defaultOptions) {
        sDefaultWaterMarkIconId = defaultWaterMarkIconId;
        sDefaultOptions = defaultOptions;
    }

    public static EditBuilder edit() {
        return new EditBuilder();
    }

    public static Bitmap addWatermark(Bitmap src, Bitmap watermark, WatermarkOptions options) {
        if (src == null) {
            return null;
        }
        if (watermark == null) {
            return src;
        }
        Bitmap mark = checkScaleWatermark(src, watermark, options);
        Point drawPoint = getDrawPosition(src, mark, options);
        Bitmap ret = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_8888);
        Log.d(TAG, "srcW=" + src.getWidth() + ", srcH=" + src.getHeight() +
                ", markW=" + mark.getWidth() + ", markH=" + mark.getHeight() +
                ", point=" + drawPoint + ", " + options);
        Canvas canvas = new Canvas(ret);
        canvas.drawBitmap(src, 0, 0, null);
        canvas.drawBitmap(mark, drawPoint.x, drawPoint.y, null);
        return ret;
    }

    private static Bitmap checkScaleWatermark(Bitmap src, Bitmap mark, WatermarkOptions options) {
        int srcW = src.getWidth();
        int srcH = src.getHeight();
        int markW = mark.getWidth();
        int markH = mark.getHeight();
        float scale = 1f;
        if (srcW < options.maxWatermarkWidthScale * markW) {
            scale = (float) srcW / (options.maxWatermarkWidthScale * markW);
        } else if (srcH < options.maxWatermarkHeightScale * markH) {
            scale = (float) srcH / (options.maxWatermarkHeightScale * markH);
        }
        if (scale != 1f) {
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            return Bitmap.createBitmap(mark, 0, 0, markW, markH, matrix, true);
        } else {
            return mark;
        }
    }

    private static Point getDrawPosition(Bitmap src, Bitmap mark, WatermarkOptions options) {
        int srcW = src.getWidth();
        int srcH = src.getHeight();
        int markW = mark.getWidth();
        int markH = mark.getHeight();
        int left, top;
        switch (options.position) {
            case TOP_LEFT:
                left = options.marginLeft;
                top = options.marginTop;
                break;
            case TOP_CENTER:
                left = (srcW - markW) / 2;
                top = options.marginTop;
                break;
            case TOP_RIGHT:
                left = srcW - markW - options.marginRight;
                top = options.marginTop;
                break;
            case CENTER_LEFT:
                left = options.marginLeft;
                top = (srcH - markH) / 2;
                break;
            case CENTER:
                left = (srcW - markW) / 2;
                top = (srcH - markH) / 2;
                break;
            case CENTER_RIGHT:
                left = srcW - markW - options.marginRight;
                top = (srcH - markH) / 2;
                break;
            case BOTTOM_LEFT:
                left = options.marginLeft;
                top = srcH - markH - options.marginBottom;
                break;
            case BOTTOM_CENTER:
                left = (srcW - markW) / 2;
                top = srcH - markH - options.marginBottom;
                break;
            case BOTTOM_RIGHT:
                left = srcW - markW - options.marginRight;
                top = srcH - markH - options.marginBottom;
                break;
            default:
                left = options.marginLeft;
                top = options.marginTop;
                break;
        }
        return new Point(left, top);
    }


    public static class EditBuilder {

        int watermarkResId;
        String watermarkPath;
        Bitmap watermarkBitmap;

        int imageResId;
        String imagePath;
        Bitmap imageBitmap;

        WatermarkOptions options;

        public EditBuilder setImageResId(int imageResId) {
            this.imageResId = imageResId;
            return this;
        }

        public EditBuilder setImagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public EditBuilder setImageBitmap(Bitmap imageBitmap) {
            this.imageBitmap = imageBitmap;
            return this;
        }

        public EditBuilder setWatermarkResId(int watermarkResId) {
            this.watermarkResId = watermarkResId;
            return this;
        }

        public EditBuilder setWatermarkPath(String watermarkPath) {
            this.watermarkPath = watermarkPath;
            return this;
        }

        public EditBuilder setWatermarkBitmap(Bitmap watermarkBitmap) {
            this.watermarkBitmap = watermarkBitmap;
            return this;
        }

        public EditBuilder setMargin(int margin) {
            getOptions().marginTop= margin;
            getOptions().marginBottom = margin;
            getOptions().marginLeft = margin;
            getOptions().marginRight = margin;
            return this;
        }

        public EditBuilder setMarginBottom(int margin) {
            getOptions().marginBottom = margin;
            return this;
        }

        public EditBuilder setMarginTop(int margin) {
            getOptions().marginTop= margin;
            return this;
        }

        public EditBuilder setMarginRight(int margin) {
            getOptions().marginRight = margin;
            return this;
        }

        public EditBuilder setMarginLeft(int margin) {
            getOptions().marginLeft = margin;
            return this;
        }

        public EditBuilder setPosition(WatermarkPosition position) {
            if (position != null) {
                getOptions().position = position;
            }
            return this;
        }

        public EditBuilder setOptions(WatermarkOptions options) {
            this.options = options;
            return this;
        }

        public Bitmap create() {
            if (imageBitmap == null && imagePath != null) {
                imageBitmap = BitmapUtils.file2bitmap(imagePath);
            }
            if (imageBitmap == null && imageResId != 0) {
                imageBitmap = BitmapUtils.res2bitmap(imageResId);
            }
            if (imageBitmap == null) {
                Log.e(TAG, "no source image found");
                return null;
            }

            if (watermarkBitmap == null && watermarkPath != null) {
                watermarkBitmap = BitmapUtils.file2bitmap(watermarkPath);
            }
            if (watermarkBitmap == null && watermarkResId != 0) {
                watermarkBitmap = BitmapUtils.res2bitmap(watermarkResId);
            }
            if (watermarkBitmap == null && sDefaultWaterMarkIconId != 0) {
                watermarkBitmap = BitmapUtils.res2bitmap(sDefaultWaterMarkIconId);
            }
            if (watermarkBitmap == null) {
                Log.e(TAG, "no watermark found");
                return null;
            }

            return addWatermark(imageBitmap, watermarkBitmap, getOptions());
        }

        public boolean save2file(String filePath) {
            Bitmap bitmap = create();
            return BitmapUtils.saveBitmap(bitmap, filePath);
        }

        private WatermarkOptions getOptions() {
            if (options == null) {
                if (sDefaultOptions != null) {
                    options = sDefaultOptions;
                } else {
                    options = new WatermarkOptions();
                }
            }
            return options;
        }

    }

}
