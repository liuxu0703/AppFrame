package lx.af.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import lx.af.R;
import lx.af.base.BaseActivity;
import lx.af.fragment.MultiImageSelectorFragment;

/**
 * 多图选择
 * Created by Nereo on 2015/4/7.
 *
 * import and modified by liuxu on 2015.04.22
 * https://github.com/lovetuzitong/MultiImageSelector
 */
public class MultiImageSelectorActivity extends BaseActivity implements
        BaseActivity.ActionBarCallbacks,
        MultiImageSelectorFragment.Callback {

    /** 最大图片选择次数，int类型，默认9 */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /** 图片选择模式，默认多选 */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /** 是否显示相机，默认显示 */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /** 选择结果，返回为图片路径集合  */
    public static final String EXTRA_RESULT = "select_result";
    /** 默认选择集 */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";

    /** 单选 */
    public static final int MODE_SINGLE = 0;
    /** 多选 */
    public static final int MODE_MULTI = 1;

    private ArrayList<String> resultList = new ArrayList<>();
    private Button mSubmitButton;
    private int mDefaultCount;
    private int mMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_activity);

        Intent intent = getIntent();
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, 9);
        mMode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);
        boolean isShowCamera = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        if (mMode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
        }
        if (mMode == MODE_SINGLE) {
            // single select mode does not need confirm button
            mSubmitButton.setVisibility(View.GONE);
        }

        Bundle bundle = new Bundle();
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount);
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mMode);
        bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShowCamera);
        bundle.putStringArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);

        Fragment fragment = Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.mis_activity_image_grid, fragment)
                .commit();
    }

    @Override
    public void onSingleImageSelected(String path) {
        Intent data = new Intent();
        resultList.add(path);
        data.putStringArrayListExtra(EXTRA_RESULT, resultList);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onImageSelected(String path) {
        if (!resultList.contains(path)) {
            resultList.add(path);
        }
        // 有图片之后，改变按钮状态
        if (resultList.size() > 0) {
            String txt = getString(
                    R.string.mis_finish_btn_with_amount, resultList.size(), mDefaultCount);
            mSubmitButton.setText(txt);
            if (!mSubmitButton.isEnabled()) {
                mSubmitButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onImageUnselected(String path) {
        if (resultList.contains(path)) {
            resultList.remove(path);
        }

        // 当为选择图片时候的状态
        if (resultList.size() == 0) {
            mSubmitButton.setText(R.string.mis_finish_btn);
            mSubmitButton.setEnabled(false);
        } else {
            String txt = getString(
                    R.string.mis_finish_btn_with_amount, resultList.size(), mDefaultCount);
            mSubmitButton.setText(txt);
        }
    }

    @Override
    public void onCameraShot(File imageFile) {
        if (imageFile != null) {
            Intent data = new Intent();
            resultList.add(imageFile.getAbsolutePath());
            data.putStringArrayListExtra(EXTRA_RESULT, resultList);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public View onCreateActionBarMenu() {
        LayoutInflater inflater = LayoutInflater.from(this);
        mSubmitButton = (Button) inflater.inflate(R.layout.mis_btn_submit, null);
        if (resultList == null || resultList.size() <= 0) {
            mSubmitButton.setText(R.string.mis_finish_btn);
            mSubmitButton.setEnabled(false);
        } else {
            String txt = getString(
                    R.string.mis_finish_btn_with_amount, resultList.size(), mDefaultCount);
            mSubmitButton.setText(txt);
            mSubmitButton.setEnabled(true);
        }

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resultList != null && resultList.size() > 0) {
                    // 返回已选择的图片数据
                    Intent data = new Intent();
                    data.putStringArrayListExtra(EXTRA_RESULT, resultList);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });

        return mSubmitButton;
    }

    @Override
    public void onActionBarCreated(View actionBar, ImageView back, TextView title, @Nullable View menu) {

    }

    @Override
    public boolean onActionBarBackClicked(View back) {
        setResult(RESULT_CANCELED);
        finish();
        return true;
    }
}
