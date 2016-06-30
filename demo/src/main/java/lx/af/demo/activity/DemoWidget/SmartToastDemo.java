package lx.af.demo.activity.DemoWidget;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import lx.af.demo.R;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseActivity;
import lx.af.utils.AlertUtils;
import lx.af.widget.frenchtoast.SmartToaster;

/**
 * author: lx
 * date: 16-6-30
 */
public class SmartToastDemo extends BaseActivity implements ActionBar.Default {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_toast);
        ButterKnife.inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.demo_smart_toast_btn_1, R.id.demo_smart_toast_btn_2,
            R.id.demo_smart_toast_btn_3, R.id.demo_smart_toast_btn_4,
            R.id.demo_smart_toast_btn_5})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.demo_smart_toast_btn_1:
                Toast.makeText(this, "system toast", Toast.LENGTH_SHORT).show();
                break;
            case R.id.demo_smart_toast_btn_2:
                SmartToaster.with(this).showText("smart toast");
                break;
            case R.id.demo_smart_toast_btn_3:
                AlertUtils.toastShort("smart toast with AlertUtils");
                break;
            case R.id.demo_smart_toast_btn_4:
                toastShort("smart toast using activity method");
                break;
            case R.id.demo_smart_toast_btn_5:
                View toast = View.inflate(this, R.layout.toast_with_icon, null);
                TextView icon = (TextView) toast.findViewById(R.id.toast_icon);
                TextView text = (TextView) toast.findViewById(R.id.toast_text);
                icon.setText("{md-info}");
                text.setText("custom smart toast");
                SmartToaster.with(this).shortLength().showView(toast);
                break;
        }
    }
}
