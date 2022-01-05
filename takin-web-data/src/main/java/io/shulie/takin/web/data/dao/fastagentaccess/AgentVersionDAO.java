package io.shulie.takin.web.data.dao.fastagentaccess;

import java.util.List;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.param.fastagentaccess.AgentVersionQueryParam;
import io.shulie.takin.web.data.param.fastagentaccess.CreateAgentVersionParam;
import io.shulie.takin.web.data.param.fastagentaccess.UpdateAgentVersionParam;
import io.shulie.takin.web.data.result.fastagentaccess.AgentVersionDetailResult;
import io.shulie.takin.web.data.result.fastagentaccess.AgentVersionListResult;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/8/12 4:03 下午
 */
public interface AgentVersionDAO {

    /**
     * 探针列表分页
     *
     * @param queryParam 分页参数
     * @return 分页列表
     */
    PagingList<AgentVersionListResult> page(AgentVersionQueryParam queryParam);

    /**
     * 根据版本号去查询记录
     *
     * @param version 版本号
     * @return AgentVersionDetailResult对象
     */
    AgentVersionDetailResult selectByVersion(String version);

    /**
     * 根据版本号进行删除
     *
     * @param version 版本号
     */
    void deleteByVersion(String version);

    /**
     * 插入一条记录
     *
     * @param createParam CreateAgentVersionParam实体对象
     * @return 影响的记录数
     */
    Integer insert(CreateAgentVersionParam createParam);

    /**
     * 更新记录
     *
     * @param updateParam UpdateAgentVersionParam实体对象
     * @return 是否更新成功
     */
    Boolean update(UpdateAgentVersionParam updateParam);

    /**
     * 获取大版本集合
     *
     * @return 字符串集合
     */
    List<String> findFirstVersionList();

    /**
     * 获取所有版本列表
     *
     * @return 字符串集合
     */
    List<String> findAllVersionList();

    /**
     * 获取最新版本的agent文件
     *
     * @return AgentVersionDetailResult对象
     */
    AgentVersionDetailResult findMaxVersionAgent();


}
