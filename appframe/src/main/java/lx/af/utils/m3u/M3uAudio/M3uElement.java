package lx.af.utils.m3u.M3uAudio;

import java.net.URI;

import lx.af.utils.m3u.M3uParser.Element;
import lx.af.utils.m3u.M3uParser.EncryptionInfo;
import lx.af.utils.m3u.M3uParser.Playlist;
import lx.af.utils.m3u.M3uParser.PlaylistInfo;

/**
 * author: lx
 * date: 16-1-12
 */
public class M3uElement implements Element {

    private final Element mRealElement;
    private final Playlist mParentList;
    private final boolean mIsFirst;
    private boolean mIsPlayed = false;
    private boolean mIsUsed = false;

    public M3uElement(Element element, Playlist playlist, boolean isFirst) {
        mRealElement = element;
        mIsFirst = isFirst;
        mParentList = playlist;
    }

    public boolean isPlayed() {
        return mIsPlayed;
    }

    public boolean isUsed() {
        return mIsUsed;
    }

    public boolean isFirst() {
        return mIsFirst;
    }

    public void setPlayed(boolean played) {
        mIsPlayed = played;
    }

    public void setUsed(boolean used) {
        mIsUsed = used;
    }

    public Playlist getParentList() {
        return mParentList;
    }

    @Override
    public String getTitle() {
        return mRealElement.getTitle();
    }

    @Override
    public int getDuration() {
        return mRealElement.getDuration();
    }

    @Override
    public double getExactDuration() {
        return mRealElement.getExactDuration();
    }

    @Override
    public URI getURI() {
        return mRealElement.getURI();
    }

    @Override
    public boolean isEncrypted() {
        return mRealElement.isEncrypted();
    }

    @Override
    public boolean isPlayList() {
        return mRealElement.isPlayList();
    }

    @Override
    public boolean isMedia() {
        return mRealElement.isMedia();
    }

    @Override
    public boolean isDiscontinuity() {
        return mRealElement.isDiscontinuity();
    }

    @Override
    public EncryptionInfo getEncryptionInfo() {
        return mRealElement.getEncryptionInfo();
    }

    @Override
    public PlaylistInfo getPlayListInfo() {
        return mRealElement.getPlayListInfo();
    }

    @Override
    public long getProgramDate() {
        return mRealElement.getProgramDate();
    }

    @Override
    public String toString() {
        return mRealElement.toString();
    }
}
