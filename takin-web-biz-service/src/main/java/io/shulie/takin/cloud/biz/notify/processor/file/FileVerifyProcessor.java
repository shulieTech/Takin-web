package io.shulie.takin.cloud.biz.notify.processor.file;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.notify.CloudNotifyProcessor;
import io.shulie.takin.cloud.biz.service.async.CloudAsyncService;
import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.model.callback.script.ResultReport;
import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.web.biz.checker.StartConditionChecker.CheckStatus;
import io.shulie.takin.web.common.util.RedisClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import static io.shulie.takin.cloud.data.util.PressureStartCache.FILE_VERIFY_MESSAGE;
import static io.shulie.takin.cloud.data.util.PressureStartCache.FILE_VERIFY_STATUS;
import static io.shulie.takin.cloud.data.util.PressureStartCache.FILE_VERIFY_SUCCESS_EVENT;
import static io.shulie.takin.cloud.data.util.PressureStartCache.getPressureFileKey;

@Service
public class FileVerifyProcessor extends AbstractIndicators implements CloudNotifyProcessor<FileVerifyParam> {

    @Resource
    private RedisClientUtil redisClientUtil;

    @Resource
    private CloudAsyncService cloudAsyncService;

    private static final String VERIFY_TYPE = "verify";

    @Override
    public String process(FileVerifyParam param) {
        ResultReport.Data data = param.getData();
        String attach = data.getAttach();
        if (StringUtils.isBlank(attach)) {
            return "";
        }
        String pressureFileKey = getPressureFileKey(attach);
        String pressureFileLockKey = pressureFileKey + ":" + VERIFY_TYPE;
        if (redisClientUtil.lockExpire(pressureFileLockKey, "1", 1, TimeUnit.DAYS)) {
            if (Boolean.TRUE.equals(data.getResult())) {
                redisClientUtil.hmset(pressureFileKey, FILE_VERIFY_STATUS, CheckStatus.SUCCESS.ordinal());
                doVerifySuccessEvent(attach);
            } else {
                String message = data.getMessage();
                redisClientUtil.hmset(pressureFileKey, FILE_VERIFY_STATUS, CheckStatus.FAIL.ordinal());
                redisClientUtil.hmset(pressureFileKey, FILE_VERIFY_MESSAGE, message);
                cloudAsyncService.checkFileFailed(attach, message);
            }
        }
        return attach;
    }

    private void doVerifySuccessEvent(String attach) {
        Event event = new Event();
        event.setEventName(FILE_VERIFY_SUCCESS_EVENT);
        event.setExt(attach);
        eventCenterTemplate.doEvents(event);
    }

    @Override
    public CallbackType type() {
        return CallbackType.SCRIPT_RESULT;
    }
}
