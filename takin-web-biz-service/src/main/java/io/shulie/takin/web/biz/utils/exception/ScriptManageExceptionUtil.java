package io.shulie.takin.web.biz.utils.exception;

import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;

/**
 * 脚本相关异常工具
 *
 * @author liuchuan
 * @date 2021/4/20 4:32 下午
 */
public class ScriptManageExceptionUtil {

    /**
     * 脚本异常下的 创建验证错误
     *
     * @param message 错误信息
     * @return 创建验证错误
     */
    public static TakinWebException getCreateValidError(String message) {
        return new TakinWebException(ExceptionCode.SCRIPT_MANAGE_CREATE_VALID_ERROR, message);
    }

    /**
     * 是否是 更新验证错误
     * 先判断条件, 如果真, 就抛出异常
     *
     * @param condition 条件
     * @param message   错误信息
     */
    public static void isCreateValidError(boolean condition, String message) {
        if (condition) {
            throw getCreateValidError(message);
        }
    }

    /**
     * 脚本异常下的 更新验证错误
     *
     * @param message 错误信息
     * @return 更新验证错误
     */
    public static TakinWebException getUpdateValidError(String message) {
        return new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, message);
    }

    /**
     * 是否是 更新验证错误
     * 先判断条件, 如果真, 就抛出异常
     *
     * @param condition 条件
     * @param message   错误信息
     */
    public static void isUpdateValidError(boolean condition, String message) {
        if (condition) {
            throw getUpdateValidError(message);
        }
    }

}
