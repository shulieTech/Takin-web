package io.shulie.takin.web.data.dao.pressureresource.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.common.util.application.RemoteCallUtils;
import io.shulie.takin.web.data.dao.application.AppRemoteCallDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.pressureresource.MockInfo;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateRemoteCallDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateRemoteCallMapper;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateRemoteCallMapperV2;
import io.shulie.takin.web.data.model.mysql.AppRemoteCallEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateRemoteCallEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateRemoteCallEntityV2;
import io.shulie.takin.web.data.param.application.AppRemoteCallQueryParam;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceRemoteCallQueryParam;
import io.shulie.takin.web.data.result.application.AppRemoteCallResult;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/14 11:09 AM
 */
@Service
public class PressureResourceRelateRemoteCallDAOImpl implements PressureResourceRelateRemoteCallDAO {

    public static final String APPLICATION_CACHE_PREFIX = "application:cache";

    @Resource
    private PressureResourceRelateRemoteCallMapper pressureResourceRelateRemoteCallMapper;

    @Resource
    private PressureResourceRelateRemoteCallMapperV2 pressureResourceRelateRemoteCallMapperV2;

    @Resource
    private AppRemoteCallDAO appRemoteCallDAO;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    @Resource
    private ApplicationDAO applicationDAO;

    /**
     * 存在则更新
     *
     * @param remoteCallEntityList
     */
    @Override
    public void saveOrUpdate(List<PressureResourceRelateRemoteCallEntity> remoteCallEntityList) {
        if (CollectionUtils.isEmpty(remoteCallEntityList)) {
            return;
        }

        Set<String> appNames = remoteCallEntityList.stream().map(entity -> entity.getAppName()).collect(Collectors.toSet());
        Map<String, Long> appNameIdMappings = new HashMap<>();
        appNames.stream().forEach(s -> appNameIdMappings.put(s, queryApplicationIdByAppName(s)));

        remoteCallEntityList.stream().forEach(remote -> {
            Long id = insertAppRemoteCall(remote, appNameIdMappings);
            remote.setRelateAppRemoteCallId(id);
            removeDuplicateProperties(remote);
            pressureResourceRelateRemoteCallMapper.saveOrUpdate(remote);
        });
    }

    /**
     * 存在则更新
     *
     * @param remoteCallEntityList
     */
    @Override
    public void saveOrUpdate_v2(List<PressureResourceRelateRemoteCallEntityV2> remoteCallEntityList) {
        if (CollectionUtils.isEmpty(remoteCallEntityList)) {
            return;
        }
        remoteCallEntityList.stream().forEach(remote -> {
            if (remote.isFind()) {
                pressureResourceRelateRemoteCallMapperV2.saveOrUpdate(remote);
            }
        });
    }

    public Long queryApplicationIdByAppName(String appName) {
        // 添加缓存：agent心跳接口太过于频繁
        String key = generateApplicationCacheKey(appName);
        String applicationId = (String) redisTemplate.opsForHash().get(APPLICATION_CACHE_PREFIX, key);
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(applicationId)) {
            return Long.valueOf(applicationId);
        }
        ApplicationDetailResult detailResult = applicationDAO.getByName(appName);
        Long result = null;
        if (detailResult != null) {
            result = detailResult.getApplicationId();
            redisTemplate.opsForHash().put(APPLICATION_CACHE_PREFIX, key, String.valueOf(result));
        }
        return result;
    }

    // 生成应用缓存key
    public static String generateApplicationCacheKey(String applicationName) {
        return String.format("%s:%s:%s", WebPluginUtils.traceTenantId(), WebPluginUtils.traceEnvCode(), applicationName);
    }

    /**
     * 写入应用配置的表
     *
     * @param entity
     * @param appNameIdMappings
     */
    private Long insertAppRemoteCall(PressureResourceRelateRemoteCallEntity entity, Map<String, Long> appNameIdMappings) {

        AppRemoteCallEntity callEntity = new AppRemoteCallEntity();
        callEntity.setInterfaceName(entity.getInterfaceName());
        callEntity.setInterfaceType(entity.getInterfaceType());
        callEntity.setServerAppName(entity.getServerAppName());
        callEntity.setAppName(entity.getAppName());
        callEntity.setType(entity.getType());
        callEntity.setMockReturnValue(entity.getMockReturnValue());
        callEntity.setIsDeleted(entity.getIsDeleted());
        callEntity.setGmtCreate(entity.getGmtCreate());
        callEntity.setGmtModified(entity.getGmtModified());
        callEntity.setIsSynchronize(isSynchronize(entity.getIsSynchronize()));
        callEntity.setManualTag(entity.getManualTag());
        callEntity.setInterfaceChildType(entity.getInterfaceChildType());
        callEntity.setRemark(entity.getRemark());
        callEntity.setMd5(entity.getMd5());
        callEntity.setUserId(entity.getUserId());
        callEntity.setTenantId(entity.getTenantId());
        callEntity.setEnvCode(entity.getEnvCode());
        callEntity.setApplicationId(appNameIdMappings.get(entity.getAppName()));

        AppRemoteCallQueryParam param = new AppRemoteCallQueryParam();
        param.setType(entity.getType());
        param.setInterfaceName(entity.getInterfaceName());
        param.setApplicationId(callEntity.getApplicationId());
        // 如果在旧表上已经有相应的记录，则关联上数据,不重复写入
        List<AppRemoteCallResult> remoteCallResults = appRemoteCallDAO.getList(param);
        // 自动梳理不能重复写入
        if (!remoteCallResults.isEmpty()) {
            return remoteCallResults.get(0).getId();
        }
        appRemoteCallDAO.save(callEntity);
        return callEntity.getId();
    }

    /**
     * 移除部分属性，防止双写
     *
     * @param entity
     */
    private void removeDuplicateProperties(PressureResourceRelateRemoteCallEntity entity) {
        entity.setInterfaceName(null);
        entity.setInterfaceType(null);
        entity.setRemark(null);
        entity.setType(null);
        entity.setMockReturnValue(null);
        entity.setUserId(null);
        entity.setIsSynchronize(null);
    }

    private Boolean isSynchronize(Integer isSynchronize) {
        return isSynchronize != null && isSynchronize > 0;
    }

    /**
     * 分页
     *
     * @param param
     * @return
     */
    @Override
    public PagingList<PressureResourceRelateRemoteCallEntity> pageList(PressureResourceRemoteCallQueryParam param) {
        QueryWrapper<PressureResourceRelateRemoteCallEntity> queryWrapper = this.getWrapper(param);
        Page<PressureResourceRelateRemoteCallEntity> page = new Page<>(param.getCurrent() + 1, param.getPageSize());
        queryWrapper.orderByAsc("rpc_id");
        IPage<PressureResourceRelateRemoteCallEntity> pageList = pressureResourceRelateRemoteCallMapper.selectPage(page, queryWrapper);
        if (pageList.getRecords().isEmpty()) {
            return PagingList.empty();
        }
        // 查询旧表数据
        Set<Long> appRemoteCallIds = pageList.getRecords().stream().map(entity -> entity.getRelateAppRemoteCallId()).collect(Collectors.toSet());
        if (!appRemoteCallIds.isEmpty()) {
            List<AppRemoteCallEntity> remoteCallEntities = appRemoteCallDAO.listByIds(appRemoteCallIds);
            Map<Long, AppRemoteCallEntity> mappings = new HashMap<>();
            for (AppRemoteCallEntity callEntity : remoteCallEntities) {
                mappings.put(callEntity.getId(), callEntity);
            }
            pageList.getRecords().forEach(entity -> populateProperties(entity, mappings.get(entity.getRelateAppRemoteCallId())));
        }

        return PagingList.of(pageList.getRecords(), pageList.getTotal());
    }

    /**
     * 分页
     *
     * @param param
     * @return
     */
    @Override
    public PagingList<PressureResourceRelateRemoteCallEntity> pageList_v2(PressureResourceRemoteCallQueryParam param) {
        QueryWrapper<PressureResourceRelateRemoteCallEntityV2> queryWrapper = this.getWrapper_v2(param);
        Page<PressureResourceRelateRemoteCallEntityV2> page = new Page<>(param.getCurrent() + 1, param.getPageSize());
        queryWrapper.orderByAsc("rpc_id");
        IPage<PressureResourceRelateRemoteCallEntityV2> pageList = pressureResourceRelateRemoteCallMapperV2.selectPage(page, queryWrapper);
        if (pageList.getRecords().isEmpty()) {
            return PagingList.empty();
        }
        List<PressureResourceRelateRemoteCallEntityV2> v2List = pageList.getRecords();
        List<PressureResourceRelateRemoteCallEntity> callEntityList = Lists.newArrayList();
        for (int i = 0; i < v2List.size(); i++) {
            PressureResourceRelateRemoteCallEntityV2 v2 = v2List.get(i);
            PressureResourceRelateRemoteCallEntity call = new PressureResourceRelateRemoteCallEntity();
            BeanUtils.copyProperties(v2, call);

            if (!param.isConvert()) {
                AppRemoteCallResult callResult = appRemoteCallDAO.queryOne(v2.getAppName(), v2.getInterfaceType(), v2.getInterfaceName());
                // 设置下mock值
                MockInfo mockInfo = new MockInfo();
                Integer type = callResult.getType();
                call.setType(callResult.getType());
                if (type == 2) {
                    // groovy脚本mock
                    mockInfo.setType(String.valueOf(1));
                    mockInfo.setMockValue(callResult.getMockReturnValue());
                    call.setMockReturnValue(JSON.toJSONString(mockInfo));
                }
                if (type == 4) {
                    mockInfo.setType(String.valueOf(0));
                    mockInfo.setMockValue(callResult.getMockReturnValue());
                    call.setMockReturnValue(JSON.toJSONString(mockInfo));
                }
                call.setServerAppName(callResult.getServerAppName());
            }
            callEntityList.add(call);
        }
        return PagingList.of(callEntityList, pageList.getTotal());
    }

    private QueryWrapper<PressureResourceRelateRemoteCallEntityV2> getWrapper_v2(PressureResourceRemoteCallQueryParam param) {
        QueryWrapper<PressureResourceRelateRemoteCallEntityV2> queryWrapper = new QueryWrapper<>();
        if (param == null) {
            return queryWrapper;
        }
        // 模糊查询
        if (StringUtils.isNotBlank(param.getQueryInterfaceName())) {
            queryWrapper.like("interface_name", param.getQueryInterfaceName());
        }
        if (param.getResourceId() != null) {
            queryWrapper.eq("resource_id", param.getResourceId());
        }
        if (param.getInterfaceChildType() != null) {
            queryWrapper.eq("interface_child_type", param.getInterfaceChildType());
        }
        if (param.getPass() != null) {
            queryWrapper.eq("pass", param.getPass());
        }
        if (param.getStatus() != null) {
            queryWrapper.eq("status", param.getStatus());
        }
        if (StringUtils.isNotBlank(param.getEntry())) {
            queryWrapper.eq("detail_id", param.getEntry());
        }
        return queryWrapper;
    }

    private QueryWrapper<PressureResourceRelateRemoteCallEntity> getWrapper(PressureResourceRemoteCallQueryParam param) {
        QueryWrapper<PressureResourceRelateRemoteCallEntity> queryWrapper = new QueryWrapper<>();
        if (param == null) {
            return queryWrapper;
        }
        // 模糊查询
        if (StringUtils.isNotBlank(param.getQueryInterfaceName())) {
            queryWrapper.like("interface_name", param.getQueryInterfaceName());
        }
        if (param.getResourceId() != null) {
            queryWrapper.eq("resource_id", param.getResourceId());
        }
        if (param.getInterfaceChildType() != null) {
            queryWrapper.eq("interface_child_type", param.getInterfaceChildType());
        }
        if (param.getPass() != null) {
            queryWrapper.eq("pass", param.getPass());
        }
        if (param.getStatus() != null) {
            queryWrapper.eq("status", param.getStatus());
        }
        if (StringUtils.isNotBlank(param.getEntry())) {
            queryWrapper.eq("detail_id", param.getEntry());
        }
        return queryWrapper;
    }

    private void populateProperties(PressureResourceRelateRemoteCallEntity entity, AppRemoteCallEntity appRemoteCall) {
        if (appRemoteCall == null) {
            return;
        }
        entity.setInterfaceName(appRemoteCall.getInterfaceName());
        entity.setInterfaceType(appRemoteCall.getInterfaceType());
        entity.setRemark(appRemoteCall.getRemark());
        entity.setType(appRemoteCall.getType());
        entity.setMockReturnValue(appRemoteCall.getMockReturnValue());
        entity.setUserId(appRemoteCall.getUserId());
        entity.setIsSynchronize(appRemoteCall.getIsSynchronize() == null ? 0 : appRemoteCall.getIsSynchronize() ? 1 : 0);
    }
}
