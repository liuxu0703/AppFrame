package lx.af.demo.base;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import lx.af.base.ActionBarAdapter;
import lx.af.demo.R;

/**
 * author: lx
 * date: 16-1-5
 */
final class ActionBarFactory {

    public static ActionBarAdapter getActionBarAdapter(BaseDemoActivity activity) {
        if (activity instanceof ActionBar.Default) {
            return new DefaultAdapter(activity);
        }
        return null;
    }

    private static class DefaultAdapter implements ActionBarAdapter {

        BaseDemoActivity mActivity;

        public DefaultAdapter(BaseDemoActivity activity) {
            mActivity = activity;
        }

        @Override
        public Type getActionBarType() {
            return mActivity instanceof ActionBar.OverlayInner ? Type.OVERLAY : Type.NORMAL;
        }

        @Override
        public View getActionBarView(Activity activity) {
            View view = View.inflate(activity, R.layout.action_bar, null);
            ImageView back = (ImageView) view.findViewById(R.id.action_bar_back);
            TextView title = (TextView) view.findViewById(R.id.action_bar_title);
            View menu = null;

            // use activity label as default action bar title
            title.setText(mActivity.getTitle());

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mActivity instanceof ActionBar.Default.BackClickCallback) {
                        ActionBar.Default.BackClickCallback c =
                                (ActionBar.Default.BackClickCallback) mActivity;
                        if (c.onActionBarBackClicked(v)) {
                            return;
                        }
                    }
                    mActivity.finish();
                }
            });

            if (mActivity instanceof ActionBar.Default.MenuCreator) {
                ActionBar.Default.MenuCreator c = (ActionBar.Default.MenuCreator) mActivity;
                menu = c.createActionBarMenu();
                if (menu != null) {
                    FrameLayout menuFrame = (FrameLayout)
                            view.findViewById(R.id.action_bar_menu_frame);
                    menuFrame.addView(menu);
                }
            }

            if (mActivity instanceof ActionBar.Default.OnCreateCallback) {
                ActionBar.Default.OnCreateCallback c = (ActionBar.Default.OnCreateCallback) mActivity;
                c.onActionBarCreated(view, back, title, menu);
            }

            return view;
        }
    }


}
