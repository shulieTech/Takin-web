package io.shulie.takin.web.biz.aspect;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisTemplate;

import io.shulie.takin.web.biz.annotation.ActivityCache;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityResponse;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityInfoQueryRequest;

@Component
@Aspect
@Slf4j
public class ActivityCacheAspect {
    @Resource
    private RedisTemplate redisTemplate;
    public final static String REDIS_PREFIX_KEY = "tro_web_activity_detail::";

    @Pointcut("@annotation(io.shulie.takin.web.biz.annotation.ActivityCache)")
    private void pointcut() {
    }

    @Around(value = "pointcut()")
    public Object advice(ProceedingJoinPoint point) throws Throwable {
        ActivityResponse activityResponse;
        ActivityCache cache = ((MethodSignature)point.getSignature()).getMethod().getAnnotation(ActivityCache.class);
        int expireTime = cache.expireTime();
        ActivityInfoQueryRequest request = (ActivityInfoQueryRequest)point.getArgs()[0];
        if (Objects.isNull(request)) {
            return null;
        }
        if (Objects.isNull(request.getFlowTypeEnum())) {
            return point.proceed();
        }
        String key = REDIS_PREFIX_KEY + request.getActivityId() + "::" + request.getFlowTypeEnum().getCode();
        Object cacheResponse = redisTemplate.opsForValue().get(key);
        if (Objects.isNull(cacheResponse)) {
            activityResponse = (ActivityResponse)point.proceed();
            redisTemplate.opsForValue().set(key, JSON.toJSONString(activityResponse), expireTime, TimeUnit.SECONDS);
        } else {
            activityResponse = JSON.parseObject(cacheResponse.toString(), ActivityResponse.class);
            log.debug("ActivityCacheAspect.advice()...[{}]", request.getActivityId());
        }
        return activityResponse;
    }
}
