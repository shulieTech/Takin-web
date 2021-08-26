package io.shulie.takin.web.biz.design.probe.impl;

import io.shulie.takin.web.biz.design.probe.AbstractApplicationNodeProbeState;
import io.shulie.takin.web.biz.utils.business.probe.ApplicationNodeProbeUtil;
import io.shulie.takin.web.common.enums.probe.ApplicationNodeProbeStatusEnum;
import io.shulie.takin.web.common.pojo.dto.probe.ApplicationNodeProbeOperateDTO;
import io.shulie.takin.web.common.pojo.dto.probe.MatchApplicationNodeProbeStateDTO;
import org.springframework.stereotype.Service;

/**
 * 已安装状态
 * \_ 卸载
 * \_ 升级
 *
 * @author liuchuan
 * @date 2021/6/8 3:39 下午
 */
@Service
public class ProbeInstalledState extends AbstractApplicationNodeProbeState {

    @Override
    public boolean match(MatchApplicationNodeProbeStateDTO dto) {
        // amdb 已安装状态, 除了安装中, 升级中, 卸载中
        return ApplicationNodeProbeUtil.isAmdbInstalled(dto.getAmdbProbeState())
            && !ApplicationNodeProbeUtil.installing(dto)
            && !ApplicationNodeProbeUtil.uninstalling(dto)
            && !ApplicationNodeProbeUtil.upgrading(dto);
    }

    @Override
    public Integer probeState() {
        return ApplicationNodeProbeStatusEnum.INSTALLED.getCode();
    }

    @Override
    public String probeStateDesc() {
        return ApplicationNodeProbeStatusEnum.INSTALLED.getDesc();
    }

    @Override
    public void uninstall(ApplicationNodeProbeOperateDTO applicationNodeProbeOperateDTO) {
        this.operate(applicationNodeProbeOperateDTO);
    }

    @Override
    public void upgrade(ApplicationNodeProbeOperateDTO applicationNodeProbeOperateDTO) {
        this.operate(applicationNodeProbeOperateDTO);
    }

    @Override
    public String operateStateDesc(MatchApplicationNodeProbeStateDTO dto) {
        if (ApplicationNodeProbeUtil.upgradeFailed(dto)) {
            return PROBE_OPERATE_RESULT_DESC_FAILED_UPGRADE;
        }

        if (ApplicationNodeProbeUtil.uninstallFailed(dto)) {
            return PROBE_OPERATE_RESULT_DESC_FAILED_UNINSTALL;
        }

        return "";
    }

}
