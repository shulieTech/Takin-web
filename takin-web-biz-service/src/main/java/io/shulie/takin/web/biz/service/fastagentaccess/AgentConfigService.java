package io.shulie.takin.web.biz.service.fastagentaccess;

import java.util.List;
import java.util.Map;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.bo.ConfigListQueryBO;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentConfigCreateRequest;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentConfigEffectQueryRequest;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentConfigQueryRequest;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentConfigUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentDynamicConfigQueryRequest;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentConfigEffectListResponse;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentConfigListResponse;
import io.shulie.takin.web.data.result.application.AgentConfigDetailResult;

/**
 * agent配置管理(AgentConfig)service
 *
 * @author ocean_wll
 * @date 2021-08-12 18:54:56
 */
public interface AgentConfigService {

    /**
     * 批量创建配置
     *
     * @param createRequestList 配置集合
     */
    void batchInsert(List<AgentConfigCreateRequest> createRequestList);

    /**
     * 获取所有的全局配置 key为en_key,value为zh_key
     *
     * @return map集合
     */
    List<Map<String, String>> getAllGlobalKey();

    /**
     * 获取当前用户所有的应用名
     *
     * @param keyword 关键词
     * @return applicationName集合
     */
    List<String> getAllApplication(String keyword);

    /**
     * 检查中文key是否重复
     *
     * @param zhKey 中文key
     * @return true重复，false不重复
     */
    Boolean checkZhKey(String zhKey);

    /**
     * 检查英文key是否重复
     *
     * @param enKey 英文key
     * @return true重复，false不重复
     */
    Boolean checkEnKey(String enKey);

    /**
     * 根据配置的主键id获取可选值列表，文本类型返回null，单选类型返回可选值
     *
     * @param id 配置主键
     * @return 数据集合
     */
    List<String> getValueOption(Long id);

    /**
     * 列表查询
     *
     * @param queryRequest 查询条件
     * @return AgentConfigListResponse集合
     */
    List<AgentConfigListResponse> list(AgentConfigQueryRequest queryRequest);

    /**
     * 更新配置
     *
     * @param updateRequest 更新请求
     */
    void update(AgentConfigUpdateRequest updateRequest);

    /**
     * 废弃应用配置以全局配置为准
     *
     * @param id 需要废弃的应用配置id
     */
    void useGlobal(Long id);

    /**
     * agent端：配置参数查询
     *
     * @param queryRequest 查询条件
     * @return AgentConfigAgentListResponse集合
     */
    Map<String, String> agentConfig(AgentDynamicConfigQueryRequest queryRequest);

    /**
     * 查询配置列表
     *
     * @param queryBO 查询条件
     * @return Map，key为key，value为AgentConfigDetailResult对象
     */
    Map<String, AgentConfigDetailResult> getConfigList(ConfigListQueryBO queryBO);

    /**
     * 配置生效列表查询
     *
     * @param queryRequest 查询条件
     * @return PagingList<AgentConfigEffectListResponse>
     */
    PagingList<AgentConfigEffectListResponse> queryConfigEffectList(AgentConfigEffectQueryRequest queryRequest);

    /**
     * 根据key和应用名查询对应的生效记录
     *
     * @param enkey       key
     * @param projectName 应用名
     * @return AgentConfigDetailResult对象
     */
    AgentConfigDetailResult queryByEnKeyAndProject(String enkey, String projectName);

}
