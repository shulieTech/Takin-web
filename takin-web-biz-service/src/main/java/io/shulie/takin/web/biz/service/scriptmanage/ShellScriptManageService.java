package io.shulie.takin.web.biz.service.scriptmanage;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.input.scriptmanage.ShellExecuteInput;
import io.shulie.takin.web.biz.pojo.input.scriptmanage.ShellScriptManageCreateInput;
import io.shulie.takin.web.biz.pojo.input.scriptmanage.ShellScriptManagePageQueryInput;
import io.shulie.takin.web.biz.pojo.input.scriptmanage.ShellScriptManageUpdateInput;
import io.shulie.takin.web.biz.pojo.output.scriptmanage.shell.ScriptExecuteOutput;
import io.shulie.takin.web.biz.pojo.output.scriptmanage.shell.ShellScriptManageContentOutput;
import io.shulie.takin.web.biz.pojo.output.scriptmanage.shell.ShellScriptManageDetailOutput;
import io.shulie.takin.web.biz.pojo.output.scriptmanage.shell.ShellScriptManageExecuteOutput;
import io.shulie.takin.web.biz.pojo.output.scriptmanage.shell.ShellScriptManageOutput;

/**
 * @author 无涯
 * @date 2020/12/8 4:23 下午
 */
public interface ShellScriptManageService {

    /**
     * 创建shell脚本
     *
     * @return
     */
    Long createScriptManage(ShellScriptManageCreateInput input);

    /**
     * 修改shell脚本
     */
    String updateScriptManage(ShellScriptManageUpdateInput input);

    /**
     * 删除shell脚本
     */
    void deleteScriptManage(Long scriptId);

    /**
     * 查询脚本实例详情
     *
     * @return
     */
    ShellScriptManageDetailOutput getScriptManageDetail(Long scriptId);

    /**
     * 分页查询脚本列表
     *
     * @return
     */
    PagingList<ShellScriptManageOutput> pageQueryScriptManage(ShellScriptManagePageQueryInput input);

    /**
     * 执行脚本，传实例id
     *
     * @return
     */
    ShellScriptManageExecuteOutput execute(Long scriptManageDeployId);

    /**
     * 根据版本获取脚本内容
     *
     * @return
     */
    ShellScriptManageContentOutput getShellScriptManageContent(Long scriptId, Integer version);

    /**
     * 获取执行记录
     *
     * @return
     */
    PagingList<ScriptExecuteOutput> getExecuteResult(ShellExecuteInput input);

}
