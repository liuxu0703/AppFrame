package lx.af.net.HttpRequest.volley.request;

/**
 * author: lx
 * date: 16-1-12
 */
/** Callback interface for delivering the progress of the responses. */
public interface ProgressListener {
    /**
     * Callback method thats called on each byte transfer.
     */
    void onProgress(long transferredBytes, long totalSize);
}
