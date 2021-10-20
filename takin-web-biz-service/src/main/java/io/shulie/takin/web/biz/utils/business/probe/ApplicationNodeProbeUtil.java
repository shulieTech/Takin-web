package io.shulie.takin.web.biz.utils.business.probe;

import io.shulie.takin.web.common.constant.ProbeConstants;
import io.shulie.takin.web.common.enums.probe.AmdbProbeStatusEnum;
import io.shulie.takin.web.common.enums.probe.ApplicationNodeProbeOperateEnum;
import io.shulie.takin.web.common.pojo.dto.probe.MatchApplicationNodeProbeStateDTO;

/**
 * 节点探针相关工具类
 *
 * @author liuchuan
 * @date 2021/6/7 11:30 上午
 */
public class ApplicationNodeProbeUtil implements ProbeConstants {

    /**
     * 本地卸载中
     *
     * @param dto 所需参数
     * @return 是否是
     */
    public static boolean uninstalling(MatchApplicationNodeProbeStateDTO dto) {
        return isUninstallOperate(dto.getProbeOperate()) && operateResultNone(dto.getProbeOperateResult());
    }

    /**
     * 本地升级中
     *
     * @param dto 所需参数
     * @return 是否是
     */
    public static boolean upgrading(MatchApplicationNodeProbeStateDTO dto) {
        return isUpgradeOperate(dto.getProbeOperate()) && operateResultNone(dto.getProbeOperateResult());
    }

    /**
     * 本地安装中
     *
     * @param dto 所需参数
     * @return 是否是
     */
    public static boolean installing(MatchApplicationNodeProbeStateDTO dto) {
        return isInstallOperate(dto.getProbeOperate()) && operateResultNone(dto.getProbeOperateResult());
    }

    /**
     * 本地升级失败
     *
     * @param dto 所需参数
     * @return 是否是
     */
    public static boolean upgradeFailed(MatchApplicationNodeProbeStateDTO dto) {
        return isUpgradeOperate(dto.getProbeOperate()) && operateResultFailed(dto.getProbeOperateResult());
    }

    /**
     * 本地安装失败
     *
     * @param dto 所需参数
     * @return 是否是
     */
    public static boolean installFailed(MatchApplicationNodeProbeStateDTO dto) {
        return isInstallOperate(dto.getProbeOperate()) && operateResultFailed(dto.getProbeOperateResult());
    }

    /**
     * 本地卸载失败
     *
     * @param dto 所需参数
     * @return 是否是
     */
    public static boolean uninstallFailed(MatchApplicationNodeProbeStateDTO dto) {
        return isUninstallOperate(dto.getProbeOperate()) && operateResultFailed(dto.getProbeOperateResult());
    }

    /**
     * 操作结果无
     *
     * @param operateResult 操作结果
     * @return 是否
     */
    public static boolean operateResultNone(Integer operateResult) {
        return PROBE_OPERATE_RESULT_NONE == operateResult;
    }

    /**
     * 操作结果成功
     *
     * @param operateResult 操作结果
     * @return 是否
     */
    public static boolean operateResultSuccess(Integer operateResult) {
        return PROBE_OPERATE_RESULT_SUCCESS == operateResult;
    }

    /**
     * 操作结果失败
     *
     * @param operateResult 操作结果
     * @return 是否
     */
    public static boolean operateResultFailed(Integer operateResult) {
        return PROBE_OPERATE_RESULT_FAILED == operateResult;
    }

    /**
     * 卸载操作
     *
     * @param operate 操作
     * @return 是否是
     */
    public static boolean isUninstallOperate(Integer operate) {
        return ApplicationNodeProbeOperateEnum.UNINSTALL.getCode().equals(operate);
    }

    /**
     * 安装操作
     *
     * @param operate 操作
     * @return 是否是
     */
    public static boolean isInstallOperate(Integer operate) {
        return ApplicationNodeProbeOperateEnum.INSTALL.getCode().equals(operate);
    }

    /**
     * 升级操作
     *
     * @param operate 操作
     * @return 是否是
     */
    public static boolean isUpgradeOperate(Integer operate) {
        return ApplicationNodeProbeOperateEnum.UPGRADE.getCode().equals(operate);
    }

    /**
     * 本地无操作
     *
     * @param operate 操作
     * @return 是否是
     */
    public static boolean isNoneOperate(Integer operate) {
        return ApplicationNodeProbeOperateEnum.NONE.getCode().equals(operate);
    }

    /**
     * agent 安装失败
     *
     * @param amdbProbeState amdb 状态
     * @return 是否是
     */
    public static boolean isAmdbInstallFailed(Integer amdbProbeState) {
        return AmdbProbeStatusEnum.INSTALL_FAILED.getCode().equals(amdbProbeState);
    }

    /**
     * agent 卸载失败
     *
     * @param amdbProbeState amdb 状态
     * @return 是否是
     */
    public static boolean isAmdbUninstallFailed(Integer amdbProbeState) {
        return AmdbProbeStatusEnum.UNINSTALL_FAILED.getCode().equals(amdbProbeState);
    }

    /**
     * agent 卸载中
     *
     * @param amdbProbeState amdb 状态
     * @return 是否是
     */
    public static boolean isAmdbUninstalling(Integer amdbProbeState) {
        return AmdbProbeStatusEnum.UNINSTALLING.getCode().equals(amdbProbeState);
    }

    /**
     * agent 未安装
     *
     * @param amdbProbeState amdb 状态
     * @return 是否是
     */
    public static boolean isAmdbNotInstalled(Integer amdbProbeState) {
        return AmdbProbeStatusEnum.NOT_INSTALLED.getCode().equals(amdbProbeState);
    }

    /**
     * agent 已安装
     *
     * @param amdbProbeState amdb 状态
     * @return 是否是
     */
    public static boolean isAmdbInstalled(Integer amdbProbeState) {
        return AmdbProbeStatusEnum.INSTALLED.getCode().equals(amdbProbeState);
    }

    /**
     * agent 安装中
     *
     * @param amdbProbeState amdb 状态
     * @return 是否是
     */
    public static boolean isAmdbInstalling(Integer amdbProbeState) {
        return AmdbProbeStatusEnum.INSTALLING.getCode().equals(amdbProbeState);
    }

    /**
     * agent 所有的失败状态
     * 安装, 卸载 失败
     *
     * @param amdbProbeState amdb 状态
     * @return 是否是
     */
    public static boolean isAmdbFailed(Integer amdbProbeState) {
        return isAmdbInstallFailed(amdbProbeState) || isAmdbUninstallFailed(amdbProbeState);
    }

}
