package lx.af.demo.base;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import lx.af.base.ActionBarAdapter;
import lx.af.demo.R;
import lx.af.widget.iconify.widget.IconTextView;

/**
 * author: lx
 * date: 16-2-10
 */
class ActionBarAdapterDefault implements ActionBarAdapter {

    private BaseActivity mActivity;

    ActionBarAdapterDefault(BaseActivity activity) {
        mActivity = activity;
    }

    @Override
    public ActionBarAdapter.Type getActionBarType() {
        return mActivity instanceof ActionBar.OverlayInner ? ActionBarAdapter.Type.OVERLAY : ActionBarAdapter.Type.NORMAL;
    }

    @Override
    public View getActionBarView(Activity activity) {
        View view = View.inflate(activity, R.layout.action_bar_default, null);
        IconTextView right = (IconTextView) view.findViewById(R.id.action_bar_btn_right);
        TextView title = (TextView) view.findViewById(R.id.action_bar_title);
        IconTextView left = (IconTextView) view.findViewById(R.id.action_bar_btn_left);

        // use activity label as default action bar title
        title.setText(mActivity.getTitle());
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
        if (mActivity instanceof ActionBar.Default.Callback) {
            ActionBar.Default.Callback c = (ActionBar.Default.Callback) mActivity;
            c.onActionBarCreated(view, left, title, right);
        }
        return view;
    }

}
