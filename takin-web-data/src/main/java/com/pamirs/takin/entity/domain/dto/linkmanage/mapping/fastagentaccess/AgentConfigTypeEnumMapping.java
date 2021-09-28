package com.pamirs.takin.entity.domain.dto.linkmanage.mapping.fastagentaccess;

import java.util.ArrayList;
import java.util.List;

import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.EnumResult;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentConfigTypeEnum;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/8/19 11:27 上午
 */
public class AgentConfigTypeEnumMapping {

    public static EnumResult parse(AgentConfigTypeEnum configTypeEnum) {
        EnumResult result = new EnumResult();
        if (configTypeEnum != null) {
            switch (configTypeEnum) {
                case GLOBAL:
                    result.label(configTypeEnum.getDesc()).value(configTypeEnum.getVal().toString()).num(0);
                    break;
                case PROJECT:
                    result.label(configTypeEnum.getDesc()).value(configTypeEnum.getVal().toString()).num(1);
                    break;
            }
        }
        return result;
    }

    public static List<EnumResult> neededEnumResults() {
        List<EnumResult> enumResults = new ArrayList<>();
        for (AgentConfigTypeEnum configTypeEnum : AgentConfigTypeEnum.values()) {
            enumResults.add(parse(configTypeEnum));
        }
        return enumResults;
    }
}
