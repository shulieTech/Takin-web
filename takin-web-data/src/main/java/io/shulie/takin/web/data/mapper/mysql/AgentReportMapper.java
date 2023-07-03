package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.AgentReportEntity;

import java.util.List;

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

    /**
     * 删除指定时间之前的数据
     *
     * @param date yyyy-MM-dd HH:mm:ss
     */
    @InterceptorIgnore(tenantLine = "true")
    void selfDelete(String date);

    /**
     * 直接删除偶尔会有锁表问题，所以先查询，再删除
     * @param date
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    List<Long> selectIdsByUpdateTime(String date);
}

