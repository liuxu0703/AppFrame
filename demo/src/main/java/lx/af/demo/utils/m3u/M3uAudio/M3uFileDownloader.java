package lx.af.demo.utils.m3u.M3uAudio;

import android.util.Log;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import lx.af.demo.utils.m3u.M3uParser.Playlist;

/**
 * author: lx
 * date: 16-1-12
 */
class M3uFileDownloader {

    public static Playlist downloadAsPlaylist(String url) {
        try {
            return Playlist.parse(getUrlStream(url));
        } catch (Exception e) {
            Log.e("liuxu", "download as playlist fail");
        }
        return null;
    }

    public static String downloadAsText(String url) {
        StringBuilder sb = new StringBuilder();
        String line = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getUrlStream(url)));
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            Log.e("liuxu", "download as text fail");
            return null;
        } finally {
            closeSilently(reader);
        }
        return sb.toString();
    }

    public boolean downloadAsFile(String url, String path) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = getUrlStream(url);
            out = new FileOutputStream(new File(path));
            byte buffer[] = new byte[4 * 1024];
            while((in.read(buffer)) != -1) {
                out.write(buffer);
            }
        } catch (Exception e) {
            Log.e("liuxu", "download as file fail");
            return false;
        }finally{
            closeSilently(in);
            closeSilently(out);
        }
        return true;
    }

    private static InputStream getUrlStream(String url) throws IOException {
        URL u = new URL(url);
        HttpURLConnection httpConn = (HttpURLConnection) u.openConnection();
        return httpConn.getInputStream();
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
