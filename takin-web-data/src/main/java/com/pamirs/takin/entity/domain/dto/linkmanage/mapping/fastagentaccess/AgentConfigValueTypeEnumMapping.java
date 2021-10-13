package com.pamirs.takin.entity.domain.dto.linkmanage.mapping.fastagentaccess;

import java.util.ArrayList;
import java.util.List;

import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.EnumResult;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentConfigValueTypeEnum;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/8/19 11:28 上午
 */
public class AgentConfigValueTypeEnumMapping {

    public static EnumResult parse(AgentConfigValueTypeEnum valueTypeEnum) {
        EnumResult result = new EnumResult();
        if (valueTypeEnum != null) {
            switch (valueTypeEnum) {
                case TEXT:
                    result.label(valueTypeEnum.getDesc()).value(valueTypeEnum.getVal().toString()).num(0);
                    break;
                case RADIO:
                    result.label(valueTypeEnum.getDesc()).value(valueTypeEnum.getVal().toString()).num(1);
                    break;
            }
        }
        return result;
    }

    public static List<EnumResult> neededEnumResults() {
        List<EnumResult> enumResults = new ArrayList<>();
        for (AgentConfigValueTypeEnum valueTypeEnum : AgentConfigValueTypeEnum.values()) {
            enumResults.add(parse(valueTypeEnum));
        }
        return enumResults;
    }
}
