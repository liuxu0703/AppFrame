package lx.af.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * author: lx
 * date: 15-12-15
 */
public abstract class AbsListAdapter<T> extends BaseAdapter {

    private Context mContext;
    private List<T> mObjects;

    /**
     * Lock used to modify the content of {@link #mObjects}. Any write operation
     * performed on the array should be synchronized on this lock.
     */
    private final Object mLock = new Object();

    /**
     * Indicates whether or not {@link #notifyDataSetChanged()} must be called whenever
     * {@link #mObjects} is modified.
     */
    private boolean mNotifyOnChange = true;

    public AbsListAdapter(Context context, List<T> list) {
        mContext = context;
        mObjects = list != null ? list : new ArrayList<T>();
    }

    public AbsListAdapter(Context context, T[] arr) {
        this(context, arr != null ? Arrays.asList(arr) : null);
    }

    public AbsListAdapter(Context context) {
        this(context, new ArrayList<T>());
    }

    /**
     * Adds the specified object at the end of the array.
     * @param object The object to add at the end of the array.
     */
    public void add(T object) {
        synchronized (mLock) {
            mObjects.add(object);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Inserts the specified object at the specified index in the array.
     * The object is inserted before the current element at the specified index.
     * @param object The object to insert into the array.
     * @param index The index at which the object must be inserted.
     */
    public void add(int index, T object) {
        synchronized (mLock) {
            mObjects.add(index, object);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Adds the specified Collection at the end of the array.
     * @param collection The Collection to add at the end of the array.
     */
    public void addAll(Collection<? extends T> collection) {
        synchronized (mLock) {
            mObjects.addAll(collection);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Adds the specified Collection at the specified index in the array.
     * The object is inserted before the current element at the specified index.
     * @param collection The Collection to add at the end of the array.
     * @param index The index at which the object must be inserted.
     */
    public void addAll(int index, Collection<? extends T> collection) {
        synchronized (mLock) {
            mObjects.addAll(index, collection);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * replace data of the adapter to the new list
     * @param list the data list
     */
    public void reset(List<T> list) {
        synchronized (mLock) {
            mObjects.clear();
            mObjects.addAll(list);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Removes the specified object from the array.
     * @param object The object to remove.
     */
    public void remove(T object) {
        synchronized (mLock) {
            mObjects.remove(object);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Removes object from the array by position.
     * @param position The position to remove.
     */
    public void remove(int position) {
        synchronized (mLock) {
            mObjects.remove(position);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        synchronized (mLock) {
            mObjects.clear();
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Sorts the content of this adapter using the specified comparator.
     * @param comparator The comparator used to sort the objects contained
     *        in this adapter.
     */
    public void sort(Comparator<? super T> comparator) {
        synchronized (mLock) {
            Collections.sort(mObjects, comparator);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Control whether methods that change the list ({@link #add},
     * {@link #addAll}, {@link #remove}, {@link #clear}) automatically call
     * {@link #notifyDataSetChanged}.
     * If set to false, caller must manually call notifyDataSetChanged()
     * to have the changes reflected in the attached view.
     *
     * The default is true.
     *
     * @param notifyOnChange if true, modifications to the list will
     *                       automatically call {@link #notifyDataSetChanged}
     */
    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }

    /**
     * get object list. for read purpose only!
     * for performance's seek, we do not return a copy of the list.
     * should not change its data!
     */
    public List<T> getList() {
        return mObjects;
    }

    /**
     * get a copy of the data list.
     */
    public ArrayList<T> getListCopy() {
        ArrayList<T> list = new ArrayList<>(mObjects.size());
        list.addAll(mObjects);
        return list;
    }

    /**
     * @return The Context associated with this adapter.
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * Returns the position of the specified item in the array.
     * @param item The item to retrieve the position of.
     * @return The position of the specified item.
     */
    public int getPosition(T item) {
        return mObjects.indexOf(item);
    }

    public T getLastItem() {
        return mObjects.size() == 0 ? null : mObjects.get(mObjects.size() - 1);
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public T getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getView(mContext, position, convertView, parent);
    }

    public abstract View getView(Context context, int position, View convertView, ViewGroup parent);

}
