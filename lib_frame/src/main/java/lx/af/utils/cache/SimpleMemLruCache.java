package lx.af.utils.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * author: lx
 * date: 16-6-1
 */
public class SimpleMemLruCache<T, E> {

    private HashMap<T, Wrapper<T, E>> mMap;
    private int mMax;
    private int mThreshold;

    private final Object mLock = new Object();

    public SimpleMemLruCache(int max) {
        this(max, (int) (max * 0.75f));
    }

    public SimpleMemLruCache(int max, int threshold) {
        if (threshold > max) {
            throw new IllegalArgumentException("threshold should not be greater than max");
        }
        mMax = max;
        mThreshold = threshold;
        mMap = new HashMap<>(max + 3);
    }

    public void put(T key, E value) {
        synchronized (mLock) {
            mMap.put(key, new Wrapper<>(key, value));
        }
        checkPrune();
    }

    public E get(T key) {
        synchronized (mLock) {
            Wrapper<T, E> wrapper = mMap.get(key);
            if (wrapper != null) {
                wrapper.update();
                return wrapper.obj;
            }
        }
        return null;
    }

    private void checkPrune() {
        if (mMap.size() < mMax) {
            return;
        }
        synchronized (mLock) {
            LinkedList<Wrapper<T, E>> list = new LinkedList<>();
            list.addAll(mMap.values());
            Collections.sort(list);
            for (int i = list.size() - 1; i >= mThreshold; i --) {
                Wrapper<T, E> wrapper = list.get(i);
                mMap.remove(wrapper.key);
            }
            list.clear();
        }
    }

    private static class Wrapper<T, E> implements Comparable<Wrapper> {

        T key;
        E obj;
        long updateTime;

        Wrapper(T key, E obj) {
            this.key = key;
            this.obj = obj;
            this.updateTime = System.currentTimeMillis();
        }

        void update() {
            updateTime = System.currentTimeMillis();
        }

        @Override
        public int compareTo(Wrapper another) {
            return (int) (updateTime - another.updateTime);
        }
    }

}
