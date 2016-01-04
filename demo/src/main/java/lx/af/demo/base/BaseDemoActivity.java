package lx.af.demo.base;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import lx.af.base.AbsBaseActivity;
import lx.af.base.ActionBarAdapter;
import lx.af.demo.R;

/**
 * Created by liuxu on 15-6-11.
 *
 */
public class BaseDemoActivity extends AbsBaseActivity {

    @Override
    protected ActionBarAdapter getActionBarAdapter() {
        return mActionBarAdapter;
    }

    ActionBarAdapter mActionBarAdapter = new ActionBarAdapter() {

        @Override
        public Type getActionBarType() {
            return Type.NORMAL;
        }

        @Override
        public View getActionBarView(Activity activity) {
            if (!(activity instanceof ActionBarImpl)) {
                return null;
            }

            View view = View.inflate(activity, R.layout.action_bar, null);
            ImageView back = (ImageView) view.findViewById(R.id.action_bar_back);
            TextView title = (TextView) view.findViewById(R.id.action_bar_title);
            // use activity label as default action bar title
            title.setText(getTitle());

            if (BaseDemoActivity.this instanceof ActionBarCallbacks) {
                final ActionBarCallbacks callbacks = (ActionBarCallbacks) activity;

                // check if menu is valid
                View menu = callbacks.onCreateActionBarMenu();
                if (menu != null) {
                    FrameLayout menuFrame = obtainView(R.id.action_bar_menu_frame);
                    menuFrame.addView(menu);
                }

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!callbacks.onActionBarBackClicked(v)) {
                            finish();
                        }
                    }
                });
                callbacks.onActionBarCreated(view, back, title, menu);
            } else {
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }

            return view;
        }
    };

    /**
     * by implementing this interface, activity extends from AbsBaseActivity
     * will get an ActionBar with a back button and a title uses activity
     * label as text.
     * if menu button is needed, or further customize is needed, try implement
     * ActionBarCallbacks instead.
     */
    public interface ActionBarImpl {
    }

    /**
     * by implementing this interface, activity extends from AbsBaseActivity
     * will get an ActionBar with a back button, a title uses activity
     * label as text, and a menu button.
     * if only back button and title is needed, try implement ActionBarImpl
     * instead.
     */
    public interface ActionBarCallbacks extends ActionBarImpl {

        /**
         * called to get a view for menu.
         * @return the menu view, or null if menu is not needed.
         */
        View onCreateActionBarMenu();

        /**
         * called when the ActionBar is first inflated.
         * @param actionBar the action bar
         * @param back back button
         * @param title TextView for title
         * @param menu the menu, can be null
         */
        void onActionBarCreated(View actionBar, ImageView back, TextView title, @Nullable View menu);

        /**
         * called when back button is clicked.
         * Activity.finish() will be called if false is returned from this method
         * @param back back button
         * @return true if click event is handled, false otherwise
         */
        boolean onActionBarBackClicked(View back);

    }

}
