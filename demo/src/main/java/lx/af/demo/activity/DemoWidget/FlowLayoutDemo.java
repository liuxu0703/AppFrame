package lx.af.demo.activity.DemoWidget;

import android.os.Bundle;

import lx.af.demo.R;
import lx.af.demo.base.BaseActivity;
import lx.af.demo.view.FLItemView;

/**
 * author: lx
 * date: 16-3-17
 */
public class FlowLayoutDemo extends BaseActivity {

    private FLItemView line1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_layout);
        line1 = (FLItemView) findViewById(R.id.activity_flow_layout_item1);

        line1.setFlowTags(new String[] {
                "Hello", "Android", "Weclome Hi ", "Button", "TextView", "Hello",
                "Android", "Weclome", "Button ImageView", "TextView", "Helloworld",
                "Android", "Weclome Hello", "Button Text", "TextView",
        });

    }
}
