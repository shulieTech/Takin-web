package io.shulie.takin.cloud.biz.notify.processor.file;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.notify.CloudNotifyProcessor;
import io.shulie.takin.cloud.common.constants.SceneStartCheckConstants;
import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.utils.json.JsonHelper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class FilePositionProcessor extends AbstractIndicators implements CloudNotifyProcessor<FilePositionParam> {

    @Resource
    private ReportDao reportDao;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public String process(FilePositionParam param) {
        FilePosition data = param.getData();
        Long jobId = data.getTaskId();
        ReportResult report = reportDao.selectByJobId(jobId);
        if (Objects.isNull(report)) {
            return null;
        }
        String resourceId = report.getResourceId();
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(PressureStartCache.getResourceKey(resourceId)))) {
            return resourceId;
        }
        // 设置位点
        String fileName = data.getFileName();
        String podNum = data.getPodNum();
        String key = String.format(SceneStartCheckConstants.SCENE_KEY, report.getSceneId());
        String field = String.format(SceneStartCheckConstants.FILE_POD_FIELD_KEY, fileName, podNum);
        Map<String, Long> value = new HashMap<>(8);
        value.put("startPosition", data.getStartPosition());
        value.put("readPosition", data.getReadPosition());
        value.put("endPosition", data.getEndPosition());
        redisTemplate.opsForHash().put(key, field, JsonHelper.bean2Json(value));
        return resourceId;
    }

    @Override
    public CallbackType type() {
        return CallbackType.FILE_USAGE;
    }
}
