package com.derbysoft.dhp.fileserver.api.util;

/**
 *  converter set for the fileserver
 *
 * @author neo.chen {neocxf@gmail.com}
 * @version 4/24/17
 */
public enum OutputSize {
    A4("A4", 0.52f),
    A5("A5", 0.36f),
    A3("A3"),

    PX_1920_1024("1920px*1024px"),
    PX_1024_768("1024px*768px"),
    PX_1366_768("1366px*768px"),
    PX_1368_1024("1368px*1024px");

    private String val;
    private float zoomFactor = 1.0f;

    OutputSize(String val) {
        this.val = val;
    }

    OutputSize(String val, float zoomFactor) {
        this.val = val;
        this.zoomFactor = zoomFactor;
    }

    public String val() {
        return this.val;
    }

    public float zoomFactor() {
        return this.zoomFactor;
    }

    public static String val(OutputSize outputSize) {
        return outputSize.val();
    }

    public static float zoomFactor(OutputSize outputSize) {
        return outputSize.zoomFactor();
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