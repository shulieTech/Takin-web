package io.shulie.takin.web.biz.design.probe.impl;

import io.shulie.takin.web.biz.design.probe.AbstractApplicationNodeProbeState;
import io.shulie.takin.web.biz.utils.business.probe.ApplicationNodeProbeUtil;
import io.shulie.takin.web.common.enums.probe.ApplicationNodeProbeStatusEnum;
import io.shulie.takin.web.common.pojo.dto.probe.MatchApplicationNodeProbeStateDTO;
import org.springframework.stereotype.Service;

/**
 * 升级中状态
 *
 * @author liuchuan
 * @date 2021/6/8 3:39 下午
 */
@Service
public class ProbeUpgradingState extends AbstractApplicationNodeProbeState {

    @Override
    public boolean match(MatchApplicationNodeProbeStateDTO dto) {
        // 本地 升级中
        return ApplicationNodeProbeUtil.upgrading(dto);
    }

    @Override
    public Integer probeState() {
        return ApplicationNodeProbeStatusEnum.UPGRADING.getCode();
    }

    @Override
    public String probeStateDesc() {
        return ApplicationNodeProbeStatusEnum.UPGRADING.getDesc();
    }

}
