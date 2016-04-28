package lx.af.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import butterknife.OnClick;
import lx.af.demo.R;
import lx.af.demo.activity.main.MainActivity;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseActivity;
import lx.af.demo.utils.m3u.M3uAudio.M3uAudioPlayer;
import lx.af.net.HttpRequest.DataHull;
import lx.af.net.HttpRequest.ErrorHandler.ErrorHandler;
import lx.af.net.HttpRequest.RequestCallback;
import lx.af.net.HttpRequest.VolleyJsonRequest;
import lx.af.test.TestRes;
import lx.af.utils.ViewInject.ViewInject;
import lx.af.utils.ViewUtils.ViewUtils;
import lx.af.utils.log.Log;

/**
 * author: lx
 * date: 15-12-8
 */
public class ActivityTest extends BaseActivity implements
        View.OnClickListener,
        ActionBar.Default {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableFeature(FEATURE_DOUBLE_BACK_EXIT);
        setContentView(R.layout.activity_test);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    @OnClick({
            R.id.test_btn_1,
            R.id.test_btn_2,
            R.id.test_btn_3,
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test_btn_1: {
                break;
            }
            case R.id.test_btn_2: {
                break;
            }
            case R.id.test_btn_3: {
                break;
            }
        }
    }

}
