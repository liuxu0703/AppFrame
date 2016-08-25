package lx.af.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import lx.af.R;

/**
 * Created by liuxu on 15-5-8.
 * dialog to select a single item.
 */
public class SingleSelectDialog extends Dialog implements AdapterView.OnItemClickListener {

    private static final String COLOR_SELECTED = "#079fff";
    private static final String COLOR_NORMAL = "#525252";

    private ListView mListView;
    private OnSelectListener mResultListener;
    private ArrayList<Item> mItems;

    public static SingleSelectDialog create(
            Context context, String[] items, int current, OnSelectListener listener) {
        return new SingleSelectDialog(context, items, current, listener);
    }

    /**
     * construct dialog.
     * @param context the context.
     * @param items the selection options
     * @param current the current selected option idx.
     * @param listener the callback.
     */
    public SingleSelectDialog(Context context, String[] items, int current, OnSelectListener listener) {
        super(context, R.style.dialog_custom);
        setContentView(R.layout.dlg_single_select);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        mResultListener = listener;

        mItems = new ArrayList<>(items.length);
        for (int i = 0; i < items.length; i ++) {
            boolean selected = i == current;
            Item item = new Item(items[i], selected);
            mItems.add(item);
        }

        mListView = (ListView) findViewById(R.id.dlg_single_select_list_view);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(new ChoiceAdapter(context, mItems));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item item = mItems.get(position);
        item.selected = true;
        TextView tv = (TextView) view;
        tv.setTextColor(Color.parseColor(COLOR_SELECTED));
        if (mResultListener != null) {
            mResultListener.onSelect(position);
        }
        dismiss();
    }


    /**
     * selection callback.
     */
    public interface OnSelectListener {

        /**
         * selection callback.
         * @param selection the selected item index in the item array.
         */
        public void onSelect(int selection);
    }


    private static class Item {
        String txt;
        boolean selected;

        Item(String txt, boolean selected) {
            this.txt = txt;
            this.selected = selected;
        }
    }

    private static class ChoiceAdapter extends BaseAdapter {

        Context mContext;
        private ArrayList<Item> mItems;

        private ChoiceAdapter(Context context, ArrayList<Item> items) {
            mContext = context;
            mItems = items;
        }

        @Override
        public int getCount() {
            return mItems == null ? 0 : mItems.size();
        }

        @Override
        public Item getItem(int position) {
            return mItems == null ? null : mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.dlg_single_select_item, null);
            }
            Item item = getItem(position);
            TextView tv = (TextView) convertView;
            tv.setText(item.txt);
            tv.setTextColor(Color.parseColor(item.selected ? COLOR_SELECTED : COLOR_NORMAL));
            return convertView;
        }
    }
}
