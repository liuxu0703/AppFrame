package lx.af.demo.activity.test;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import lx.af.adapter.AbsListAdapter;
import lx.af.demo.R;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseActivity;

/**
 * author: lx
 * date: 15-11-20
 * a hide page to do some code test
 */
public class TestListActivity extends BaseActivity implements
        ActionBar.Default,
        AdapterView.OnItemClickListener {

    @InjectView(R.id.activity_test_list_view)
    ListView mListView;

    ActionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list);
        ButterKnife.inject(this);
        mAdapter = new ActionAdapter(this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    @OnItemClick(R.id.activity_test_list_view)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TestAction action = mAdapter.getItem(position);
        if (action.action != null) {
            action.action.doAction(this);
        } else if (action.activity != null) {
            startActivity(action.activity);
        }
    }

    private static class ActionAdapter extends AbsListAdapter<TestAction> {

        public ActionAdapter(Context context) {
            super(context, TestActionList.ARR);
        }

        @Override
        public View getView(Context context, int position, View convertView, ViewGroup parent) {
            TextView tv;
            if (convertView == null) {
                int padding = 25;
                tv = new TextView(getContext());
                tv.setTextSize(18);
                tv.setTextColor(getContext().getResources().getColor(R.color.text_color));
                tv.setGravity(Gravity.CENTER);
                tv.setPadding(padding, padding, padding, padding);
                convertView = tv;
            } else {
                tv = (TextView) convertView;
            }
            tv.setText(getItem(position).title);
            return convertView;
        }
    }

}
