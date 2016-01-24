package lx.af.activity.CodeScanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.SurfaceView;

import com.google.zxing.Result;
import com.mining.app.zxing.view.ViewfinderView;

/**
 * author: lx
 * date: 16-1-24
 */
public interface ICodeScanner {

    ViewfinderView getViewFinder();

    SurfaceView getSurfaceView();

    Intent handleResult(Result result, Bitmap barcode);

}
