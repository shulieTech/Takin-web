package com.pamirs.takin.entity.domain.dto.linkmanage.mapping.fastagentaccess;

import java.util.ArrayList;
import java.util.List;

import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.EnumResult;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentConfigEditableEnum;

/**
 * 配置是否可编辑枚举类
 *
 * @author ocean_wll
 * @date 2021/8/13 10:06 上午
 */
public class AgentConfigEditableEnumMapping {

    public static EnumResult parse(AgentConfigEditableEnum agentConfigEditableEnum) {
        EnumResult result = new EnumResult();
        if (agentConfigEditableEnum != null) {
            switch (agentConfigEditableEnum) {
                case CAN:
                    result.label(agentConfigEditableEnum.getDesc()).value(agentConfigEditableEnum.getVal().toString())
                        .num(0);
                    break;
                case NOT:
                    result.label(agentConfigEditableEnum.getDesc()).value(agentConfigEditableEnum.getVal().toString())
                        .num(1);
                    break;
            }
        }
        return result;
    }

    public static List<EnumResult> neededEnumResults() {
        List<EnumResult> enumResults = new ArrayList<>();
        for (AgentConfigEditableEnum editableEnum : AgentConfigEditableEnum.values()) {
            enumResults.add(parse(editableEnum));
        }
        return enumResults;
    }
}
