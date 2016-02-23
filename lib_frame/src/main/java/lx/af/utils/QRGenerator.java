package lx.af.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * author: lx
 * date: 16-01-23
 * QRCode generator based on google zxing package.
 */
public class QRGenerator {

    private static final File CACHE_DIR = PathUtils.getExtCacheDir("qr_img_cache");
    private static final float LOGO_BKG_PADDING = 3f;
    private static final float LOGO_BKG_CORNER = 6f;

    private String text;
    private Bitmap logo;
    private int logoResId;
    private int size;
    private boolean cache;
    private boolean logoBkg;

    private String cacheFilePath;

    /**
     * @param text the content to be encoded
     */
    public static QRGenerator with(String text) {
        return new QRGenerator(text);
    }

    private QRGenerator(String text) {
        this.text = text;
    }

    /**
     * target QRCode image bitmap size.
     * if not set, half screen size will be used as default size.
     */
    public QRGenerator size(int size) {
        this.size = size;
        return this;
    }

    /**
     * if set, the logo will be put in the center of the generated QRCode image.
     */
    public QRGenerator logo(Bitmap logo) {
        this.logo = logo;
        return this;
    }

    /**
     * if set, the logo will be put in the center of the generated QRCode image.
     */
    public QRGenerator logo(int resId) {
        this.logoResId = resId;
        return this;
    }

    /**
     * if set, a white rounded rect with shadow will be drawn under the logo.
     * will take no effect if logo is not set.
     */
    public QRGenerator logoBackground(boolean drawBackground) {
        this.logoBkg = drawBackground;
        return this;
    }

    /**
     * if set to true, we will find and decode cached QRCode image file
     * before generate a new one.
     * and if cache file not found, we will save the generated QRCode image
     * into cache dir.
     * the to-be-encode content, size param, ogo param and its background param
     * will all be considered to determine if the cache is hit.
     */
    public QRGenerator cache(boolean useCache) {
        this.cache = useCache;
        return this;
    }

    /**
     * create QRCode image bitmap.
     */
    public Bitmap create() {
        checkParams();
        if (cache) {
            Bitmap bitmap = fromCache();
            if (bitmap != null) {
                return bitmap;
            }
        }

        if (logo == null && logoResId != 0) {
            logo = BitmapUtils.res2bitmap(logoResId);
        }

        Bitmap bitmap = createQRBitmap(text, size, size, logo, logoBkg);
        if (cache && bitmap != null) {
            toCache(bitmap);
        }
        return bitmap;
    }

    public String getCacheFilePath() {
        return cacheFilePath;
    }

    /**
     * save QRCode image bitmap to file.
     */
    public boolean save2file(String path) {
        Bitmap bitmap = create();
        return BitmapUtils.saveBitmap(bitmap, path, 75);
    }

    private void checkParams() {
        if (size == 0) {
            size = ScreenUtils.getScreenWidth() / 2;
        }
    }

    private boolean toCache(Bitmap bitmap) {
        File file = getCacheFile();
        if (BitmapUtils.saveBitmap(bitmap, file.getAbsolutePath(), 50)) {
            cacheFilePath = file.getAbsolutePath();
            return true;
        } else {
            return false;
        }
    }

    private Bitmap fromCache() {
        File file = getCacheFile();
        if (!file.exists()) {
            return null;
        } else {
            cacheFilePath = file.getAbsolutePath();
            return BitmapUtils.file2bitmap(file.getAbsolutePath());
        }
    }

    private File getCacheFile() {
        long logoKey = logoResId != 0 ? logoResId : BitmapUtils.bitmap2hashFull(logo);
        String key = text + "_" + size + "_" + logoKey + (logoBkg ? "_1" : "_0");
        return new File(CACHE_DIR, StringUtils.toMd5(key) + ".jpg");
    }

    /**
     * 生成二维码Bitmap
     * @param content   内容
     * @param w         图片宽度
     * @param h         图片高度
     * @param logo      二维码中心的Logo图标（可以为null）
     * @return 生成二维码及保存文件是否成功
     */
    private static Bitmap createQRBitmap(
            String content, int w, int h, Bitmap logo, boolean drawLogoBkg) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }

        //配置参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //容错级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //设置空白边距的宽度
        hints.put(EncodeHintType.MARGIN, 1); //default is 4

        BitMatrix result;
        try {
            result = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, w, h, hints);
        } catch (Exception e) {
            Log.e("liuxu", "encode QRCode fail", e);
            return null;
        }

        //BitMatrix result = deleteWhite(result);
        int rWidth = result.getWidth();
        int rHeight = result.getHeight();
        int[] pixels = new int[rWidth * rHeight];

        // 下面这里按照二维码的算法,逐个生成二维码的图片,两个for循环是图片横列扫描的结果
        for (int y = 0; y < rHeight; y++) {
            int offset = y * rWidth;
            for (int x = 0; x < rWidth; x++) {
                pixels[offset + x] = result.get(x, y) ? 0xff000000 : 0xffffffff;
            }
        }

        // 生成二维码图片的格式,使用ARGB_8888
        Bitmap bitmap;
        try {
            bitmap = Bitmap.createBitmap(rWidth, rHeight, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, rWidth, 0, 0, rWidth, rHeight);
        } catch (Exception e) {
            Log.e("liuxu", "create QRCode bitmap fail", e);
            return null;
        }

        if (logo != null) {
            bitmap = addLogo(bitmap, logo, drawLogoBkg);
        }
        return bitmap;
    }

    /** 在二维码中间添加Logo图案 */
    private static Bitmap addLogo(Bitmap src, Bitmap logo, boolean drawLogobkg) {
        if (src == null) {
            return null;
        }

        if (logo == null) {
            return src;
        }

        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();
        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        float logoTop = (srcHeight - logoHeight) / 2;
        float logoLeft = (srcWidth - logoWidth) / 2;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);

            if (drawLogobkg) {
                float logoRight = (srcWidth + logoWidth) / 2;
                float logoBottom = (srcWidth + logoWidth) / 2;
                RectF rect = new RectF(
                        logoLeft - LOGO_BKG_PADDING, logoTop - LOGO_BKG_PADDING,
                        logoRight + LOGO_BKG_PADDING, logoBottom + LOGO_BKG_PADDING);
                Paint paint = new Paint();
                paint.setShadowLayer(10, 15, 15, Color.LTGRAY);
                paint.setColor(Color.WHITE);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRoundRect(rect, LOGO_BKG_CORNER, LOGO_BKG_CORNER, paint);
                paint.setStrokeWidth(1);
                paint.setColor(Color.GRAY);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawRoundRect(rect, LOGO_BKG_CORNER, LOGO_BKG_CORNER, paint);
            }

            canvas.drawBitmap(logo, logoLeft, logoTop, null);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            Log.e("liuxu", "add logo to QRCode bitmap fail", e);
            return null;
        }

        return bitmap;
    }

    /** 去白边 */
    private static BitMatrix deleteWhite(BitMatrix matrix){
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + 1;
        int resHeight = rec[3] + 1;

        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; j < resHeight; j++) {
                if (matrix.get(i + rec[0], j + rec[1]))
                    resMatrix.set(i, j);
            }
        }
        return resMatrix;
    }

}
