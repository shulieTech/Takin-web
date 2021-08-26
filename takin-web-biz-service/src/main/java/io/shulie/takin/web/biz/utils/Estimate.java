package io.shulie.takin.web.biz.utils;

import org.apache.commons.lang3.StringUtils;

public class Estimate {

    public static void notBlank(Object object, String errorMessage) {
        if (null == object) {
            throw new IllegalArgumentException(errorMessage);
        }
        if (object instanceof String) {
            String obj = (String)object;
            if (StringUtils.isBlank(obj)) {
                throw new IllegalArgumentException(errorMessage);
            }
        } else if (object instanceof Integer) {
            Integer obj = (Integer)object;
            if (0 == obj) {
                throw new IllegalArgumentException(errorMessage);
            }
        }
    }
}
