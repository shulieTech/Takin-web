package io.shulie.takin.web.biz.design.probe.impl;

import io.shulie.takin.web.biz.design.probe.AbstractApplicationNodeProbeState;
import io.shulie.takin.web.biz.utils.business.probe.ApplicationNodeProbeUtil;
import io.shulie.takin.web.common.enums.probe.ApplicationNodeProbeStatusEnum;
import io.shulie.takin.web.common.pojo.dto.probe.ApplicationNodeProbeOperateDTO;
import io.shulie.takin.web.common.pojo.dto.probe.MatchApplicationNodeProbeStateDTO;
import org.springframework.stereotype.Service;

/**
 * 卸载失败状态
 * \_ 卸载
 * \_ 安装
 *
 * @author liuchuan
 * @date 2021/6/8 3:39 下午
 */
@Service
public class ProbeUninstallFailedState extends AbstractApplicationNodeProbeState {

    @Override
    public boolean match(MatchApplicationNodeProbeStateDTO dto) {
        // 以 agent 卸载失败为准
        return ApplicationNodeProbeUtil.isAmdbUninstallFailed(dto.getAmdbProbeState());
    }

    @Override
    public Integer probeState() {
        return ApplicationNodeProbeStatusEnum.UNINSTALL_FAILED.getCode();
    }

    @Override
    public String probeStateDesc() {
        return ApplicationNodeProbeStatusEnum.UNINSTALL_FAILED.getDesc();
    }

    @Override
    public void uninstall(ApplicationNodeProbeOperateDTO applicationNodeProbeOperateDTO) {
        this.operate(applicationNodeProbeOperateDTO);
    }

    @Override
    public void install(ApplicationNodeProbeOperateDTO applicationNodeProbeOperateDTO) {
        this.operate(applicationNodeProbeOperateDTO);
    }

    @Override
    public String operateStateDesc(MatchApplicationNodeProbeStateDTO dto) {
        // 卸载操作失败
        if (ApplicationNodeProbeUtil.uninstallFailed(dto)) {
            return PROBE_OPERATE_RESULT_DESC_FAILED_UNINSTALL;
        }

        // 升级操作失败
        if (ApplicationNodeProbeUtil.upgradeFailed(dto)) {
            return PROBE_OPERATE_RESULT_DESC_FAILED_UPGRADE;
        }

        return "";
    }

}
