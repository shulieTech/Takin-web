package com.pamirs.takin.entity.domain.dto.linkmanage.mapping.fastagentaccess;

import java.util.ArrayList;
import java.util.List;

import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.EnumResult;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentStatusEnum;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/8/19 11:32 上午
 */
public class AgentStatusEnumMapping {

    public static EnumResult parse(AgentStatusEnum agentStatusEnum) {
        EnumResult result = new EnumResult();
        if (agentStatusEnum != null) {
            switch (agentStatusEnum) {
                case INSTALLED:
                    result.label(agentStatusEnum.getDesc()).value(agentStatusEnum.getVal().toString()).num(0);
                    break;
                case INSTALL_FAILED:
                    result.label(agentStatusEnum.getDesc()).value(agentStatusEnum.getVal().toString()).num(1);
                    break;
            }
        }
        return result;
    }

    public static List<EnumResult> neededEnumResults() {
        List<EnumResult> enumResults = new ArrayList<>();
        for (AgentStatusEnum agentStatusEnum : AgentStatusEnum.values()) {
            enumResults.add(parse(agentStatusEnum));
        }
        return enumResults;
    }
}
