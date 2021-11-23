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

import java.util.List;
import java.util.Map;

import com.pamirs.takin.entity.domain.vo.application.NodeNumParam;
import io.shulie.takin.web.data.model.mysql.ApplicationAttentionListEntity;
import io.shulie.takin.web.data.model.mysql.ApplicationMntEntity;
import io.shulie.takin.web.data.param.application.ApplicationAttentionParam;
import io.shulie.takin.web.data.param.application.ApplicationCreateParam;
import io.shulie.takin.web.data.param.application.ApplicationQueryParam;
import io.shulie.takin.web.data.param.application.ApplicationUpdateParam;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationResult;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;

/**
 * application_mnt dao 层
 *
 * @author shiyajian
 * create: 2020-09-20
 */
public interface ApplicationDAO {

    List<ApplicationDetailResult> getApplications(List<String> appNames);

    /**
     * 根据应用查
     * @param appNames
     * @return
     */
    List<ApplicationResult> getApplicationByName(List<String> appNames);

    /**
     * 根据租户查询
     * @param appNames
     * @param userAppKey
     * @param envCode
     * @return
     */
    List<ApplicationResult> getApplicationByName(List<String> appNames,String userAppKey,String envCode);

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
     * 不用租户拦截查询 导出接口用
     * @param appId
     * @return
     */
    ApplicationDetailResult getApplicationByIdWithInterceptorIgnore(Long appId);


    /**
     * 根据租户查询
     *
     * @return
     */
    ApplicationDetailResult getApplicationByTenantIdAndName(String appName);

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
    List<ApplicationMntEntity> listByApplicationNamesAndTenantId(List<String> applicationNames);

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
     * @param tenantId 租户id
     */
    void batchUpdateAppNodeNum(List<NodeNumParam> paramList, Long tenantId);

    List<ApplicationAttentionListEntity> getAttentionList(ApplicationAttentionParam param);

    void attendApplicationService(Map<String, String> param);

    /**
     * 根据租户获取相关应用
     * @param commonExts
     * @return
     */
    List<ApplicationDetailResult> getAllTenantApp(List<TenantCommonExt> commonExts);

    /**
     * 根据应用名称， 获得该租户下的应用ids
     *
     * @param applicationNameList 应用名称列表
     * @return 应用ids
     */
    List<Long> listIdsByNameListAndCustomerId(List<String> applicationNameList);

}
