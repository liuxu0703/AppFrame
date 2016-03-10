package lx.af.activity.ImageEditor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import lx.af.R;
import lx.af.activity.ImageBrowser.ImageBrowserActivity;
import lx.af.utils.ActivityLauncher.ActivityResultCallback;
import lx.af.utils.ActivityLauncher.SimpleStringLauncher;
import lx.af.utils.ScreenUtils;
import lx.af.utils.ViewUtils.BufferedOnClickListener;

/**
 * author: lx
 * date: 16-3-10
 */
public class ImageBrowserEditActivity extends ImageBrowserActivity {

    public static final String EXTRA_RESULT = "edit_result";

    @Override
    protected boolean isAutoHideFunctionBar() {
        return false;
    }

    @Override
    protected boolean isTapExit() {
        return true;
    }

    @Override
    protected boolean isBrowserEnabled() {
        return false;
    }

    @Override
    protected View getActionBarMenu() {
        int paddingH = ScreenUtils.dip2px(10);
        int paddingV = ScreenUtils.dip2px(6);
        TextView menu = new TextView(this);
        menu.setPadding(paddingH, paddingV, paddingH, paddingV);
        menu.setGravity(Gravity.CENTER);
        menu.setBackgroundResource(R.drawable.bkg_clickable);
        menu.setTextColor(Color.WHITE);
        menu.setTextSize(16);
        menu.setText(R.string.image_editor_browser_menu);
        menu.setOnClickListener(mEditClickListener);
        return menu;
    }

    protected void onEditDone(String result) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT, result);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private BufferedOnClickListener mEditClickListener = new BufferedOnClickListener() {
        @Override
        public void onBufferedClick(View v, int clickCount) {
            final String uri = getCurrentImageUri();
            SimpleStringLauncher
                    .of(ImageBrowserEditActivity.this, ImageEditorActivity.class, ImageEditorActivity.EXTRA_RESULT)
                    .putExtra(ImageEditorActivity.EXTRA_PATH, Uri.parse(uri).getPath())
                    .start(new ActivityResultCallback<String>() {
                        @Override
                        public void onActivityResult(int resultCode, @NonNull String result) {
                            onEditDone(result);
                        }
                    });
        }
    };

}
