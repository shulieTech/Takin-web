package io.shulie.takin.web.biz.design.probe.impl;

import io.shulie.takin.web.biz.design.probe.AbstractApplicationNodeProbeState;
import io.shulie.takin.web.biz.utils.business.probe.ApplicationNodeProbeUtil;
import io.shulie.takin.web.common.enums.probe.ApplicationNodeProbeStatusEnum;
import io.shulie.takin.web.common.pojo.dto.probe.MatchApplicationNodeProbeStateDTO;
import org.springframework.stereotype.Service;

/**
 * 安装中状态
 *
 * @author liuchuan
 * @date 2021/6/8 3:39 下午
 */
@Service
public class ProbeInstallingState extends AbstractApplicationNodeProbeState {

    @Override
    public boolean match(MatchApplicationNodeProbeStateDTO dto) {
        // 本地安装中
        if (ApplicationNodeProbeUtil.installing(dto)) {
            return true;
        }

        // amdb 安装中状态, 除了 升级中, 卸载中
        return ApplicationNodeProbeUtil.isAmdbInstalling(dto.getAmdbProbeState())
            && !ApplicationNodeProbeUtil.uninstalling(dto)
            && !ApplicationNodeProbeUtil.upgrading(dto);
    }

    @Override
    public Integer probeState() {
        return ApplicationNodeProbeStatusEnum.INSTALLING.getCode();
    }

    @Override
    public String probeStateDesc() {
        return ApplicationNodeProbeStatusEnum.INSTALLING.getDesc();
    }

}
