package lx.af.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * author: lx
 * date: 16-2-20
 */
public class SubFileHandler {

    private int mDepthTotal;
    private boolean mIncludeSelf;
    private File mFolder;
    private FileFilter mFilter;

    // use this class to mark depth for each file
    private class FileWrapper {
        int depth;
        File file;

        FileWrapper(File file, int depth) {
            this.file = file;
            this.depth = depth;
        }

        List<FileWrapper> listFileWrapper() {
            if (this.file.isDirectory()) {
                List<FileWrapper> list = new ArrayList<>();
                File[] files = this.file.listFiles(mFilter);
                for (File f : files) {
                    list.add(new FileWrapper(f, this.depth + 1));
                }
                return list;
            } else {
                return null;
            }
        }

        boolean isDirectory() {
            return this.file.isDirectory();
        }
    }

    public SubFileHandler(File folder, FileFilter filter,
                          int depth, boolean includeSelf) throws IllegalArgumentException {
        mFolder = folder;
        mFilter = filter;
        mIncludeSelf = includeSelf;

        if (!mFolder.exists()) {
            // should be a folder to go on
            throw new IllegalArgumentException(
                    "not a valid folder: " + folder);
        }

        if (depth <= 0) {
            // consider 0 and negative number as no limit.
            mDepthTotal = Integer.MAX_VALUE;
        } else {
            mDepthTotal = depth;
        }
    }

    public List<File> getSubFiles() {
        List<FileWrapper> list = new ArrayList<>();
        FileWrapper root = new FileWrapper(mFolder, 0);
        doGetSubFiles(root, list);
        if (!mIncludeSelf) {
            // the folder being searched is always the first
            // element in the list
            list.remove(0);
        }
        List<File> retList = new ArrayList<>();
        for (FileWrapper f : list) {
            retList.add(f.file);
        }
        return retList;
    }

    // recursively add sub files into the list
    private void doGetSubFiles(
            FileWrapper wrapper, List<FileWrapper> list) {
        if (wrapper.isDirectory()) {
            list.add(wrapper);
        }
        if (wrapper.depth >= mDepthTotal) {
            // folder depth reaches the limit, stop recursion
            return;
        }
        List<FileWrapper> wrapperList = wrapper.listFileWrapper();
        if (wrapperList != null) {
            list.addAll(wrapperList);
            for (FileWrapper f : wrapperList) {
                doGetSubFiles(f, list);
            }
        }
    }
}
