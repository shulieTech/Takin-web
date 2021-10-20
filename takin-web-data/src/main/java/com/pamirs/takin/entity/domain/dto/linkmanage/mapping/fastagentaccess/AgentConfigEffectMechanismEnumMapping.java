package com.pamirs.takin.entity.domain.dto.linkmanage.mapping.fastagentaccess;

import java.util.ArrayList;
import java.util.List;

import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.EnumResult;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentConfigEffectMechanismEnum;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/8/19 11:21 上午
 */
public class AgentConfigEffectMechanismEnumMapping {

    public static EnumResult parse(AgentConfigEffectMechanismEnum effectMechanismEnum) {
        EnumResult result = new EnumResult();
        if (effectMechanismEnum != null) {
            switch (effectMechanismEnum) {
                case REBOOT:
                    result.label(effectMechanismEnum.getDesc()).value(effectMechanismEnum.getVal().toString())
                        .num(0);
                    break;
                case IMMEDIATELY:
                    result.label(effectMechanismEnum.getDesc()).value(effectMechanismEnum.getVal().toString())
                        .num(1);
                    break;
            }
        }
        return result;
    }

    public static List<EnumResult> neededEnumResults() {
        List<EnumResult> enumResults = new ArrayList<>();
        for (AgentConfigEffectMechanismEnum effectMechanismEnum : AgentConfigEffectMechanismEnum.values()) {
            enumResults.add(parse(effectMechanismEnum));
        }
        return enumResults;
    }
}
