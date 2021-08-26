package io.shulie.takin.web.data.dao.application;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.DsModelWithBLOBs;
import io.shulie.takin.web.data.param.application.ApplicationDsCreateParam;
import io.shulie.takin.web.data.param.application.ApplicationDsDeleteParam;
import io.shulie.takin.web.data.param.application.ApplicationDsEnableParam;
import io.shulie.takin.web.data.param.application.ApplicationDsQueryParam;
import io.shulie.takin.web.data.param.application.ApplicationDsUpdateParam;
import io.shulie.takin.web.data.param.application.ApplicationDsUpdateUserParam;
import io.shulie.takin.web.data.result.application.ApplicationDsResult;

/**
 * @author fanxx
 * @date 2020/11/9 8:26 下午
 */
public interface ApplicationDsDAO {
    int insert(ApplicationDsCreateParam createParam);

    int update(ApplicationDsUpdateParam updateParam);

    /**
     * 启动状态
     *
     * @return
     */
    int enable(ApplicationDsEnableParam enableParam);

    int delete(ApplicationDsDeleteParam deleteParam);

    ApplicationDsResult queryByPrimaryKey(Long id);

    List<ApplicationDsResult> queryList(ApplicationDsQueryParam param);

    int allocationUser(ApplicationDsUpdateUserParam param);

    List<DsModelWithBLOBs> selectByAppIdForAgent(Long applicationId);

    List<DsModelWithBLOBs> getAllEnabledDbConfig(Long applicationId);
}
