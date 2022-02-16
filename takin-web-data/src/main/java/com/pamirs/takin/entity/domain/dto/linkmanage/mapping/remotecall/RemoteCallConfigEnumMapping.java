package com.pamirs.takin.entity.domain.dto.linkmanage.mapping.remotecall;

import java.util.ArrayList;
import java.util.List;

import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.EnumResult;
import io.shulie.takin.web.common.enums.application.AppRemoteCallConfigEnum;

/**
 * @author 无涯
 * @date 2021/6/1 9:53 上午
 */
public class RemoteCallConfigEnumMapping {
    public static AppRemoteCallConfigEnum getByCode(Integer code) {
        for (AppRemoteCallConfigEnum callConfigEnum : AppRemoteCallConfigEnum.values()) {
            if (callConfigEnum.getType().equals(code)) {
                return callConfigEnum;
            }
        }
        return AppRemoteCallConfigEnum.CLOSE_CONFIGURATION;
    }

    public static EnumResult parse(AppRemoteCallConfigEnum callConfigEnum) {
        EnumResult result = new EnumResult();
        if (callConfigEnum != null) {
            switch (callConfigEnum) {
                case CLOSE_CONFIGURATION:
                    result.label(callConfigEnum.getConfigName()).value(String.valueOf(callConfigEnum.getType())).num(0);
                    break;
                case OPEN_WHITELIST:
                    result.label(callConfigEnum.getConfigName()).value(String.valueOf(callConfigEnum.getType())).num(1);
                    break;
                case RETURN_MOCK:
                    result.label(callConfigEnum.getConfigName()).value(String.valueOf(callConfigEnum.getType())).num(2);
                    break;
                case FORWARD_MOCK:
                    result.label(callConfigEnum.getConfigName()).value(String.valueOf(callConfigEnum.getType())).num(3);
                    break;
                case  FIX_FORWARD_MOCK:
                    result.label(callConfigEnum.getConfigName()).value(String.valueOf(callConfigEnum.getType())).num(4);
                default:
                    break;
            }
        } else {
            result.label(callConfigEnum.getConfigName()).value(String.valueOf(callConfigEnum.getType())).num(0);
        }
        return result;
    }

    public static List<EnumResult> neededEnumResults() {
        List<EnumResult> enumResults = new ArrayList<>();
        for (AppRemoteCallConfigEnum callConfigEnum : AppRemoteCallConfigEnum.values()) {
            enumResults.add(parse(callConfigEnum));
        }
        return enumResults;
    }
}
