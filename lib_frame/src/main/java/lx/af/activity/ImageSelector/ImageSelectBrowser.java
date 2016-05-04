package lx.af.activity.ImageSelector;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lx.af.R;
import lx.af.activity.ImageBrowser.ImageBrowserActivity;

/**
 * author: lx
 * date: 15-12-2
 *
 * big image browser for Multi-Image-Selector
 */
public class ImageSelectBrowser extends ImageBrowserActivity {

    /** already selected uri list */
    public static final String EXTRA_SELECTED_LIST = "max_select_list";
    /** desired image count */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";

    /** select result by this activity, a list of image file path (not uri) */
    public static final String EXTRA_RESULT = "select_result";
    /** select result, whether user has done select. if not, go back to multi-image-selector */
    public static final String EXTRA_RESULT_DONE = "select_result_done";

    private int mSelectMaxCount = 9;

    private ImageView mCheck;
    private TextView mText;
    private Button mSubmitButton;

    private ArrayList<String> mSelectedImages;
    private boolean mIsCurrentSelected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectedImages = getIntent().getStringArrayListExtra(EXTRA_SELECTED_LIST);
        mSelectMaxCount = getIntent().getIntExtra(EXTRA_SELECT_COUNT, 9);
    }

    @Override
    public void onBackPressed() {
        submitResult(false);
    }

    @Override
    protected boolean isAutoHideFunctionBar() {
        return false;
    }

    @Override
    protected View getActionBarMenu() {
        if (mSelectMaxCount == 1) {
            // single select mode does not need a submit button
            return null;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        mSubmitButton = (Button) inflater.inflate(R.layout.mis_btn_submit, null);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitResult(true);
            }
        });
        refreshSubmitButton();
        return mSubmitButton;
    }

    @Override
    protected View getBottomBar() {
        View bar = View.inflate(this, R.layout.mis_browser_bottom_bar, null);
        mCheck = (ImageView) bar.findViewById(R.id.mis_browser_bottom_bar_check);
        mText = (TextView) bar.findViewById(R.id.mis_browser_bottom_bar_text);
        mCheck.setOnClickListener(mCheckClickListener);
        mText.setOnClickListener(mCheckClickListener);
        refreshSelected();
        return bar;
    }

    private View.OnClickListener mCheckClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mIsCurrentSelected) {
                mSelectedImages.remove(getCurrentImageUri());
            } else {
                if (mSelectedImages == null) {
                    mSelectedImages = new ArrayList<>();
                } else if (mSelectedImages.size() == mSelectMaxCount) {
                    Toast.makeText(ImageSelectBrowser.this,
                            R.string.mis_toast_amount_limit, Toast.LENGTH_SHORT).show();
                    return;
                }

                mSelectedImages.add(getCurrentImageUri());
                if (mSelectMaxCount == 1) {
                    // single select mode, set result and return
                    refreshSelected();
                    submitResult(true);
                    return;
                }
            }

            refreshSubmitButton();
            refreshSelected();
        }
    };

    @Override
    protected boolean onActionBarBackClicked() {
        submitResult(false);
        return true;
    }

    @Override
    protected void onBrowseImage(List<String> images, int position) {
        super.onBrowseImage(images, position);
        ImageValidation valid = getCurrentImageValidation();
        if (valid == ImageValidation.UNKNOWN) {
            setCheckEnabled(false);
        } else {
            setCheckEnabled(valid == ImageValidation.VALID);
        }
        refreshSelected();
    }

    @Override
    protected void onImageLoadComplete(String imgUri, boolean success) {
        super.onImageLoadComplete(imgUri, success);
        if (getCurrentImageUri().equals(imgUri)) {
            setCheckEnabled(success);
        }
    }

    private void submitResult(boolean done) {
        if (mSelectedImages != null && mSelectedImages.size() != 0) {
            ArrayList<String> paths = new ArrayList<>(mSelectedImages.size());
            for (String uri : mSelectedImages) {
                paths.add(Uri.parse(uri).getPath());
            }
            Intent intent = new Intent();
            intent.putStringArrayListExtra(EXTRA_RESULT, paths);
            intent.putExtra(EXTRA_RESULT_DONE, done);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    private void setCheckEnabled(boolean enabled) {
        if (enabled) {
            mText.setTextColor(Color.WHITE);
            mCheck.setClickable(true);
        } else {
            mText.setTextColor(Color.DKGRAY);
            mCheck.setClickable(false);
        }
    }

    private void refreshSelected() {
        if (mSelectedImages != null && mSelectedImages.size() != 0) {
            mIsCurrentSelected = mSelectedImages.indexOf(getCurrentImageUri()) != -1;
        } else {
            mIsCurrentSelected = false;
        }
        mCheck.setImageResource(mIsCurrentSelected ?
                R.drawable.mis_ic_selected : R.drawable.mis_ic_unselected);
    }

    private void refreshSubmitButton() {
        if (mSubmitButton == null) {
            return;
        }
        if (mSelectedImages == null || mSelectedImages.size() <= 0) {
            mSubmitButton.setText(R.string.mis_finish_btn);
            mSubmitButton.setEnabled(false);
        } else {
            String txt = getString(
                    R.string.mis_finish_btn_with_amount, mSelectedImages.size(), mSelectMaxCount);
            mSubmitButton.setText(txt);
            mSubmitButton.setEnabled(true);
        }
    }

}
