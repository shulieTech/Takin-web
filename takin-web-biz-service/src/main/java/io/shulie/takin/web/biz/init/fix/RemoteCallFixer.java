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
import java.util.Map;
import java.util.stream.Collectors;

import com.pamirs.takin.common.util.MD5Util;
import io.shulie.takin.web.data.dao.application.AppRemoteCallDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.model.mysql.AppRemoteCallEntity;
import io.shulie.takin.web.data.model.mysql.ApplicationMntEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    private ApplicationDAO applicationDAO;

    @Value("${fix.remote.call.data:false}")
    private Boolean fixData;
    public void fix() {
        if(!fixData) {
            log.info("无需订正远程调用数据");
            return;
        }
        List<AppRemoteCallEntity> list = appRemoteCallDAO.getListWithOutTenant();
        if(CollectionUtils.isEmpty(list)) {
            log.info("无需订正远程调用数据");
            return;
        }
        // 应用名补充
        List<ApplicationMntEntity> allApps = applicationDAO.getAllApplicationsWithoutTenant();
        if(CollectionUtils.isEmpty(allApps)) {
            return;
        }
        Map<Long,List<ApplicationMntEntity>> allAppMap = allApps.stream().collect(Collectors.groupingBy(ApplicationMntEntity::getApplicationId));

        // 大规模数据修复
        List<AppRemoteCallEntity> entities = list.stream()
            .map(e -> {
                AppRemoteCallEntity updateEntity = new AppRemoteCallEntity();
                updateEntity.setId(e.getId());
                updateEntity.setAppName(e.getAppName());
                List<ApplicationMntEntity> entityList = allAppMap.get(e.getApplicationId());
                if(CollectionUtils.isNotEmpty(entityList)) {
                    ApplicationMntEntity mntEntity = entityList.get(0);
                    if(StringUtils.isEmpty(updateEntity.getAppName()) || !mntEntity.getApplicationName().equals(updateEntity.getAppName())) {
                        updateEntity.setAppName(mntEntity.getApplicationName());
                    }
                }
                String data = updateEntity.getAppName() + "@@"+  e.getInterfaceName() + "@@" + e.getInterfaceType() + "@@" +
                    e.getTenantId() + "@@" + e.getEnvCode();
                updateEntity.setMd5(MD5Util.getMD5(data));
                return updateEntity;
            }).collect(Collectors.toList());
        appRemoteCallDAO.updateWithOutTenant(entities);
    }
}
