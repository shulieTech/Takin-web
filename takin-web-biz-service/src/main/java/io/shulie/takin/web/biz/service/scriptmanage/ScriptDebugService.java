package io.shulie.takin.web.biz.service.scriptmanage;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.PageScriptDebugRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.PageScriptDebugRequestRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptDebugDoDebugRequest;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptDebugDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptDebugListResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptDebugRequestListResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptDebugResponse;

/**
 * 脚本调试表(ScriptDebug)表服务接口
 *
 * @author liuchuan
 * @since 2021-05-10 17:13:18
 */
public interface ScriptDebugService {

    /**
     * 调试
     *
     * @param scriptDebugDoDebugRequest 所需参数
     * @return 调试记录实例或错误
     */
    ScriptDebugResponse debug(ScriptDebugDoDebugRequest scriptDebugDoDebugRequest);

    /**
     * 通过调试记录id获得调试记录详情
     *
     * @param scriptDebugId 调试记录id
     * @return 调试记录详情
     */
    ScriptDebugDetailResponse getById(Long scriptDebugId);

    /**
     * 通过脚本发布id, 分页查询完成的调试记录
     *
     * @param pageScriptDebugRequest 查询参数
     * @return 分页数据
     */
    PagingList<ScriptDebugListResponse> pageFinishedByScriptDeployId(PageScriptDebugRequest pageScriptDebugRequest);

    /**
     * 查询调试记录下的请求流量明细
     *
     * @param pageScriptDebugRequestRequest 查询参数
     * @return 分页数据
     */
    PagingList<ScriptDebugRequestListResponse> pageScriptDebugRequest(PageScriptDebugRequestRequest pageScriptDebugRequestRequest);

}
