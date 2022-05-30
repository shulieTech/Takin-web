package io.shulie.takin.web.entrypoint.controller.pressure;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import io.shulie.takin.adapter.api.constant.EntrypointUrl;
import io.shulie.takin.cloud.biz.notify.CloudNotifyParam;
import io.shulie.takin.cloud.biz.notify.CloudNotifyProcessor;
import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.data.mapper.mysql.PressureTaskCallbackMapper;
import io.shulie.takin.cloud.data.model.mysql.PressureTaskCallbackEntity;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/" + EntrypointUrl.MODULE_CALLBACK + "/" + EntrypointUrl.METHOD_ENGINE_CALLBACK_TASK_RESULT_NOTIFY)
@Slf4j
public class PressureCallbackController {

    @Value("${pressure.callback.record: true}")
    private boolean recordCallback;

    @Value("${pressure.callback.ignore.type: 100,200,303}")
    private List<Integer> excludeFilterType;

    @Resource
    private PressureTaskCallbackMapper callbackMapper;
    @Resource
    private List<CloudNotifyProcessor<?>> processorList;

    @Resource(name = "cloudCallbackThreadPool")
    private ExecutorService cloudCallbackThreadPool;

    private Map<CallbackType, CloudNotifyProcessor<?>> processorMap;

    private final Predicate<CallbackType> excludeFilter = type -> excludeFilterType.contains(type.getCode());

    @PostMapping(EntrypointUrl.METHOD_ENGINE_CALLBACK_TASK_RESULT_NOTIFY)
    @ApiOperation(value = "cloud回调状态")
    public <T extends CloudNotifyParam> ResponseResult<?> taskResultNotify(@RequestBody T param) {
        CloudNotifyProcessor processor = processorMap.get(param.getType());
        if (processor != null) {
            cloudCallbackThreadPool.execute(() -> {
                Date now = new Date();
                String resourceId = processor.process(param);
                CallbackType type = processor.type();
                if (recordCallback && !excludeFilter.test(type) && StringUtils.isNotBlank(resourceId)) {
                    callbackMapper.insert(new PressureTaskCallbackEntity(resourceId,
                        type.getCode(), JSON.toJSONString(param), now));
                }
            });
        }
        return ResponseResult.success("SUCCESS");
    }

    @PostConstruct
    private void init() {
        processorMap = new HashMap<>(8);
        if (!CollectionUtils.isEmpty(processorList)) {
            processorList.forEach(processor -> processorMap.putIfAbsent(processor.type(), processor));
        }
    }
}
