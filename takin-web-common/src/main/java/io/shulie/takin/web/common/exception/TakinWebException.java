package io.shulie.takin.web.common.exception;

import io.shulie.takin.parent.exception.entity.BaseException;
import io.shulie.takin.parent.exception.entity.ExceptionReadable;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 无涯
 * @date 2021/6/28 4:21 下午
 */
@Getter
@Setter
public class TakinWebException extends BaseException {

    /**
     * 错误信息
     */
    private String message;

    public TakinWebException(ExceptionReadable ex, Object source) {
        super(ex, source);
        if (source == null) {
            setMessage("");
        } else {
            setMessage(source.toString());
        }
    }

    public TakinWebException(ExceptionReadable ex, Throwable source) {
        super(ex, source);
    }

    public TakinWebException(ExceptionReadable ex, Object source, Throwable e) {
        super(ex, source, e);
    }

}
