package com.derbysoft.dhp.fileserver.api.util;

/**
 *  converter set for the fileserver
 *
 * @author neo.chen {neocxf@gmail.com}
 * @version 4/24/17
 */
public enum OutputSize {
    A4("A4"),
    A5("A5"),
    A3("A3"),

    PX_1920_1024("1920px*1024px"),
    PX_1024_768("1024px*768px"),
    PX_1366_768("1366px*768px"),
    PX_1368_1024("1368px*1024px");

    private String val;

    OutputSize(String val) {
        this.val = val;
    }

    public String val() {
        return this.val;
    }

    public static String val(OutputSize outputSize) {
        return outputSize.val();
    }

    public static boolean assertValid(String val) {
        for (OutputSize os :
                OutputSize.values()) {
            if (os.val().equals(val)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public String toString() {
        return this.val;
    }
}