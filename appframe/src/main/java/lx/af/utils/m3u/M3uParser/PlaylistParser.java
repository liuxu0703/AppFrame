package lx.af.utils.m3u.M3uParser;

import android.util.Log;

import java.io.File;
import java.net.URI;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static lx.af.utils.m3u.M3uParser.M3uConstants.COMMENT_PREFIX;
import static lx.af.utils.m3u.M3uParser.M3uConstants.EXTINF;
import static lx.af.utils.m3u.M3uParser.M3uConstants.EXTM3U;
import static lx.af.utils.m3u.M3uParser.M3uConstants.EXT_X_DISCONTINUITY;
import static lx.af.utils.m3u.M3uParser.M3uConstants.EXT_X_ENDLIST;
import static lx.af.utils.m3u.M3uParser.M3uConstants.EXT_X_KEY;
import static lx.af.utils.m3u.M3uParser.M3uConstants.EXT_X_MEDIA_SEQUENCE;
import static lx.af.utils.m3u.M3uParser.M3uConstants.EXT_X_PROGRAM_DATE_TIME;
import static lx.af.utils.m3u.M3uParser.M3uConstants.EXT_X_STREAM_INF;
import static lx.af.utils.m3u.M3uParser.M3uConstants.EXT_X_TARGET_DURATION;
import static lx.af.utils.m3u.M3uParser.M3uConstants.EX_PREFIX;
import static lx.af.utils.m3u.M3uParser.M3uConstants.Patterns;

/**
 * Implementation based on http://tools.ietf.org/html/draft-pantos-http-live-streaming-02#section-3.1.
 *
 * @author dkuffner
 */
final class PlaylistParser {
    private static final String TAG = "M3uParser";

    static PlaylistParser create(PlaylistType type) {
        return new PlaylistParser(type);
    }

    private PlaylistType type;

    public PlaylistParser(PlaylistType type) {
        if (type == null) {
            throw new NullPointerException("type"); //NonNls
        }
        this.type = type;
    }


    /**
     * See {@link Channels#newReader(java.nio.channels.ReadableByteChannel, String)}
     * See {@link java.io.StringReader}
     *
     * @param source the source.
     * @return a playlist.
     * @throws ParseException parsing fails.
     */
    public Playlist parse(Readable source) throws ParseException {

        final Scanner scanner = new Scanner(source);

        boolean firstLine = true;

        int lineNumber = 0;
                          
        final List<Element> elements = new ArrayList<>(10);
        final ElementBuilder builder = new ElementBuilder();
        boolean endListSet = false;
        int targetDuration = -1;
        int mediaSequenceNumber = -1;

        EncryptionInfo currentEncryption = null;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (line.length() > 0) {
                if (line.startsWith(EX_PREFIX)) {
                    if (firstLine) {
                        checkFirstLine(lineNumber, line);
                        firstLine = false;
                    } else if (line.startsWith(EXTINF)) {
                        parseExtInf(line, lineNumber, builder);
                    } else if (line.startsWith(EXT_X_ENDLIST)) {
                        endListSet = true;
                    } else if (line.startsWith(EXT_X_TARGET_DURATION)) {
                        if (targetDuration != -1) {
                            throw new ParseException(line, lineNumber, EXT_X_TARGET_DURATION + " duplicated");
                        }
                        targetDuration = parseTargetDuration(line, lineNumber);
                    } else if (line.startsWith(EXT_X_MEDIA_SEQUENCE)) {
                        if (mediaSequenceNumber != -1) {
                            throw new ParseException(line, lineNumber, EXT_X_MEDIA_SEQUENCE + " duplicated");
                        }
                        mediaSequenceNumber = parseMediaSequence(line, lineNumber);
                    } else if (line.startsWith(EXT_X_DISCONTINUITY)) {
                    	builder.discontinuity(true);
                    } else if (line.startsWith(EXT_X_PROGRAM_DATE_TIME)) {
                        long programDateTime = parseProgramDateTime(line, lineNumber);
                        builder.programDate(programDateTime);
                    } else if (line.startsWith(EXT_X_KEY)) {
                        currentEncryption = parseEncryption(line, lineNumber);
                    } else if (line.startsWith(EXT_X_STREAM_INF)) {
                        parseExtStreamInf(line, builder);
                    } else {
                        Log.w(TAG, "unknown: '" + line + "'");
                    }
                } else if (line.startsWith(COMMENT_PREFIX)) {
                    // no first line check because comments will be ignored.
                    // comment do nothing
                    Log.d(TAG, "commit line: \n" + line);
                } else {
                    if (firstLine) {
                        checkFirstLine(lineNumber, line);
                    }

                    // No prefix: must be the media uri.
                    builder.encrypted(currentEncryption);

                    builder.uri(toURI(line));
                    elements.add(builder.create());

                    // a new element begins.
                    builder.reset();
                }
            }

            lineNumber++;
        }

        return new Playlist(Collections.unmodifiableList(elements), endListSet, targetDuration, mediaSequenceNumber);
    }

    private URI toURI(String line) {
        try {
            return (URI.create(line));
        } catch (IllegalArgumentException e) {
            return new File(line).toURI();
        }
    }

    private long parseProgramDateTime(String line, int lineNumber) throws ParseException {
        return Patterns.toDate(line, lineNumber);
    }

    private int parseTargetDuration(String line, int lineNumber) throws ParseException {
        return (int) parseNumberTag(line, lineNumber, Patterns.EXT_X_TARGET_DURATION, EXT_X_TARGET_DURATION);
    }

    private int parseMediaSequence(String line, int lineNumber) throws ParseException {
        return (int) parseNumberTag(line, lineNumber, Patterns.EXT_X_MEDIA_SEQUENCE, EXT_X_MEDIA_SEQUENCE);
    }

    private long parseNumberTag(String line, int lineNumber, Pattern patter, String property) throws ParseException {
        Matcher matcher = patter.matcher(line);
        if (!matcher.find() && !matcher.matches() && matcher.groupCount() < 1) {
            throw new ParseException(line, lineNumber, property + " must specify duration");
        }

        try {
            return Long.valueOf(matcher.group(1));
        } catch (NumberFormatException e) {
            // should not happen because of
            throw new ParseException(line, lineNumber, e);
        }
    }

    private void checkFirstLine(int lineNumber, String line) throws ParseException {
        if (type == PlaylistType.M3U8 && !line.startsWith(EXTM3U)) {
            throw new ParseException(line, lineNumber, "Playlist type '" + PlaylistType.M3U8 + "' must start with " + EXTM3U);
        }
    }

    private void parseExtInf(String line, int lineNumber, ElementBuilder builder) throws ParseException {
        // EXTINF:200,Title
        final Matcher matcher = Patterns.EXTINF.matcher(line);

        if (!matcher.find() && !matcher.matches() && matcher.groupCount() < 1) {
            throw new ParseException(line, lineNumber, "EXTINF must specify at least the duration");
        }

        String duration = matcher.group(1);
        String title = matcher.groupCount() > 1 ? matcher.group(2) : "";

        try {
            builder.duration(Double.valueOf(duration)).title(title);
        } catch (NumberFormatException e) {
            // should not happen because of 
            throw new ParseException(line, lineNumber, e);
        }
    }

    /**
     * author: liuxu
     * @throws ParseException
     */
    private void parseExtStreamInf(String line, ElementBuilder builder) throws ParseException {
        // EXT-X-STREAM-INF:PROGRAM-ID=123,BANDWIDTH=24576,CODECS="mp4a.40.29"
        int program_id = 0;
        int bandwidth = 0;
        String codecs = null;

        Matcher m_program_id = Patterns.EXT_X_STREAM_INF_PROGRAM_ID.matcher(line);
        if (m_program_id.find() && m_program_id.groupCount() > 0) {
            program_id = Integer.parseInt(m_program_id.group(1));
        }

        Matcher m_bandwidth = Patterns.EXT_X_STREAM_INF_BANDWIDTH.matcher(line);
        if (m_bandwidth.find() && m_bandwidth.groupCount() > 0) {
            bandwidth = Integer.parseInt(m_bandwidth.group(1));
        }

        Matcher m_codecs = Patterns.EXT_X_STREAM_INF_CODECS.matcher(line);
        if (m_codecs.find() && m_codecs.groupCount() > 0) {
            codecs = m_codecs.group(1);
        }

        builder.playList(program_id, bandwidth, codecs);
    }

    private EncryptionInfo parseEncryption(String line, int lineNumber) throws ParseException {
        Matcher matcher = Patterns.EXT_X_KEY.matcher(line);

        if (!matcher.find() || !matcher.matches() || matcher.groupCount() < 1) {
            throw new ParseException(line, lineNumber, "illegal input: " + line);
        }

        String method = matcher.group(1);
        String uri = matcher.group(3);

        if (method.equalsIgnoreCase("none")) {
            return null;
        }

        return new ElementImpl.EncryptionInfoImpl(uri != null ? toURI(uri) : null, method);
    }

}
