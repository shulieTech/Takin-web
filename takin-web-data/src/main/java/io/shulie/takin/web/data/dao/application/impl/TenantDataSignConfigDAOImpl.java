package io.shulie.takin.web.data.dao.application.impl;

import cn.hutool.core.collection.CollStreamUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.shulie.takin.web.common.constant.CacheConstants;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.application.TenantDataSignConfigDAO;
import io.shulie.takin.web.data.mapper.mysql.TenantDataSignConfigMapper;
import io.shulie.takin.web.data.model.mysql.TenantDataSignConfigEntity;
import io.shulie.takin.web.data.util.MPUtil;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 租户数据存储签名配置(TenantDataSignConfig)表数据库 dao 层实现
 *
 * @author 南风
 * @date 2022-05-23 15:46:05
 */
@Service
public class TenantDataSignConfigDAOImpl implements TenantDataSignConfigDAO, MPUtil<TenantDataSignConfigEntity> {

    @Autowired
    private TenantDataSignConfigMapper tenantDataSignConfigMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Integer> queryTenantStatus() {
        LambdaQueryWrapper<TenantDataSignConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        List<TenantDataSignConfigEntity> list = tenantDataSignConfigMapper.selectList(queryWrapper);
        return CollStreamUtil.toMap(list, (x -> x.getEnvCode() + x.getTenantId()), TenantDataSignConfigEntity::getStatus);
    }

    @Override
    public void updateStatus(int status, Long tenantId) {
        LambdaQueryWrapper<TenantDataSignConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        String envCode = WebPluginUtils.traceEnvCode();
        queryWrapper.eq(TenantDataSignConfigEntity::getTenantId, tenantId);
        queryWrapper.eq(TenantDataSignConfigEntity::getEnvCode, envCode);
        TenantDataSignConfigEntity entity = tenantDataSignConfigMapper.selectOne(queryWrapper);
        String cacheKey = CacheConstants.CACHE_KEY_TENANT_DATA_SIGN_CLEAN_STATUS + "_" + envCode;
        if(Objects.isNull(entity)){
            //初始化状态
            TenantDataSignConfigEntity insert = new TenantDataSignConfigEntity();
            insert.setStatus(status);
            insert.setTenantId(tenantId);
            insert.setEnvCode(envCode);
            tenantDataSignConfigMapper.insert(insert);
            redisTemplate.opsForHash().put(CacheConstants.CACHE_KEY_TENANT_DATA_SIGN, envCode + tenantId, status);
            if(status == 0){
                redisTemplate.opsForSet().add(cacheKey, tenantId);
            }
        }
        if (Objects.nonNull(entity) && entity.getStatus() != status) {
            if (status == 1) {
                //开启签名
                Set members = redisTemplate.opsForSet().members(cacheKey);
                if (members != null && members.contains(tenantId.intValue())) {
                    throw new TakinWebException(TakinWebExceptionEnum.DATA_SIGN_CLEAR_ERROR, "数据正在清理中,暂时无法开启");
                }
                TenantDataSignConfigEntity update = new TenantDataSignConfigEntity();
                update.setStatus(status);
                update.setId(entity.getId());
                tenantDataSignConfigMapper.updateById(update);
                redisTemplate.opsForHash().put(CacheConstants.CACHE_KEY_TENANT_DATA_SIGN, envCode + tenantId, status);
            }
            if (status == 0) {
                //关闭签名
                redisTemplate.opsForSet().add(cacheKey, tenantId);
                TenantDataSignConfigEntity update = new TenantDataSignConfigEntity();
                update.setStatus(status);
                update.setId(entity.getId());
                tenantDataSignConfigMapper.updateById(update);
                redisTemplate.opsForHash().put(CacheConstants.CACHE_KEY_TENANT_DATA_SIGN, envCode + tenantId, status);
            }
        }
    }

    @Override
    public Integer getStatus(Long tenantId) {
        LambdaQueryWrapper<TenantDataSignConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TenantDataSignConfigEntity::getTenantId, tenantId);
        queryWrapper.eq(TenantDataSignConfigEntity::getEnvCode, WebPluginUtils.traceEnvCode());
        TenantDataSignConfigEntity entity = tenantDataSignConfigMapper.selectOne(queryWrapper);
        return entity.getStatus();
    }

    @Override
    public void clearSign(List<Long> tenantIds, String envCode,String tableName) {
        if(CollectionUtils.isEmpty(tenantIds)){
            return;
        }
        tenantDataSignConfigMapper.clearSign(tenantIds, envCode,tableName);
    }
}

