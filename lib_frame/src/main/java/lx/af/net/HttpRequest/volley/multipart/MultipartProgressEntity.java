package lx.af.net.HttpRequest.volley.multipart;

import java.io.IOException;
import java.io.OutputStream;

import lx.af.net.HttpRequest.volley.request.ProgressListener;

public class MultipartProgressEntity extends MultipartEntity {

    private ProgressListener listener;

    public void setListener(ProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        if (listener == null) {
            super.writeTo(outstream);
        } else {
            super.writeTo(new OutputStreamProgress(outstream, this.listener));
        }
    }
}