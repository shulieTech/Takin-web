package io.shulie.takin.web.data.dao.script;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import io.shulie.takin.web.common.vo.script.ScriptDeployFinishDebugVO;
import io.shulie.takin.web.data.model.mysql.ScriptDebugEntity;
import io.shulie.takin.web.data.param.scriptmanage.PageScriptDebugParam;
import io.shulie.takin.web.data.param.scriptmanage.SaveOrUpdateScriptDebugParam;
import io.shulie.takin.web.data.result.scriptmanage.ScriptDebugListResult;

/**
 * 脚本调试表(ScriptDebug)表数据库 dao
 *
 * @author liuchuan
 * @since 2021-05-10 16:58:55
 */
public interface ScriptDebugDAO extends IService<ScriptDebugEntity> {

    /**
     * 该脚本实例下, 是否有未完成的调试
     *
     * @param scriptDeployId 脚本发布id
     * @return 是否
     */
    boolean hasUnfinished(Long scriptDeployId);

    /**
     * 通过脚本发布id, 状态列表, 分页查询
     *
     * @param pageScriptDebugParam 查询参数
     * @return 调试记录列表
     */
    IPage<ScriptDebugEntity> pageByScriptDeployIdAndStatusList(PageScriptDebugParam pageScriptDebugParam);

    /**
     * 根据脚本发布ids, 查询对应的调试是否结束
     *
     * @param scriptDeployIds 脚本发布ids
     * @return 调试结束结果列表
     */
    List<ScriptDeployFinishDebugVO> listScriptDeployFinishDebugResult(List<Long> scriptDeployIds);

    /**
     * 列出未完成的脚本
     *
     * @param scriptDeployId 脚本实例id
     * @return 未完成的脚本
     */
    List<ScriptDebugListResult> listUnfinished(Long scriptDeployId);

    /**
     * 更新调试记录
     *
     * @param saveOrUpdateScriptDebugParam 入参
     * @return 是否成功
     */
    boolean updateById(SaveOrUpdateScriptDebugParam saveOrUpdateScriptDebugParam);

}
