package lx.af.demo.utils.m3u.M3uParser;

import java.net.URI;

/**
 * Contains information about media encryption.
 */
public interface EncryptionInfo {

    URI getURI();

    String getMethod();
}
