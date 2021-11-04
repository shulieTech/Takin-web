package io.shulie.takin.web.entrypoint.controller.tenant;

import java.util.List;

import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.util.TenantUtils;
import io.shulie.takin.web.ext.entity.tenant.SwitchTenantExt;
import io.shulie.takin.web.ext.entity.tenant.TenantConfigExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
    public List<TenantConfigExt> getConfig(@RequestParam(value = "tenantAppKey",required = false) String tenantAppKey,
        @RequestParam(value = "envCode",required = false) String envCode) {

        // 先从缓存获取
        String tenantConfigRedisKey = TenantUtils.getTenantConfigRedisKey();
        if (redisTemplate.hasKey(tenantConfigRedisKey)) {
            return (List<TenantConfigExt>)redisTemplate.opsForValue().get(tenantConfigRedisKey);
        }
        List<TenantConfigExt> exts =WebPluginUtils.getTenantConfig(tenantAppKey,envCode);
        redisTemplate.opsForValue().set(tenantConfigRedisKey,exts);
        return exts;
    }

}
