package io.shulie.takin.web.biz.service;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.param.application.ApplicationPluginsConfigParam;
import io.shulie.takin.web.data.result.application.ApplicationPluginsConfigVO;

import java.util.List;

/**
 * (ApplicationPluginsConfig)表服务接口
 *
 * @author caijy
 * @since 2021-05-18 17:22:39
 */
public interface ApplicationPluginsConfigService {

    ApplicationPluginsConfigVO getById(Long id);

    PagingList<ApplicationPluginsConfigVO> getPageByParam(ApplicationPluginsConfigParam param);

    Boolean add(ApplicationPluginsConfigParam param);

    Boolean addBatch(List<ApplicationPluginsConfigParam> params);

    Boolean updateBatch(List<ApplicationPluginsConfigParam> params);

    Boolean update(ApplicationPluginsConfigParam param);

    List<ApplicationPluginsConfigVO> getListByParam(ApplicationPluginsConfigParam param);

    void init();
}
