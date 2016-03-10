package lx.af.activity.ImageEditor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import lx.af.R;
import lx.af.base.AbsBaseActivity;
import lx.af.manager.GlobalThreadManager;
import lx.af.utils.BitmapUtils;
import lx.af.utils.PathUtils;

public class ImageEditorActivity extends AbsBaseActivity {

    /** action bar title */
    public static final String EXTRA_TITLE = "activity_title";
    /** origin image path in local file system */
    public static final String EXTRA_PATH = "image_path";
    /** where to save edited image. if not set, a tmp path will be used */
    public static final String EXTRA_SAVE_PATH = "save_path";
    /** path of the edited image. retrieve it in onActivityResult() */
    public static final String EXTRA_RESULT = "result_path";

    private static final int COLOR_RED = 0;
    private static final int COLOR_ORANGE = 1;
    private static final int COLOR_YELLOW = 2;
    private static final int COLOR_GREEN = 3;
    private static final int COLOR_BLUE_GREEN = 4;
    private static final int COLOR_BLUE = 5;
    private static final int COLOR_GRAY = 6;
    private static final int COLOR_SIZE = 7;

    private static final int LINE_WIDTH_1 = 5;
    private static final int LINE_WIDTH_2 = 15;
    private static final int LINE_WIDTH_3 = 25;

    private FrameLayout mImageContainer;
    private ImageView mImageView;
    private ImageView mIconWidth;
    private ImageView mIconColor;
    private ImageView mIconRect;
    private ImageView mIconPen;

    private Bitmap mBitmapOrigin;
    private Bitmap mBitmapDraw;
    private Bitmap mBitmapSwap;

    float downX = 0.0F;
    float downY = 0.0F;
    float rectX = 0.0F;
    float rectY = 0.0F;
    private int width;
    private int height;

    private boolean mDrawLine = true;
    private int mDrawWidth = LINE_WIDTH_2;
    private int mDrawColor = 0;

    private Canvas mCanvas;
    private Paint mPaint;
    private Path mPaintPath = new Path();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        final String imagePath = getIntent().getStringExtra(EXTRA_PATH);
        if (TextUtils.isEmpty(imagePath)) {
            toastShort(R.string.image_editor_toast_invalid_image);
            finish();
            return;
        }

        setContentView(R.layout.image_editor_activity);

        findViewById(R.id.image_editor_btn_width).setOnClickListener(mClickListener);
        findViewById(R.id.image_editor_btn_color).setOnClickListener(mClickListener);
        findViewById(R.id.image_editor_btn_rect).setOnClickListener(mClickListener);
        findViewById(R.id.image_editor_btn_pen).setOnClickListener(mClickListener);
        findViewById(R.id.image_editor_btn_clean).setOnClickListener(mClickListener);
        findViewById(R.id.image_editor_btn_done).setOnClickListener(mClickListener);
        findViewById(R.id.image_editor_action_bar_back).setOnClickListener(mClickListener);

        mIconPen = (ImageView) findViewById(R.id.image_editor_btn_pen_icon);
        mIconRect = (ImageView) findViewById(R.id.image_editor_btn_rect_icon);
        mIconWidth = (ImageView) findViewById(R.id.image_editor_btn_width_icon);
        mIconColor = (ImageView) findViewById(R.id.image_editor_btn_color_icon);
        mImageView = (ImageView) findViewById(R.id.image_editor_image);
        mImageContainer = (FrameLayout) findViewById(R.id.image_editor_image_container);

        refreshBtnDrawType();
        refreshBtnLineWidth();

        // action bar title
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (!TextUtils.isEmpty(title)) {
            TextView tvTitle = (TextView) findViewById(R.id.image_editor_action_bar_title);
            tvTitle.setText(title);
        }

        GlobalThreadManager.runInThreadPool(new Runnable() {
            @Override
            public void run() {
                mBitmapOrigin = BitmapUtils.file2bitmap(imagePath, 480, 640);
                if (mBitmapOrigin == null) {
                    toastShort(R.string.image_editor_toast_invalid_image);
                    finish();
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initImageView();
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBitmapOrigin != null) {
            mBitmapOrigin.recycle();
        }
        if (mBitmapDraw != null) {
            mBitmapDraw.recycle();
        }
        if (mBitmapSwap != null) {
            mBitmapSwap.recycle();
        }
    }

    protected FrameLayout getActionBarMenuFrame() {
        return (FrameLayout) findViewById(R.id.image_editor_action_bar_menu);
    }

    protected String saveEdit() {
        String path = getIntent().getStringExtra(EXTRA_SAVE_PATH);
        if (TextUtils.isEmpty(path)) {
            path = PathUtils.generateTmpPath(".jpg").getAbsolutePath();
        }
        if (BitmapUtils.saveBitmap(mBitmapDraw, path)) {
            return path;
        } else {
            return null;
        }
    }

    protected void submit(String result) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT, result);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void resetPaint() {
        mCanvas = new Canvas(mBitmapDraw);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        switch (mDrawColor % COLOR_SIZE) {
            case COLOR_RED:
                mPaint.setColor(Color.parseColor("#cf3427"));
                break;
            case COLOR_BLUE:
                mPaint.setColor(Color.parseColor("#1e7ed4"));
                break;
            case COLOR_BLUE_GREEN:
                mPaint.setColor(Color.parseColor("#45b39c"));
                break;
            case COLOR_GRAY:
                mPaint.setColor(Color.parseColor("#6b6f72"));
                break;
            case COLOR_GREEN:
                mPaint.setColor(Color.parseColor("#74b443"));
                break;
            case COLOR_ORANGE:
                mPaint.setColor(Color.parseColor("#f37700"));
                break;
            case COLOR_YELLOW:
                mPaint.setColor(Color.parseColor("#ffcc00"));
                break;
        }

        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mDrawWidth);
    }

    private void initImageView() {
        if (measureImageView()) {
            return;
        }
        mImageView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (measureImageView()) {
                            mImageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    }
                });
    }

    private boolean measureImageView() {
        if (width == 0 || height == 0) {
            int maxWidth = mImageContainer.getMeasuredWidth();
            int maxHeight = mImageContainer.getMeasuredHeight();
            if (maxWidth == 0 || maxHeight == 0) {
                return false;
            }

            int w = mBitmapOrigin.getWidth();
            int h = mBitmapOrigin.getHeight();
            float containerRatio = (float) maxWidth / maxHeight;
            float imageRatio = (float) w / h;
            if (imageRatio < containerRatio) {
                height = mImageContainer.getMeasuredHeight();
                width = height * w / h;
            } else {
                width = mImageContainer.getMeasuredWidth();
                height = width * h / w;
            }
            Log.d(TAG, "measure image, view: " + width + "," + height + ", bitmap: " + w + "," + h);
            Matrix matrix = new Matrix();
            matrix.postScale((float) (width * 1.0D / w), (float) (height * 1.0D / h));
            mBitmapDraw = Bitmap.createBitmap(mBitmapOrigin, 0, 0, w, h, matrix, true)
                    .copy(Bitmap.Config.ARGB_8888, true);
            resetPaint();
            mImageView.setImageBitmap(mBitmapDraw);
            mImageView.setOnTouchListener(mImageTouchListener);
        }

        return width != 0 && height != 0;
    }

    private void refreshBtnLineWidth() {
        if (mDrawWidth == LINE_WIDTH_3) {
            mIconWidth.setImageResource(R.drawable.image_editor_dot_large);
        } else if (mDrawWidth == LINE_WIDTH_2) {
            mIconWidth.setImageResource(R.drawable.image_editor_dot_middle);
        } else {
            mIconWidth.setImageResource(R.drawable.image_editor_dot_small);
        }
    }

    private void refreshBtnDrawType() {
        mIconPen.setImageResource(mDrawLine ?
                R.drawable.image_editor_pen_on : R.drawable.image_editor_pen_off);
        mIconRect.setImageResource(mDrawLine ?
                R.drawable.image_editor_rectangle_off : R.drawable.image_editor_rectangle_on);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.image_editor_btn_rect) {
                mDrawLine = false;
                resetPaint();
                refreshBtnDrawType();

            } else if (v.getId() == R.id.image_editor_btn_pen) {
                mDrawLine = true;
                resetPaint();
                refreshBtnDrawType();

            } else if (v.getId() == R.id.image_editor_btn_width) {
                if (mDrawWidth == LINE_WIDTH_3) {
                    mDrawWidth = LINE_WIDTH_1;
                } else if (mDrawWidth == LINE_WIDTH_2) {
                    mDrawWidth = LINE_WIDTH_3;
                } else {
                    mDrawWidth = LINE_WIDTH_2;
                }
                resetPaint();
                refreshBtnLineWidth();

            } else if (v.getId() == R.id.image_editor_btn_color) {
                mDrawColor += 1;
                resetPaint();
                switch (mDrawColor % COLOR_SIZE) {
                    case COLOR_RED:
                        mIconColor.setImageResource(R.drawable.image_editor_colorchoose_red);
                        break;
                    case COLOR_BLUE:
                        mIconColor.setImageResource(R.drawable.image_editor_colorchoose_blue);
                        break;
                    case COLOR_BLUE_GREEN:
                        mIconColor.setImageResource(R.drawable.image_editor_colorchoose_blue_green);
                        break;
                    case COLOR_GRAY:
                        mIconColor.setImageResource(R.drawable.image_editor_colorchoose_gray);
                        break;
                    case COLOR_GREEN:
                        mIconColor.setImageResource(R.drawable.image_editor_colorchoose_green);
                        break;
                    case COLOR_ORANGE:
                        mIconColor.setImageResource(R.drawable.image_editor_colorchoose_orange);
                        break;
                    case COLOR_YELLOW:
                        mIconColor.setImageResource(R.drawable.image_editor_colorchoose_yellow);
                        break;
                    default:
                        break;
                }

            } else if (v.getId() == R.id.image_editor_btn_clean) {
                if (mImageView != null && mBitmapDraw != null) {
                    int w = mBitmapOrigin.getWidth();
                    int h = mBitmapOrigin.getHeight();
                    Matrix matrix = new Matrix();
                    matrix.postScale((float) (width * 1.0D / w), (float) (height * 1.0D / h));
                    mBitmapDraw = Bitmap.createBitmap(mBitmapOrigin, 0, 0, w, h, matrix, true)
                            .copy(Bitmap.Config.ARGB_8888, true);
                    mImageView.setImageBitmap(mBitmapDraw);
                    resetPaint();
                    mImageView.invalidate();
                }

            } else if (v.getId() == R.id.image_editor_btn_done) {
                String result = saveEdit();
                if (result != null) {
                    submit(result);
                } else {
                    toastShort(R.string.image_editor_toast_save_fail);
                }

            } else if (v.getId() == R.id.image_editor_action_bar_back) {
                finish();
            }
        }
    };

    private View.OnTouchListener mImageTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float upx, upy;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    rectX = event.getX();
                    rectY = event.getY();
                    downX = event.getX();
                    downY = event.getY();
                    mPaintPath.moveTo(downX, downY);
                    mBitmapSwap = Bitmap.createBitmap(mBitmapDraw);
                    break;

                case MotionEvent.ACTION_MOVE:
                    upx = event.getX();
                    upy = event.getY();
                    if (mDrawLine) {
                        mBitmapDraw = Bitmap.createBitmap(mBitmapSwap);
                        mImageView.setImageBitmap(mBitmapDraw);
                        resetPaint();
                        mPaintPath.lineTo(upx, upy);
                        mCanvas.drawPath(mPaintPath, mPaint);
                    } else {
                        mBitmapDraw = Bitmap.createBitmap(mBitmapSwap);
                        mImageView.setImageBitmap(mBitmapDraw);
                        resetPaint();
                        mCanvas.drawLine(rectX, rectY, rectX, upy, mPaint);
                        mCanvas.drawLine(rectX, rectY, upx, rectY, mPaint);
                        mCanvas.drawLine(rectX, upy, upx, upy, mPaint);
                        mCanvas.drawLine(upx, rectY, upx, upy, mPaint);
                    }
                    downX = upx;
                    downY = upy;
                    mImageView.invalidate();
                    break;

                case MotionEvent.ACTION_UP:
                    upx = event.getX();
                    upy = event.getY();
                    if (mDrawLine) {
                        mPaintPath.reset();
                    } else {
                        mCanvas.drawLine(rectX, rectY, rectX, upy, mPaint);
                        mCanvas.drawLine(rectX, rectY, upx, rectY, mPaint);
                        mCanvas.drawLine(rectX, upy, upx, upy, mPaint);
                        mCanvas.drawLine(upx, rectY, upx, upy, mPaint);
                        mImageView.invalidate();
                    }
                    break;
            }
            return true;
        }
    };

}