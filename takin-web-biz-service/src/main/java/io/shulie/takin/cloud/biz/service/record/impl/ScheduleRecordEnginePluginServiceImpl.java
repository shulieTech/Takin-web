package io.shulie.takin.cloud.biz.service.record.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import io.shulie.takin.cloud.biz.service.record.ScheduleRecordEnginePluginService;
import io.shulie.takin.cloud.data.mapper.mysql.ScheduleRecordEnginePluginMapper;
import io.shulie.takin.cloud.data.model.mysql.ScheduleRecordEnginePluginRefEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 调度记录引擎插件实现
 *
 * @author lipeng
 * @date 2021-01-13 11:31 上午
 */
@Slf4j
@Service
public class ScheduleRecordEnginePluginServiceImpl extends ServiceImpl<ScheduleRecordEnginePluginMapper, ScheduleRecordEnginePluginRefEntity> implements ScheduleRecordEnginePluginService {
    /**
     * 保存调度记录引擎插件信息
     *
     * @param recordId
     * @param enginePluginFilePath
     */
    @Override
    public void saveScheduleRecordEnginePlugins(Long recordId, List<String> enginePluginFilePath) {
        if (recordId == null) {
            log.warn("recordId不能为空。");
            return;
        }

        //没有额外引擎插件加载
        if (enginePluginFilePath == null || enginePluginFilePath.size() == 0) {
            return;
        }

        //组装数据保存
        List<ScheduleRecordEnginePluginRefEntity> infos = Lists.newArrayList();
        enginePluginFilePath.forEach(item -> {
            ScheduleRecordEnginePluginRefEntity info = new ScheduleRecordEnginePluginRefEntity();
            info.setScheduleRecordId(recordId);
            info.setEnginePluginFilePath(item);
            infos.add(info);
        });
        this.saveBatch(infos);

    }
}
