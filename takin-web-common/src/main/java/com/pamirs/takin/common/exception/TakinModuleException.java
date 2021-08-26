package com.pamirs.takin.common.exception;

import com.pamirs.takin.common.constant.TakinErrorEnum;

/**
 * 说明：压测模块异常枚举类
 *
 * @author shulie
 * @version 1.0
 * @date 2018年4月27日
 */
@Deprecated
public class TakinModuleException extends Exception {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;

    /**
     * takin错误枚举类
     */
    private TakinErrorEnum takinErrorEnum;

    /**
     * 异常信息
     */
    private String message;

    /**
     * 构造方法
     *
     * @param takinErrorEnum 异常枚举
     */
    public TakinModuleException(TakinErrorEnum takinErrorEnum) {
        super(takinErrorEnum.getErrorMessage());
        this.takinErrorEnum = takinErrorEnum;
    }

    /**
     * 构造方法
     *
     * @param takinErrorEnum 异常枚举
     * @param cause          异常链
     */
    public TakinModuleException(TakinErrorEnum takinErrorEnum, Throwable cause) {
        super(takinErrorEnum.getErrorMessage(), cause);
        this.takinErrorEnum = takinErrorEnum;
    }

    /**
     * 构造方法
     *
     * @param cause 异常链
     */
    public TakinModuleException(Throwable cause) {
        super(cause);
    }

    /**
     * 构造方法
     *
     * @param message 错误信息
     */
    public TakinModuleException(String message) {
        this.message = message;
    }

    /**
     * 2018年5月21日
     *
     * @return the message
     *
     * @author shulie
     * @version 1.0
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 2018年5月21日
     *
     * @param message the message to set
     *
     * @author shulie
     * @version 1.0
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 2018年5月21日
     *
     * @return the TakinErrorEnum
     *
     * @author shulie
     * @version 1.0
     */
    public TakinErrorEnum getTakinErrorEnum() {
        return takinErrorEnum;
    }

    /**
     * 获取错误信息
     *
     * @return 错误信息
     *
     * @author shulie
     * @date 2018年5月21日
     * @version v1.0
     */
    public String getErrorMessage() {
        return getTakinErrorEnum().getErrorMessage();
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     *
     * @author shulie
     * @date 2018年5月21日
     * @version v1.0
     */
    public String getErrorCode() {
        return getTakinErrorEnum().getErrorCode();
    }
}
