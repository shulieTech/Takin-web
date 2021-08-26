package io.shulie.takin.web.biz.constant;

import io.shulie.takin.parent.exception.entity.ExceptionReadable;

/**
 * 仪表盘异常代码
 *
 * @author 张天赐
 */
public enum DashboardExceptionCode implements ExceptionReadable {
    /**
     * 错误枚举
     */
    DEFAULT("-1", "发生错误");

    private final String errorCode;
    private final String defaultValue;

    @Override
    public String getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getDefaultValue() {
        return this.defaultValue;
    }

    DashboardExceptionCode(String errorCode, String defaultValue) {
        this.errorCode = errorCode;
        this.defaultValue = defaultValue;
    }
}
