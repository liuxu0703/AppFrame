package lx.af.demo.consts;

import android.app.Activity;

/**
 * author: lx
 * date: 16-3-19
 */
public class ActionModel {

    public String title;
    public String sub;
    public String icon = "{md-apps}";
    public Class<? extends Activity> activity;

    public ActionModel(String title, String sub, String icon, Class<? extends Activity> activity) {
        this.title = title;
        this.sub = sub;
        this.icon = icon;
        this.activity = activity;
    }

    public ActionModel(String title, Class<? extends Activity> activity) {
        this.title = title;
        this.activity = activity;
    }
}
