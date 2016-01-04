package lx.af.net.request.ErrorHandler;

/**
 * author: lx
 * date: 16-1-1
 */
public class ErrorHandler {

    private static ToastHandler sToastHandler = new ToastHandler();

    private IHandler mHandler;

    private ErrorHandler(IHandler handler) {
        mHandler = handler;
    }

    public static ToastHandler typeToast() {
        return sToastHandler;
    }

}
