package lx.af.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import lx.af.R;
import lx.af.utils.ScreenUtils;

/**
 * Created by LX on 2015/5/9.
 * dialog that shows on the bottom of the screen, with multiple options to select.
 */
public class BottomSelectDialog extends Dialog {

    /**
     * create dialog with string resources.
     * when an option is selected, the string resource id will be returned
     * by the OnSelectListener callback.
     * Note: for the two different constructor, the value returned by the callback is different.
     * @param context the context.
     * @param resIds the string resource ids.
     * @param listener the callback.
     */
    public BottomSelectDialog(Context context, final int[] resIds, final OnSelectListener listener) {
        super(context, R.style.dialog_custom);
        setContentView(R.layout.dlg_bottom_selector);
        initView();

        ListView listView = (ListView) findViewById(R.id.dlg_bottom_selector_list_view);
        listView.setAdapter(new ResIdAdapter(context, resIds));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.onSelect(resIds[position]);
                }
                dismiss();
            }
        });
    }

    /**
     * create dialog with string resources.
     * when an option is selected, the item position will be returned
     * by the OnSelectListener callback.
     * Note: for the two different constructor, the value returned by the callback is different.
     * @param context the context.
     * @param items the option strings.
     * @param listener the callback.
     */
    public BottomSelectDialog(Context context, String[] items, final OnSelectListener listener) {
        super(context, R.style.dialog_custom);
        setContentView(R.layout.dlg_bottom_selector);
        initView();

        ListView listView = (ListView) findViewById(R.id.dlg_bottom_selector_list_view);
        listView.setAdapter(new StringAdapter(context, items));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.onSelect(position);
                }
                dismiss();
            }
        });
    }

    private void initView() {
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = ScreenUtils.getScreenWidth();
        dialogWindow.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        dialogWindow.setAttributes(lp);
        dialogWindow.setWindowAnimations(R.style.pop_window_bottom_anim);

        findViewById(R.id.dlg_bottom_selector_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * select callback.
     */
    public interface OnSelectListener {

        /**
         * callback when an option is selected.
         * @param selection the selection:
         *                  if the BottomSelectDialog is constructed by string resource ids,
         *                  then the resource id will be returned by the callback;
         *                  if the BottomSelectDialog is constructed by strings,
         *                  then the item position in the string array will be returned by the callback.
         */
        public void onSelect(int selection);
    }


    private static class ResIdAdapter extends BaseAdapter {

        private Context mContext;
        private int[] mIds;

        ResIdAdapter(Context context, int[] ids) {
            mContext = context;
            mIds = ids;
        }

        @Override
        public int getCount() {
            return mIds == null ? 0 : mIds.length;
        }

        @Override
        public String getItem(int position) {
            return mIds == null ? null : mContext.getString(mIds[position]);
        }

        @Override
        public long getItemId(int position) {
            return mIds == null ? 0 : mIds[position];
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.dlg_bottom_selector_item, null);
            }
            TextView tv = (TextView) convertView;
            tv.setText(getItem(position));
            return convertView;
        }
    }

    private static class StringAdapter extends BaseAdapter {

        Context mContext;
        String[] mArr;

        StringAdapter(Context context, String[] arr) {
            mContext = context;
            mArr = arr;
        }

        @Override
        public int getCount() {
            return mArr == null ? 0 : mArr.length;
        }

        @Override
        public String getItem(int position) {
            return mArr == null ? null : mArr[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.dlg_bottom_selector_item, null);
            }
            TextView tv = (TextView) convertView;
            tv.setText(getItem(position));
            return convertView;
        }
    }

}
