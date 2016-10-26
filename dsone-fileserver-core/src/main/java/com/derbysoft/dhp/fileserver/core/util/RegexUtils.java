package com.derbysoft.dhp.fileserver.core.util;

/**
 *
 * @author neo.fei {neocxf@gmail.com}
 */
public class RegexUtils {
    // http://stackoverflow.com/questions/163360/regular-expression-to-match-urls-in-java
    public static final String URL_PATTERN = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    private RegexUtils() {
        throw new AssertionError("should not initialize this class");
    }

    public static boolean isValidUrl(String url) {
        return url.matches(url);
    }
}
