package lx.af.demo.activity.DemoWidget;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import lx.af.demo.R;
import lx.af.demo.adapter.DanmakuDemoAdapter1;
import lx.af.demo.adapter.DanmakuDemoAdapter2;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseActivity;
import lx.af.demo.model.DanmakuModel1;
import lx.af.demo.utils.ImageLoaderHelper;
import lx.af.demo.utils.TestData.TestImageHelper;
import lx.af.widget.DanmakuLayout.DanmakuBaseAdapter;
import lx.af.widget.DanmakuLayout.DanmakuLayout;

/**
 * author: lx
 * date: 16-3-1
 */
public class DanmakuLayoutDemo extends BaseActivity implements
        View.OnClickListener,
        ActionBar.Default {

    private View mDanmakuBtnToggle;
    private TextView mDanmakuInfoText;
    private DanmakuLayout mDanmaku;
    private DanmakuDemoAdapter1 mDanmakuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danmaku);

        final DanmakuLayout danmaku2 = (DanmakuLayout) findViewById(R.id.danmaku_layout_2);
        danmaku2.setAdapter(new DanmakuDemoAdapter2(this));
        danmaku2.startDanmaku();
        danmaku2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                danmaku2.toggleDanmaku();
                String msg = "danmaku " + (danmaku2.isDanmakuRunning() ? "on" : "off");
                Toast.makeText(DanmakuLayoutDemo.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        // =======================================

        ImageView bkg = (ImageView) findViewById(R.id.danmaku_bkg_img);
        ImageLoaderHelper.displayImage(bkg, TestImageHelper.randomImageL());

        mDanmaku = (DanmakuLayout) findViewById(R.id.danmaku_layout);
        mDanmakuInfoText = (TextView) findViewById(R.id.danmaku_info_text);
        mDanmakuBtnToggle = findViewById(R.id.danmaku_btn_toggle);
        mDanmakuBtnToggle.setOnClickListener(this);
        mDanmakuBtnToggle.setSelected(mDanmaku.isDanmakuRunning());
        mDanmakuAdapter = new DanmakuDemoAdapter1(this);
        mDanmaku.setAdapter(mDanmakuAdapter);
        mDanmaku.startDanmaku();
        findViewById(R.id.danmaku_btn_poll_mode).setOnClickListener(this);
        findViewById(R.id.danmaku_btn_interval).setOnClickListener(this);
        findViewById(R.id.danmaku_btn_add).setOnClickListener(this);

        refreshInfoText();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.danmaku_btn_toggle: {
                mDanmaku.toggleDanmaku();
                mDanmakuBtnToggle.setSelected(mDanmaku.isDanmakuRunning());
                if (mDanmaku.isDanmakuRunning() && mDanmaku.isDataEmpty()) {
                    mDanmakuAdapter.loadData();
                }
                break;
            }
            case R.id.danmaku_btn_add: {
                DanmakuDemoAdapter1.ItemWrapper item = new DanmakuDemoAdapter1.ItemWrapper();
                item.d4 = DanmakuModel1.createRandom();
                mDanmakuAdapter.addDataHighPriority(item);
                break;
            }
            case R.id.danmaku_btn_poll_mode: {
                switch (mDanmakuAdapter.getPollDataMode()) {
                    case FIFO:
                        mDanmakuAdapter.setPollDataMode(DanmakuBaseAdapter.PollDataMode.LIFO);
                        break;
                    case LIFO:
                        mDanmakuAdapter.setPollDataMode(DanmakuBaseAdapter.PollDataMode.RANDOM);
                        break;
                    case RANDOM:
                        mDanmakuAdapter.setPollDataMode(DanmakuBaseAdapter.PollDataMode.FIFO);
                        break;
                }
                break;
            }
            case R.id.danmaku_btn_interval: {
                long interval = mDanmaku.getDanmakuMinInterval();
                if (interval <= 1000) {
                    mDanmaku.setDanmakuMinInterval(2000);
                } else if (interval > 1000 && interval <= 2000) {
                    mDanmaku.setDanmakuMinInterval(3000);
                } else {
                    mDanmaku.setDanmakuMinInterval(500);
                }
                break;
            }
        }

        refreshInfoText();
    }

    private void refreshInfoText() {
        StringBuilder info = new StringBuilder();
        info.append("running: ").append(mDanmaku.isDanmakuRunning()).append("\n")
                .append("interval: ").append(mDanmaku.getDanmakuMinInterval()).append("\n")
                .append("poll mode: ").append(mDanmakuAdapter.getPollDataMode());
        mDanmakuInfoText.setText(info.toString());
    }

}
