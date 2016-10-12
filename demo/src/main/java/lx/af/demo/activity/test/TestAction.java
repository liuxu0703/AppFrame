package lx.af.demo.activity.test;

import android.app.Activity;

import lx.af.demo.base.BaseActivity;

/**
 * author: lx
 * date: 16-4-1
 */
final class TestAction {

    public String title;
    public Action action;
    public Class<? extends Activity> activity;

    public TestAction(String title, Action action) {
        this.title = title;
        this.action = action;
    }

    public TestAction(String title, Class<? extends Activity> activity) {
        this.title = title;
        this.activity = activity;
    }

    static interface Action {

        void doAction(BaseActivity activity);

    }

}
