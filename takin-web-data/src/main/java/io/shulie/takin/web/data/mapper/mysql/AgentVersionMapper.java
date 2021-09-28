package io.shulie.takin.web.data.mapper.mysql;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.AgentVersionEntity;
import io.shulie.takin.web.data.result.fastagentaccess.AgentVersionDetailResult;
import org.apache.ibatis.annotations.Param;

/**
 * agent版本管理(AgentVersion)表数据库 mapper
 *
 * @author ocean_wll
 * @date 2021-08-11 19:44:47
 */
public interface AgentVersionMapper extends BaseMapper<AgentVersionEntity> {

    /**
     * 获取所有大版本列表
     * @param tenantId 租户id
     * @param envCode 环境编码
     * @return 字符串集合
     */
    List<String> findFirstVersionList(@Param("tenantId") long tenantId,@Param("envCode") String envCode);

    /**
     * 获取所有版本集合
     *
     * @return 字符串集合
     */
    List<String> findAllVersionList(@Param("tenantId") long tenantId,@Param("envCode") String envCode);

    /**
     * 查询最新版本的agent信息
     *
     * @return AgentVersionDetailResult对象
     */
    AgentVersionDetailResult findMaxVersionAgent(@Param("tenantId") long tenantId,@Param("envCode") String envCode);
}

