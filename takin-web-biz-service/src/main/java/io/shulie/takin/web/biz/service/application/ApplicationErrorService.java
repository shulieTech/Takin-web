package io.shulie.takin.web.biz.service.application;

import java.util.List;

import io.shulie.takin.web.biz.pojo.output.application.ApplicationErrorOutput;
import io.shulie.takin.web.biz.pojo.output.application.ApplicationExceptionOutput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationErrorQueryInput;

/**
 * @author shiyajian
 * create: 2020-10-15
 */
public interface ApplicationErrorService {

    /**
     * 应用异常列表
     */
    List<ApplicationErrorOutput> list(ApplicationErrorQueryInput queryRequest);

    /**
     * 根据ids
     *
     * @return
     */
    List<ApplicationExceptionOutput> getAppException(List<String> appNames);

}
