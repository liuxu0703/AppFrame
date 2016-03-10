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
class ActionBarAdapterTextMenu implements ActionBarAdapter {

    private BaseDemoActivity mActivity;

    ActionBarAdapterTextMenu(BaseDemoActivity activity) {
        mActivity = activity;
    }

    @Override
    public Type getActionBarType() {
        return mActivity instanceof ActionBar.OverlayInner ? Type.OVERLAY : Type.NORMAL;
    }

    @Override
    public View getActionBarView(Activity activity) {
        View view = View.inflate(activity, R.layout.action_bar_text_menu, null);
        IconTextView left = (IconTextView) view.findViewById(R.id.action_bar_btn_left);
        TextView title = (TextView) view.findViewById(R.id.action_bar_title);
        TextView right = (TextView) view.findViewById(R.id.action_bar_btn_right);

        // use activity label as default action bar title
        title.setText(mActivity.getTitle());
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
        if (mActivity instanceof ActionBar.TextMenu.Callback) {
            ActionBar.TextMenu.Callback c = (ActionBar.TextMenu.Callback) mActivity;
            c.onActionBarCreated(view, left, title, right);
        }
        return view;
    }

}
