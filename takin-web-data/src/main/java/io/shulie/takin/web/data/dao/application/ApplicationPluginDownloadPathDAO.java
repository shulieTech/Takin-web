package io.shulie.takin.web.data.dao.application;

import io.shulie.takin.web.common.enums.application.ApplicationAgentPathValidStatusEnum;
import io.shulie.takin.web.data.param.application.CreateApplicationPluginDownloadPathParam;
import io.shulie.takin.web.data.param.application.UpdateApplicationPluginDownloadPathParam;
import io.shulie.takin.web.data.result.application.ApplicationPluginDownloadPathDetailResult;

/**
 * 探针根目录(ApplicationPluginDownloadPath)表数据库 dao 层
 *
 * @author 南风
 * @date 2021-11-10 16:12:07
 */
public interface ApplicationPluginDownloadPathDAO {

    ApplicationPluginDownloadPathDetailResult queryDetailByCustomerId();

    void createConfig(CreateApplicationPluginDownloadPathParam createParam);

    void updateConfig(UpdateApplicationPluginDownloadPathParam updateParam);

    void saveValidState(Boolean state,Long recordId);

    ApplicationPluginDownloadPathDetailResult queryDetailByCustomerId(ApplicationAgentPathValidStatusEnum statusEnum);
}

