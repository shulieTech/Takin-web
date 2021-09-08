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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import com.alibaba.excel.util.CollectionUtils;
import com.alibaba.excel.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.api.ApplicationClient;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationQueryDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationDTO;
import io.shulie.takin.web.amdb.bean.result.application.InstanceInfoDTO;
import io.shulie.takin.web.amdb.bean.result.application.LibraryDTO;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.shulie.takin.web.data.mapper.mysql.ApplicationMntMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationMntEntity;
import io.shulie.takin.web.data.param.application.ApplicationCreateParam;
import io.shulie.takin.web.data.param.application.ApplicationQueryParam;
import io.shulie.takin.web.data.param.application.ApplicationUpdateParam;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationResult;
import io.shulie.takin.web.data.result.application.InstanceInfoResult;
import io.shulie.takin.web.data.result.application.LibraryResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author shiyajian
 * create: 2020-09-20
 */
@Service
public class ApplicationDAOImpl
    implements ApplicationDAO, MPUtil<ApplicationMntEntity> {

    @Autowired
    private ApplicationClient applicationClient;

    @Autowired
    private ApplicationMntMapper applicationMntMapper;

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
    public List<ApplicationResult> getApplicationByName(List<String> appNames) {
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
            // List<TakinUserEntity> takinUserEntities = takinUserMapper.selectBatchIds(userIds);
            // todo user
            Map<Long, String> userIdUserNameMap = Maps.newHashMap();
            //takinUserEntities.stream().collect(
            // Collectors.toMap(TakinUserEntity::getId, TakinUserEntity::getName));
            for (Entry<String, Long> entry : appNameUserIdMap.entrySet()) {
                String k = entry.getKey();
                Long v = entry.getValue();
                String value = appNameUserNameMap.get(k);
                if (value == null) {
                    appNameUserNameMap.put(k, userIdUserNameMap.get(v));
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
    public List<ApplicationDetailResult> getApplicationListByUserIds(List<Long> userIdList) {
        List<ApplicationDetailResult> applicationDetailResultList = Lists.newArrayList();
        LambdaUpdateWrapper<ApplicationMntEntity> wrapper = new LambdaUpdateWrapper();
        if (!CollectionUtils.isEmpty(userIdList)) {
            wrapper.in(ApplicationMntEntity::getUserId, userIdList);
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
        LambdaQueryWrapper<ApplicationMntEntity> wrapper = new LambdaQueryWrapper();
        wrapper.in(ApplicationMntEntity::getApplicationId, ids);
        return getApplicationDetailResults(wrapper);
    }

    private List<ApplicationDetailResult> getApplicationDetailResults(LambdaQueryWrapper<ApplicationMntEntity> wrapper) {
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
        LambdaQueryWrapper<ApplicationMntEntity> wrapper = new LambdaQueryWrapper();
        if (param.getCustomerId() != null) {
            wrapper.eq(ApplicationMntEntity::getCustomerId, param.getCustomerId());
        }
        return getApplicationDetailResults(wrapper);
    }

    @Override
    public List<String> getAllApplicationName(ApplicationQueryParam param) {
        LambdaQueryWrapper<ApplicationMntEntity> wrapper = new LambdaQueryWrapper<>();
        if (param.getCustomerId() != null) {
            wrapper.eq(ApplicationMntEntity::getCustomerId, param.getCustomerId());
        }
        if (param.getUserId() != null) {
            wrapper.eq(ApplicationMntEntity::getUserId, param.getUserId());
        }
        List<ApplicationMntEntity> entities = applicationMntMapper.selectList(wrapper);
        return entities.stream()
            .map(ApplicationMntEntity::getApplicationName)
            .filter(applicationName -> !StringUtils.isEmpty(applicationName)).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDetailResult> getApplicationList(List<String> appNames) {
        List<ApplicationDetailResult> applicationDetailResultList = Lists.newArrayList();
        LambdaUpdateWrapper<ApplicationMntEntity> wrapper = new LambdaUpdateWrapper();
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
        ApplicationMntEntity applicationMntEntity =
            applicationMntMapper.selectOne(this.getLimitOneLambdaQueryWrapper().eq(ApplicationMntEntity::getApplicationId, appId));
        if (!Objects.isNull(applicationMntEntity)) {
            ApplicationDetailResult detailResult = new ApplicationDetailResult();
            BeanUtils.copyProperties(applicationMntEntity, detailResult);
            return detailResult;
        }
        return null;
    }

    @Override
    public ApplicationDetailResult getApplicationByCustomerIdAndName(String appName) {
        if (!StringUtils.isEmpty(appName)) {
            LambdaQueryWrapper<ApplicationMntEntity> queryWrapper = new LambdaQueryWrapper<>();
            if (WebPluginUtils.checkUserData()) {
                queryWrapper.eq(ApplicationMntEntity::getCustomerId, WebPluginUtils.getCustomerId());
            }
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
    public List<ApplicationMntEntity> listByApplicationNamesAndCustomerId(List<String> applicationNames) {
        LambdaQueryWrapper<ApplicationMntEntity> wrapper = this.getLambdaQueryWrapper().select(ApplicationMntEntity::getApplicationId,
                ApplicationMntEntity::getApplicationName, ApplicationMntEntity::getAccessStatus,
                ApplicationMntEntity::getSwitchStatus, ApplicationMntEntity::getNodeNum)
            .in(ApplicationMntEntity::getApplicationName, applicationNames);
        if (WebPluginUtils.checkUserData()) {
            wrapper.eq(ApplicationMntEntity::getCustomerId, WebPluginUtils.getCustomerId());
        }
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
        ;
        wrapper.set(ApplicationMntEntity::getAccessStatus, status)
            .eq(ApplicationMntEntity::getApplicationId, applicationId);
        applicationMntMapper.update(null, wrapper);
    }
}
