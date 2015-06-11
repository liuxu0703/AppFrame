package lx.af.base;

import android.content.Intent;
import android.support.v4.app.Fragment;

import lx.af.utils.AlertUtils;

/**
 * Created by liuxu on 15-2-11.
 *
 */
public class BaseFragment extends Fragment {


    @Override
    public void startActivity(Intent intent) {
        if (getActivity() != null) {
            startActivity(intent);
        }
    }

    public void startActivity(Class cls){
        if (getActivity() != null) {
            startActivity(new Intent(getActivity(), cls));
        }
    }

    public void showToastLong(String msg){
        AlertUtils.showToastLong(msg);
    }

    public void showToastLong(int resId){
        AlertUtils.showToastLong(resId);
    }

    public void showToastShort(String msg){
        AlertUtils.showToastShort(msg);
    }

    public void showToastShort(int resId){
        AlertUtils.showToastShort(resId);
    }

}
