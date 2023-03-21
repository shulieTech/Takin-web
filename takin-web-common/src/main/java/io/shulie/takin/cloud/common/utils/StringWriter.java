package io.shulie.takin.cloud.common.utils;

import java.io.IOException;
import java.io.Writer;

/**
 * @author xuyh
 */
public class StringWriter extends Writer {
    private StringBuilder stringBuilder;

    public StringWriter() {
    }

    public StringWriter(StringBuilder stringBuilder) {
        this.stringBuilder = stringBuilder;
    }

    @Override
    public void write(char[] buf, int off, int len) throws IOException {
        if (stringBuilder == null) {
            stringBuilder = new StringBuilder();
        }
        stringBuilder.append(buf, off, len);
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws IOException {

    }

    public StringBuilder getStringBuilder() {
        return stringBuilder;
    }

    public void setStringBuilder(StringBuilder stringBuilder) {
        this.stringBuilder = stringBuilder;
    }

    public String getString() {
        return this.stringBuilder.toString();
    }
}
