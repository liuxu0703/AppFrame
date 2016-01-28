package lx.af.activity.ImageSelector;

import java.util.List;

class FolderModel {
    public String name;
    public String path;
    public ImageModel cover;
    public List<ImageModel> images;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FolderModel)) {
            return false;
        }
        FolderModel other = (FolderModel) o;
        return this.path.equalsIgnoreCase(other.path);
    }
}
