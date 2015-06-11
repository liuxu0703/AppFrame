package lx.af.view.MultiImageSelector.bean;

import java.util.List;

/**
 * 文件夹
 * Created by Nereo on 2015/4/7.
 */
public class Folder {
    public String name;
    public String path;
    public Image cover;
    public List<Image> images;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Folder)) {
            return false;
        }
        Folder other = (Folder) o;
        return this.path.equalsIgnoreCase(other.path);
    }
}
