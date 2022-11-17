/*
 * Copyright 2021 Shulie Technology, Co.Ltd
 * Email: opensource@shulie.io
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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.entity.domain.vo.application.NodeNumParam;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.api.ApplicationClient;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationQueryDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationDTO;
import io.shulie.takin.web.amdb.bean.result.application.InstanceInfoDTO;
import io.shulie.takin.web.amdb.bean.result.application.LibraryDTO;
import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.common.util.DataTransformUtil;
import io.shulie.takin.web.data.mapper.mysql.ApplicationAttentionListMapper;
import io.shulie.takin.web.data.mapper.mysql.ApplicationMntMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationAttentionListEntity;
import io.shulie.takin.web.data.model.mysql.ApplicationMntEntity;
import io.shulie.takin.web.data.param.application.ApplicationAttentionParam;
import io.shulie.takin.web.data.param.application.ApplicationCreateParam;
import io.shulie.takin.web.data.param.application.ApplicationQueryParam;
import io.shulie.takin.web.data.param.application.ApplicationUpdateParam;
import io.shulie.takin.web.data.param.application.QueryApplicationByUpgradeParam;
import io.shulie.takin.web.data.param.application.QueryApplicationParam;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationListResult;
import io.shulie.takin.web.data.result.application.ApplicationListResultByUpgrade;
import io.shulie.takin.web.data.result.application.ApplicationResult;
import io.shulie.takin.web.data.result.application.InstanceInfoResult;
import io.shulie.takin.web.data.result.application.LibraryResult;
import io.shulie.takin.web.data.util.MPUtil;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author shiyajian
 * create: 2020-09-20
 */
@Service
public class ApplicationDAOImpl
    implements ApplicationDAO, MPUtil<ApplicationMntEntity> {

    @Resource
    private ApplicationClient applicationClient;

    @Resource
    private ApplicationMntMapper applicationMntMapper;

    @Resource
    private ApplicationAttentionListMapper applicationAttentionListMapper;

    @Override
    public List<ApplicationDetailResult> getApplications(List<String> appNames) {
        LambdaQueryWrapper<ApplicationMntEntity> query = new LambdaQueryWrapper<>();
        if (appNames != null && appNames.size() > 0) {
            query.in(ApplicationMntEntity::getApplicationName, appNames);
        }
        List<ApplicationMntEntity> applicationMntEntities = applicationMntMapper.selectList(query);
        if (applicationMntEntities == null || applicationMntEntities.size() == 0) {
            return Lists.newArrayList();
        }
        return applicationMntEntities.stream().map(entity -> {
            ApplicationDetailResult result = new ApplicationDetailResult();
            BeanUtils.copyProperties(entity, result);
            return result;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationResult> listAmdbApplicationByAppNames(List<String> appNames) {
        return this.getApplicationByName(appNames);
    }

    @Override
    public List<ApplicationResult> getApplicationByName(List<String> appNames) {
        if (CollectionUtils.isEmpty(appNames)) {
            return Lists.newArrayList();
        }

        // 候补List
        List<ApplicationResult> applicationResultList = Lists.newArrayList();
        // wait to fill
        List<ApplicationDTO> applicationDtoTotalList = Lists.newArrayList();

        //分批从amdb获取应用数据
        int batchSize = 100;
        List<String> pageAppNameList;

        for (int from = 0, to, size = appNames.size(); from < size; from = to) {
            to = Math.min(from + batchSize, size);
            pageAppNameList = appNames.subList(from, to);

            ApplicationQueryDTO queryDTO = new ApplicationQueryDTO();
            queryDTO.setAppNames(pageAppNameList);
            queryDTO.setFields(Arrays.asList("library", "instanceInfo"));
            queryDTO.setPageSize(99999);

            PagingList<ApplicationDTO> applicationDtoPagingList = applicationClient.pageApplications(queryDTO);
            // case1 notEmpty
            if (!applicationDtoPagingList.isEmpty()) {
                List<ApplicationDTO> applicationDTOList = applicationDtoPagingList.getList();
                applicationDtoTotalList.addAll(applicationDTOList);
            }

            if (CollectionUtils.isEmpty(applicationDtoTotalList)) {
                return applicationResultList;
            }

        }

        return toAppResult(applicationResultList, applicationDtoTotalList);
    }

    @Override
    public List<ApplicationResult> getApplicationByName(List<String> appNames, String userAppKey, String envCode) {
        if (CollectionUtils.isEmpty(appNames)) {
            return Lists.newArrayList();
        }
        List<ApplicationResult> applicationResultList = Lists.newArrayList();
        List<ApplicationDTO> applicationDtoTotalList = Lists.newArrayList();
        //分批从amdb获取应用数据
        int BATCH_SIZE = 100;
        List<String> pageAppNameList;
        for (int from = 0, to = 0, size = appNames.size(); from < size; from = to) {
            to = Math.min(from + BATCH_SIZE, size);
            pageAppNameList = appNames.subList(from, to);
            ApplicationQueryDTO queryDTO = new ApplicationQueryDTO();
            queryDTO.setAppNames(pageAppNameList);
            queryDTO.setFields(Lists.newArrayList("library,instanceInfo".split(",")));
            queryDTO.setPageSize(99999);
            //补充租户查询条件
            queryDTO.setTenantAppKey(userAppKey);
            queryDTO.setTenantAppKey(envCode);

            PagingList<ApplicationDTO> applicationDtoPagingList = applicationClient.pageApplications(queryDTO);

            if (!applicationDtoPagingList.isEmpty()) {
                List<ApplicationDTO> applicationDTOList = applicationDtoPagingList.getList();
                applicationDtoTotalList.addAll(applicationDTOList);
            }
            if (CollectionUtils.isEmpty(applicationDtoTotalList)) {
                return applicationResultList;
            }
        }

        return toAppResult(applicationResultList, applicationDtoTotalList);
    }

    private List<ApplicationResult> toAppResult(List<ApplicationResult> applicationResultList,
        List<ApplicationDTO> applicationDtoTotalList) {

        List<String> appName = applicationDtoTotalList.stream().map(ApplicationDTO::getAppName).collect(
            Collectors.toList());

        LambdaQueryWrapper<ApplicationMntEntity> query = new LambdaQueryWrapper<>();
        query.in(ApplicationMntEntity::getApplicationName, appName);
        List<ApplicationMntEntity> applicationMntEntities = applicationMntMapper.selectList(query);

        /* key：应用名称，value：userId */
        Map<String, Long> appNameUserIdMap = Maps.newHashMap();
        /* key：应用名称，value：用户名称 */
        Map<String, String> appNameUserNameMap = Maps.newHashMap();

        if (!CollectionUtils.isEmpty(applicationMntEntities)) {
            applicationMntEntities.forEach(localApp -> {
                appNameUserIdMap.computeIfAbsent(localApp.getApplicationName(), k -> localApp.getUserId());
            });
            List<Long> userIds = applicationMntEntities.stream().map(ApplicationMntEntity::getUserId).distinct()
                .collect(Collectors.toList());
            Map<Long, UserExt> userExtMap = WebPluginUtils.getUserMapByIds(userIds);

            for (Entry<String, Long> entry : appNameUserIdMap.entrySet()) {
                String k = entry.getKey();
                Long v = entry.getValue();
                String value = appNameUserNameMap.get(k);
                if (value == null) {
                    if (userExtMap.get(v) != null) {
                        appNameUserNameMap.put(k, userExtMap.get(v).getName());
                    }
                }
            }
        }

        applicationDtoTotalList.forEach(applicationDTO -> {
            ApplicationResult applicationResult = new ApplicationResult();
            applicationResult.setAppId(applicationDTO.getAppId());
            applicationResult.setAppName(applicationDTO.getAppName());
            applicationResult.setAppVersionCode(applicationDTO.getAppVersionCode());
            applicationResult.setAppIsException(applicationDTO.getAppIsException());
            applicationResult.setAppManagerName(appNameUserNameMap.get(applicationDTO.getAppName()));
            applicationResult.setManagerUserId(appNameUserIdMap.get(applicationDTO.getAppName()));
            applicationResult.setAppUpdateTime(applicationDTO.getAppUpdateTime());
            applicationResult.setAppSummary(applicationDTO.getAppSummary());

            LibraryDTO[] libraryDtoArray = applicationDTO.getLibrary();
            List<LibraryResult> libraryResultList = Lists.newArrayList();
            if (libraryDtoArray.length > 0) {
                for (LibraryDTO libraryDTO : libraryDtoArray) {
                    LibraryResult libraryResult = new LibraryResult();
                    BeanUtils.copyProperties(libraryDTO, libraryResult);
                    libraryResultList.add(libraryResult);
                }
            }
            LibraryResult[] libraryResults = new LibraryResult[libraryResultList.size()];
            libraryResults = libraryResultList.toArray(libraryResults);
            applicationResult.setLibrary(libraryResults);
            InstanceInfoDTO instanceInfoDTO = applicationDTO.getInstanceInfo();
            if (!Objects.isNull(instanceInfoDTO)) {
                InstanceInfoResult instanceInfoResult = new InstanceInfoResult();
                instanceInfoResult.setInstanceAmount(instanceInfoDTO.getInstanceAmount());
                instanceInfoResult.setInstanceOnlineAmount(instanceInfoDTO.getInstanceOnlineAmount());
                applicationResult.setInstanceInfo(instanceInfoResult);
                applicationResultList.add(applicationResult);
            }
        });

        return applicationResultList;
    }

    @Override
    public List<ApplicationDetailResult> getApplicationListByUserIds(List<Long> userIdList, List<Long> deptIdList) {
        List<ApplicationDetailResult> applicationDetailResultList = Lists.newArrayList();
        LambdaQueryWrapper<ApplicationMntEntity> wrapper = new LambdaQueryWrapper<>();
        if (!CollectionUtils.isEmpty(userIdList)) {
            wrapper.in(ApplicationMntEntity::getUserId, userIdList);
        }
        if (!CollectionUtils.isEmpty(deptIdList)) {
            wrapper.in(ApplicationMntEntity::getDeptId, deptIdList);
        }
        List<ApplicationMntEntity> entityList = applicationMntMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(entityList)) {
            return applicationDetailResultList;
        }
        applicationDetailResultList = entityList.stream().map(applicationMntEntity -> {
            ApplicationDetailResult result = new ApplicationDetailResult();
            result.setApplicationId(applicationMntEntity.getApplicationId());
            result.setApplicationName(applicationMntEntity.getApplicationName());
            return result;
        }).collect(Collectors.toList());
        return applicationDetailResultList;
    }

    @Override
    public List<ApplicationDetailResult> getApplicationByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        LambdaQueryWrapper<ApplicationMntEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ApplicationMntEntity::getApplicationId, ids);
        return getApplicationDetailResults(wrapper);
    }

    private List<ApplicationDetailResult> getApplicationDetailResults(
        LambdaQueryWrapper<ApplicationMntEntity> wrapper) {
        List<ApplicationMntEntity> entityList = applicationMntMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(entityList)) {
            return Lists.newArrayList();
        }
        return entityList.stream().map(applicationMntEntity -> {
            ApplicationDetailResult result = new ApplicationDetailResult();
            BeanUtils.copyProperties(applicationMntEntity, result);
            return result;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDetailResult> getApplicationList(ApplicationQueryParam param) {
        LambdaQueryWrapper<ApplicationMntEntity> wrapper = new LambdaQueryWrapper<>();
        if (param.getTenantId() != null) {
            wrapper.eq(ApplicationMntEntity::getTenantId, param.getTenantId());
        }
        if (StringUtils.isNotBlank(param.getEnvCode())) {
            wrapper.eq(ApplicationMntEntity::getEnvCode, param.getEnvCode());
        }
        if(StringUtils.isNotBlank(param.getApplicationName())){
            wrapper.eq(ApplicationMntEntity::getApplicationName, param.getApplicationName());
        }
        return getApplicationDetailResults(wrapper);
    }

    @Override
    public List<String> getAllApplicationName(ApplicationQueryParam param) {
        LambdaQueryWrapper<ApplicationMntEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(ApplicationMntEntity::getApplicationName);
        if (CollectionUtils.isNotEmpty(WebPluginUtils.queryAllowUserIdList())) {
            wrapper.in(ApplicationMntEntity::getUserId, WebPluginUtils.queryAllowUserIdList());
        }
        if (CollectionUtils.isNotEmpty(WebPluginUtils.queryAllowDeptIdList())) {
            wrapper.in(ApplicationMntEntity::getDeptId, WebPluginUtils.queryAllowDeptIdList());
        }
        List<ApplicationMntEntity> entities = applicationMntMapper.selectList(wrapper);
        return entities.stream()
            .map(ApplicationMntEntity::getApplicationName)
            .filter(applicationName -> !StringUtils.isEmpty(applicationName)).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDetailResult> getApplicationList(List<String> appNames) {
        List<ApplicationDetailResult> applicationDetailResultList = Lists.newArrayList();
        LambdaUpdateWrapper<ApplicationMntEntity> wrapper = new LambdaUpdateWrapper<>();
        if (appNames != null && appNames.size() > 0) {
            wrapper.in(ApplicationMntEntity::getApplicationName, appNames);
        }
        List<ApplicationMntEntity> entityList = applicationMntMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(entityList)) {
            return applicationDetailResultList;
        }
        applicationDetailResultList = entityList.stream().map(applicationMntEntity -> {
            ApplicationDetailResult result = new ApplicationDetailResult();
            BeanUtils.copyProperties(applicationMntEntity, result);
            return result;
        }).collect(Collectors.toList());
        return applicationDetailResultList;
    }

    @Override
    public int insert(ApplicationCreateParam param) {
        ApplicationMntEntity entity = new ApplicationMntEntity();
        BeanUtils.copyProperties(param, entity);
        return applicationMntMapper.insert(entity);
    }

    @Override
    public ApplicationDetailResult getApplicationById(Long appId) {
        // 修改原因 ：分页插件与 mybatis-plus 污染线程
        ApplicationMntEntity applicationMntEntity =
            applicationMntMapper.selectOne(
                this.getLambdaQueryWrapper().eq(ApplicationMntEntity::getApplicationId, appId));
        if (!Objects.isNull(applicationMntEntity)) {
            ApplicationDetailResult detailResult = new ApplicationDetailResult();
            BeanUtils.copyProperties(applicationMntEntity, detailResult);
            return detailResult;
        }
        return null;
    }

    @Override
    public ApplicationDetailResult getApplicationByIdWithInterceptorIgnore(Long appId) {
        // 修改原因 ：分页插件与 mybatis-plus 污染线程
        ApplicationMntEntity applicationMntEntity = applicationMntMapper.getApplicationByIdWithInterceptorIgnore(appId);

        if (!Objects.isNull(applicationMntEntity)) {
            ApplicationDetailResult detailResult = new ApplicationDetailResult();
            BeanUtils.copyProperties(applicationMntEntity, detailResult);
            return detailResult;
        }
        return null;
    }

    @Override
    public ApplicationDetailResult getApplicationByTenantIdAndName(String appName) {
        if (!StringUtils.isEmpty(appName)) {
            LambdaQueryWrapper<ApplicationMntEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ApplicationMntEntity::getApplicationName, appName);
            ApplicationMntEntity applicationMntEntity = applicationMntMapper.selectOne(queryWrapper);
            if (!Objects.isNull(applicationMntEntity)) {
                ApplicationDetailResult detailResult = new ApplicationDetailResult();
                BeanUtils.copyProperties(applicationMntEntity, detailResult);
                return detailResult;
            }
        }
        return null;
    }

    /**
     * 指定责任人-应用管理
     *
     * @return
     */
    @Override
    public int allocationUser(ApplicationUpdateParam param) {
        LambdaUpdateWrapper<ApplicationMntEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(ApplicationMntEntity::getUserId, param.getUserId())
            .eq(ApplicationMntEntity::getApplicationId, param.getId());
        return applicationMntMapper.update(null, wrapper);
    }

    @Override
    public List<ApplicationMntEntity> listByApplicationNamesAndTenantId(List<String> applicationNames) {
        LambdaQueryWrapper<ApplicationMntEntity> wrapper = this.getLambdaQueryWrapper().select(
                ApplicationMntEntity::getApplicationId, ApplicationMntEntity::getApplicationName,
                ApplicationMntEntity::getAccessStatus, ApplicationMntEntity::getSwitchStatus,
                ApplicationMntEntity::getNodeNum)
            .in(ApplicationMntEntity::getApplicationName, applicationNames);
        return applicationMntMapper.selectList(wrapper);
    }

    @Override
    public ApplicationDetailResult getByName(String applicationName) {
        ApplicationMntEntity applicationMntEntity = applicationMntMapper.selectOne(this.getLimitOneLambdaQueryWrapper()
            .eq(ApplicationMntEntity::getApplicationName, applicationName));
        if (applicationMntEntity == null) {
            return null;
        }
        ApplicationDetailResult applicationResult = new ApplicationDetailResult();
        BeanUtils.copyProperties(applicationMntEntity, applicationResult);
        return applicationResult;
    }

    @Override
    public ApplicationDetailResult getById(Long applicationPrimaryKeyId) {
        ApplicationMntEntity applicationMntEntity = applicationMntMapper.selectById(applicationPrimaryKeyId);
        if (applicationMntEntity == null) {
            return null;
        }
        ApplicationDetailResult applicationDetailResult = new ApplicationDetailResult();
        BeanUtils.copyProperties(applicationMntEntity, applicationDetailResult);
        return applicationDetailResult;
    }

    @Override
    public void updateApplicationStatus(Long applicationId, Integer status) {
        ApplicationMntEntity entity = new ApplicationMntEntity();
        entity.setApplicationId(applicationId);
        entity.setAccessStatus(status);
        LambdaUpdateWrapper<ApplicationMntEntity> wrapper = this.getLambdaUpdateWrapper();

        wrapper.set(ApplicationMntEntity::getAccessStatus, status)
            .eq(ApplicationMntEntity::getApplicationId, applicationId);
        applicationMntMapper.update(null, wrapper);
    }

    @Override
    public void batchUpdateAppNodeNum(List<NodeNumParam> paramList, String envCode, Long tenantId) {
        paramList.forEach(param -> applicationMntMapper.updateAppNodeNum(param, envCode, tenantId));
    }

    @Override
    public List<ApplicationAttentionListEntity> getAttentionList(ApplicationAttentionParam param) {
        return applicationAttentionListMapper.getAttentionList(param);
    }

    @Override
    public void attendApplicationService(Map<String, String> param) {
        applicationAttentionListMapper.attendApplicationService(param);
    }

    @Override
    public List<ApplicationDetailResult> getAllTenantApp(List<TenantCommonExt> commonExtList) {
        if (CollectionUtils.isEmpty(commonExtList)) {
            return Lists.newArrayList();
        }
        List<ApplicationMntEntity> entities = applicationMntMapper.getAllTenantApp(commonExtList);
        if (CollectionUtils.isEmpty(entities)) {
            return Lists.newArrayList();
        }
        return DataTransformUtil.list2list(entities, ApplicationDetailResult.class);
    }

    @Override
    public List<Long> listIdsByNameListAndCustomerId(List<String> applicationNameList) {
        return CommonUtil.list2list(applicationMntMapper.selectObjs(this.getCustomerLambdaQueryWrapper()
            .select(ApplicationMntEntity::getApplicationId)
            .in(ApplicationMntEntity::getApplicationName, applicationNameList)), Long.class);
    }

    @Override
    public String selectApplicationName(String applicationId) {
        return applicationMntMapper.selectApplicationName(applicationId);
    }

    @Override
    public void updateApplicationAgentVersion(Long applicationId, String agentVersion, String pradarVersion) {
        applicationMntMapper.updateApplicationAgentVersion(applicationId, agentVersion, pradarVersion);
    }

    @Override
    public Long queryIdByApplicationName(String applicationName) {
        return applicationMntMapper.queryIdByApplicationName(applicationName);
    }

    @Override
    public List<String> queryIdsByNameAndTenant(List<String> names, Long tenantId, String envCode) {
        return applicationMntMapper.queryIdsByNameAndTenant(names, tenantId, envCode);
    }

    @Override
    public List<ApplicationDetailResult> getAllApplications() {
        List<ApplicationMntEntity> allApplications = applicationMntMapper.getAllApplications();
        if (CollectionUtils.isEmpty(allApplications)) {
            return Lists.newArrayList();
        }
        return DataTransformUtil.list2list(allApplications, ApplicationDetailResult.class);
    }

    @Override
    public List<ApplicationMntEntity> getAllApplicationsWithoutTenant() {
       return applicationMntMapper.getAllApplicationsWithoutTenant();
    }

    @Override
    public List<ApplicationDetailResult> getDashboardAppData() {
        LambdaQueryWrapper<ApplicationMntEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(ApplicationMntEntity::getApplicationId,
            ApplicationMntEntity::getApplicationName, ApplicationMntEntity::getNodeNum,
            ApplicationMntEntity::getAgentVersion);
        if (CollectionUtils.isNotEmpty(WebPluginUtils.queryAllowUserIdList())) {
            queryWrapper.in(ApplicationMntEntity::getUserId, WebPluginUtils.queryAllowUserIdList());
        }
        if (CollectionUtils.isNotEmpty(WebPluginUtils.queryAllowDeptIdList())) {
            queryWrapper.in(ApplicationMntEntity::getDeptId, WebPluginUtils.queryAllowDeptIdList());
        }
        List<ApplicationMntEntity> applicationMntEntities = applicationMntMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(applicationMntEntities)) {
            return Lists.newArrayList();
        }
        return DataTransformUtil.list2list(applicationMntEntities, ApplicationDetailResult.class);
    }

    @Override
    public List<ApplicationDetailResult> getAllApplicationByStatus(List<Integer> statusList) {
        List<ApplicationMntEntity> allApplications = applicationMntMapper.getAllApplicationByStatus(statusList);
        if (CollectionUtils.isEmpty(allApplications)) {
            return Lists.newArrayList();
        }
        return DataTransformUtil.list2list(allApplications, ApplicationDetailResult.class);
    }

    @Override
    public List<ApplicationDetailResult> getApplicationMntByUserIdsAndKeyword(List<Long> userIds, List<Long> deptIdList, String keyword) {

        List<ApplicationMntEntity> allApplications = applicationMntMapper.getApplicationMntByUserIdsAndKeyword(userIds,
                deptIdList, keyword);
        if (CollectionUtils.isEmpty(allApplications)) {
            return Lists.newArrayList();
        }
        return DataTransformUtil.list2list(allApplications, ApplicationDetailResult.class);
    }

    @Override
    public int applicationExistByTenantIdAndAppName(Long tenantId, String envCode, String applicationName) {
        return applicationMntMapper.applicationExistByTenantIdAndAppName(tenantId, envCode, applicationName);
    }

    @Override
    public PagingList<ApplicationDetailResult> queryApplicationList(ApplicationQueryParam queryParam) {
        LambdaQueryWrapper<ApplicationMntEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(queryParam.getApplicationName())) {
            queryWrapper.like(ApplicationMntEntity::getApplicationName, queryParam.getApplicationName());
        }
        if (CollectionUtils.isNotEmpty(queryParam.getApplicationIds())) {
            queryWrapper.in(ApplicationMntEntity::getApplicationId, queryParam.getApplicationIds());
        }
        if (CollectionUtils.isNotEmpty(WebPluginUtils.queryAllowUserIdList())) {
            queryWrapper.in(ApplicationMntEntity::getUserId, WebPluginUtils.queryAllowUserIdList());
        }
        if (CollectionUtils.isNotEmpty(WebPluginUtils.queryAllowDeptIdList())) {
            queryWrapper.in(ApplicationMntEntity::getDeptId, WebPluginUtils.queryAllowDeptIdList());
        }
        queryWrapper.orderByDesc(ApplicationMntEntity::getApplicationId);
        if (queryParam.getPageSize() > 0) {
            Page<ApplicationMntEntity> page = new Page<>(queryParam.getCurrent(), queryParam.getPageSize());
            IPage<ApplicationMntEntity> entityIPage = applicationMntMapper.selectPage(page, queryWrapper);
            if (entityIPage.getTotal() == 0) {
                return PagingList.empty();
            }
            return PagingList.of(DataTransformUtil.list2list(entityIPage.getRecords(), ApplicationDetailResult.class),
                entityIPage.getTotal());
        } else {
            // 查询所有
            List<ApplicationMntEntity> applicationMntEntities = applicationMntMapper.selectList(queryWrapper);
            if (CollectionUtils.isEmpty(applicationMntEntities)) {
                return PagingList.empty();
            }
            return PagingList.of(DataTransformUtil.list2list(applicationMntEntities, ApplicationDetailResult.class),
                applicationMntEntities.size());
        }

    }

    @Override
    public int applicationExist(String applicationName) {
        return applicationMntMapper.applicationExist(applicationName);
    }

    @Override
    public void updateApplicationInfo(ApplicationCreateParam updateParam) {
        ApplicationMntEntity entity = new ApplicationMntEntity();
        BeanUtils.copyProperties(updateParam, entity);
        applicationMntMapper.updateApplicationInfo(entity);
    }

    @Override
    public Map<String, Object> queryApplicationRelationBasicLinkByApplicationId(String applicationId) {
        return applicationMntMapper.queryApplicationRelationBasicLinkByApplicationId(applicationId);
    }

    @Override
    public void deleteApplicationInfoByIds(List<Long> applicationIdLists) {
        applicationMntMapper.deleteApplicationInfoByIds(applicationIdLists);
    }

    @Override
    public List<Map<String, Object>> queryApplicationListByIds(List<Long> applicationIds) {
        return applicationMntMapper.queryApplicationListByIds(applicationIds);
    }

    @Override
    public List<Map<String, Object>> queryApplicationData() {
        return applicationMntMapper.queryApplicationData();
    }

    @Override
    public void batchUpdateApplicationStatus(List<Long> applicationIds, Integer accessStatus) {
        applicationMntMapper.batchUpdateApplicationStatus(applicationIds, accessStatus);
    }

    @Override
    public Map<String, Object> queryCacheExpTime(String applicationId) {
        return applicationMntMapper.queryCacheExpTime(applicationId);
    }

    @Override
    public String selectScriptPath(String applicationId, String scriptType) {
        return applicationMntMapper.selectScriptPath(applicationId, scriptType);
    }

    @Override
    public String getIdByName(String applicationName) {
        return applicationMntMapper.getIdByName(applicationName);
    }

    @Override
    public Long getApplicationCount() {
        LambdaQueryWrapper<ApplicationMntEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (CollectionUtils.isNotEmpty(WebPluginUtils.queryAllowUserIdList())) {
            queryWrapper.in(ApplicationMntEntity::getUserId, WebPluginUtils.queryAllowUserIdList());
        }
        if (CollectionUtils.isNotEmpty(WebPluginUtils.queryAllowDeptIdList())) {
            queryWrapper.in(ApplicationMntEntity::getDeptId, WebPluginUtils.queryAllowDeptIdList());
        }
        return applicationMntMapper.selectCount(queryWrapper);
    }

    @Override
    public IPage<ApplicationListResult> pageByParam(QueryApplicationParam param) {
        return applicationMntMapper.selectApplicationPageByParam(this.setPage(param), param);
    }

    @Override
    public List<ApplicationListResult> pageFromSync(PageBaseDTO pageBaseDTO) {
        IPage<ApplicationMntEntity> applicationMntEntityPage = applicationMntMapper.selectPage(this.setPage(pageBaseDTO),
            this.getLambdaQueryWrapper().select(ApplicationMntEntity::getApplicationId,
                ApplicationMntEntity::getApplicationName, ApplicationMntEntity::getAccessStatus,
                ApplicationMntEntity::getNodeNum));
        return DataTransformUtil.list2list(applicationMntEntityPage.getRecords(), ApplicationListResult.class);
    }

    @Override
    public boolean updateStatusByApplicationIds(Collection<Long> applicationIds, Integer status) {
        return CollectionUtil.isNotEmpty(applicationIds)
            && SqlHelper.retBool(applicationMntMapper.update(null, this.getLambdaUpdateWrapper()
            .set(ApplicationMntEntity::getAccessStatus, status)
            .in(ApplicationMntEntity::getApplicationId, applicationIds)));
    }


    @Override
    public List<ApplicationDetailResult> getAllApplicationsByField() {
        List<ApplicationMntEntity> allApplications = applicationMntMapper.getAllApplicationsByField();
        if (CollectionUtils.isEmpty(allApplications)) {
            return Lists.newArrayList();
        }
        return DataTransformUtil.list2list(allApplications, ApplicationDetailResult.class);
    }

    @Override
    public IPage<ApplicationListResultByUpgrade> getApplicationList(QueryApplicationByUpgradeParam param) {
        return applicationMntMapper.selectApplicationListByUpgrade(this.setPage(param), param);
    }

    @Override
    public List<ApplicationListResult> listByApplicationNamesAndUserId(Collection<String> applicationNames, Long userId) {
        return DataTransformUtil.list2list(applicationMntMapper.selectList(this.getWrapperByApplicationNamesAndUserId(applicationNames, userId)), ApplicationListResult.class);
    }

    /**
     * 根据应用名称, 用户id, 获得sql条件
     *
     * @param applicationNames 应用名称
     * @param userId 用户id
     * @return 应用列表
     */
    private LambdaQueryWrapper<ApplicationMntEntity> getWrapperByApplicationNamesAndUserId(Collection<String> applicationNames, Long userId) {
        return this.getLambdaQueryWrapper()
            .select(ApplicationMntEntity::getApplicationId, ApplicationMntEntity::getApplicationName)
            .in(ApplicationMntEntity::getApplicationName, applicationNames)
            .eq(userId != null, ApplicationMntEntity::getUserId, userId);
    }

    @Override
    public PagingList<ApplicationListResult> pageByApplicationNamesAndUserId(Collection<String> applicationNames,
        PageBaseDTO pageBaseDTO) {
        Page<ApplicationMntEntity> page = applicationMntMapper.selectPage(this.setPage(pageBaseDTO),
            this.getWrapperByApplicationNamesAndUserId(applicationNames, null));
        if (page.getTotal() == 0) {
            return PagingList.empty();
        }

        return PagingList.of(DataTransformUtil.list2list(page.getRecords(), ApplicationListResult.class),
            page.getTotal());
    }

    @Override
    public Map getStatus(String name) {
        return applicationMntMapper.getStatus(name);
    }

    @Override
    public void updateStatus(Long applicationId, String e) {
        LambdaUpdateWrapper<ApplicationMntEntity> wrapper = this.getLambdaUpdateWrapper();

        wrapper.set(ApplicationMntEntity::getAccessStatus, 3)
                .set(ApplicationMntEntity::getExceptionInfo,e)
                .eq(ApplicationMntEntity::getApplicationId, applicationId);
        applicationMntMapper.update(null, wrapper);
    }

    @Override
    public void updateStatus(Long applicationId) {
        LambdaUpdateWrapper<ApplicationMntEntity> wrapper = this.getLambdaUpdateWrapper();

        wrapper.set(ApplicationMntEntity::getAccessStatus, 0)
                .set(ApplicationMntEntity::getExceptionInfo,"")
                .eq(ApplicationMntEntity::getApplicationId, applicationId);
        applicationMntMapper.update(null, wrapper);
    }

    @Override
    public boolean existsApplication(Long tenantId, String envCode) {
        LambdaQueryWrapper<ApplicationMntEntity> wrapper = this.getLambdaQueryWrapper()
            .eq(ApplicationMntEntity::getTenantId, tenantId)
            .eq(ApplicationMntEntity::getEnvCode, envCode);
        return SqlHelper.retBool(applicationMntMapper.selectCount(wrapper));
    }
}
