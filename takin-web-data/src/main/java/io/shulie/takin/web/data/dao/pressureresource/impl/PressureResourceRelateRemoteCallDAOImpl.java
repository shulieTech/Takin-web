package io.shulie.takin.web.data.dao.pressureresource.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.dao.application.AppRemoteCallDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.pressureresource.MockInfo;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateRemoteCallDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateRemoteCallMapperV2;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateRemoteCallEntityV2;
import io.shulie.takin.web.data.model.mysql.pressureresource.RelateRemoteCallEntity;
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
import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/14 11:09 AM
 */
@Service
public class PressureResourceRelateRemoteCallDAOImpl implements PressureResourceRelateRemoteCallDAO {

    public static final String APPLICATION_CACHE_PREFIX = "application:cache";

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
    public PagingList<RelateRemoteCallEntity> pageList_v2(PressureResourceRemoteCallQueryParam param) {
        QueryWrapper<PressureResourceRelateRemoteCallEntityV2> queryWrapper = this.getWrapper_v2(param);
        Page<PressureResourceRelateRemoteCallEntityV2> page = new Page<>(param.getCurrent() + 1, param.getPageSize());
        queryWrapper.orderByAsc("rpc_id");
        IPage<PressureResourceRelateRemoteCallEntityV2> pageList = pressureResourceRelateRemoteCallMapperV2.selectPage(page, queryWrapper);
        if (pageList.getRecords().isEmpty()) {
            return PagingList.empty();
        }
        List<PressureResourceRelateRemoteCallEntityV2> v2List = pageList.getRecords();
        List<RelateRemoteCallEntity> callEntityList = Lists.newArrayList();
        for (int i = 0; i < v2List.size(); i++) {
            PressureResourceRelateRemoteCallEntityV2 v2 = v2List.get(i);
            RelateRemoteCallEntity call = new RelateRemoteCallEntity();
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
}
