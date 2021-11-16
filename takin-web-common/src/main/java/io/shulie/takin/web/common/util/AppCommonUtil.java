package io.shulie.takin.web.common.util;

import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.constant.ProbeConstants;

/**
 * 应用通用工具类
 *
 * @author liuchuan
 * @date 2021/6/18 10:02 上午
 */
public class AppCommonUtil implements ProbeConstants, AppConstants {

    /**
     * 版本号检查
     *
     * @param versionString 探针版本号 5.1.2, 4.1.1.9
     * @param versionBase 对比的版本号, int 数组形式
     * @return 是否正确 false 不正确, true 正确
     */
    public static boolean checkVersion(String versionString, int[] versionBase) {
        // 英文 . 分隔版本号
        String[] versionArray = versionString.split(ENGLISH_PERIOD);

        // 数组没有值的直接跳过
        if (versionArray.length < 1) {
            return false;
        }

        for (int i = 0; i < versionArray.length; i++) {
            // 对比前三位即可
            if (i > 2) {
                return true;
            }

            if (Integer.parseInt(versionArray[i]) < versionBase[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 探针版本号检查
     *
     * @param probeVersionString 探针版本号 5.1.2, 4.1.1.9
     * @return 是否正确 false 不正确, true 正确
     */
    public static boolean isNewProbeVersion(String probeVersionString) {
        return checkVersion(probeVersionString, PROBE_VERSION_BASE);
    }

    /**
     * agent 版本号检查
     *
     * @param agentVersionString agent 版本号 5.1.2, 4.1.1.9
     * @return 是否正确 false 不正确, true 正确
     */
    public static boolean isNewAgentVersion(String agentVersionString) {
        return checkVersion(agentVersionString, AGENT_VERSION_BASE);
    }

    /**
     * 是否是通用错误
     *
     * @param condition 条件
     * @param message 信息
     */
    public static void isCommonError(boolean condition, String message) {
        if (condition) {
            throw getCommonError(message);
        }
    }

    /**
     * 通用错误
     *
     * @param message 错误信息
     * @return 通用错误
     */
    public static TakinWebException getCommonError(String message) {
        return getError(ExceptionCode.ERROR_COMMON, message);
    }

    /**
     * 是否是错误
     *
     * @param exceptionCode 错误码
     * @param condition 条件
     * @param message 信息
     */
    public static void isError(boolean condition, ExceptionCode exceptionCode, String message) {
        if (condition) {
            throw getError(exceptionCode, message);
        }
    }

    /**
     * 动态错误
     *
     * @param exceptionCode 错误码
     * @param message 错误信息
     * @return 通用错误
     */
    public static TakinWebException getError(ExceptionCode exceptionCode, String message) {
        return new TakinWebException(exceptionCode, message);
    }

}
