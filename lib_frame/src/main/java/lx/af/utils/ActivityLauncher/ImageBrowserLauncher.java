package lx.af.utils.ActivityLauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import lx.af.R;
import lx.af.activity.ImageBrowser.ImageBrowserActivity;

/**
 * author: lx
 * date: 15-12-25
 */
public class ImageBrowserLauncher {

    private Activity mActivity;
    private Context mContext;
    private Intent mIntent;

    private View mCurrentView;

    private ImageBrowserLauncher(Context context, Class<?> browserClazz) {
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        } else {
            mContext = context;
        }
        mIntent = new Intent(context, browserClazz);
    }

    public static ImageBrowserLauncher of(Context context) {
        return new ImageBrowserLauncher(context, ImageBrowserActivity.class);
    }

    public static ImageBrowserLauncher of(Context context, Class<?> browserClazz) {
        return new ImageBrowserLauncher(context, browserClazz);
    }

    public ImageBrowserLauncher paths(List<String> paths) {
        if (paths != null && paths.size() != 0) {
            ArrayList<String> list = new ArrayList<>(paths.size());
            for (String path : paths) {
                list.add("file://" + path);
            }
            mIntent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URI_LIST, list);
        }
        return this;
    }

    public ImageBrowserLauncher uris(List<String> uris) {
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

    public ImageBrowserLauncher currentPath(String path) {
        if (path != null) {
            mIntent.putExtra(ImageBrowserActivity.EXTRA_CURRENT_IMAGE_URI, "file://" + path);
        }
        return this;
    }

    public ImageBrowserLauncher currentUri(String uri) {
        if (uri != null) {
            mIntent.putExtra(ImageBrowserActivity.EXTRA_CURRENT_IMAGE_URI, uri);
        }
        return this;
    }

    public ImageBrowserLauncher currentUri(Uri uri) {
        if (uri != null) {
            mIntent.putExtra(ImageBrowserActivity.EXTRA_CURRENT_IMAGE_URI, uri.toString());
        }
        return this;
    }

    public ImageBrowserLauncher currentView(View view) {
        mCurrentView = view;
        return this;
    }

    public ImageBrowserLauncher autoHideFunctionBar(boolean autoHide) {
        mIntent.putExtra(ImageBrowserActivity.EXTRA_AUTO_HIDE_FUNCTION_BAR, autoHide);
        return this;
    }

    public ImageBrowserLauncher tapExit(boolean tapExit) {
        mIntent.putExtra(ImageBrowserActivity.EXTRA_TAP_EXIT, tapExit);
        return this;
    }

    public void start() {
        if (mActivity == null) {
            mContext.startActivity(mIntent);
        } else {
            ActivityOptionsCompat options;
            if (mCurrentView != null) {
                options = ActivityOptionsCompat.makeScaleUpAnimation(
                        mCurrentView,
                        mCurrentView.getWidth() / 2, mCurrentView.getHeight() / 2,
                        0, 0);
            } else {
                options = ActivityOptionsCompat.makeCustomAnimation(
                        mActivity, R.anim.image_browser_show, R.anim.fade_out);
            }
            ActivityCompat.startActivity(mActivity, mIntent, options.toBundle());
        }
    }

}
