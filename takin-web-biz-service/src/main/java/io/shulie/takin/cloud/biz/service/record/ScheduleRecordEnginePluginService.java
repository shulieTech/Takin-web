package io.shulie.takin.cloud.biz.service.record;

import java.util.List;

/**
 * 调度记录引擎插件接口
 *
 * @author lipeng
 * @date 2021-01-13 11:29 上午
 */
public interface ScheduleRecordEnginePluginService {

    /**
     * 保存调度记录引擎插件信息
     *
     * @param recordId
     * @param enginePluginFilePath
     */
    void saveScheduleRecordEnginePlugins(Long recordId, List<String> enginePluginFilePath);
}
