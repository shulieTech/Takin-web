package io.shulie.takin.cloud.biz.notify.processor.file;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import io.shulie.takin.adapter.api.entrypoint.script.ScriptFileApi;
import io.shulie.takin.adapter.api.model.request.script.ScriptVerifyRequest;
import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.notify.CloudNotifyProcessor;
import io.shulie.takin.cloud.biz.service.async.CloudAsyncService;
import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.model.callback.file.ProgressReport;
import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.web.biz.checker.StartConditionChecker.CheckStatus;
import io.shulie.takin.web.common.util.RedisClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import static io.shulie.takin.cloud.data.util.PressureStartCache.FILE_DOWNLOAD_MESSAGE;
import static io.shulie.takin.cloud.data.util.PressureStartCache.FILE_DOWNLOAD_STATUS;
import static io.shulie.takin.cloud.data.util.PressureStartCache.FILE_DOWNLOAD_SUCCESS_EVENT;
import static io.shulie.takin.cloud.data.util.PressureStartCache.FILE_VERIFY_MESSAGE;
import static io.shulie.takin.cloud.data.util.PressureStartCache.FILE_VERIFY_STATUS;
import static io.shulie.takin.cloud.data.util.PressureStartCache.getPressureFileKey;
import static io.shulie.takin.cloud.data.util.PressureStartCache.getScriptDownloadFilesKey;
import static io.shulie.takin.cloud.data.util.PressureStartCache.getScriptVerifyKey;

@Service
public class FileDownloadProcessor extends AbstractIndicators implements CloudNotifyProcessor<FileDownloadParam> {

    @Resource
    private ScriptFileApi scriptFileApi;

    @Resource
    private RedisClientUtil redisClientUtil;

    @Resource
    private CloudAsyncService cloudAsyncService;

    private static final String DOWNLOAD_TYPE = "download";

    // 检测文件是否下载完成
    @Override
    public String process(FileDownloadParam param) {
        ProgressReport.Data data = param.getData();
        String attach = data.getAttach();
        if (StringUtils.isBlank(attach)) {
            return "";
        }
        String path = data.getPath();
        String downloadFilesKey = getScriptDownloadFilesKey(attach);
        String pressureFileKey = getPressureFileKey(attach);
        String pressureFileLockKey = pressureFileKey + ":" + DOWNLOAD_TYPE;
        if (!redisClientUtil.setContainsValue(downloadFilesKey, path) || redisClientUtil.hasLockKey(pressureFileLockKey)) {
            return "";
        }
        Boolean complete = data.getComplete();
        if (!complete) {
            String message = data.getMessage();
            int failStatus = CheckStatus.FAIL.ordinal();
            redisClientUtil.lockExpire(pressureFileLockKey, String.valueOf(System.currentTimeMillis()), 1, TimeUnit.DAYS);
            redisClientUtil.hmset(pressureFileKey, FILE_DOWNLOAD_STATUS, failStatus);
            redisClientUtil.hmset(pressureFileKey, FILE_DOWNLOAD_MESSAGE, message);
            cloudAsyncService.checkFileFailed(attach, message);
            return attach;
        }
        Long count = redisClientUtil.remSetValueAndReturnCount(downloadFilesKey, path);
        if (count == 0) {
            // 文件下载成功
            int successStatus = CheckStatus.SUCCESS.ordinal();
            if (redisClientUtil.lockExpire(pressureFileLockKey, String.valueOf(System.currentTimeMillis()), 1, TimeUnit.DAYS)) {
                redisClientUtil.hmset(pressureFileKey, FILE_DOWNLOAD_STATUS, successStatus);
                redisClientUtil.del(downloadFilesKey);
                doDownloadSuccessEvent(attach);
                // 文件下载完成，触发文件校验
                String scriptVerifyKey = getScriptVerifyKey(attach);
                ScriptVerifyRequest request = (ScriptVerifyRequest)redisClientUtil.get(scriptVerifyKey);
                redisClientUtil.del(scriptVerifyKey);
                try {
                    scriptFileApi.verify(request);
                    redisClientUtil.hmset(pressureFileKey, FILE_VERIFY_STATUS, CheckStatus.PENDING.ordinal());
                } catch (Exception e) {
                    // 文件校验失败
                    String message = e.getMessage();
                    redisClientUtil.hmset(pressureFileKey, FILE_VERIFY_STATUS, CheckStatus.FAIL.ordinal());
                    redisClientUtil.hmset(pressureFileKey, FILE_VERIFY_MESSAGE, message);
                    cloudAsyncService.checkFileFailed(attach, message);
                }
            }
        }
        return attach;
    }

    private void doDownloadSuccessEvent(String attach) {
        Event event = new Event();
        event.setEventName(FILE_DOWNLOAD_SUCCESS_EVENT);
        event.setExt(attach);
        eventCenterTemplate.doEvents(event);
    }

    @Override
    public CallbackType type() {
        return CallbackType.FILE_RESOURCE_PROGRESS;
    }
}
