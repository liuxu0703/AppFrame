package lx.af.demo.activity.DemoUtils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import lx.af.demo.R;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseActivity;
import lx.af.demo.utils.ImageLoaderHelper;
import lx.af.manager.GlobalThreadManager;
import lx.af.utils.ActivityLauncher.ActivityResultCallback;
import lx.af.utils.ActivityLauncher.ImageBrowserLauncher;
import lx.af.utils.ActivityLauncher.ImageSelectorLauncher;
import lx.af.utils.PathUtils;
import lx.af.utils.WatermarkHelper;
import lx.af.utils.WatermarkHelper.WatermarkPosition;
import lx.af.widget.NineGrid.NineGridLayout;
import lx.af.widget.NineGrid.NineImageUILAdapter;

/**
 * author: lx
 * date: 16-3-25
 */
public class WatermarkDemo extends BaseActivity implements
        View.OnClickListener,
        ActionBar.Default {

    private ImageView mWatermarkImageView;
    private NineGridLayout mResultImageGrid;
    private NineImageUILAdapter mImageAdapter;

    private String mImagePath;
    private String mWatermarkPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watermark);
        mResultImageGrid = (NineGridLayout) findViewById(R.id.watermark_image_grid);
        mWatermarkImageView = (ImageView) findViewById(R.id.watermark_image);
        findViewById(R.id.watermark_btn_select_image).setOnClickListener(this);
        findViewById(R.id.watermark_btn_select_watermark).setOnClickListener(this);

        mImageAdapter = new NineImageUILAdapter(mResultImageGrid);
        mImageAdapter.setOnItemClickListener(new NineImageUILAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, NineImageUILAdapter adapter, int position) {
                ImageBrowserLauncher.of(WatermarkDemo.this)
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
            case R.id.watermark_btn_select_image: {
                ImageSelectorLauncher.of(this).singleSelect().start(new ActivityResultCallback<ArrayList<String>>() {
                    @Override
                    public void onActivityResult(int resultCode, @NonNull ArrayList<String> result) {
                        mImagePath = result.get(0);
                        addWatermark();
                    }
                });
                break;
            }
            case R.id.watermark_btn_select_watermark: {
                ImageSelectorLauncher.of(this).singleSelect().start(new ActivityResultCallback<ArrayList<String>>() {
                    @Override
                    public void onActivityResult(int resultCode, @NonNull ArrayList<String> result) {
                        mWatermarkPath = result.get(0);
                        ImageLoaderHelper.displayImage(mWatermarkImageView, "file://" + mWatermarkPath);
                        if (mImagePath != null) {
                            addWatermark();
                        }
                    }
                });
                break;
            }
        }
    }

    private void addWatermark() {
        showLoadingDialog();
        GlobalThreadManager.runInThreadPool(new Runnable() {
            @Override
            public void run() {
                final ArrayList<String> imageUriList = new ArrayList<>(9);
                for (WatermarkPosition position : WatermarkPosition.values()) {
                    imageUriList.add("file://" + getImageWithWatermark(position));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mImageAdapter.setImageUris(imageUriList);
                        dismissLoadingDialog();
                    }
                });
            }
        });
    }

    private String getImageWithWatermark(WatermarkPosition position) {
        String path = PathUtils.generateTmpPath(".jpg").getAbsolutePath();
        WatermarkHelper.edit()
                .setImagePath(mImagePath)
                .setWatermarkPath(mWatermarkPath)
                .setPosition(position)
                .save2file(path);
        return path;
    }

}
