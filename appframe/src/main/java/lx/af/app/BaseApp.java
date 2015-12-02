package lx.af.app;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import lx.af.manager.ActivityTaskManager;
import lx.af.manager.GlobalThreadManager;
import lx.af.utils.KV;
import lx.af.utils.AlertUtils;
import lx.af.utils.CrashHandler;
import lx.af.utils.NetStateUtils;
import lx.af.utils.PathUtils;
import lx.af.utils.ScreenUtils;
import lx.af.utils.StringUtils;
import lx.af.utils.SystemUtils;
import lx.af.utils.log.LogUtils;

public class BaseApp extends Application{

    private static Application sInstance;

    public static Application getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        PathUtils.init(this);
        LogUtils.init(this);
        StringUtils.init(this);

        KV.init(this);
        CrashHandler.init();
        GlobalThreadManager.init(this);
        initImageLoader(this);

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
        int cpu_count = Runtime.getRuntime().availableProcessors();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .threadPoolSize(cpu_count + 1)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCacheSizePercentage(12)
                .memoryCacheExtraOptions(480, 480)
                .diskCacheSize(100 * 1024 * 1024)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .writeDebugLogs()
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .build();
        ImageLoader.getInstance().init(config);
    }

}
