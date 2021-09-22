package com.pamirs.takin.entity.domain.dto.linkmanage.mapping.fastagentaccess;

import java.util.ArrayList;
import java.util.List;

import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.EnumResult;
import io.shulie.takin.web.common.enums.fastagentaccess.ProbeStatusEnum;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/8/19 11:37 上午
 */
public class ProbeStatusEnumMapping {

    public static EnumResult parse(ProbeStatusEnum probeStatusEnum) {
        EnumResult result = new EnumResult();
        if (probeStatusEnum != null) {
            switch (probeStatusEnum) {
                case INSTALLED:
                    result.label(probeStatusEnum.getDesc()).value(probeStatusEnum.getVal().toString()).num(0);
                    break;
                case UNINSTALL:
                    result.label(probeStatusEnum.getDesc()).value(probeStatusEnum.getVal().toString()).num(1);
                    break;
                case INSTALLING:
                    result.label(probeStatusEnum.getDesc()).value(probeStatusEnum.getVal().toString()).num(2);
                    break;
                case UNINSTALLING:
                    result.label(probeStatusEnum.getDesc()).value(probeStatusEnum.getVal().toString()).num(3);
                    break;
                case INSTALL_FAILED:
                    result.label(probeStatusEnum.getDesc()).value(probeStatusEnum.getVal().toString()).num(4);
                    break;
                case UNINSTALL_FAILED:
                    result.label(probeStatusEnum.getDesc()).value(probeStatusEnum.getVal().toString()).num(5);
                    break;
            }
        }
        return result;
    }

    public static List<EnumResult> neededEnumResults() {
        List<EnumResult> enumResults = new ArrayList<>();
        for (ProbeStatusEnum probeStatusEnum : ProbeStatusEnum.values()) {
            enumResults.add(parse(probeStatusEnum));
        }
        return enumResults;
    }
}
