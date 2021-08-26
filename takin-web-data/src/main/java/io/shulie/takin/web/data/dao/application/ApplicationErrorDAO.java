package io.shulie.takin.web.data.dao.application;

import java.util.List;

import io.shulie.takin.web.data.param.application.ApplicationErrorQueryParam;
import io.shulie.takin.web.data.result.application.ApplicationErrorResult;

/**
 * @author fanxx
 * @date 2020/10/16 11:02 上午
 */
public interface ApplicationErrorDAO {

    List<ApplicationErrorResult> selectErrorList(ApplicationErrorQueryParam param);

}
