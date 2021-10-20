package com.pamirs.takin.entity.domain.dto.linkmanage.mapping.fastagentaccess;

import java.util.ArrayList;
import java.util.List;

import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.EnumResult;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentConfigEffectTypeEnum;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/8/19 11:23 上午
 */
public class AgentConfigEffectTypeEnumMapping {

    public static EnumResult parse(AgentConfigEffectTypeEnum effectTypeEnum) {
        EnumResult result = new EnumResult();
        if (effectTypeEnum != null) {
            switch (effectTypeEnum) {
                case PROBE:
                    result.label(effectTypeEnum.getDesc()).value(effectTypeEnum.getVal().toString()).num(0);
                    break;
                case AGENT:
                    result.label(effectTypeEnum.getDesc()).value(effectTypeEnum.getVal().toString()).num(1);
                    break;
            }
        }
        return result;
    }

    public static List<EnumResult> neededEnumResults() {
        List<EnumResult> enumResults = new ArrayList<>();
        for (AgentConfigEffectTypeEnum effectTypeEnum : AgentConfigEffectTypeEnum.values()) {
            enumResults.add(parse(effectTypeEnum));
        }
        return enumResults;
    }
}
