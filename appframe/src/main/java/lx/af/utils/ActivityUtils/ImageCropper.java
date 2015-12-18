package lx.af.utils.ActivityUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.io.File;
import java.util.ArrayList;

import lx.af.activity.ImageCropper.ImageCropActivity;
import lx.af.activity.ImageCropper.ImageCropActivity.Extra;

/**
 * Builder for crop Intents and utils for handling result
 */
public class ImageCropper extends ActivityLauncherBase<Uri> {

    private static final int FROM_URI = 0;
    private static final int FROM_CAMERA = 1;
    private static final int FROM_GALLERY = 2;
    private static final int FROM_IMAGE_SELECTOR = 3;

    private int mRequestCode;
    private ActivityResultCallback<Uri> mCallback;

    private int mFrom = FROM_URI;
    private int mAspectX = 1;
    private int mAspectY = 1;
    private int mMaxX = 0;
    private int mMaxY = 0;
    private Uri mOriginUri;
    private Uri mOutputUri;
    private Uri mCameraUri;

    protected ImageCropper(Activity activity) {
        super(activity);
    }

    protected ImageCropper(Fragment fragment) {
        super(fragment);
    }

    public static ImageCropper of(Activity activity) {
        return new ImageCropper(activity);
    }

    public static ImageCropper of(Fragment fragment) {
        return new ImageCropper(fragment);
    }

    public ImageCropper output(Uri uri) {
        mOutputUri = uri;
        return this;
    }

    public ImageCropper output(String path) {
        mOutputUri = Uri.parse("file://" + path);
        return this;
    }

    public ImageCropper from(Uri source) {
        mFrom = FROM_URI;
        mOriginUri = source;
        return this;
    }

    public ImageCropper from(String path) {
        mFrom = FROM_URI;
        mOriginUri = Uri.parse("file://" + path);
        return this;
    }

    public ImageCropper fromCamera(Uri cameraOutput) {
        mFrom = FROM_CAMERA;
        mCameraUri = cameraOutput;
        return this;
    }

    public ImageCropper fromCamera(String cameraOutput) {
        mFrom = FROM_CAMERA;
        mCameraUri = Uri.parse("file://" + cameraOutput);
        return this;
    }

    public ImageCropper fromCamera() {
        // use camera default path as output
        mFrom = FROM_CAMERA;
        return this;
    }

    public ImageCropper fromGallery() {
        mFrom = FROM_GALLERY;
        return this;
    }

    public ImageCropper fromImageSelector() {
        mFrom = FROM_IMAGE_SELECTOR;
        return this;
    }

    public ImageCropper aspect(int aspectX, int aspectY) {
        mAspectX = aspectX;
        mAspectY = aspectY;
        return this;
    }

    public ImageCropper asSquare() {
        mAspectX = 1;
        mAspectY = 1;
        return this;
    }

    public ImageCropper maxSize(int maxX, int maxY) {
        mMaxX = maxX;
        mMaxY = maxY;
        return this;
    }

    @Override
    protected Uri extractResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            return data.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
        } else {
            return null;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mFrom = savedInstanceState.getInt("image_cropper_from");
        mAspectX = savedInstanceState.getInt("image_cropper_aspect_x");
        mAspectY = savedInstanceState.getInt("image_cropper_aspect_y");
        mMaxX = savedInstanceState.getInt("image_cropper_max_x");
        mMaxY = savedInstanceState.getInt("image_cropper_max_y");
        mOriginUri = savedInstanceState.getParcelable("image_cropper_origin_uri");
        mOutputUri = savedInstanceState.getParcelable("image_cropper_output_uri");
        mCameraUri = savedInstanceState.getParcelable("image_cropper_camera_uri");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("image_cropper_from", mFrom);
        outState.putInt("image_cropper_aspect_x", mAspectX);
        outState.putInt("image_cropper_aspect_y", mAspectY);
        outState.putInt("image_cropper_max_x", mMaxX);
        outState.putInt("image_cropper_max_y", mMaxY);
        if (mOriginUri != null) {
            outState.putParcelable("image_cropper_origin_uri", mOriginUri);
        }
        if (mOutputUri != null) {
            outState.putParcelable("image_cropper_output_uri", mOutputUri);
        }
        if (mCameraUri != null) {
            outState.putParcelable("image_cropper_camera_uri", mCameraUri);
        }
    }

    @Override
    protected int getDefaultRequestCode() {
        return RequestCode.IMAGE_CROP;
    }

    public Intent createIntent() {
        if (mOriginUri == null) {
            throw new IllegalStateException("crop source not set");
        }
        if (mOutputUri == null) {
            File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File file = new File(dcim, getPackageName() + "_crop_" + System.currentTimeMillis());
            mOutputUri = Uri.fromFile(file);
        }
        Intent cropIntent = newIntent(ImageCropActivity.class);
        cropIntent.setData(mOriginUri);
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputUri);
        cropIntent.putExtra(Extra.ASPECT_X, mAspectX);
        cropIntent.putExtra(Extra.ASPECT_Y, mAspectY);
        cropIntent.putExtra(Extra.MAX_X, mMaxX);
        cropIntent.putExtra(Extra.MAX_Y, mMaxY);
        return cropIntent;
    }

    @Override
    public void start(int requestCode, ActivityResultCallback<Uri> c) {
        mRequestCode = requestCode;
        mCallback = c;

        switch (mFrom) {
            case FROM_URI: {
                startCrop();
                break;
            }
            case FROM_CAMERA: {
                if (mActivity != null) {
                    ImageByCamera.of(mActivity).output(mCameraUri).start(mUriResultCallback);
                } else {
                    ImageByCamera.of(mFragment).output(mCameraUri).start(mUriResultCallback);
                }
                break;
            }
            case FROM_GALLERY: {
                if (mActivity != null) {
                    ImageByGallery.of(mActivity).start(mUriResultCallback);
                } else {
                    ImageByGallery.of(mFragment).start(mUriResultCallback);
                }
                break;
            }
            case FROM_IMAGE_SELECTOR: {
                if (mActivity != null) {
                    ImageSelector.of(mActivity).singleSelect().start(mSelectorCallback);
                } else {
                    ImageSelector.of(mFragment).singleSelect().start(mSelectorCallback);
                }
                break;
            }
        }
    }

    private void startCrop() {
        super.start(mRequestCode, mCallback);
    }

    private ActivityResultCallback<Uri> mUriResultCallback = new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(int resultCode, @NonNull Uri result) {
            mOriginUri = result;
            startCrop();
        }
    };

    private ActivityResultCallback<ArrayList<String>> mSelectorCallback =
            new ActivityResultCallback<ArrayList<String>>() {
                @Override
                public void onActivityResult(int resultCode, @NonNull ArrayList<String> result) {
                    if (result.size() != 0) {
                        String path = result.get(0);
                        mOriginUri = Uri.parse("file://" + path);
                        startCrop();
                    }
                }
            };

}
