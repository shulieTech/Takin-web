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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pamirs.takin.entity.domain.vo.application.NodeNumParam;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.model.mysql.ApplicationAttentionListEntity;
import io.shulie.takin.web.data.model.mysql.ApplicationMntEntity;
import io.shulie.takin.web.data.param.application.ApplicationAttentionParam;
import io.shulie.takin.web.data.param.application.ApplicationCreateParam;
import io.shulie.takin.web.data.param.application.ApplicationQueryParam;
import io.shulie.takin.web.data.param.application.ApplicationUpdateParam;
import io.shulie.takin.web.data.param.application.QueryApplicationParam;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationListResult;
import io.shulie.takin.web.data.result.application.ApplicationResult;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import org.apache.ibatis.annotations.Param;

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
     *
     * @param appNames
     * @return
     */
    List<ApplicationResult> getApplicationByName(List<String> appNames);

    /**
     * 根据租户查询
     *
     * @param appNames
     * @param userAppKey
     * @param envCode
     * @return
     */
    List<ApplicationResult> getApplicationByName(List<String> appNames, String userAppKey, String envCode);

    /**
     * 接口只返回 应用id 应用名
     *
     * @param userIdList
     * @return
     */
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

    /**
     * 获取应用名
     *
     * @param param
     * @return
     */
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
     *
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
     * @param paramList 参数集合
     * @param envCode   环境变量
     * @param tenantId  租户id
     */
    void batchUpdateAppNodeNum(List<NodeNumParam> paramList, String envCode, Long tenantId);

    List<ApplicationAttentionListEntity> getAttentionList(ApplicationAttentionParam param);

    void attendApplicationService(Map<String, String> param);

    /**
     * 根据租户获取相关应用
     *
     * @param commonExtList
     * @return
     */
    List<ApplicationDetailResult> getAllTenantApp(List<TenantCommonExt> commonExtList);

    /**
     * 根据应用名称， 获得该租户下的应用ids
     *
     * @param applicationNameList 应用名称列表
     * @return 应用ids
     */
    List<Long> listIdsByNameListAndCustomerId(List<String> applicationNameList);

    /**
     * 说明: 根据应用id查询应用名称
     *
     * @param applicationId 应用id
     * @return 应用名称
     * @author shulie
     */
    String selectApplicationName(@Param("applicationId") String applicationId);

    /**
     * 更新 agentVersion
     *
     * @param applicationId
     * @param agentVersion
     * @param pradarVersion
     */
    void updateApplicationAgentVersion(Long applicationId, String agentVersion, String pradarVersion);

    /**
     * 根据applicationName查询 id
     *
     * @param applicationName
     * @return
     */
    Long queryIdByApplicationName(String applicationName);

    /**
     * 返回id
     *
     * @param names
     * @param tenantId
     * @param envCode
     * @return
     */
    List<String> queryIdsByNameAndTenant(List<String> names, Long tenantId, String envCode);

    /**
     * 获取应用
     *
     * @return
     */
    List<ApplicationDetailResult> getAllApplications();

    /**
     * 大盘获取应用
     *
     * @return
     */
    List<ApplicationDetailResult> getDashboardAppData();

    /**
     * 根据状态查
     *
     * @param statusList
     * @return
     */
    List<ApplicationDetailResult> getAllApplicationByStatus(List<Integer> statusList);

    /**
     * 根据关键字查询
     *
     * @param userIds
     * @param keyword
     * @return
     */
    List<ApplicationDetailResult> getApplicationMntByUserIdsAndKeyword(List<Long> userIds, String keyword);

    /**
     * 判断是否存在
     *
     * @param tenantId
     * @param envCode
     * @param applicationName
     * @return
     */
    int applicationExistByTenantIdAndAppName(Long tenantId, String envCode, String applicationName);

    /**
     * 应用列表
     *
     * @param queryParam
     * @return
     */
    PagingList<ApplicationDetailResult> queryApplicationList(ApplicationQueryParam queryParam);

    /**
     * 是否重名
     *
     * @param applicationName
     * @return
     */
    int applicationExist(String applicationName);

    /**
     * 更新
     *
     * @param tApplicationMnt
     */
    void updateApplicationInfo(ApplicationCreateParam tApplicationMnt);

    /**
     * 说明: 根据应用id查询关联的基础链路是否存在
     *
     * @param applicationId 应用id
     * @return 关联的基础链路数量和应用名称
     * @author shulie
     * @date 2018/7/10 12:43
     */
    Map<String, Object> queryApplicationRelationBasicLinkByApplicationId(String applicationId);

    /**
     * 删除应用
     *
     * @param applicationIdLists
     */
    void deleteApplicationInfoByIds(List<Long> applicationIdLists);

    /**
     * 说明: 根据id列表批量查询应用和白名单信息
     *
     * @param applicationIds 应用id集合
     * @return 应用数据
     * @author shulie
     * @date 2018/11/5 10:30
     */
    List<Map<String, Object>> queryApplicationListByIds(List<Long> applicationIds);

    /**
     * 说明: 查询应用下拉框数据接口
     *
     * @return 应用列表
     * @author shulie
     */
    List<Map<String, Object>> queryApplicationData();

    /**
     * 批量更新
     *
     * @param applicationIds
     * @param accessStatus
     */
    void batchUpdateApplicationStatus(List<Long> applicationIds, Integer accessStatus);

    /**
     * 说明: 查询缓存失效时间
     *
     * @param applicationId 应用id
     * @return 缓存失效时间
     * @author shulie
     */
    Map<String, Object> queryCacheExpTime(String applicationId);

    /**
     * 说明: 根据应用id和脚本类型查询脚本路径
     *
     * @param applicationId 应用id
     * @param scriptType    脚本类型
     * @return 脚本路径
     * @author shulie
     */
    String selectScriptPath(String applicationId, String scriptType);

    /**
     * e2e使用
     *
     * @param applicationName
     * @return
     */
    String getIdByName(String applicationName);

    /**
     * 获取应用个数
     *
     * @return
     */
    Long getApplicationCount();

    /**
     * 查询应用列表
     *
     * @param param 筛选条件
     * @return 应用列表
     */
    IPage<ApplicationListResult> listByParam(QueryApplicationParam param);

}
