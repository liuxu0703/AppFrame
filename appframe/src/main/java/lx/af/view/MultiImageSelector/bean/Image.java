package lx.af.view.MultiImageSelector.bean;

import android.net.Uri;
import android.text.TextUtils;

/**
 * 图片实体
 * Created by Nereo on 2015/4/7.
 */
public class Image {
    public String path;
    public String name;
    public long time;
    public String thumbnail;

    public boolean invalid = false;
    public boolean selected = false;

    public Image(String path, String name, long time, String thumbnail) {
        this.path = path;
        this.name = name;
        this.time = time;
        this.thumbnail = thumbnail;
    }

    public String getDisplayUri() {
        // use MediaStore thumbnail when possible
        if (!TextUtils.isEmpty(thumbnail)) {
            return Uri.parse("file://" + thumbnail).toString();
        } else {
            return Uri.parse("file://" + path).toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        try {
            Image other = (Image) o;
            return this.path.equalsIgnoreCase(other.path);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }

    @Override
    public String toString() {
        return "Image{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", time=" + time +
                ", thumbnail='" + thumbnail + '\'' +
                ", invalid=" + invalid +
                ", selected=" + selected +
                '}';
    }
}
