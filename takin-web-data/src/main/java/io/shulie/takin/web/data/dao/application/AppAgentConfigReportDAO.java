package io.shulie.takin.web.data.dao.application;

import io.shulie.takin.web.data.param.application.CreateAppAgentConfigReportParam;
import io.shulie.takin.web.data.param.application.UpdateAppAgentConfigReportParam;
import io.shulie.takin.web.data.result.application.AppAgentConfigReportDetailResult;

import java.util.List;

/**
 * agent配置上报详情(AppAgentConfigReport)表数据库 dao 层
 *
 * @author 南风
 * @date 2021-09-28 17:27:22
 */
public interface AppAgentConfigReportDAO {


    void batchSave(List<CreateAppAgentConfigReportParam> list) ;

    List<AppAgentConfigReportDetailResult> listByAppId(Long appId);

    void batchUpdate(List<UpdateAppAgentConfigReportParam> list);

    List<AppAgentConfigReportDetailResult> listByBizType(Integer BizType,String appName);

}

