/*
 * Copyright 2021 Shulie Technology, Co.Ltd
 * Email: shulie@shulie.io
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.shulie.takin.web.biz.init.fix;

import java.util.List;
import java.util.stream.Collectors;

import com.pamirs.takin.common.util.MD5Util;
import io.shulie.takin.web.data.dao.application.AppRemoteCallDAO;
import io.shulie.takin.web.data.model.mysql.AppRemoteCallEntity;
import io.shulie.takin.web.data.param.application.AppRemoteCallQueryParam;
import io.shulie.takin.web.data.result.application.AppRemoteCallResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @description:订正远程调用字段
 * @date 2021/6/9 9:18 下午
 */
@Component
@Slf4j
public class RemoteCallFixer {


    @Autowired
    private AppRemoteCallDAO appRemoteCallDAO;

    @Value("${fix.remote.call.data:false}")
    private Boolean fixData;
    public void fix() {
        if(!fixData) {
            log.info("无需订正远程调用数据");
        }
        AppRemoteCallQueryParam param = new AppRemoteCallQueryParam();
        List<AppRemoteCallResult> list = appRemoteCallDAO.getList(param);
        // 大规模数据修复
        List<AppRemoteCallEntity> entities = list.stream()
            .map(e -> {
                AppRemoteCallEntity entity = new AppRemoteCallEntity();
                entity.setId(e.getId());
                String data = e.getAppName() + "@@"+  e.getInterfaceName() + "@@" + e.getType() + "@@" +
                    e.getTenantId() + "@@" + e.getEnvCode();
                entity.setMd5(MD5Util.getMD5(data));
                return entity;
            }).collect(Collectors.toList());
        appRemoteCallDAO.updateBatchById(entities);
    }
}
