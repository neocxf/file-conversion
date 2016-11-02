package com.derbysoft.dhp.fileserver.api.util;

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

    /**
     *  check to see that if the given string is a valid url (could be a valid file url, ftp url or http url)
     * @param url the checking string
     * @return true if the string is a valid url; false if not
     *
     */
    public static boolean isValidUrl(String url) {
        return url.matches(URL_PATTERN);
    }
}
