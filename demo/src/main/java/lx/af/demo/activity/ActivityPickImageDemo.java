package lx.af.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.File;
import java.util.ArrayList;

import lx.af.activity.ImageBrowserActivity;
import lx.af.activity.MultiImageSelectorActivity;
import lx.af.activity.MultiImageSelectorBrowser;
import lx.af.base.BaseActivity;
import lx.af.demo.R;
import lx.af.demo.base.BaseDemoActivity;
import lx.af.utils.PathUtils;
import lx.af.utils.log.Log;
import lx.af.view.SquareImageView;
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
    private static final int AC_REQUEST_CODE_MULTI_IMAGE = 104; // get multi image use multi-image-selector
    private static final int AC_REQUEST_CODE_CROP = 105; // crop the selected image

    private GridView mImageGrid;
    private EditText mEditorSize;
    private EditText mEditorAspectW;
    private EditText mEditorAspectH;
    private RadioGroup mRadioGroup;

    private String mCameraPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_image);
        findViewById(R.id.activity_avatar_btn_from_camera).setOnClickListener(this);
        findViewById(R.id.activity_avatar_btn_from_gallery).setOnClickListener(this);
        findViewById(R.id.activity_avatar_btn_from_multi_selector).setOnClickListener(this);
        findViewById(R.id.activity_avatar_btn_multi_image).setOnClickListener(this);
        mEditorSize = getView(R.id.activity_avatar_editor_size);
        mEditorAspectW = getView(R.id.activity_avatar_editor_aspect_width);
        mEditorAspectH = getView(R.id.activity_avatar_editor_aspect_height);
        mRadioGroup = getView(R.id.activity_avatar_radio_group);
        mImageGrid = getView(R.id.activity_avatar_img_grid);
        RadioButton radio = getView(R.id.activity_avatar_radio_9);
        radio.setChecked(true);

        mImageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageAdapter adapter = (ImageAdapter) parent.getAdapter();
                startImageBrowser(adapter.getImageList(), adapter.getItem(position));
            }
        });
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
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "image picker demo, onActivityResult," +
                " requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(data).getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case AC_REQUEST_CODE_CAMERA: {
                Uri uri = Uri.parse("file://" + mCameraPath);
                String targetPath = PathUtils.generateGallerySavePath("crop");
                if (!PathUtils.ensurePathExistsWithErrorToast(targetPath, false)) {
                    return;
                }
                startImageCrop(uri);
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
                startImageCrop(data.getData());
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
                    startImageCrop(uri);
                }
                break;
            }
            case AC_REQUEST_CODE_CROP: {
                if (data == null) {
                    return;
                }
                Uri uri = Crop.getOutput(data);
                Log.d(TAG, "crop image result: " + uri);
                mImageGrid.setAdapter(new ImageAdapter(this, Crop.getOutput(data).toString()));
                break;
            }
            case AC_REQUEST_CODE_MULTI_IMAGE: {
                ArrayList<String> list = data.getStringArrayListExtra(
                        MultiImageSelectorActivity.EXTRA_RESULT);
                if (list != null && list.size() != 0) {
                    ArrayList<String> uris = new ArrayList<>(list.size());
                    for (String path : list) {
                        uris.add(Uri.parse("file://" + path).toString());
                    }
                    mImageGrid.setAdapter(new ImageAdapter(this, uris));
                }
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_avatar_btn_from_camera: {
                mCameraPath = PathUtils.generateGallerySavePath("cam");
                if (!PathUtils.ensurePathExistsWithErrorToast(mCameraPath, false)) {
                    return;
                }
                File file = new File(mCameraPath);
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
                startMultiImageSelector(1, true, AC_REQUEST_CODE_MULTI_SELECTOR);
                break;
            }
            case R.id.activity_avatar_btn_multi_image: {
                int count = getSelectCount();
                startMultiImageSelector(count, true, AC_REQUEST_CODE_MULTI_IMAGE);
                break;
            }
        }
    }

    private int getSelectCount() {
        int id = mRadioGroup.getCheckedRadioButtonId();
        switch (id) {
            case R.id.activity_avatar_radio_1:
                return 1;
            case R.id.activity_avatar_radio_4:
                return 4;
            case R.id.activity_avatar_radio_9:
                return 9;
        }
        return 9;
    }

    private void startMultiImageSelector(int count, boolean showCamera, int requestCode) {
        Intent intent = new Intent(this, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, showCamera);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, count);
        startActivityForResult(intent, requestCode);
    }

    private void startImageCrop(Uri uri) {
        Log.d(TAG, "image picker demo, startImageCrop, uri=" + uri);
        int size = 1000;
        int aspectW = 1;
        int aspectH = 1;
        try {
            size = Integer.parseInt(mEditorSize.getText().toString());
            aspectW = Integer.parseInt(mEditorAspectW.getText().toString());
            aspectH = Integer.parseInt(mEditorAspectH.getText().toString());
        } catch (NumberFormatException ignore) {
        }

        String targetPath = PathUtils.generateGallerySavePath("crop");
        Uri destination = Uri.parse("file://" + targetPath);
        if (aspectW == aspectH) {
            Crop.of(uri, destination).asSquare().withMaxSize(size, size).start(this, AC_REQUEST_CODE_CROP);
        } else {
            Crop.of(uri, destination).withAspect(aspectW, aspectH).withMaxSize(size, size).start(this, AC_REQUEST_CODE_CROP);
        }
    }

    private void startImageBrowser(ArrayList<String> imgUris, String currentUri) {
        Intent intent = new Intent(this, ImageBrowserActivity.class);
        intent.putExtra(MultiImageSelectorBrowser.EXTRA_IMAGE_URI_LIST, imgUris);
        intent.putExtra(MultiImageSelectorBrowser.EXTRA_CURRENT_IMAGE_URI, currentUri);
        startActivity(intent);
    }

    private static class ImageAdapter extends BaseAdapter {

        Context context;
        ArrayList<String> images;
        ImageSize imageSize = new ImageSize(200, 200);

        public ImageAdapter(Context context, ArrayList<String> imageUris) {
            this.context = context;
            this.images = imageUris;
        }

        public ImageAdapter(Context context, String imageUri) {
            this.context = context;
            this.images = new ArrayList<>();
            this.images.add(imageUri);
        }

        public ArrayList<String> getImageList() {
            return images;
        }

        @Override
        public int getCount() {
            return images == null ? 0 : images.size();
        }

        @Override
        public String getItem(int position) {
            return images == null ? null : images.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                SquareImageView img = new SquareImageView(context);
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                convertView = img;
            }
            String uri = getItem(position);
            ImageLoader.getInstance().displayImage(uri, (ImageView) convertView, imageSize);
            return convertView;
        }
    }

}
