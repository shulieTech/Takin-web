package io.shulie.takin.web.biz.design.probe.impl;

import io.shulie.takin.web.biz.design.probe.AbstractApplicationNodeProbeState;
import io.shulie.takin.web.biz.utils.business.probe.ApplicationNodeProbeUtil;
import io.shulie.takin.web.common.constant.ProbeConstants;
import io.shulie.takin.web.common.enums.probe.ApplicationNodeProbeStatusEnum;
import io.shulie.takin.web.common.pojo.dto.probe.ApplicationNodeProbeOperateDTO;
import io.shulie.takin.web.common.pojo.dto.probe.MatchApplicationNodeProbeStateDTO;
import org.springframework.stereotype.Service;

/**
 * 未安装状态
 * \_ 安装
 *
 * @author liuchuan
 * @date 2021/6/8 3:39 下午
 */
@Service(ProbeConstants.ENGLISH_DESC_NOT_INSTALLED)
public class ProbeNotInstalledState extends AbstractApplicationNodeProbeState {

    @Override
    public boolean match(MatchApplicationNodeProbeStateDTO dto) {
        // amdb 未安装状态, 除了安装中, 升级中, 卸载中
        return ApplicationNodeProbeUtil.isAmdbNotInstalled(dto.getAmdbProbeState())
            && !ApplicationNodeProbeUtil.installing(dto)
            && !ApplicationNodeProbeUtil.uninstalling(dto)
            && !ApplicationNodeProbeUtil.upgrading(dto);
    }

    @Override
    public Integer probeState() {
        return ApplicationNodeProbeStatusEnum.NOT_INSTALLED.getCode();
    }

    @Override
    public String probeStateDesc() {
        return ApplicationNodeProbeStatusEnum.NOT_INSTALLED.getDesc();
    }

    @Override
    public void install(ApplicationNodeProbeOperateDTO applicationNodeProbeOperateDTO) {
        this.operate(applicationNodeProbeOperateDTO);
    }

    @Override
    public String probeVersion(String probeVersion) {
        return ApplicationNodeProbeStatusEnum.NOT_INSTALLED.getDesc();
    }

    @Override
    public String operateStateDesc(MatchApplicationNodeProbeStateDTO dto) {
        // 安装操作, 失败结果
        if (ApplicationNodeProbeUtil.installFailed(dto)) {
            return PROBE_OPERATE_RESULT_DESC_FAILED_INSTALL;
        }

        // 升级操作, 失败结果
        if (ApplicationNodeProbeUtil.upgradeFailed(dto)) {
            return PROBE_OPERATE_RESULT_DESC_FAILED_UPGRADE;
        }

        return "";
    }

}
