package lx.af.demo.base;

import lx.af.base.AbsBaseActivity;
import lx.af.base.ActionBarAdapter;

/**
 * Created by liuxu on 15-6-11.
 *
 */
public class BaseDemoActivity extends AbsBaseActivity {

    @Override
    protected ActionBarAdapter getActionBarAdapter() {
        return ActionBarFactory.getActionBarAdapter(this);
    }

}
