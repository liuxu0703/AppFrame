package lx.af.utils.ActivityUtils;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import lx.af.R;
import lx.af.activity.ImageBrowser.ImageBrowserActivity;

/**
 * author: lx
 * date: 15-12-25
 */
public class ImageBrowser {

    private Activity mActivity;
    private Fragment mFragment;
    private Intent mIntent;

    private View mCurrentView;

    private ImageBrowser(Activity activity) {
        mActivity = activity;
        mIntent = new Intent(activity, ImageBrowserActivity.class);
    }

    private ImageBrowser(Fragment fragment) {
        mFragment = fragment;
        mIntent = new Intent(fragment.getActivity(), ImageBrowserActivity.class);
    }

    public static ImageBrowser of(Activity activity) {
        return new ImageBrowser(activity);
    }

    public static ImageBrowser of(Fragment fragment) {
        return new ImageBrowser(fragment);
    }

    public ImageBrowser uris(List<String> uris) {
        ArrayList<String> list;
        if (uris instanceof ArrayList) {
            list = (ArrayList<String>) uris;
        } else {
            list = new ArrayList<>(uris.size());
            list.addAll(uris);
        }
        mIntent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URI_LIST, list);
        return this;
    }

    public ImageBrowser currentUri(String uri) {
        if (uri != null) {
            mIntent.putExtra(ImageBrowserActivity.EXTRA_CURRENT_IMAGE_URI, uri);
        }
        return this;
    }

    public ImageBrowser currentView(View view) {
        mCurrentView = view;
        return this;
    }

    public ImageBrowser autoHideFunctionBar(boolean autoHide) {
        mIntent.putExtra(ImageBrowserActivity.EXTRA_AUTO_HIDE_FUNCTION_BAR, autoHide);
        return this;
    }

    public ImageBrowser tapExit(boolean tapExit) {
        mIntent.putExtra(ImageBrowserActivity.EXTRA_TAP_EXIT, tapExit);
        return this;
    }

    public void start() {
        Activity activity = mActivity != null ? mActivity : mFragment.getActivity();
        ActivityOptionsCompat options;
        if (mCurrentView != null) {
            options = ActivityOptionsCompat.makeScaleUpAnimation(
                    mCurrentView,
                    mCurrentView.getWidth() / 2, mCurrentView.getHeight() / 2,
                    0, 0);
        } else {
            options = ActivityOptionsCompat.makeCustomAnimation(
                    activity, R.anim.image_browser_show, R.anim.fade_out);
        }
        ActivityCompat.startActivity(activity, mIntent, options.toBundle());
    }

}
