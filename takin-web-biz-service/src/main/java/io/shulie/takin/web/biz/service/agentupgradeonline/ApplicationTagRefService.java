package io.shulie.takin.web.biz.service.agentupgradeonline;

import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.web.data.result.application.ApplicationTagRefDetailResult;

import java.util.List;

/**
 * 应用标签表(ApplicationTagRef)service
 *
 * @author ocean_wll
 * @date 2021-11-09 20:09:44
 */
public interface ApplicationTagRefService {


    List<ApplicationTagRefDetailResult> getList(List<Long> applicationIds);

    List<ApplicationTagRefDetailResult> getList(Long tagId);

    List<SelectVO> getListByTenant();

}
