package lx.af.utils.cache;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * author: lx
 * date: 16-6-1
 *
 * simple memory object LRU cache. based on HashMap.
 * usage of the cache is simple:
 * specify a max object count and a purge threshold, then the cache is ready to go.
 * or you can specify the max count, and the purge threshold will be set to max * 0.75.
 *
 * when to purge the cache:
 * when add objects to the cache, total object count will be checked against max count.
 * if exceeded, objects will be deleted due to access time order until count reaches threshold.
 * access time of objects will be recorded on put() and get(), and items with shorter
 * access time will get purged first.
 * the purge operation will be done automatically.
 *
 * this class is threadsafe.
 *
 * @param <K> the type of keys maintained by cache. rule is the same as of HashMap.
 * @param <V> the type of mapped values. rule is the same as of HashMap.
 */
public class SimpleMemLruCache<K, V> {

    private HashMap<K, Wrapper<K, V>> mMap;
    private int mMax;
    private int mThreshold;

    private final Object mLock = new Object();

    /**
     * create cache with max object count.
     * the threshold will be set to max * 0.75.
     * @param max max object count of the cache, exceed which will trigger object purge.
     */
    public SimpleMemLruCache(int max) {
        this(max, (int) (max * 0.75f));
    }

    /**
     * create cache with both max object count and object purge threshold.
     * threshold should not be greater then max, or else exception will be thrown.
     * @param max max object count of the cache, exceed which will trigger object purge.
     * @param threshold purge threshold: when object exceeds max count, object deletion
     *                  will be triggered. oldest object will get deleted first,
     *                  until object count reaches threshold.
     */
    public SimpleMemLruCache(int max, int threshold) {
        if (threshold > max) {
            throw new IllegalArgumentException("threshold should not be greater than max");
        }
        mMax = max;
        mThreshold = threshold;
        mMap = new HashMap<>(max + 3);
    }

    /**
     * put object to cache.
     * when called, the object's last access time will be set to current time.
     * @param key key, as in {@link HashMap#put(Object, Object)}
     * @param value value, as in {@link HashMap#put(Object, Object)}
     */
    public void put(K key, V value) {
        synchronized (mLock) {
            mMap.put(key, new Wrapper<>(key, value));
        }
        checkPrune();
    }

    /**
     * get object from cache.
     * when called, the object's last access time will be updated to current time.
     * @param key key, as in {@link HashMap#get(Object)}
     * @return value, as in {@link HashMap#get(Object)}
     */
    public V get(K key) {
        synchronized (mLock) {
            Wrapper<K, V> wrapper = mMap.get(key);
            if (wrapper != null) {
                wrapper.update();
                return wrapper.obj;
            }
        }
        return null;
    }

    /**
     * clear all cached objects.
     */
    public void clear() {
        synchronized (mLock) {
            mMap.clear();
        }
    }

    // check and purge objects
    private void checkPrune() {
        if (mMap.size() < mMax) {
            return;
        }
        synchronized (mLock) {
            // use list to sort the map first
            LinkedList<Wrapper<K, V>> list = new LinkedList<>();
            list.addAll(mMap.values());
            Collections.sort(list);
            // delete oldest objects
            for (int i = list.size() - 1; i >= mThreshold; i --) {
                Wrapper<K, V> wrapper = list.get(i);
                mMap.remove(wrapper.key);
            }
            list.clear();
        }
    }

    // wrapper class to record object's last access time.
    private static class Wrapper<K, V> implements Comparable<Wrapper> {

        K key;
        V obj;
        long updateTime;

        Wrapper(K key, V obj) {
            this.key = key;
            this.obj = obj;
            this.updateTime = System.currentTimeMillis();
        }

        void update() {
            updateTime = System.currentTimeMillis();
        }

        @Override
        public int compareTo(@NonNull Wrapper another) {
            return (int) (updateTime - another.updateTime);
        }
    }

}
