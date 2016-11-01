package com.derbysoft.dhp.fileserver.api.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
public class ServletStringWrapper extends HttpServletResponseWrapper {
    private StringWriter stringWriter;


    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response
     * @throws IllegalArgumentException if the response is null
     */
    public ServletStringWrapper(HttpServletResponse response) {
        super(response);
        this.stringWriter = new StringWriter();
    }

    /**
     *  when servlets or JSP pages ask for the Writter, don't give them the real one.
     *  Instead, give them a version that writes into the StringBuffer.
     * @return
     * @throws IOException
     */
    @Override
    public PrintWriter getWriter() throws IOException {
        return  new PrintWriter(stringWriter);
    }

    /**
     *  when the resources call getOutputStream, give them a phony output stream that just
     *  buffers up the ouput.
     *
     * @return
     * @throws IOException
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new StringOutputStream(stringWriter);
    }

    /**
     *  Get a string representation of the entire buffer.
     *
     *  Be sure <strong>NOT</strong> call this method multiple times on the same wrapper.
     *  The API for ServletStringWrapper does not guarantee that it "remembers" the previous
     *  value, so the call is likely to make a new String every time.
     * @return
     */
    @Override
    public String toString() {
        return (stringWriter.toString());
    }
}
