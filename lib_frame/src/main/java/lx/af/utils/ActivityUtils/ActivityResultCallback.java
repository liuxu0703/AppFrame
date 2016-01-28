package lx.af.utils.ActivityUtils;

import android.support.annotation.NonNull;

/**
 * author: lx
 * date: 15-12-6
 */
public interface ActivityResultCallback<T> {

    void onActivityResult(int resultCode, @NonNull T result);

}
