package com.lezo.idober.security;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;

public class GzipResponseStream extends ServletOutputStream {
    private GZIPOutputStream gzipStream;

    public GzipResponseStream(OutputStream output) throws IOException {
        gzipStream = new GZIPOutputStream(output);
    }

    @Override
    public void close() throws IOException {
        gzipStream.close();
    }

    @Override
    public void flush() throws IOException {
        gzipStream.flush();
    }

    @Override
    public void write(byte b[]) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte b[], int off, int len) throws IOException {
        gzipStream.write(b, off, len);
    }

    @Override
    public void write(int b) throws IOException {
        gzipStream.write(b);
    }
}