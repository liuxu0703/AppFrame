package lx.af.base;

import android.app.Activity;
import android.view.View;

/**
 * author: lx
 * date: 15-12-5
 */
public interface ActionBarAdapter {

    enum Type {
        NORMAL,
        OVERLAY,
    }

    Type getActionBarType();

    View getActionBarView(Activity activity);

}
