package lx.af.demo.base;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * author: lx
 * date: 16-1-5
 */
public interface ActionBar {

    /**
     * by implementing this interface, activity extends from AbsBaseActivity
     * will get an ActionBar with a back button and a title uses activity
     * label as text by default.
     *
     * to create menu button, implements {@link Default.MenuCreator}
     * to change default behavior of back button, implements {@link Default.BackClickCallback}
     * to change elements on ActionBar, implements {@link OnCreateCallback}
     * if every operate above is needed, implements {@link Default.MenuCreator}
     */
    interface Default {

        interface MenuCreator extends Default {

            /**
             * called to get a view for menu.
             * @return the menu view, or null if menu is not needed.
             */
            View createActionBarMenu();
        }

        interface BackClickCallback extends Default {

            /**
             * called when back button is clicked.
             * Activity.finish() will be called if false is returned from this method
             * @param back back button
             * @return true if click event is handled, false otherwise
             */
            boolean onActionBarBackClicked(View back);
        }

        interface OnCreateCallback extends Default {

            /**
             * called when the ActionBar is first inflated.
             * @param actionBar the action bar
             * @param back back button
             * @param title TextView for title
             * @param menu the menu, can be null
             */
            void onActionBarCreated(View actionBar, ImageView back, TextView title, @Nullable View menu);
        }

        interface Callbacks extends MenuCreator, OnCreateCallback, BackClickCallback {
        }

    }

}
