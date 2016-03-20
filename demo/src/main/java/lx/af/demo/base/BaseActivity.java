package lx.af.demo.base;

import android.os.Bundle;
import android.text.TextUtils;

import lx.af.base.AbsBaseActivity;
import lx.af.base.ActionBarAdapter;

/**
 * Created by liuxu on 15-6-11.
 *
 */
public class BaseActivity extends AbsBaseActivity {

    public static final String EXTRA_ACTIVITY_TITLE = "activity_title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String title = getIntent().getStringExtra(EXTRA_ACTIVITY_TITLE);
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected ActionBarAdapter getActionBarAdapter() {
        return ActionBarFactory.getActionBarAdapter(this);
    }

}
