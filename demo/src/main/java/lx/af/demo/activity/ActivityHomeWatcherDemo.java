package lx.af.demo.activity;

import android.os.Bundle;
import android.widget.Toast;

import lx.af.base.BaseActivity;
import lx.af.demo.R;
import lx.af.demo.base.BaseDemoActivity;
import lx.af.utils.HomeWatcher;

/**
 * Created by liuxu on 15-3-4.
 *
 */
public class ActivityHomeWatcherDemo extends BaseDemoActivity implements
        BaseDemoActivity.ActionBarImpl,
        BaseActivity.SwipeBackImpl,
        HomeWatcher.OnHomePressedListener {

    private HomeWatcher mHomeWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_watcher);
        initHomeWatch();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopHomeWatch();
    }

    @Override
    public void onHomePressed() {
        Toast.makeText(this, "home pressed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHomeLongPressed() {
        Toast.makeText(this, "home long pressed", Toast.LENGTH_SHORT).show();
    }


    private void initHomeWatch() {
        this.mHomeWatcher = new HomeWatcher(this);
        this.mHomeWatcher.setOnHomePressedListener(this);
        this.mHomeWatcher.startWatch();
    }

    private void stopHomeWatch() {
        this.mHomeWatcher.stopWatch();
    }
}
