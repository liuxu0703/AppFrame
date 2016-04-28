package lx.af.utils.UIL;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import lx.af.R;
import lx.af.utils.UIL.displayer.AnimateDisplayer;

/**
 * author: lx
 * date: 16-4-25
 */
public class SimpleOptionsPool {

    private static final Object mLock = new Object();
    private static HashMap<String, OptionsWrapper> sOptionsMap = new HashMap<>();

    private SimpleOptionsPool() {}

    public static DisplayImageOptions getOptions() {
        return getOptions(R.drawable.img_default, 0);
    }

    public static DisplayImageOptions getOptions(int defaultId) {
        return getOptions(defaultId, 0);
    }

    public static DisplayImageOptions getAdapterOptions() {
        return getOptions(R.drawable.img_default, 100);
    }

    public static DisplayImageOptions getOptions(int defaultId, int delayTime) {
        String key = defaultId + "|" + delayTime;
        synchronized (mLock) {
            OptionsWrapper wrapper = sOptionsMap.get(key);
            if (wrapper != null) {
                wrapper.refresh();
                return wrapper.options;
            } else {
                wrapper = createOptions(defaultId, delayTime, key);
                sOptionsMap.put(key, wrapper);
                checkPurge();
                return wrapper.options;
            }
        }
    }

    public static OptionsWrapper createOptions(int defaultId, int delay, String key) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultId)
                .showImageForEmptyUri(defaultId)
                .showImageOnFail(defaultId)
                .delayBeforeLoading(delay)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new AnimateDisplayer())
                .build();
        return new OptionsWrapper(options, key);
    }

    private static void checkPurge() {
        if (sOptionsMap.size() > 20) {
            synchronized (mLock) {
                LinkedList<OptionsWrapper> list = new LinkedList<>();
                list.addAll(sOptionsMap.values());
                Collections.sort(list);
                for (int i = 0; i < 10; i ++) {
                    OptionsWrapper wrapper = list.get(i);
                    sOptionsMap.remove(wrapper.key);
                }
            }
        }
    }


    private static class OptionsWrapper implements Comparable<OptionsWrapper> {
        DisplayImageOptions options;
        String key;
        long time;
        int count;

        public OptionsWrapper(DisplayImageOptions options, String key) {
            this.options = options;
            this.key = key;
            this.time = System.currentTimeMillis();
            this.count = 1;
        }

        public void refresh() {
            this.time = System.currentTimeMillis();
            this.count ++;
        }

        @Override
        public int compareTo(@NonNull OptionsWrapper that) {
            int c = this.count - that.count;
            if (c != 0) {
                return c;
            }
            long t = this.time - that.time;
            if (t != 0) {
                return (int) t;
            }
            return 0;
        }
    }

}
