package io.shulie.takin.web.data.dao.fastagentaccess;

import java.util.List;

import io.shulie.takin.web.data.param.fastagentaccess.AgentConfigQueryParam;
import io.shulie.takin.web.data.param.fastagentaccess.AgentProjectConfigQueryParam;
import io.shulie.takin.web.data.param.fastagentaccess.CreateAgentConfigParam;
import io.shulie.takin.web.data.param.fastagentaccess.UpdateAgentConfigParam;
import io.shulie.takin.web.data.result.application.AgentConfigDetailResult;

/**
 * agent配置管理(AgentConfig)表数据库 dao 层
 *
 * @author ocean_wll
 * @date 2021-08-12 18:57:00
 */
public interface AgentConfigDAO {

    /**
     * 插入一条配置记录
     *
     * @param createParam CreateAgentConfigParam对象
     */
    void insert(CreateAgentConfigParam createParam);

    /**
     * 批量插入
     *
     * @param paramList 新增配置集合
     */
    void batchInsert(List<CreateAgentConfigParam> paramList);

    /**
     * 根据英文key去数据库中查询对应的记录
     *
     * @param enKey 配置key
     * @return AgentConfigDetailResult
     */
    AgentConfigDetailResult findGlobalConfigByEnKey(String enKey);

    /**
     * 根据中文key去数据库中查询对应的记录
     *
     * @param zhKey 配置key
     * @return AgentConfigDetailResult
     */
    AgentConfigDetailResult findGlobalConfigByZhKey(String zhKey);

    /**
     * 根据key集合去数据库中查询对应的记录
     *
     * @param enKeyList 英文key集合
     * @return AgentConfigDetailResult集合
     */
    List<AgentConfigDetailResult> findGlobalConfigByEnKeyList(List<String> enKeyList);

    /**
     * 根据key集合去数据库中查询对应的记录
     *
     * @param zhKeyList 英文key集合
     * @return AgentConfigDetailResult集合
     */
    List<AgentConfigDetailResult> findGlobalConfigByZhKeyList(List<String> zhKeyList);

    /**
     * 获取所有的全局配置集合
     *
     * @return AgentConfigDetailResult集合
     */
    List<AgentConfigDetailResult> getAllGlobalConfig();

    /**
     * 根据主键id查询记录
     *
     * @param id 主键id
     * @return AgentConfigDetailResult
     */
    AgentConfigDetailResult findById(Long id);

    /**
     * 根据主键id查询记录
     *
     * @param queryParam 查询条件
     * @return AgentConfigDetailResult
     */
    AgentConfigDetailResult findProjectConfig(AgentProjectConfigQueryParam queryParam);

    /**
     * 查询全局的配置列表
     *
     * @param queryParam 查询对象
     * @return AgentConfigDetailResult集合
     */
    List<AgentConfigDetailResult> findGlobalList(AgentConfigQueryParam queryParam);

    /**
     * 查询应用的配置列表
     *
     * @param queryParam 查询对象
     * @return AgentConfigDetailResult集合
     */
    List<AgentConfigDetailResult> findProjectList(AgentConfigQueryParam queryParam);

    /**
     * 更新配置值
     *
     * @param updateParam 更新对象
     * @return 影响的记录数
     */
    Integer updateConfigValue(UpdateAgentConfigParam updateParam);

    /**
     * 根据id删除对应的记录
     *
     * @param id 主键id
     * @return 影响记录数
     */
    Integer deleteById(Long id);

}

