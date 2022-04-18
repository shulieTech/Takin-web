package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.AgentReportEntity;
import org.apache.ibatis.annotations.Param;

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
     * 查询需要删除的记录数
     *
     * @param date yyyy-MM-dd HH:mm:ss
     */
    @InterceptorIgnore(tenantLine = "true")
    Integer selectDeleteCount(String date);

    /**
     * 查询需要删除的主键ID集合
     *
     * @param date   yyyy-MM-dd HH:mm:ss
     * @param start  开始下标
     * @param length 查询长度
     */
    @InterceptorIgnore(tenantLine = "true")
    List<Long> selectDeleteIds(@Param("date") String date, @Param("start") Integer start, @Param("length") Integer length);


    /**
     * 根据主键id去删除数据
     *
     * @param ids
     */
    @InterceptorIgnore(tenantLine = "true")
    void deleteByIds(List<Long> ids);
}

