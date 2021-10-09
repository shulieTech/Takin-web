package io.shulie.takin.web.biz.design.probe.impl;

import io.shulie.takin.web.biz.design.probe.AbstractApplicationNodeProbeState;
import io.shulie.takin.web.biz.utils.business.probe.ApplicationNodeProbeUtil;
import io.shulie.takin.web.common.enums.probe.ApplicationNodeProbeStatusEnum;
import io.shulie.takin.web.common.pojo.dto.probe.ApplicationNodeProbeOperateDTO;
import io.shulie.takin.web.common.pojo.dto.probe.MatchApplicationNodeProbeStateDTO;
import org.springframework.stereotype.Service;

/**
 * 安装失败状态
 * \_ 安装
 *
 * @author liuchuan
 * @date 2021/6/8 3:39 下午
 */
@Service
public class ProbeInstallFailedState extends AbstractApplicationNodeProbeState {

    @Override
    public boolean match(MatchApplicationNodeProbeStateDTO dto) {
        // 以 agent 安装失败为准
        return ApplicationNodeProbeUtil.isAmdbInstallFailed(dto.getAmdbProbeState());
    }

    @Override
    public Integer probeState() {
        return ApplicationNodeProbeStatusEnum.INSTALL_FAILED.getCode();
    }

    @Override
    public String probeStateDesc() {
        return ApplicationNodeProbeStatusEnum.INSTALL_FAILED.getDesc();
    }

    @Override
    public void install(ApplicationNodeProbeOperateDTO applicationNodeProbeOperateDTO) {
        this.operate(applicationNodeProbeOperateDTO);
    }

    @Override
    public String operateStateDesc(MatchApplicationNodeProbeStateDTO dto) {
        // 安装操作失败
        if (ApplicationNodeProbeUtil.installFailed(dto)) {
            return PROBE_OPERATE_RESULT_DESC_FAILED_INSTALL;
        }

        // 升级操作失败
        if (ApplicationNodeProbeUtil.upgradeFailed(dto)) {
            return PROBE_OPERATE_RESULT_DESC_FAILED_UPGRADE;
        }

        return "";
    }

}
