package com.pamirs.takin.entity.domain.dto.linkmanage.mapping.fastagentaccess;

import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.EnumResult;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentShowStatusEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 南风
 * @Date: 2021/11/17 2:52 下午
 */
public class AgentShowStatusEnumMapping {

    public static EnumResult parse(AgentShowStatusEnum agentShowStatusEnum) {
        EnumResult result = new EnumResult();
        if (agentShowStatusEnum != null) {
            switch (agentShowStatusEnum) {
                case UPGRADE:
                    result.label(agentShowStatusEnum.getDesc()).value(agentShowStatusEnum.getVal().toString()).num(0);
                    break;
                case NORMAL:
                    result.label(agentShowStatusEnum.getDesc()).value(agentShowStatusEnum.getVal().toString()).num(1);
                    break;
                case ERROR:
                    result.label(agentShowStatusEnum.getDesc()).value(agentShowStatusEnum.getVal().toString()).num(2);
                    break;
            }
        }
        return result;
    }

    public static List<EnumResult> neededEnumResults() {
        List<EnumResult> enumResults = new ArrayList<>();
        for (AgentShowStatusEnum agentShowStatusEnum : AgentShowStatusEnum.values()) {
            enumResults.add(parse(agentShowStatusEnum));
        }
        return enumResults;
    }
}
