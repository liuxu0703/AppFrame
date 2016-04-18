package lx.af.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

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

    @ViewInject(id = R.id.test_root)
    RelativeLayout root;
    @ViewInject(id = R.id.test_btn_1, click = "onClick")
    Button btn;
    @ViewInject(id = R.id.test_btn_2, click = "onClick")
    Button btn2;
    @ViewInject(id = R.id.test_btn_3, click = "onClick")
    Button btn3;
    @ViewInject(id = R.id.test_img)
    View img;
    @ViewInject(id = R.id.test_text)
    TextView tv;

    M3uAudioPlayer mM3uAudioPlayer;

    static int activity_count = 0;

    static final String S1 = "111111111111111111";
    static final String S2 = "22222222222";

    String text = S1;

    private static final String SERVICECMD =
            "com.android.music.musicservicecommand";
    private static final String CMDNAME = "command";
    private static final String CMDPAUSE = "pause";
    public static final String CMDTOGGLEPAUSE = "togglepause";

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
//                activity_count ++;
//                if (activity_count >= 3) {
//                    startActivity(MainActivity.class);
//                    activity_count = 0;
//                } else {
//                    startActivity(getClass());
//                }


                Intent freshIntent = new Intent();
                freshIntent.setAction("com.android.music.musicservicecommand.pause");
                freshIntent.putExtra("command", "pause");
                sendBroadcast(freshIntent);
                break;
            }
            case R.id.test_btn_3: {
                text = text.equals(S1) ? S2 : S1;
                ViewUtils.animateTextChangeByWidth(tv, text);


                toggleNativePlayer();
                break;
            }
        }
    }

    private void toggleNativePlayer() {
        Log.d("liuxu", "111111111 toggle player 1111111111111111");
        Intent intent = new Intent("com.android.music.musicservicecommand");
        intent.putExtra("command", "togglepause");
        sendBroadcast(intent);
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
