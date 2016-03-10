package lx.af.utils.ActivityLauncher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import lx.af.R;
import lx.af.activity.ImageEditor.ImageBrowserEditActivity;

/**
 * author: lx
 * date: 16-3-10
 */
public class ImageBrowserEditLauncher extends ActivityLauncherBase<String> {

    private Intent mIntent;

    protected ImageBrowserEditLauncher(Activity activity) {
        super(activity);
        mIntent = new Intent(activity, ImageBrowserEditActivity.class);
        mInAnimResId = R.anim.image_browser_show;
    }

    protected ImageBrowserEditLauncher(Fragment fragment) {
        super(fragment);
        mIntent = new Intent(fragment.getActivity(), ImageBrowserEditActivity.class);
    }

    public static ImageBrowserEditLauncher of(Activity activity) {
        return new ImageBrowserEditLauncher(activity);
    }

    public static ImageBrowserEditLauncher of(Fragment fragment) {
        return new ImageBrowserEditLauncher(fragment);
    }

    public ImageBrowserEditLauncher uri(String uri) {
        mIntent.putExtra(ImageBrowserEditActivity.EXTRA_CURRENT_IMAGE_URI, uri);
        return this;
    }

    public ImageBrowserEditLauncher path(String path) {
        return uri("file://" + path);
    }

    @Override
    protected String extractResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            return data.getStringExtra(ImageBrowserEditActivity.EXTRA_RESULT);
        }
        return null;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public Intent createIntent() {
        return mIntent;
    }

    @Override
    protected int getDefaultRequestCode() {
        return RequestCode.IMAGE_BROWSER_EDITOR;
    }

}
