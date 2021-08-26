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

import io.shulie.takin.web.amdb.bean.result.application.ApplicationRemoteCallDTO;
import io.shulie.takin.web.biz.service.linkManage.AppRemoteCallService;
import io.shulie.takin.web.common.enums.application.AppRemoteCallConfigEnum;
import io.shulie.takin.web.common.enums.blacklist.BlacklistEnableEnum;
import io.shulie.takin.web.common.util.application.RemoteCallUtils;
import io.shulie.takin.web.data.dao.application.AppRemoteCallDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.application.WhiteListDAO;
import io.shulie.takin.web.data.param.application.AppRemoteCallCreateParam;
import io.shulie.takin.web.data.param.application.AppRemoteCallQueryParam;
import io.shulie.takin.web.data.param.whitelist.WhitelistSearchParam;
import io.shulie.takin.web.data.result.application.AppRemoteCallResult;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.whitelist.WhitelistResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @description:白名单数据迁移
 * @date 2021/6/9 9:18 下午
 */
@Component
@Slf4j
public class RemoteCallFixer {

    @Autowired
    private WhiteListDAO whiteListDAO;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private AppRemoteCallDAO appRemoteCallDAO;

    @Autowired
    private AppRemoteCallService appRemoteCallService;

    public void fix() {
        log.info("开始迁移白名单数据至远程调用数据");
        // 是否迁移过 是否有迁移印记
        AppRemoteCallQueryParam param = new AppRemoteCallQueryParam();
        param.setIsSynchronize(true);
        List<AppRemoteCallResult> results = appRemoteCallDAO.getList(param);
        if (CollectionUtils.isNotEmpty(results)) {
            log.info("已有迁移印记，不需要再迁移");
            return;
        }
        // 查询所有的白名单
        WhitelistSearchParam searchParam = new WhitelistSearchParam();
        List<WhitelistResult> whitelistResults = whiteListDAO.getList(searchParam);
        // 查询所有白名单的应用
        List<Long> appIds = whitelistResults.stream().map(WhitelistResult::getApplicationId).distinct().collect(Collectors.toList());
        List<ApplicationDetailResult> appDetailResults = applicationDAO.getApplicationByIds(appIds);
        Map<Long, List<ApplicationDetailResult>> appMap = appDetailResults.stream().collect(
                Collectors.groupingBy(ApplicationDetailResult::getApplicationId));
        // 查询所有应用的服务端应用
        Map<String, List<ApplicationRemoteCallDTO>> serverAppNamesMap = appRemoteCallService.getServerAppListMap(
                appDetailResults.stream().map(ApplicationDetailResult::getApplicationName).collect(Collectors.joining(",")));

        // 去除重复的
        AppRemoteCallQueryParam queryParam = new AppRemoteCallQueryParam();
        List<AppRemoteCallResult> callResults = appRemoteCallDAO.getList(queryParam);
        List<String> buildRemoteCallIds = callResults.stream().map(t ->
                RemoteCallUtils.buildRemoteCallName(t.getAppName(), t.getInterfaceName(), t.getInterfaceType())).collect(Collectors.toList());

        List<AppRemoteCallCreateParam> params = whitelistResults.stream().map(result -> {
            AppRemoteCallCreateParam createParam = new AppRemoteCallCreateParam();
            createParam.setInterfaceName(result.getInterfaceName());
            createParam.setInterfaceType(Integer.valueOf(result.getType()));
            createParam.setApplicationId(result.getApplicationId());
            if (result.getUseYn().equals(BlacklistEnableEnum.ENABLE.getStatus())) {
                createParam.setType(AppRemoteCallConfigEnum.OPEN_WHITELIST.getType());
            } else {
                createParam.setType(AppRemoteCallConfigEnum.CLOSE_CONFIGURATION.getType());
            }
            createParam.setIsSynchronize(true);
            List<ApplicationDetailResult> detailResults = appMap.get(result.getApplicationId());
            if (CollectionUtils.isNotEmpty(detailResults)) {
                // todo 存在多个
                ApplicationDetailResult detailResult = detailResults.get(0);
                createParam.setCustomerId(detailResult.getCustomerId());
                createParam.setAppName(detailResult.getApplicationName());
                createParam.setUserId(detailResult.getUserId());
                // 补充服务应用
                String appNameRemoteCallId = RemoteCallUtils.buildRemoteCallName(createParam.getAppName(), createParam.getInterfaceName(),
                        createParam.getInterfaceType());
                List<ApplicationRemoteCallDTO> callDtoList = serverAppNamesMap.get(appNameRemoteCallId);
                if (CollectionUtils.isNotEmpty(callDtoList)) {
                    createParam.setServerAppName(callDtoList.stream().map(ApplicationRemoteCallDTO::getAppName).collect(Collectors.joining(",")));
                }
            }
            return createParam;
        }).filter(t -> {
            String appNameRemoteCallId = RemoteCallUtils.buildRemoteCallName(t.getAppName(), t.getInterfaceName(), t.getInterfaceType());
            return StringUtils.isNotBlank(t.getAppName()) && !buildRemoteCallIds.contains(appNameRemoteCallId);
        }).collect(Collectors.toList());
        appRemoteCallDAO.batchInsert(params);
    }
}
