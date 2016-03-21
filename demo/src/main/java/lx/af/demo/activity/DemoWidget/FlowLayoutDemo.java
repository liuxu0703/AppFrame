package lx.af.demo.activity.DemoWidget;

import android.os.Bundle;

import lx.af.demo.R;
import lx.af.demo.base.BaseActivity;
import lx.af.demo.view.FLItemView;
import lx.af.utils.ViewInject.ViewInject;

/**
 * author: lx
 * date: 16-3-17
 */
public class FlowLayoutDemo extends BaseActivity {

    @ViewInject(id = R.id.activity_flow_layout_item1)
    private FLItemView line1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_layout);

        line1.setFlowTags(new String[] {
                "Hello", "Android", "Weclome Hi ", "Button", "TextView", "Hello",
                "Android", "Weclome", "Button ImageView", "TextView", "Helloworld",
                "Android", "Weclome Hello", "Button Text", "TextView",
        });

    }
}
