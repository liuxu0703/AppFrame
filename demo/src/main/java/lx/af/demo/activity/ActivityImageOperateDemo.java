package lx.af.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.ArrayList;
import java.util.List;

import lx.af.activity.ImageBrowser.ImageBrowserActivity;
import lx.af.adapter.AbsListAdapter;
import lx.af.demo.R;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseDemoActivity;
import lx.af.utils.ActivityUtils.ActivityResultCallback;
import lx.af.utils.ActivityUtils.ImageBrowser;
import lx.af.utils.ActivityUtils.ImageCropper;
import lx.af.utils.ActivityUtils.ImageSelector;
import lx.af.utils.PathUtils;
import lx.af.utils.ScreenUtils;
import lx.af.utils.log.Log;
import lx.af.view.NineGrid.NineGridLayout;
import lx.af.view.NineGrid.NineImageUILAdapter;

/**
 * Created by liuxu on 15-2-9.
 *
 */
public class ActivityImageOperateDemo extends BaseDemoActivity implements
        View.OnClickListener,
        ActionBar.Default {

    private EditText mEditorSize;
    private EditText mEditorAspectW;
    private EditText mEditorAspectH;
    private RadioGroup mRadioGroup;
    private NineGridLayout mImageGrid;
    private NineImageUILAdapter mImageGridAdapter;

    private int mCropMaxSize;
    private int mCropAspectX;
    private int mCropAspectY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_operate);
        findViewById(R.id.apid_btn_from_camera).setOnClickListener(this);
        findViewById(R.id.apid_btn_from_gallery).setOnClickListener(this);
        findViewById(R.id.apid_btn_from_multi_selector).setOnClickListener(this);
        findViewById(R.id.apid_btn_multi_image).setOnClickListener(this);
        mEditorSize = obtainView(R.id.apid_editor_size);
        mEditorAspectW = obtainView(R.id.apid_editor_aspect_width);
        mEditorAspectH = obtainView(R.id.apid_editor_aspect_height);
        mRadioGroup = obtainView(R.id.apid_radio_group);
        mImageGrid = obtainView(R.id.apid_img_9_grid);
        RadioButton radio = obtainView(R.id.apid_radio_9);
        radio.setChecked(true);

        mImageGridAdapter = new NineImageUILAdapter(mImageGrid);
        mImageGridAdapter.setOnItemClickListener(new NineImageUILAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, NineImageUILAdapter adapter, int position) {
                ImageBrowser.of(ActivityImageOperateDemo.this)
                        .uris(adapter.getImageUris())
                        .currentUri(adapter.getData(position))
                        .currentView(view)
                        .tapExit(true)
                        .start();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.apid_btn_from_camera: {
                retrieveCropParams();
                String targetPath = PathUtils.generateGallerySavePath("crop");
                String cameraPath = PathUtils.generateGallerySavePath("cam");
                ImageCropper.of(this)
                        .fromCamera(cameraPath).output(targetPath)
                        .maxSize(mCropMaxSize, mCropMaxSize)
                        .aspect(mCropAspectX, mCropAspectY)
                        .start(mCropCallback);
                break;
            }
            case R.id.apid_btn_from_gallery: {
                retrieveCropParams();
                String targetPath = PathUtils.generateGallerySavePath("crop");
                ImageCropper.of(this)
                        .fromGallery().output(targetPath)
                        .maxSize(mCropMaxSize, mCropMaxSize)
                        .aspect(mCropAspectX, mCropAspectY)
                        .start(mCropCallback);
                break;
            }
            case R.id.apid_btn_from_multi_selector: {
                retrieveCropParams();
                String targetPath = PathUtils.generateGallerySavePath("crop");
                ImageCropper.of(this)
                        .fromImageSelector().output(targetPath)
                        .maxSize(mCropMaxSize, mCropMaxSize)
                        .aspect(mCropAspectX, mCropAspectY)
                        .start(mCropCallback);
                break;
            }
            case R.id.apid_btn_multi_image: {
                int count = getSelectCount();
                ImageSelector.of(this).count(count).showCamera(true).start(mSelectorCallback);
                break;
            }
        }
    }

    private ActivityResultCallback<Uri> mCropCallback = new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(int resultCode, @NonNull Uri result) {
            Log.d(TAG, "crop image result: " + result);
            List<String> list = new ArrayList<>(1);
            list.add(result.toString());
            mImageGridAdapter.setImageUris(list);
        }
    };

    private ActivityResultCallback<ArrayList<String>> mSelectorCallback = new ActivityResultCallback<ArrayList<String>>() {
        @Override
        public void onActivityResult(int resultCode, @NonNull ArrayList<String> list) {
            if (list.size() != 0) {
                ArrayList<String> uris = new ArrayList<>(list.size());
                for (String path : list) {
                    uris.add(Uri.parse("file://" + path).toString());
                }
                mImageGridAdapter.setImageUris(uris);
            }
        }
    };

    private int getSelectCount() {
        int id = mRadioGroup.getCheckedRadioButtonId();
        switch (id) {
            case R.id.apid_radio_1:
                return 1;
            case R.id.apid_radio_4:
                return 4;
            case R.id.apid_radio_9:
                return 9;
        }
        return 9;
    }

    private void retrieveCropParams() {
        try {
            mCropMaxSize = Integer.parseInt(mEditorSize.getText().toString());
        } catch (NumberFormatException ignore) {
        }
        try {
            mCropAspectX = Integer.parseInt(mEditorAspectW.getText().toString());
        } catch (NumberFormatException ignore) {
        }
        try {
            mCropAspectY = Integer.parseInt(mEditorAspectH.getText().toString());
        } catch (NumberFormatException ignore) {
        }
    }

    private void startImageBrowser(ArrayList<String> imgUris, String currentUri) {
        Intent intent = new Intent(this, ImageBrowserActivity.class);
        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URI_LIST, imgUris);
        intent.putExtra(ImageBrowserActivity.EXTRA_CURRENT_IMAGE_URI, currentUri);
        startActivity(intent);
    }

    private static class ImageAdapter extends AbsListAdapter<String> {

        ImageSize imageSize = new ImageSize(200, 200);
        int size;

        public ImageAdapter(Context context, List<String> list) {
            super(context, list);
            size = ScreenUtils.getScreenWidth() / 3 - ScreenUtils.dip2px(3);
        }

        @Override
        public View getView(Context context, int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                ImageView img = new ImageView(context);
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                convertView = img;
            }

            /** Fixed View Size */
            GridView.LayoutParams lp = (GridView.LayoutParams) convertView.getLayoutParams();
            if (lp == null || lp.height != size) {
                GridView.LayoutParams p = new GridView.LayoutParams(size, size);
                convertView.setLayoutParams(p);
            }

            String uri = getItem(position);
            ImageLoader.getInstance().displayImage(uri, (ImageView) convertView, imageSize);
            return convertView;
        }
    }

}
