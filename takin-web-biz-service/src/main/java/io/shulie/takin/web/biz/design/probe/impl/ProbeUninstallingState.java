package io.shulie.takin.web.biz.design.probe.impl;

import io.shulie.takin.web.biz.design.probe.AbstractApplicationNodeProbeState;
import io.shulie.takin.web.biz.utils.business.probe.ApplicationNodeProbeUtil;
import io.shulie.takin.web.common.enums.probe.ApplicationNodeProbeStatusEnum;
import io.shulie.takin.web.common.pojo.dto.probe.MatchApplicationNodeProbeStateDTO;
import org.springframework.stereotype.Service;

/**
 * 卸载中状态
 *
 * @author liuchuan
 * @date 2021/6/8 3:39 下午
 */
@Service
public class ProbeUninstallingState extends AbstractApplicationNodeProbeState {

    @Override
    public boolean match(MatchApplicationNodeProbeStateDTO dto) {
        // amdb 失败
        if (ApplicationNodeProbeUtil.isAmdbFailed(dto.getAmdbProbeState())) {
            return false;
        }

        // 本地 卸载中状态
        if (ApplicationNodeProbeUtil.uninstalling(dto)) {
            return true;
        }

        // amdb 卸载中状态, 除了 安装中, 升级中, 则都是卸载中
        return ApplicationNodeProbeUtil.isAmdbUninstalling(dto.getAmdbProbeState())
            && !ApplicationNodeProbeUtil.installing(dto)
            && !ApplicationNodeProbeUtil.upgrading(dto);
    }

    @Override
    public Integer probeState() {
        return ApplicationNodeProbeStatusEnum.UNINSTALLING.getCode();
    }

    @Override
    public String probeStateDesc() {
        return ApplicationNodeProbeStatusEnum.UNINSTALLING.getDesc();
    }

}
