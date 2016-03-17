package lx.af.widget.FlowLayout;

import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public abstract class TagAdapter<T>
{
    protected List<T> mTagDataList;
    private OnDataChangedListener mOnDataChangedListener;
    private HashSet<Integer> mCheckedPosList = new HashSet<Integer>();

    public TagAdapter(List<T> datas)
    {
        mTagDataList = datas;
    }

    public TagAdapter(T[] datas)
    {
        mTagDataList = new ArrayList<T>(Arrays.asList(datas));
    }

    static interface OnDataChangedListener
    {
        void onChanged();
    }

    void setOnDataChangedListener(OnDataChangedListener listener)
    {
        mOnDataChangedListener = listener;
    }

    public void setSelectedList(int... pos)
    {
        for (int i = 0; i < pos.length; i++)
            mCheckedPosList.add(pos[i]);
        notifyDataChanged();
    }

    HashSet<Integer> getPreCheckedList()
    {
        return mCheckedPosList;
    }


    public int getCount()
    {
        return mTagDataList == null ? 0 : mTagDataList.size();
    }

    public void notifyDataChanged()
    {
        mOnDataChangedListener.onChanged();
    }

    public T getItem(int position)
    {
        return mTagDataList.get(position);
    }

    public abstract View getView(FlowLayout parent, int position, T t);

}