package lx.af.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lx.af.R;
import lx.af.base.BaseActivity;
import lx.af.view.CropImage.CropImageLayout;

public class CropImageActivity extends BaseActivity implements BaseActivity.ActionBarImpl {

    private CropImageLayout mImageView;
    private Uri mSaveUri;
    private int mMaxWidth;
    private int mQuality;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        initView();
        setupFromIntent();
    }

    public void initView() {
        mImageView = (CropImageLayout) findViewById(R.id.activity_cropimage_clip);
        findViewById(R.id.activity_crop_image_btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOutput();
                setResult();
                finish();
            }
        });
        findViewById(R.id.activity_crop_image_btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupFromIntent() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            mMaxWidth = extras.getInt(CropBuilder.Extra.MAX_WIDTH);
            mQuality = extras.getInt(CropBuilder.Extra.QUALITY, 100);
            mSaveUri = extras.getParcelable(MediaStore.EXTRA_OUTPUT);
        }

        Uri data = getIntent().getData();
        if (data == null) {
            finish();
        }
        Bitmap b = retrieveBitmap(data);
        if (b != null) {
            mImageView.setImageBitmap(b);
        } else {
            Toast.makeText(this, "can not open image", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private Bitmap retrieveBitmap(Uri uri) {
        int degree = 0;
        try {
            // rotate according to exif
            ExifInterface exifInterface = new ExifInterface(uri.getPath());
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream is = null;
        Bitmap b = null;
        try {
            int sampleSize = calculateBitmapSampleSize(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = sampleSize;
            is = getContentResolver().openInputStream(uri);
            b = BitmapFactory.decodeStream(is, null, options);
            mImageView.setImageBitmap(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSilently(is);
        }

        if (b == null || degree == 0) {
            return b;
        }

        Matrix m = new Matrix();
        m.setRotate(degree, (float) b.getWidth(), (float) b.getHeight());
        try {
            Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
            if (b != b2) {
                b.recycle();
                b = b2;
            }
        } catch (OutOfMemoryError ignore) {
        }
        return b;
    }

    private int calculateBitmapSampleSize(Uri bitmapUri) throws IOException {
        InputStream is = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            is = getContentResolver().openInputStream(bitmapUri);
            BitmapFactory.decodeStream(is, null, options); // Just get image size
        } finally {
            closeSilently(is);
        }

        int maxSize = getMaxImageSize();
        int sampleSize = 1;
        while (options.outHeight / sampleSize > maxSize || options.outWidth / sampleSize > maxSize) {
            sampleSize = sampleSize << 1;
        }

        return sampleSize;
    }

    private int getMaxImageSize() {
        int textureLimit = getScreenHeight();
        if (textureLimit == 0) {
            return CropImageLayout.MAX_WIDTH;
        } else {
            return Math.min(textureLimit, CropImageLayout.MAX_WIDTH);
        }
    }

    private int getScreenHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    private void saveOutput() {
        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = getContentResolver().openOutputStream(mSaveUri);
                Bitmap b = mImageView.clip();

                if (outputStream != null && b != null) {
                    if (mMaxWidth > 0 && b.getWidth() > mMaxWidth) {
                        b = Bitmap.createScaledBitmap(b, mMaxWidth, mMaxWidth, true);
                    }
                    b.compress(Bitmap.CompressFormat.JPEG, mQuality, outputStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeSilently(outputStream);
            }
        }
    }

    private void setResult() {
        Intent intent = new Intent();
        intent.setData(mSaveUri);
        setResult(RESULT_OK, intent);
    }

    public static void closeSilently(Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (Throwable t) {
            // Do nothing
        }
    }

    /**
     * Builder for crop Intents and utils for handling result
     * Created by sam on 14-10-16.
     */
    public static class CropBuilder {

        private int mRequestCode;

        static interface Extra {
            String MAX_WIDTH = "max_width";
            String QUALITY = "quality";
            String ERROR = "error";
        }

        private Intent cropIntent;

        /**
         * Create a crop Intent builder with source image
         *
         * @param source Source image URI
         */
        public CropBuilder(Uri source, int requestCode) {
            cropIntent = new Intent();
            cropIntent.setData(source);
            mRequestCode = requestCode;
        }

        /**
         * Create a crop Intent builder with source image
         *
         * @param source Source image file
         */
        public CropBuilder(File source, int requestCode) {
            this(Uri.fromFile(source), requestCode);
        }

        /**
         * Set output URI where the cropped image will be saved
         *
         * @param output Output image URI
         */
        public CropBuilder output(Uri output) {
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, output);
            return this;
        }

        /**
         * Set output URI where the cropped image will be saved
         *
         * @param file Output image file
         */
        public CropBuilder output(File file) {
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            return this;
        }

        /**
         * Set maximum crop size
         *
         * @param width Max width
         */
        public CropBuilder withWidth(int width) {
            cropIntent.putExtra(Extra.MAX_WIDTH, width);
            return this;
        }

        /**
         * set image quality when save to file
         *
         * @param quality hint to the compressor, 0-100.
         */
        public CropBuilder withQuality(int quality) {
            if (quality > 0 && quality <= 100) {
                cropIntent.putExtra(Extra.QUALITY, quality);
            }
            return this;
        }

        /**
         * Send the crop Intent!
         *
         * @param activity Activity that will receive result
         */
        public void start(Activity activity) {
            activity.startActivityForResult(getIntent(activity), mRequestCode);
        }

        /**
         * Send the crop Intent!
         *
         * @param context Context
         * @param fragment Fragment that will receive result
         */
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public void start(Context context, Fragment fragment) {
            fragment.startActivityForResult(getIntent(context), mRequestCode);
        }

        /**
         * Send the crop Intent!
         *
         * @param context Context
         * @param fragment Fragment that will receive result
         */
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public void start(Context context, android.support.v4.app.Fragment fragment) {
            fragment.startActivityForResult(getIntent(context), mRequestCode);
        }

        private Intent getIntent(Context context) {
            cropIntent.setClass(context, CropImageActivity.class);
            return cropIntent;
        }

        /**
         * Retrieve URI for cropped image, as set in the Intent builder
         *
         * @param result Output Image URI
         */
        public static Uri getOutput(Intent result) {
            return result.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
        }

        /**
         * Retrieve error that caused crop to fail
         *
         * @param result Result Intent
         * @return Throwable handled in CropImageActivity
         */
        public static Throwable getError(Intent result) {
            return (Throwable) result.getSerializableExtra(Extra.ERROR);
        }

    }
}
