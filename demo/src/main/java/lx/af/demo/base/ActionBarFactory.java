package lx.af.demo.base;

import lx.af.base.ActionBarAdapter;

/**
 * author: lx
 * date: 16-1-5
 */
final class ActionBarFactory {

    public static ActionBarAdapter getActionBarAdapter(BaseActivity activity) {
        if (activity instanceof ActionBar.Default) {
            return new ActionBarAdapterDefault(activity);
        }
        if (activity instanceof ActionBar.FrameMenu) {
            return new ActionBarAdapterFrameMenu(activity);
        }
        if (activity instanceof ActionBar.TextMenu) {
            return new ActionBarAdapterTextMenu(activity);
        }
        return null;
    }

}
