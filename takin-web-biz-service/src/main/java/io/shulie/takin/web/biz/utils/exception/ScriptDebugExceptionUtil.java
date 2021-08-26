package io.shulie.takin.web.biz.utils.exception;

import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;

/**
 * 脚本调试相关异常工具
 *
 * @author liuchuan
 * @date 2021/4/20 4:32 下午
 */
public class ScriptDebugExceptionUtil {

    /**
     * 是否是 通用错误错误
     * 先判断条件, 如果真, 就抛出异常
     *
     * @param condition 条件
     * @param message   错误信息
     */
    public static void isCommonError(boolean condition, String message) {
        if (condition) {
            throw getDebugError(message);
        }
    }

    /**
     * 脚本调试异常下通用错误
     *
     * @param message 错误信息
     * @return 创建验证错误
     */
    public static TakinWebException getCommonError(String message) {
        return new TakinWebException(ExceptionCode.SCRIPT_DEBUG_COMMON_ERROR, message);
    }

    /**
     * 是否是 状态检查错误
     * 先判断条件, 如果真, 就抛出异常
     *
     * @param condition 条件
     * @param message   错误信息
     */
    public static void isCheckStatusError(boolean condition, String message) {
        if (condition) {
            throw getRequestListError(message);
        }
    }

    /**
     * 脚本调试异常下状态检查错误
     *
     * @param message 错误信息
     * @return 创建错误
     */
    public static TakinWebException getCheckStatusError(String message) {
        return new TakinWebException(ExceptionCode.SCRIPT_DEBUG_CHECK_STATUS_ERROR, message);
    }

    /**
     * 是否是 请求流量明细错误
     * 先判断条件, 如果真, 就抛出异常
     *
     * @param condition 条件
     * @param message   错误信息
     */
    public static void isRequestListError(boolean condition, String message) {
        if (condition) {
            throw getRequestListError(message);
        }
    }

    /**
     * 脚本调试异常下请求流量明细错误
     *
     * @param message 错误信息
     * @return 创建错误
     */
    public static TakinWebException getRequestListError(String message) {
        return new TakinWebException(ExceptionCode.SCRIPT_DEBUG_REQUEST_ERROR, message);
    }

    /**
     * 是否是 调试错误
     * 先判断条件, 如果真, 就抛出异常
     *
     * @param condition 条件
     * @param message   错误信息
     */
    public static void isDebugError(boolean condition, String message) {
        if (condition) {
            throw getDebugError(message);
        }
    }

    /**
     * 脚本调试异常下调试错误
     *
     * @param message 错误信息
     * @return 创建验证错误
     */
    public static TakinWebException getDebugError(String message) {
        return new TakinWebException(TakinWebExceptionEnum.SCRIPT_DEBUG_VALIDATE_ERROR, message);
    }

}
