package lx.af.demo.activity;

import android.os.Bundle;

import lx.af.demo.R;
import lx.af.demo.base.BaseDemoActivity;
import lx.af.demo.view.FLItemView;
import lx.af.manager.GlobalThreadManager;
import lx.af.utils.ViewInject.ViewInject;
import lx.af.widget.DotCircleProgress;
import lx.af.widget.RunningDigitView;

/**
 * author: lx
 * date: 16-3-17
 *
 * 0780001
 */
public class ActivityFlowLayout extends BaseDemoActivity {

    @ViewInject(id = R.id.activity_flow_layout_digit)
    private RunningDigitView mDigitView;
    @ViewInject(id = R.id.activity_flow_layout_progress)
    private DotCircleProgress mProgress;
    @ViewInject(id = R.id.activity_flow_layout_item1)
    private FLItemView line1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_layout);
        mDigitView.setDigitalType(RunningDigitView.DIGITAL_TYPE_CURRENCY);
        mDigitView.setRollingDuration(1200);

        line1.setFlowTags(new String[] {
                "Hello", "Android", "Weclome Hi ", "Button", "TextView", "Hello",
                "Android", "Weclome", "Button ImageView", "TextView", "Helloworld",
                "Android", "Weclome Hello", "Button Text", "TextView",
        });

        mProgress.startSpin();
        GlobalThreadManager.runInUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                int progress = 88;
                mDigitView.setDigit(progress);
                mDigitView.startRolling(true);
                mProgress.setProgressWithAnim(progress * 360 / 100);
            }
        }, 2000);
    }
}
