package lx.af.demo.activity;

import android.os.Bundle;

import lx.af.demo.R;
import lx.af.demo.base.BaseDemoActivity;
import lx.af.demo.view.FLItemView;
import lx.af.utils.ViewInject.ViewInject;

/**
 * author: lx
 * date: 16-3-17
 */
public class ActivityFlowLayout extends BaseDemoActivity {

    @ViewInject(id = R.id.activity_flow_layout_item1)
    private FLItemView line1;
    @ViewInject(id = R.id.activity_flow_layout_item2)
    private FLItemView line2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_layout);
        line1.setFlowTags(new String[] {
                "Hello", "Android", "Welcome",
        });
        line2.setFlowTags(new String[] {
                "Hello", "Android", "Weclome Hi ", "Button", "TextView", "Hello",
                "Android", "Weclome", "Button ImageView", "TextView", "Helloworld",
                "Android", "Weclome Hello", "Button Text", "TextView",
                "Hello", "Android", "Weclome Hi ", "Button", "TextView", "Hello",
                "Android", "Weclome", "Button ImageView", "TextView", "Helloworld",
                "Android", "Weclome Hello", "Button Text", "TextView",
        });
    }
}
