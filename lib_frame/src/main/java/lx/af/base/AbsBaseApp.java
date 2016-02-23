package lx.af.base;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import lx.af.R;
import lx.af.manager.ActivityTaskManager;
import lx.af.manager.GlobalThreadManager;
import lx.af.net.HttpRequest.VolleyManager;
import lx.af.utils.AlertUtils;
import lx.af.utils.BitmapUtils;
import lx.af.utils.CrashHandler;
import lx.af.manager.KV;
import lx.af.utils.NetStateUtils;
import lx.af.utils.PathUtils;
import lx.af.utils.ResourceUtils;
import lx.af.utils.ScreenUtils;
import lx.af.utils.StringUtils;
import lx.af.utils.SystemUtils;
import lx.af.utils.log.LogUtils;
import lx.af.widget.iconify.Iconify;

/**
 * author: lx
 * date: 15-12-01
 */
public class AbsBaseApp extends Application{

    private static Application sInstance;

    public static Application getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        GlobalThreadManager.init(this);
        PathUtils.init(this);
        LogUtils.init(this);
        CrashHandler.init();
        Iconify.init();
        VolleyManager.init(this);
        initImageLoader(this);
        KV.init(this);

        StringUtils.init(this);
        ResourceUtils.init(this);
        BitmapUtils.init(this);
        AlertUtils.init(this);
        SystemUtils.init(this);
        ScreenUtils.init(this);
        NetStateUtils.init(this);

        registerActivityLifecycleCallbacks(ActivityTaskManager.getInstance());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        GlobalThreadManager.shutdownThreadPool();
    }

    // init UniversalImageLoader
    private static void initImageLoader(Context context) {
        if (ImageLoader.getInstance().isInited()) {
            return;
        }

        int cpu_count = Runtime.getRuntime().availableProcessors();
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.img_default)
                .showImageForEmptyUri(R.drawable.img_default)
                .showImageOnFail(R.drawable.img_default)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .threadPoolSize(cpu_count + 1)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCacheSizePercentage(12)
                .memoryCacheExtraOptions(480, 480)
                .diskCacheSize(60 * 1024 * 1024)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .writeDebugLogs()
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
    }

}
