package io.shulie.takin.web.biz.service.pressureresource.common.dy;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class StringObject extends SimpleJavaFileObject {
    private String content = null;

    protected StringObject(String className, String contents) throws URISyntaxException {
        super(new URI(className), Kind.SOURCE);
        this.content = contents;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return content;
    }
}