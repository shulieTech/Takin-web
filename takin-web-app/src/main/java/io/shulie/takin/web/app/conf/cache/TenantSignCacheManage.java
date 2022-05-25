package io.shulie.takin.web.app.conf.cache;

import io.shulie.takin.web.biz.constant.TakinWebContext;
import io.shulie.takin.web.common.constant.CacheConstants;
import io.shulie.takin.web.data.dao.application.TenantDataSignConfigDAO;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @Author: 南风
 * @Date: 2022/5/19 3:18 下午
 */
@Component
public class TenantSignCacheManage {

    @Autowired
    private  RedisTemplate redisTemplate;

    @Autowired
    private TenantDataSignConfigDAO configDAO;


    @PostConstruct
    public void  init(){
        Map<String, Integer> map = configDAO.queryTenantStatus();
        if(!map.isEmpty()){
            redisTemplate.opsForHash().putAll(CacheConstants.CACHE_KEY_TENANT_DATA_SIGN,map);
        }
    }


    public Boolean get(Long tenantId) {

        Boolean hasKey = redisTemplate.opsForHash().hasKey(CacheConstants.CACHE_KEY_TENANT_DATA_SIGN, WebPluginUtils.traceEnvCode() + tenantId);
        if(!hasKey){
            return false;
        }
        Object o = redisTemplate.opsForHash().get(CacheConstants.CACHE_KEY_TENANT_DATA_SIGN, WebPluginUtils.traceEnvCode() + tenantId);
        if(o ==null){
            return false;
        }
        return Integer.parseInt(o.toString()) != 0;
    }

    public void setContext(Long tenantId){
        TakinWebContext.setTenantStatus(this.get(tenantId));
    }

}
