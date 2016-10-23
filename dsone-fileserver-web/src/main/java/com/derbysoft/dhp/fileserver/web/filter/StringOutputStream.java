package com.derbysoft.dhp.fileserver.web.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by fei on 10/16/16.
 */
public class StringOutputStream extends ServletOutputStream {
    private StringWriter stringWriter;

    public StringOutputStream(StringWriter stringWriter) {
        this.stringWriter = stringWriter;
    }

    @Override
    public void write(int b) throws IOException {
        stringWriter.write(b);
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {

    }
}
