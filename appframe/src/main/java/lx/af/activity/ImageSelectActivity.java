package lx.af.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lx.af.R;
import lx.af.base.BaseActivity;
import lx.af.fragment.ImageSelectFragment;

/**
 * 多图选择
 * Created by Nereo on 2015/4/7.
 *
 * import and modified by liuxu on 2015.04.22
 * https://github.com/lovetuzitong/MultiImageSelector
 */
public class ImageSelectActivity extends BaseActivity implements
        View.OnClickListener,
        ImageSelectFragment.Callback {

    /** 最大图片选择次数，int类型，默认9 */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /** 是否显示相机，默认显示 */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /** 默认选择集 */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";

    /** 选择结果，返回为图片路径集合  */
    public static final String EXTRA_RESULT = "select_result";

    private ArrayList<String> mResultList = new ArrayList<>();
    private Button mSubmitButton;
    private int mDefaultCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_activity);
        findViewById(R.id.mis_activity_btn_back).setOnClickListener(this);
        mSubmitButton = obtainView(R.id.mis_activity_btn_submit);
        mSubmitButton.setOnClickListener(this);

        Intent intent = getIntent();
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, 9);
        boolean isShowCamera = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        if (mDefaultCount > 1 && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            mResultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
        }
        if (mDefaultCount == 1) {
            // single select mode does not need submit button
            mSubmitButton.setVisibility(View.INVISIBLE);
        }
        refreshSubmitButton();

        Bundle bundle = new Bundle();
        bundle.putInt(ImageSelectFragment.EXTRA_SELECT_COUNT, mDefaultCount);
        bundle.putBoolean(ImageSelectFragment.EXTRA_SHOW_CAMERA, isShowCamera);
        bundle.putStringArrayList(ImageSelectFragment.EXTRA_DEFAULT_SELECTED_LIST, mResultList);

        Fragment fragment = Fragment.instantiate(this, ImageSelectFragment.class.getName(), bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.mis_activity_image_grid, fragment)
                .commit();
    }

    @Override
    public void onImageSelected(String path) {
        if (!mResultList.contains(path)) {
            mResultList.add(path);
        }
        refreshSubmitButton();
    }

    @Override
    public void onImageUnselected(String path) {
        if (mResultList.contains(path)) {
            mResultList.remove(path);
        }
        refreshSubmitButton();
    }

    @Override
    public void onRefreshImageSelected(List<String> paths) {
        mResultList.clear();
        mResultList.addAll(paths);
        refreshSubmitButton();
    }

    @Override
    public void onCameraShot(File imageFile) {
        if (imageFile != null) {
            mResultList.add(imageFile.getAbsolutePath());
            onSelectDone(mResultList);
        }
    }

    @Override
    public void onSelectDone(ArrayList<String> paths) {
        if (paths != null && paths.size() != 0) {
            mResultList = paths;
            Intent data = new Intent();
            data.putStringArrayListExtra(EXTRA_RESULT, paths);
            setResult(RESULT_OK, data);
            finish();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void refreshSubmitButton() {
        // only refresh submit button on multi-select mode
        if (mDefaultCount > 1) {
            if (mResultList == null || mResultList.size() == 0) {
                mSubmitButton.setText(R.string.mis_finish_btn);
                mSubmitButton.setEnabled(false);
            } else {
                String txt = getString(
                        R.string.mis_finish_btn_with_amount, mResultList.size(), mDefaultCount);
                mSubmitButton.setText(txt);
                mSubmitButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.mis_activity_btn_back) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (id == R.id.mis_activity_btn_submit) {
            onSelectDone(mResultList);
        }
    }
}
