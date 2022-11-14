package io.shulie.takin.web.biz.service.pressureresource.common.dy;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.net.URI;

public class CharSequenceJavaFileObject extends SimpleJavaFileObject {

    //表示java源代码
    private CharSequence content;

    protected CharSequenceJavaFileObject(String className, String content) {
        super(URI.create("string:///" + className.replaceAll("\\.", "/") + Kind.SOURCE.extension), Kind.SOURCE);
        this.content = content;
    }

    /**
     * 获取需要编译的源代码
     * @param ignoreEncodingErrors
     * @return
     * @throws IOException
     */
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return content;
    }
}