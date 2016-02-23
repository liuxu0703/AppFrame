package lx.af.activity.CodeScanner;

import android.graphics.Bitmap;
import android.view.SurfaceView;

import com.google.zxing.Result;
import com.mining.app.zxing.view.ViewfinderView;

/**
 * author: lx
 * date: 16-02-02
 */
public interface ICodeScanner {

    ViewfinderView getViewFinder();

    SurfaceView getSurfaceView();

    boolean handleResult(Result result, Bitmap barcode);

}
