package com.pamirs.takin.entity.domain.dto.linkmanage.mapping.remotecall;

import java.util.ArrayList;
import java.util.List;

import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.EnumResult;
import io.shulie.takin.web.common.enums.application.AppRemoteCallConfigEnum;
import io.shulie.takin.web.data.model.mysql.RemoteCallConfigEntity;

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

    public static EnumResult parse(RemoteCallConfigEntity entity) {
        EnumResult result = new EnumResult();
        if (entity != null) {
            Integer order = entity.getValueOrder();
            result.label(entity.getName()).value(String.valueOf(order)).num(order);
        }
        return result;
    }

    public static List<EnumResult> neededEnumResults(List<RemoteCallConfigEntity> entityList) {
        List<EnumResult> enumResults = new ArrayList<>();
        entityList.forEach(entity -> enumResults.add(parse(entity)));
        return enumResults;
    }
}
