package io.shulie.takin.web.biz.aspect;

import com.alibaba.fastjson.JSON;
import io.shulie.takin.web.biz.annotation.ActivityCache;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityInfoQueryRequest;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
@Slf4j
public class ActivityCacheAspect {
    @Autowired
    private RedisTemplate redisTemplate;
    public final static String REDIS_PREFIX_KEY = "tro_web_activity_detail::";

    @Pointcut("@annotation(io.shulie.takin.web.biz.annotation.ActivityCache)")
    private void pointcut() {
    }

    @Around(value = "pointcut()")
    public Object advice(ProceedingJoinPoint point) throws Throwable {
        ActivityResponse activityResponse;
        ActivityCache cache = ((MethodSignature) point.getSignature()).getMethod().getAnnotation(ActivityCache.class);
        int expireTime = cache.expireTime();
        ActivityInfoQueryRequest request = (ActivityInfoQueryRequest) point.getArgs()[0];
        if (Objects.isNull(request)) {
            return null;
        }
        if (Objects.isNull(request.getFlowTypeEnum())) {
            return point.proceed();
        }
        String key = REDIS_PREFIX_KEY + request.getActivityId() + "::" + request.getFlowTypeEnum().getCode();
        Object cacheResponse = redisTemplate.opsForValue().get(key);
        if (Objects.isNull(cacheResponse)) {
            activityResponse = (ActivityResponse) point.proceed();
            redisTemplate.opsForValue().set(key, JSON.toJSONString(activityResponse), expireTime, TimeUnit.SECONDS);
        } else {
            activityResponse = JSON.parseObject(cacheResponse.toString(), ActivityResponse.class);
            log.info("ActivityCacheAspect.advice()...[{}]", request.getActivityId());
        }
        return activityResponse;
    }
}
