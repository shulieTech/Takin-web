package io.shulie.takin.web.data.dao.agentupgradeonline;

import io.shulie.takin.web.data.result.application.ApplicationTagRefDetailResult;

import java.util.List;

/**
 * 应用标签表(ApplicationTagRef)表数据库 dao 层
 *
 * @author ocean_wll
 * @date 2021-11-09 20:48:38
 */
public interface ApplicationTagRefDAO {


    List<ApplicationTagRefDetailResult> getList(List<Long> applicationIds);
}

