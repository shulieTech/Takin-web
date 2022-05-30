package io.shulie.takin.cloud.common.exception;

import io.shulie.takin.parent.exception.entity.BaseException;
import io.shulie.takin.parent.exception.entity.ExceptionReadable;

/**
 * @author shiyajian
 * @author 张天赐
 * @date 2020-09-26
 * @date 2021-11-19 16:48:02
 */
public class TakinCloudException extends BaseException {

    public TakinCloudException(ExceptionReadable ex, Object source) {
        super(ex, source);
    }

    public TakinCloudException(ExceptionReadable ex, Object source, Throwable e) {
        super(ex, source, e);
    }
}
