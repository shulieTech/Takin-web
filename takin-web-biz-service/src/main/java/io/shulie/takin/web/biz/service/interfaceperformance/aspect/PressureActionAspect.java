package io.shulie.takin.web.biz.service.interfaceperformance.aspect;

import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceConfigCreateInput;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceConfigQueryRequest;
import io.shulie.takin.web.biz.pojo.request.scene.SceneDetailResponse;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformancePressureService;
import io.shulie.takin.web.common.vo.interfaceperformance.PerformanceConfigVO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @Author: vernon
 * @Date: 2022/5/24 14:42
 * @Description:
 */
@Component
@Aspect
public class PressureActionAspect {
    @Autowired
    PerformancePressureService performancePressureService;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Pointcut("@annotation(Action)")
    public void afterReturning() {
    }

    private void doAction(Object arg, Object response, Action.ActionEnum action) {
        switch (action) {
            case create:
                PerformanceConfigCreateInput createIn = (PerformanceConfigCreateInput) arg;
                performancePressureService.add(createIn.getPressureConfigRequest());
                break;
            case delete:
                Long deleteIn = (Long) arg;
                performancePressureService.delete(deleteIn);
                break;
            case update:
                PerformanceConfigCreateInput updateIn = (PerformanceConfigCreateInput) arg;
                performancePressureService.update(updateIn.getPressureConfigRequest());
                break;
            case detail:
                Long detailIn = (Long) arg;
                PerformanceConfigQueryRequest request = new PerformanceConfigQueryRequest();
                request.setId(detailIn);
                ResponseResult<SceneDetailResponse> result = performancePressureService.query(request);
                SceneDetailResponse data = result.getData();
                if (response.getClass().isAssignableFrom(PerformanceConfigVO.class)) {
                    // TODO: 2022/5/24  需要吧打data放进响应体
                }
                break;
            case select:
                break;


        }
    }

    @AfterReturning(value = "afterReturning()", returning = "obj")
    public Object afterReturning(ProceedingJoinPoint point, Object obj) {
        Action annotation = ((MethodSignature) point
                .getSignature())
                .getMethod()
                .getAnnotation(Action.class);
        if (!Objects.isNull(annotation)) {
            Action.ActionEnum actionEnum = annotation.action();
            Object $arg1 = point.getArgs()[0];
            try {
                doAction($arg1, obj, actionEnum);
            } catch (Throwable t) {
                logger.error("pressure config aspect error:{}", t.getCause());
            }
        }
        return obj;
    }
}
