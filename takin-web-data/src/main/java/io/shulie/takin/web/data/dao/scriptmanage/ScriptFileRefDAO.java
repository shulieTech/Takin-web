package io.shulie.takin.web.data.dao.scriptmanage;

import java.util.List;

import io.shulie.takin.web.data.result.scriptmanage.ScriptFileRefResult;

/**
 * @author zhaoyong
 */
public interface ScriptFileRefDAO {
    /**
     * 根据脚本发布id获取文件id列表
     * @param scriptDeployId
     * @return
     */
    List<ScriptFileRefResult> selectFileIdsByScriptDeployId(Long scriptDeployId);

    /**
     * 批量删除关联关系
     * @param scriptFileRefIds
     */
    void deleteByIds(List<Long> scriptFileRefIds);

    /**
     * 根据脚本发布实例id批量查询关联关系
     * @param scriptDeployIds
     * @return
     */
    List<ScriptFileRefResult> selectFileIdsByScriptDeployIds(List<Long> scriptDeployIds);

    /**
     *
     * @param fileIds
     * @param id
     */
    void createScriptFileRefs(List<Long> fileIds, Long id);

}
