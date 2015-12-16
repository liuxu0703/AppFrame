package lx.af.utils.ActivityUtils;

/**
 * author: lx
 * date: 15-12-6
 */
public interface ActivityResultCallback<T> {

    void onActivityResult(int requestCode, T result);

}
