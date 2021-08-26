package io.shulie.takin.web.data.dao.application;

import io.shulie.takin.web.data.param.application.ApplicationApiCreateParam;
import io.shulie.takin.web.data.param.application.ApplicationApiUpdateUserParam;

/**
 * @author fanxx
 * @date 2020/11/4 5:53 下午
 */
public interface ApplicationApiDAO {

    int insert(ApplicationApiCreateParam param);

    public int allocationUser(ApplicationApiUpdateUserParam param);
}
