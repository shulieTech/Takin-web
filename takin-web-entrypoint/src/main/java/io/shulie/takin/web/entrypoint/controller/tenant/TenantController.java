package io.shulie.takin.web.entrypoint.controller.tenant;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.util.TenantUtils;
import io.shulie.takin.web.ext.entity.tenant.SwitchTenantExt;
import io.shulie.takin.web.ext.entity.tenant.TenantApplicationExt;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantConfigExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoConfigExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.entrypoint.controller.tenant
 * @ClassName: TenantController
 * @Description: 租户接口
 * @Date: 2021/10/20 15:58
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL +"tenant")
@Api(tags = "租户接口")
public class TenantController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ApplicationService applicationService;

    @GetMapping
    @ApiOperation("获取租户列表 以及 环境列表")
    public List<TenantInfoExt> getTenantInfoList(@RequestParam(value = "tenantCode",required = false) String tenantCode) {
        // 没有传则取默认
        if(StringUtils.isBlank(tenantCode)) {
            tenantCode = WebPluginUtils.DEFAULT_TENANT_CODE;
        }
        return WebPluginUtils.getTenantInfoListByTenantCode(tenantCode);
    }

    @PutMapping("/switch")
    @ApiOperation("切换租户")
    public TenantInfoExt switchTenant(@RequestBody SwitchTenantExt ext) {
       return WebPluginUtils.switchTenant(ext);
    }

    @PutMapping("/env/switch")
    @ApiOperation("切换环境")
    public void switchEnv(@RequestBody SwitchTenantExt ext) {
        WebPluginUtils.switchEnv(ext);
    }

    @GetMapping("/config")
    @ApiOperation("获取租户配置，无需登录")
    public List<TenantConfigExt> getConfig() {
        // 先从缓存获取
        String tenantConfigRedisKey = TenantUtils.getTenantConfigRedisKey();
        if (redisTemplate.hasKey(tenantConfigRedisKey)) {
            return (List<TenantConfigExt>)redisTemplate.opsForValue().get(tenantConfigRedisKey);
        }
        List<TenantConfigExt> exts =WebPluginUtils.getTenantConfig();
        redisTemplate.opsForValue().set(tenantConfigRedisKey,exts,5,TimeUnit.MINUTES);
        return exts;
    }

    @GetMapping("/config/all")
    @ApiOperation("获取租户配置，无需登录")
    public List<TenantInfoConfigExt> getAllConfig() {
        // 先从缓存获取
        String tenantConfigRedisKey = TenantUtils.getAllTenantConfigRedisKey();
        if (redisTemplate.hasKey(tenantConfigRedisKey)) {
            return (List<TenantInfoConfigExt>)redisTemplate.opsForValue().get(tenantConfigRedisKey);
        }
        List<TenantInfoConfigExt> exts =WebPluginUtils.getAllTenantConfig();
        redisTemplate.opsForValue().set(tenantConfigRedisKey,exts,5, TimeUnit.MINUTES);
        return exts;
    }


    @GetMapping("/tenant/app/all")
    @ApiOperation("获取租户配置，无需登录")
    public List<TenantApplicationExt> getAllTenantApp() {
        String key = "appSynchronizeToAmdb";
        // 先从缓存获取
        List<TenantInfoConfigExt> allConfig = this.getAllConfig();
        // 找到配置，需要获取应用的配置
        List<TenantCommonExt> commonExts = Lists.newArrayList();
        for (TenantInfoConfigExt ext : allConfig) {
            for(Entry<String,List<TenantConfigExt>> map: ext.getConfigs().entrySet()) {
                if(CollectionUtils.isEmpty(map.getValue())) {
                    continue;
                }
                List<TenantConfigExt> collect = map.getValue().stream().filter(configExt -> key.equals(configExt.getConfigKey())).collect(Collectors.toList());
                if(CollectionUtils.isEmpty(collect)){
                    continue;
                }
                TenantConfigExt tenantConfigExt = collect.get(0);
                if(Boolean.parseBoolean(tenantConfigExt.getConfigValue())) {
                    TenantCommonExt tenantCommonExt = new TenantCommonExt();
                    tenantCommonExt.setTenantCode(ext.getTenantCode());
                    tenantCommonExt.setTenantAppKey(ext.getTenantAppKey());
                    tenantCommonExt.setEnvCode(map.getKey());
                    commonExts.add(tenantCommonExt);
                }
            }
        }

        return  applicationService.getAllTenantApp(commonExts);
    }


}
