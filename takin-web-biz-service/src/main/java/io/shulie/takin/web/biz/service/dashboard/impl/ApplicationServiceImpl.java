package io.shulie.takin.web.biz.service.dashboard.impl;

import javax.annotation.Resource;

import com.pamirs.takin.common.constant.AppSwitchEnum;
import io.shulie.takin.web.biz.cache.AgentConfigCacheManager;
import io.shulie.takin.web.biz.constant.DashboardExceptionCode;
import io.shulie.takin.web.biz.pojo.response.dashboard.AppPressureSwitchSetResponse;
import io.shulie.takin.web.biz.pojo.response.dashboard.ApplicationSwitchStatusResponse;
import io.shulie.takin.web.biz.service.dashboard.ApplicationService;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service(value = "dashboard-ApplicationServiceImpl")
public class ApplicationServiceImpl implements ApplicationService {

    private static final String NEED_VERIFY_USER_MAP = "NEED_VERIFY_USER_MAP";
    private static final String PRADAR_SWITCH_STATUS = "PRADAR_SWITCH_STATUS_";
    private static final String PRADAR_SWITCH_STATUS_VO = "PRADAR_SWITCH_STATUS_VO_";

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    AgentConfigCacheManager agentConfigCacheManager;
    @Resource
    io.shulie.takin.web.biz.service.ApplicationService takinApplicationService;

    @Override
    public ApplicationSwitchStatusResponse getUserAppSwitchInfo() {
        return new ApplicationSwitchStatusResponse() {{
            setSwitchStatus(takinApplicationService.getUserSwitchStatusForVo());
        }};
    }

    /**
     * 设置用户的全局压测开关
     *
     * @return 开关状态
     */
    @Override
    public AppPressureSwitchSetResponse setUserAppPressureSwitch(Boolean enable) {
        //全局开关只保留 开/关
        if (enable == null) {
            throw new TakinWebException(DashboardExceptionCode.DEFAULT, "开关状态不能为空");
        }
        Long customerId = WebPluginUtils.getCustomerId();
        String realStatus = getUserPressureSwitchFromRedis(customerId);
        AppPressureSwitchSetResponse result = new AppPressureSwitchSetResponse();
        if (realStatus.equals(AppSwitchEnum.CLOSING.getCode()) || realStatus.equals(AppSwitchEnum.OPENING.getCode())) {
            result.setSwitchStutus(realStatus);
        } else {
            String status = (enable ? AppSwitchEnum.OPENED : AppSwitchEnum.CLOSED).getCode();
            String voStatus = (enable ? AppSwitchEnum.OPENING : AppSwitchEnum.CLOSING).getCode();
            //开关状态、开关开启、关闭的时间存放在redis
            redisTemplate.opsForValue().set(PRADAR_SWITCH_STATUS + customerId, status);
            redisTemplate.opsForValue().set(PRADAR_SWITCH_STATUS_VO + customerId, voStatus);
            redisTemplate.opsForHash().put(NEED_VERIFY_USER_MAP, String.valueOf(customerId), System.currentTimeMillis());

            result.setSwitchStutus(voStatus);
        }
        agentConfigCacheManager.evictPressureSwitch();
        return result;
    }

    /**
     * 从redis获取用户全局开关状态
     *
     * @param customerId 用户主键
     * @return 开关状态
     */
    private String getUserPressureSwitchFromRedis(long customerId) {
        // 返回缓存中的值
        Object status = redisTemplate.opsForValue().get(PRADAR_SWITCH_STATUS_VO + customerId);
        if (status != null) {
            return (String)status;
        }
        // 默认返回开启
        else {
            redisTemplate.opsForValue().set(PRADAR_SWITCH_STATUS_VO + customerId, AppSwitchEnum.OPENED.getCode());
            redisTemplate.opsForValue().set(PRADAR_SWITCH_STATUS + customerId, AppSwitchEnum.OPENED.getCode());
            return AppSwitchEnum.OPENED.getCode();
        }
    }
}
