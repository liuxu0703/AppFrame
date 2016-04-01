package lx.af.demo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import lx.af.demo.R;
import lx.af.demo.activity.main.MainActivity;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseActivity;
import lx.af.test.TestRes;
import lx.af.net.HttpRequest.DataHull;
import lx.af.net.HttpRequest.ErrorHandler.ErrorHandler;
import lx.af.net.HttpRequest.RequestCallback;
import lx.af.net.HttpRequest.VolleyJsonRequest;
import lx.af.utils.ViewInject.ViewInject;
import lx.af.utils.log.Log;
import lx.af.demo.utils.m3u.M3uAudio.M3uAudioPlayer;
import lx.af.widget.kenburnsview.KenBurnsView;

/**
 * author: lx
 * date: 15-12-8
 */
public class ActivityTest extends BaseActivity implements
        View.OnClickListener,
        ActionBar.Default {

    private static final String L = "http://i.k1982.com/design_img/201008/20100806201117702.jpg";
    private static final String T = "http://img5.duitang.com/uploads/item/201405/03/20140503222852_aNXJL.thumb.700_0.jpeg";

    @ViewInject(id = R.id.test_kbv)
    KenBurnsView kbv;

    @ViewInject(id = R.id.test_btn_1, click = "onClick")
    Button btn;
    @ViewInject(id = R.id.test_btn_2, click = "onClick")
    Button btn2;

    String current = L;

    M3uAudioPlayer mM3uAudioPlayer;

    static int activity_count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableFeature(FEATURE_DOUBLE_BACK_EXIT);
        setContentView(R.layout.activity_test);
        mM3uAudioPlayer = new M3uAudioPlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mM3uAudioPlayer.release();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test_btn_1: {
                //String url = "http://hls.bj.qingting.fm/live/1133.m3u8?deviceid=c3579d72a4134990a056bd99b45c498d";
                //String url = TestRes.M3U8_RADIO_LIVE;
                VolleyJsonRequest<LiveUrlModel> r = new VolleyJsonRequest<>(
                        TestRes.GET_LIVE_STREAM_URL_2, null, new TypeToken<LiveUrlModel>() {});
                r.requestAsync(new RequestCallback() {
                    @Override
                    public void onRequestComplete(DataHull d) {
                        Log.d("liuxu", "11111 request live radio url: " + d);
                        if (d.isRequestSuccess()) {
                            LiveUrlModel model = d.getParsedData();
                            mM3uAudioPlayer.start(model.data);
                        } else {
                            ErrorHandler.typeToast().handleError(d);
                        }
                    }
                });
                break;
            }
            case R.id.test_btn_2: {
                activity_count ++;
                if (activity_count >= 3) {
                    startActivity(MainActivity.class);
                    activity_count = 0;
                } else {
                    startActivity(getClass());
                }
                break;
            }
        }
    }


    private static class LiveUrlModel {
        /** state 字段取值: 请求成功 */
        public final static int STATE_SUCCESS = 200;
        /** state 字段取值: 请求失败,失败信息见 msg 字段 */
        public final static int STATE_FAILED  = 300;
        /** state 字段取值: 登录信息过期 */
        public final static int STATE_LOGIN_EXPIRE = 310;

        @Expose
        public int count;

        @Expose
        public int state;

        @Expose
        public int code;

        @Expose
        public int totalPage;

        @Expose
        public String msg;

        @Expose
        public String data;

        @Override
        public String toString() {
            return "JsonHolder [count=" + count + ", data=" + data + ", msg=" + msg
                    + ", state=" + state + ", totalPage=" + totalPage + "]";
        }
    }

}
