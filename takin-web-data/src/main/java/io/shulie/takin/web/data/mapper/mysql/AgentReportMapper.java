package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.AgentReportEntity;

/**
 * 探针心跳数据(AgentReport)表数据库 mapper
 *
 * @author ocean_wll
 * @date 2021-11-09 20:35:32
 */
public interface AgentReportMapper extends BaseMapper<AgentReportEntity> {

    /**
     * 不存在则插入，存在则更新
     *
     * @param agentReportEntity AgentReportEntity对象
     * @return 影响记录数
     */
    Integer insertOrUpdate(AgentReportEntity agentReportEntity);
}

