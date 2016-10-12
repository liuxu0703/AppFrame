package lx.af.utils.ActivityLauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        if (!TextUtils.isEmpty(path)) {
            mIntent.putExtra(ImageBrowserActivity.EXTRA_CURRENT_IMAGE_URI, "file://" + path);
        }
        return this;
    }

    public ImageBrowserLauncher currentUri(String uri) {
        if (!TextUtils.isEmpty(uri)) {
            mIntent.putExtra(ImageBrowserActivity.EXTRA_CURRENT_IMAGE_URI, uri);
        }
        return this;
    }

    public ImageBrowserLauncher currentUri(Uri uri) {
        if (uri != null) {
            currentUri(uri.toString());
        }
        return this;
    }

    public ImageBrowserLauncher preloadUri(String preloadUri) {
        if (!TextUtils.isEmpty(preloadUri)) {
            mIntent.putExtra(ImageBrowserActivity.EXTRA_PRELOAD_URI, preloadUri);
        }
        return this;
    }

    public ImageBrowserLauncher preloadForUri(String uri, String preloadUri) {
        if (!TextUtils.isEmpty(uri) && !TextUtils.isEmpty(preloadUri)) {
            mIntent.putExtra(ImageBrowserActivity.EXTRA_PRELOAD_URI_PREFIX + uri, preloadUri);
        }
        return this;
    }

    public ImageBrowserLauncher preloadForUri(Map<String, String> uriMap) {
        if (uriMap != null && uriMap.size() > 0) {
            Iterator<Map.Entry<String, String>> it = uriMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                mIntent.putExtra(
                        ImageBrowserActivity.EXTRA_PRELOAD_URI_PREFIX + entry.getKey(),
                        entry.getValue());
            }
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
