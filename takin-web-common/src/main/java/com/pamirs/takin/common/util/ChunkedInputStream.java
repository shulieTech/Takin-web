package com.pamirs.takin.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: vernon
 * @Date: 2022/2/24 19:13
 * @Description:
 */
public class ChunkedInputStream extends InputStream {
    private InputStream in;

    private boolean closed = false;

    private boolean bof = true;

    private boolean eof = false;

    private int chunkSize;

    private int pos;

    public ChunkedInputStream(final InputStream in) {
        if (in == null) {
            throw new IllegalArgumentException("InputStream parameter may not be null");
        }
        this.in = in;
        this.pos = 0;
    }

    @Override
    public int read() throws IOException {
        if (closed) {
            throw new IOException("Attempted read from closed stream.");
        }
        if (eof) {
            return -1;
        }
        if (pos >= chunkSize) {
            nextChunk();
            if (eof) {
                return -1;
            }
        }
        pos++;
        return in.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {

        if (closed) {
            throw new IOException("Attempted read from closed stream.");
        }

        if (eof) {
            return -1;
        }
        if (pos >= chunkSize) {
            nextChunk();
            if (eof) {
                return -1;
            }
        }
        len = Math.min(len, chunkSize - pos);
        int count = in.read(b, off, len);
        pos += count;
        return count;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    private void readCrlf() throws IOException {
        int cr = in.read();
        int lf = in.read();
        if ((cr != '\r') || (lf != '\n')) {
            throw new IOException(
                    "CRLF expected at end of chunk: " + cr + "/" + lf);
        }
    }

    private void nextChunk() throws IOException {
        if (!bof) {
            readCrlf();
        }
        chunkSize = getChunkSizeFromInputStream(in);
        bof = false;
        pos = 0;
        if (chunkSize == 0) {
            readHeaders(in);
            eof = true;
        }
    }

    public static String readLine(InputStream input) throws IOException {
        ByteArrayOutputStream bufdata = new ByteArrayOutputStream();
        int ch;
        while ((ch = input.read()) >= 0) {
            bufdata.write(ch);
            if (ch == '\n') {
                break;
            }
        }
        if (bufdata.size() == 0) {
            return null;
        }
        byte[] rawdata = bufdata.toByteArray();
        int len = rawdata.length;
        int offset = 0;
        if (len > 0) {
            if (rawdata[len - 1] == '\n') {
                offset++;
                if (len > 1) {
                    if (rawdata[len - 2] == '\r') {
                        offset++;
                    }
                }
            }
        }
        return new String(rawdata, 0, len - offset, UTF_8);
    }

    private static final String UTF_8 = "utf-8";


    public static Map<String, List<String>> readHeaders(InputStream input)
            throws IOException {
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        String line = readLine(input);
        while (line != null && !line.isEmpty()) {
            String[] headerPair = line.split(":");
            String name = headerPair[0].trim();
            String value = headerPair[1].trim();
            List<String> values = headers.get(name);
            if (values == null) {
                values = new ArrayList<String>();
                headers.put(name, values);
            }
            values.add(value);
            line = readLine(input);
        }
        return headers;
    }

    private static int getChunkSizeFromInputStream(final InputStream in)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int state = 0;
        while (state != -1) {
            int b = in.read();
            if (b == -1) {
                throw new IOException("chunked stream ended unexpectedly");
            }
            switch (state) {
                case 0:
                    switch (b) {
                        case '\r':
                            state = 1;
                            break;
                        case '\"':
                            state = 2;
                            /* fall through */
                        default:
                            baos.write(b);
                    }
                    break;

                case 1:
                    if (b == '\n') {
                        state = -1;
                    } else {
                        // this was not CRLF
                        throw new IOException("Protocol violation: Unexpected"
                                + " single newline character in chunk size");
                    }
                    break;

                case 2:
                    switch (b) {
                        case '\\':
                            b = in.read();
                            baos.write(b);
                            break;
                        case '\"':
                            state = 0;
                            /* fall through */
                        default:
                            baos.write(b);
                    }
                    break;
                default:
                    throw new RuntimeException("assertion failed");
            }
        }

        //parse data
        String dataString = new String(baos.toByteArray(), "US-ASCII");
        int separator = dataString.indexOf(';');
        dataString = (separator > 0)
                ? dataString.substring(0, separator).trim()
                : dataString.trim();

        int result;
        try {
            result = Integer.parseInt(dataString.trim(), 16);
        } catch (NumberFormatException e) {
            throw new IOException("Bad chunk size: " + dataString);
        }
        return result;
    }

    public static void exhaustInputStream(InputStream inStream)
            throws IOException {
        byte buffer[] = new byte[1024];
        while (inStream.read(buffer) >= 0) {
        }
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            try {
                if (!eof) {
                    exhaustInputStream(this);
                }
            } finally {
                eof = true;
                closed = true;
            }
        }
    }
}
