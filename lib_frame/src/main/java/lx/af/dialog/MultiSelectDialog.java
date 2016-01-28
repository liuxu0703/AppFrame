package lx.af.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import lx.af.R;

/**
 * Created by liuxu on 15-5-11.
 * multi select dialog.
 */
public class MultiSelectDialog extends Dialog implements
        AdapterView.OnItemClickListener {

    private static final String COLOR_SELECTED = "#079fff";
    private static final String COLOR_NORMAL = "#525252";

    private ListView mListView;
    private ArrayList<Item> mItems;
    private OnMultiSelectListener mResultListener;

    /**
     * create multi select dialog.
     * @param context the context.
     * @param items the selection items.
     * @param listener the result callback.
     */
    public MultiSelectDialog(Context context, String[] items, OnMultiSelectListener listener) {
        super(context, R.style.dialog_custom);
        setContentView(R.layout.dlg_multi_select);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        mResultListener = listener;
        mListView = (ListView) findViewById(R.id.dlg_multi_select_list_view);

        mItems = new ArrayList<Item>(items.length);
        for (int i = 0; i < items.length; i ++) {
            Item item = new Item(items[i], false);
            mItems.add(item);
        }

        mListView.setOnItemClickListener(this);
        mListView.setAdapter(new ChoiceAdapter(context, mItems));

        findViewById(R.id.dlg_multi_select_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.dlg_multi_select_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSelectionResult();
                dismiss();
            }
        });
    }

    /**
     * set current selected item idx.
     */
    public void setCurrentSelection(int[] selection) {
        // we do not check array boundary here. make sure they are good.
        for (int idx : selection) {
            mItems.get(idx).selected = true;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item item = mItems.get(position);
        item.selected = !item.selected;
        ViewHolder vh = (ViewHolder) view.getTag();
        vh.txt.setText(item.txt);
        vh.txt.setTextColor(Color.parseColor(item.selected ? COLOR_SELECTED : COLOR_NORMAL));
        vh.img.setVisibility(item.selected ? View.VISIBLE : View.INVISIBLE);
    }

    private void handleSelectionResult() {
        ArrayList<Integer> selectedList = new ArrayList<Integer>();
        for (int i = 0; i < mItems.size(); i ++) {
            Item item = mItems.get(i);
            if (item.selected) {
                selectedList.add(i);
            }
        }
        int[] selection = null;
        if (selectedList.size() != 0) {
            selection = new int[selectedList.size()];
            for (int i = 0; i < selection.length; i ++) {
                selection[i] = selectedList.get(i);
            }
        }
        if (mResultListener != null) {
            mResultListener.onMultiSelect(selection);
        }
        dismiss();
    }

    /**
     * selection callback.
     */
    public interface OnMultiSelectListener {

        /**
         * selection callback.
         * @param selections the selected item index in the item array, could be null.
         */
        public void onMultiSelect(@Nullable int[] selections);
    }


    private static class Item {
        String txt;
        boolean selected;

        Item(String txt, boolean selected) {
            this.txt = txt;
            this.selected = selected;
        }
    }

    private static class ViewHolder {
        TextView txt;
        ImageView img;
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
            ViewHolder vh;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.dlg_multi_select_item, null);
                vh = new ViewHolder();
                vh.txt = (TextView) convertView.findViewById(R.id.dlg_multi_select_item_text);
                vh.img = (ImageView) convertView.findViewById(R.id.dlg_multi_select_item_check);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            Item item = getItem(position);
            vh.txt.setText(item.txt);
            vh.txt.setTextColor(Color.parseColor(item.selected ? COLOR_SELECTED : COLOR_NORMAL));
            vh.img.setVisibility(item.selected ? View.VISIBLE : View.INVISIBLE);
            return convertView;
        }
    }

}
