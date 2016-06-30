package lx.af.widget.frenchtoast;

/**
 * import from https://github.com/pyricau/frenchtoast
 */
public final class Toasted {

    private final ToastQueue toastQueue;
    private final Mixture mixture;

    Toasted(ToastQueue toastQueue, Mixture mixture) {
        this.toastQueue = toastQueue;
        this.mixture = mixture;
    }

    public boolean cancel() {
        return toastQueue.cancel(mixture);
    }
}
