package top.neospot.conversion.api.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *  data utilities
 * Created by fei on 10/23/16.
 */
public class DateUtils {
    /**
     * get the string representation of current timestamp
     * @return current timestamp
     */
    public static String getCurrentLocalDateTimeStamp() {
        return getCurrentLocalDate("yyyy-MM-dd HH:mm:ss.SSS");
    }

    /**
     *  get the string representation of current time
     * @param pattern the pattern to be formatted with
     * @return current date
     */
    public static String getCurrentLocalDate(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }
}
