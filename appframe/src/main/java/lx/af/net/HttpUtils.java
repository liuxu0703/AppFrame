package lx.af.net;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * author: liuxu
 * date: 2015-03-11
 *
 * simple operations about http, header or cookie or async are not considered here.
 */
public final class HttpUtils {

    private static final int HTTP_CONNECT_TIMEOUT = 10 * 1000;
    private static final int HTTP_READ_TIMEOUT = 10 * 1000;

    private HttpUtils() {}

    public static String getString(String uri) {
        InputStream is = doGet(uri);
        if (is == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(is);
        char[] buf = new char[100];
        int len;
        try {
            while ((len = reader.read(buf)) != -1) {
                sb.append(buf, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeSilently(reader);
            closeSilently(is);
        }

        return sb.toString();
    }

    public static File getFile(String uri, String cachePath) {
        InputStream is = doGet(uri);
        if (is == null) {
            return null;
        }

        // save input stream to a file
        File cacheFile = new File(cachePath);
        BufferedInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new BufferedInputStream(is);
            out = new FileOutputStream(cacheFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) >= 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeSilently(in);
            closeSilently(out);
        }

        return cacheFile;
    }

    private static String readString(InputStream is) throws IOException {
        if (is == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(is);
        char[] buf = new char[100];
        int len;
        try {
            while ((len = reader.read(buf)) != -1) {
                sb.append(buf, 0, len);
            }
        } finally {
            closeSilently(reader);
        }
        return sb.toString();
    }

    // http get use HttpClient
    private static InputStream doGet(String uri) {
        if (uri == null) {
            return null;
        }

        InputStream is = null;
        HttpGet httpGet = new HttpGet(uri);
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getCookieStore().clear();

        httpClient.getParams().setParameter(
                CoreConnectionPNames.CONNECTION_TIMEOUT, HTTP_CONNECT_TIMEOUT);
        httpClient.getParams().setParameter(
                CoreConnectionPNames.SO_TIMEOUT, HTTP_READ_TIMEOUT);

        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufEntity = new BufferedHttpEntity(entity);
            is = bufEntity.getContent();
            return is;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeSilently(is);
        }
    }

    private static void closeSilently(Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (Throwable t) {
            // Do nothing
        }
    }


}
