package io.shulie.takin.web.data.dao.application;

import java.util.List;

import io.shulie.takin.web.data.param.application.ApplicationWhiteListCreateParam;
import io.shulie.takin.web.data.param.application.ApplicationWhiteListUpdateParam;

/**
 * @author fanxx
 * @date 2020/11/9 9:19 下午
 */
public interface ApplicationWhiteListDAO {

    int insert(ApplicationWhiteListCreateParam param);

    int insertBatch(List<ApplicationWhiteListCreateParam> paramList);

    int allocationUser(ApplicationWhiteListUpdateParam param);

}
