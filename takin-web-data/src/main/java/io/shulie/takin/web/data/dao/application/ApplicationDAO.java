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

package io.shulie.takin.web.data.dao.application;

import java.util.Map;
import java.util.List;

import com.pamirs.takin.entity.domain.vo.application.NodeNumParam;
import io.shulie.takin.web.data.model.mysql.ApplicationMntEntity;
import io.shulie.takin.web.data.param.application.ApplicationCreateParam;
import io.shulie.takin.web.data.param.application.ApplicationQueryParam;
import io.shulie.takin.web.data.param.application.ApplicationUpdateParam;
import io.shulie.takin.web.data.model.mysql.ApplicationAttentionListEntity;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationResult;

/**
 * application_mnt dao 层
 *
 * @author shiyajian
 * create: 2020-09-20
 */
public interface ApplicationDAO {

    List<ApplicationDetailResult> getApplications(List<String> appNames);

    List<ApplicationResult> getApplicationByName(List<String> appNames);

    List<ApplicationDetailResult> getApplicationListByUserIds(List<Long> userIdList);

    /**
     * 获取应用
     *
     * @return
     */
    List<ApplicationDetailResult> getApplicationByIds(List<Long> ids);

    /**
     * 获取应用
     *
     * @return
     */
    List<ApplicationDetailResult> getApplicationList(ApplicationQueryParam param);

    List<ApplicationDetailResult> getApplicationList(List<String> appNames);

    List<String> getAllApplicationName(ApplicationQueryParam param);

    int insert(ApplicationCreateParam param);

    /**
     * 通过应用id, 获取应用详情
     *
     * @param appId 应用id
     * @return 应用详情
     */
    ApplicationDetailResult getApplicationById(Long appId);

    /**
     * 根据租户查询
     *
     * @return
     */
    ApplicationDetailResult getApplicationByCustomerIdAndName(String appName);

    /**
     * 指定责任人-应用管理
     *
     * @return
     */
    int allocationUser(ApplicationUpdateParam param);

    /**
     * 根据 应用名称列表, 租户id
     * 查找 应用列表
     *
     * @param applicationNames 应用名称列表
     * @return 应用列表
     */
    List<ApplicationMntEntity> listByApplicationNamesAndCustomerId(List<String> applicationNames);

    /**
     * 通过名称获得应用
     *
     * @param applicationName 应用
     * @return 应用
     */
    ApplicationDetailResult getByName(String applicationName);

    /**
     * 通过主键id 获得应用详情
     *
     * @param applicationPrimaryKeyId 应用主键id
     * @return 应用详情
     */
    ApplicationDetailResult getById(Long applicationPrimaryKeyId);

    /**
     * 更新应用状态
     */
    void updateApplicationStatus(Long applicationId, Integer status);

    /**
     * 批量更新应用节点数
     *
     * @param paramList  参数集合
     * @param customerId 租户id
     */
    void batchUpdateAppNodeNum(List<NodeNumParam> paramList, Long customerId);

    List<ApplicationAttentionListEntity> getAttentionList(String applicationName);

    void attendApplicationService(Map<String, String> param);

    /**
     * 根据应用名称， 获得该租户下的应用ids
     *
     * @param applicationNameList 应用名称列表
     * @return 应用ids
     */
    List<Long> listIdsByNameListAndCustomerId(List<String> applicationNameList);

}
