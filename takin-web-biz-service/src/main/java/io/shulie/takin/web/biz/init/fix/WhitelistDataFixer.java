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

import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.application.WhiteListDAO;
import io.shulie.takin.web.data.param.whitelist.WhitelistSaveOrUpdateParam;
import io.shulie.takin.web.data.param.whitelist.WhitelistSearchParam;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.whitelist.WhitelistResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author 无涯
 * @description:白名单数据补全操作
 * @date 2021/5/14 3:01 下午
 */
@Component
@Slf4j
public class WhitelistDataFixer {

    @Autowired
    private WhiteListDAO whiteListDAO;

    @Autowired
    private ApplicationDAO applicationDAO;

    public void fix() {
        log.info("开始订正白名单租户字段和用户字段");
        WhitelistSearchParam param = new WhitelistSearchParam();
        List<WhitelistResult> whitelistResults = whiteListDAO.getList(param);

        if (whitelistResults.stream().noneMatch(t -> t.getCustomerId() == null)) {
            log.info("没有可订正白名单租户的数据");
            return;
        }
        // 找到租户字段为空的数据
        List<WhitelistResult> fixWhitelistResults = whitelistResults.stream().filter(t -> t.getCustomerId() == null).collect(Collectors.toList());
        List<Long> ids = fixWhitelistResults.stream().map(WhitelistResult::getApplicationId).distinct().collect(Collectors.toList());

        List<ApplicationDetailResult> applications = applicationDAO.getApplicationByIds(ids);
        Map<Long, List<ApplicationDetailResult>> whitelistResultsMap = applications.stream()
            .collect(Collectors.groupingBy(ApplicationDetailResult::getApplicationId));

        List<WhitelistSaveOrUpdateParam> params = fixWhitelistResults.stream().map(fix -> {
            WhitelistSaveOrUpdateParam updateParam = new WhitelistSaveOrUpdateParam();
            BeanUtils.copyProperties(fix, updateParam);
            List<ApplicationDetailResult> results = whitelistResultsMap.get(fix.getApplicationId());
            if (!CollectionUtils.isEmpty(results)) {
                updateParam.setCustomerId(results.get(0).getCustomerId());
                updateParam.setUserId(results.get(0).getUserId());
            }
            return updateParam;
        }).collect(Collectors.toList());
        whiteListDAO.batchSaveOrUpdate(params);
    }
}
