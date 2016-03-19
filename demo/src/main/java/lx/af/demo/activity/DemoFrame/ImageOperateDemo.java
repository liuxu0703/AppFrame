package lx.af.demo.activity.DemoFrame;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import lx.af.activity.ImageBrowser.ImageBrowserActivity;
import lx.af.demo.R;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseActivity;
import lx.af.demo.utils.Paths;
import lx.af.utils.ActivityLauncher.ActivityResultCallback;
import lx.af.utils.ActivityLauncher.ImageBrowserEditLauncher;
import lx.af.utils.ActivityLauncher.ImageBrowserLauncher;
import lx.af.utils.ActivityLauncher.ImageCropperLauncher;
import lx.af.utils.ActivityLauncher.ImageSelectorLauncher;
import lx.af.utils.log.Log;
import lx.af.widget.NineGrid.NineGridLayout;
import lx.af.widget.NineGrid.NineImageUILAdapter;

/**
 * Created by liuxu on 15-2-9.
 *
 */
public class ImageOperateDemo extends BaseActivity implements
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
                final String current = adapter.getData(position);
                if (position % 2 == 0) {
                    ImageBrowserLauncher.of(ImageOperateDemo.this)
                            .uris(adapter.getImageUris())
                            .currentUri(current)
                            .currentView(view)
                            .tapExit(true)
                            .start();
                } else {
                    ImageBrowserEditLauncher.of(ImageOperateDemo.this)
                            .uri(current)
                            .start(new ActivityResultCallback<String>() {
                                @Override
                                public void onActivityResult(int resultCode, @NonNull String result) {
                                    List<String> list = mImageGridAdapter.getImageUris();
                                    int idx = list.indexOf(current);
                                    list.add(idx, "file://" + result);
                                    list.remove(current);
                                    mImageGridAdapter.setImageUris(list);
                                }
                            });
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.apid_btn_from_camera: {
                retrieveCropParams();
                String targetPath = Paths.generateCropImagePath().toString();
                String cameraPath = Paths.generateCameraImagePath().toString();
                ImageCropperLauncher.of(this)
                        .fromCamera(cameraPath).output(targetPath)
                        .maxSize(mCropMaxSize, mCropMaxSize)
                        .aspect(mCropAspectX, mCropAspectY)
                        .start(mCropCallback);
                break;
            }
            case R.id.apid_btn_from_gallery: {
                retrieveCropParams();
                String targetPath = Paths.generateCropImagePath().toString();
                ImageCropperLauncher.of(this)
                        .fromGallery().output(targetPath)
                        .maxSize(mCropMaxSize, mCropMaxSize)
                        .aspect(mCropAspectX, mCropAspectY)
                        .start(mCropCallback);
                break;
            }
            case R.id.apid_btn_from_multi_selector: {
                retrieveCropParams();
                String targetPath = Paths.generateCropImagePath().toString();
                ImageCropperLauncher.of(this)
                        .fromImageSelector().output(targetPath)
                        .maxSize(mCropMaxSize, mCropMaxSize)
                        .aspect(mCropAspectX, mCropAspectY)
                        .start(mCropCallback);
                break;
            }
            case R.id.apid_btn_multi_image: {
                int count = getSelectCount();
                ImageSelectorLauncher.of(this).count(count).showCamera(true).start(mSelectorCallback);
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

}
