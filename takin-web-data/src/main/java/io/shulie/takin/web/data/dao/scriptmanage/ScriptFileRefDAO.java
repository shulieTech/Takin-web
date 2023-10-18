package io.shulie.takin.web.data.dao.scriptmanage;

import io.shulie.takin.web.data.model.mysql.ScriptFileRefEntity;
import io.shulie.takin.web.data.result.scriptmanage.ScriptFileRefResult;

import java.util.List;

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
     * 删除原来的绑定关系
     * @param scriptDeployId
     */
    void deleteByScriptDeployId(Long scriptDeployId);

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


    /**
     * 根据文件Id获取脚本实例id
     * @param fileManageId
     * @return
     */
    ScriptFileRefResult selectByFileManageId(Long fileManageId);

    /**
     * 根据文件Ids获取脚本实例id
     * @param fileManageIds
     * @return
     */
    List<ScriptFileRefResult> selectByFileManageIds(List<Long> fileManageIds);


    void batchUpdateIds(List<ScriptFileRefEntity> collect);
}
