package com.pamirs.takin.entity.domain.dto.linkmanage.mapping.fastagentaccess;

import java.util.ArrayList;
import java.util.List;

import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.EnumResult;
import io.shulie.takin.web.common.enums.fastagentaccess.PluginStatusEnum;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/8/19 11:35 上午
 */
public class PluginStatusEnumMapping {

    public static EnumResult parse(PluginStatusEnum pluginStatusEnum) {
        EnumResult result = new EnumResult();
        if (pluginStatusEnum != null) {
            switch (pluginStatusEnum) {
                case LOAD_SUCCESS:
                    result.label(pluginStatusEnum.getDesc()).value(pluginStatusEnum.getVal()).num(0);
                    break;
                case LOAD_FAILED:
                    result.label(pluginStatusEnum.getDesc()).value(pluginStatusEnum.getVal()).num(1);
                    break;
                case LOAD_DISABLE:
                    result.label(pluginStatusEnum.getDesc()).value(pluginStatusEnum.getVal()).num(2);
                    break;
                case UNLOAD:
                    result.label(pluginStatusEnum.getDesc()).value(pluginStatusEnum.getVal()).num(3);
                    break;
            }
        }
        return result;
    }

    public static List<EnumResult> neededEnumResults() {
        List<EnumResult> enumResults = new ArrayList<>();
        for (PluginStatusEnum pluginStatusEnum : PluginStatusEnum.values()) {
            enumResults.add(parse(pluginStatusEnum));
        }
        return enumResults;
    }
}
