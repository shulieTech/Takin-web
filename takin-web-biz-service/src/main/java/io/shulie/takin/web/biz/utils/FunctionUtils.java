package io.shulie.takin.web.biz.utils;

import org.apache.commons.lang3.StringUtils;

public class FunctionUtils {

    public static void ifDone(boolean cond, Runnable runnable) {
        if (cond) {
            runnable.run();
        }
    }

    public static void ifBlankDone(String text, Runnable runnable) {
        ifDone(StringUtils.isBlank(text), runnable);
    }

}
