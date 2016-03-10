package lx.af.demo.base;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import lx.af.widget.iconify.widget.IconTextView;

/**
 * author: lx
 * date: 16-2-10
 */
public interface ActionBar {

    /**
     * by implementing this interface, activity extends from AbsBaseActivity
     * will get an ActionBar with a back button and a title uses activity
     * label as text by default.
     *
     * to config views, implements {@link Callback}
     */
    interface Default {

        interface Overlay extends Default, OverlayInner {}

        interface Callback extends Default {

            /**
             * called when the ActionBar is first inflated.
             * @param actionBar the action bar
             * @param left left button
             * @param title TextView for title
             * @param right right button
             */
            void onActionBarCreated(View actionBar, IconTextView left, TextView title, IconTextView right);

            interface Overlay extends Callback, OverlayInner {}
        }

    }


    interface TextMenu {

        interface Overlay extends TextMenu, OverlayInner {}

        interface Callback extends TextMenu {

            /**
             * called when the ActionBar is first inflated.
             * @param actionBar the action bar
             * @param left left button
             * @param title TextView for title
             * @param right right button
             */
            void onActionBarCreated(View actionBar, IconTextView left, TextView title, TextView right);

            interface Overlay extends Callback, OverlayInner {}
        }

    }


    interface FrameMenu {

        interface Overlay extends FrameMenu, OverlayInner {}

        interface Callback extends FrameMenu {

            /**
             * called when the ActionBar is first inflated.
             * @param actionBar the action bar
             * @param left left button
             * @param title TextView for title
             * @param right a frame layout as menu container
             */
            void onActionBarCreated(View actionBar, IconTextView left, TextView title, FrameLayout right);

            interface Overlay extends Callback, OverlayInner {}
        }

    }


    interface OverlayInner {}

}
