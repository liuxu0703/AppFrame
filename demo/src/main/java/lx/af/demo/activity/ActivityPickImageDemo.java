package lx.af.demo.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import lx.af.activity.MultiImageSelectorActivity;
import lx.af.base.BaseActivity;
import lx.af.demo.R;
import lx.af.demo.base.BaseDemoActivity;
import lx.af.utils.PathUtils;
import lx.af.utils.log.Log;
import lx.af.view.crop.Crop;

/**
 * Created by liuxu on 15-2-9.
 *
 */
public class ActivityPickImageDemo extends BaseDemoActivity implements
        View.OnClickListener,
        BaseActivity.ActionBarImpl,
        BaseActivity.SwipeBackImpl {

    // activity request code
    private static final int AC_REQUEST_CODE_CAMERA = 101; // get image from camera
    private static final int AC_REQUEST_CODE_GALLERY = 102; // get image from gallery
    private static final int AC_REQUEST_CODE_MULTI_SELECTOR = 103; // get image use multi-image-selector
    private static final int AC_REQUEST_CODE_CROP = 104; // crop the selected image

    private ImageView mAvatar;
    private String mImgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);
        findViewById(R.id.activity_avatar_btn_from_camera).setOnClickListener(this);
        findViewById(R.id.activity_avatar_btn_from_gallery).setOnClickListener(this);
        findViewById(R.id.activity_avatar_btn_from_multi_selector).setOnClickListener(this);
        mAvatar = getView(R.id.activity_avatar_img);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult, requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(data).getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case AC_REQUEST_CODE_CAMERA: {
                Uri uri = Uri.parse("file://" + mImgPath);
                String targetPath = PathUtils.generateGallerySavePath("crop");
                if (!PathUtils.ensurePathExistsWithErrorToast(targetPath, false)) {
                    return;
                }
                startImageCropper(uri, targetPath, AC_REQUEST_CODE_CROP);
                break;
            }
            case AC_REQUEST_CODE_GALLERY: {
                if (data == null) {
                    return;
                }
                String targetPath = PathUtils.generateGallerySavePath("crop");
                if (!PathUtils.ensurePathExistsWithErrorToast(targetPath, false)) {
                    return;
                }
                startImageCropper(data.getData(), targetPath, AC_REQUEST_CODE_CROP);
                break;
            }
            case AC_REQUEST_CODE_MULTI_SELECTOR: {
                ArrayList<String> list = data.getStringArrayListExtra(
                        MultiImageSelectorActivity.EXTRA_RESULT);
                if (list != null && list.size() != 0) {
                    Uri uri = Uri.parse("file://" + list.get(0));
                    String targetPath = PathUtils.generateGallerySavePath("crop");
                    if (!PathUtils.ensurePathExistsWithErrorToast(targetPath, false)) {
                        return;
                    }
                    startImageCropper(uri, targetPath, AC_REQUEST_CODE_CROP);
                }
                break;
            }
            case AC_REQUEST_CODE_CROP: {
                if (data == null) {
                    return;
                }
                Uri uri = Crop.getOutput(data);
                Log.d(TAG, "crop image result: " + uri);
                mAvatar.setImageURI(Crop.getOutput(data));
                break;
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_avatar_btn_from_camera: {
                mImgPath = PathUtils.generateGallerySavePath("cam");
                if (!PathUtils.ensurePathExistsWithErrorToast(mImgPath, false)) {
                    return;
                }
                File file = new File(mImgPath);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, AC_REQUEST_CODE_CAMERA);
                break;
            }
            case R.id.activity_avatar_btn_from_gallery: {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, AC_REQUEST_CODE_GALLERY);
                break;
            }
            case R.id.activity_avatar_btn_from_multi_selector: {
                startImagePicker(AC_REQUEST_CODE_MULTI_SELECTOR);
            }
        }
    }

    public void startImagePicker(int requestCode) {
        Intent intent = new Intent(this, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
        startActivityForResult(intent, requestCode);
    }

    public void startImageCropper(Uri uri, String targetPath, int requestCode) {
        Uri destination = Uri.parse("file://" + targetPath);
        Crop.of(uri, destination).asSquare().start(this, requestCode);
    }

}
