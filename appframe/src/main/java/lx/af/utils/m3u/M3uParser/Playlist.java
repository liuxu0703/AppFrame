package lx.af.utils.m3u.M3uParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Iterator;
import java.util.List;

/**
 * @author dkuffner
 */
public final class Playlist implements Iterable<Element> {

    private final List<Element> elements;
    private final boolean endSet;
    private final int targetDuration;
    private int mediaSequenceNumber;

    public static Playlist parse(Readable readable) throws ParseException {
        if (readable == null) {
            throw new NullPointerException("playlist");
        }
        return PlaylistParser.create(PlaylistType.M3U8).parse(readable);
    }

    public static Playlist parse(String playlist) throws ParseException {
        if (playlist == null) {
            throw new NullPointerException("playlist");
        }
        return parse(new StringReader(playlist));
    }

    public static Playlist parse(InputStream playlist) throws ParseException {
        if (playlist == null) {
            throw new NullPointerException("playlist");
        }
        return parse(new InputStreamReader(playlist));
    }

    public static Playlist parse(ReadableByteChannel playlist) throws ParseException {
        if (playlist == null) {
            throw new NullPointerException("playlist");
        }
        return parse(makeReadable(playlist));
    }

    Playlist(List<Element> elements, boolean endSet, int targetDuration, int mediaSequenceNumber) {
        if (elements == null) {
            throw new NullPointerException("elements");
        }
        this.targetDuration = targetDuration;
        this.elements = elements;
        this.endSet = endSet;
        this.mediaSequenceNumber = mediaSequenceNumber;
    }

    public int getTargetDuration() {
        return targetDuration;
    }

    public Iterator<Element> iterator() {
        return elements.iterator();
    }

    public List<Element> getElements() {
        return elements;
    }

    public boolean isEndSet() {
        return endSet;
    }

    public int getMediaSequenceNumber() {
        return mediaSequenceNumber;
    }

    /**
     * author: liuxu
     * according to protocol:
     * The initial minimum reload delay is the duration of the last media
     * file in the Playlist or 3 times the target duration, whichever is
     * less. Media file duration is specified by the EXTINF tag.
     */
    public int getMinimumReloadDelay() {
        int lastMediaDuration;
        if (elements.size() > 0) {
            lastMediaDuration = elements.get(elements.size() - 1).getDuration();
        } else {
            lastMediaDuration = targetDuration * 3;
        }
        return Math.min(lastMediaDuration, targetDuration * 3);
    }

    /**
     * author: liuxu
     * according to protocol:
     * The minimum delay is three times the target duration or a multiple of the
     * initial minimum reload delay, whichever is less. This multiple is
     * 0.5 for the first attempt, 1.5 for the second, and 3.0 thereafter.
     */
    public int getRetryReloadDelay(int retryCount) {
        int minimumReloadDelay = getMinimumReloadDelay();
        if (retryCount == 0) {
            return Math.min(targetDuration * 3, (int) (0.5 * minimumReloadDelay));
        } else if (retryCount == 1) {
            return Math.min(targetDuration * 3, (int) (1.5 * minimumReloadDelay));
        } else {
            return Math.min(targetDuration * 3, (int) (3.0 * minimumReloadDelay));
        }
    }

    private static Readable makeReadable(ReadableByteChannel source) {
        if (source == null) {
            throw new NullPointerException("source");
        }
        return Channels.newReader(source,
                java.nio.charset.Charset.defaultCharset().name());
    }

    @Override
    public String toString() {
        return "PlayListImpl{" +
                "elementCount=" + elements.size() +
                ", endSet=" + endSet +
                ", targetDuration=" + targetDuration +
                ", mediaSequenceNumber=" + mediaSequenceNumber +
                '}';
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("Playlist - ")
                .append(" Media Sequence No: ").append(getMediaSequenceNumber())
                .append(" Target Duration: ").append(getTargetDuration());
        int index = 0;
        for (Element el : this) {
            sb.append("\n").append(index).append(" - ");
            if (el.isPlayList()) {
                PlaylistInfo pl = el.getPlayListInfo();
                sb.append(M3uConstants.EXT_X_STREAM_INF)
                        .append(", PROGRAM-ID: ").append(pl.getProgramId())
                        .append(", BINDWIDTH: ").append(pl.getBandWitdh())
                        .append(", CODECS: ").append(pl.getCodecs());
            } else {
                sb.append(M3uConstants.EXTINF)
                        .append(", Dur: ").append(el.getDuration())
                        .append(", DIS: ").append(el.isDiscontinuity())
                        .append(", Title: ").append(el.getTitle());
            }
            sb.append("\n").append("URI: ").append(el.getURI());
            index ++;
        }
        return sb.toString();
    }

}