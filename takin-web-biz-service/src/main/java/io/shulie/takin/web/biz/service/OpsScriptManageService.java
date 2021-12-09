package io.shulie.takin.web.biz.service;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.param.opsscript.OpsScriptParam;
import io.shulie.takin.web.data.result.opsscript.OpsExecutionVO;
import io.shulie.takin.web.data.result.opsscript.OpsScriptDetailVO;
import io.shulie.takin.web.data.result.opsscript.OpsScriptVO;

/**
 * 运维脚本主表(OpsScriptManage)表服务接口
 *
 * @author caijy
 * @since 2021-06-16 10:41:26
 */
public interface OpsScriptManageService {

    PagingList<OpsScriptVO> page(OpsScriptParam param);

    Boolean add(OpsScriptParam param);

    Boolean update(OpsScriptParam param);

    OpsScriptDetailVO detail(OpsScriptParam param);

    Boolean delete(OpsScriptParam param);

    Boolean execute(OpsScriptParam param);

    OpsExecutionVO getExcutionLog(String id);

    String getExcutionResult(String id);

}
