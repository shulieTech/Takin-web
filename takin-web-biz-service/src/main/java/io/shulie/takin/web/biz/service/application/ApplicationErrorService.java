package io.shulie.takin.web.biz.service.application;

import com.pamirs.takin.entity.domain.vo.ApplicationVo;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationErrorQueryInput;
import io.shulie.takin.web.biz.pojo.output.application.ApplicationErrorOutput;
import io.shulie.takin.web.biz.pojo.output.application.ApplicationExceptionOutput;
import io.shulie.takin.web.data.result.application.ApplicationListResult;

import java.util.List;
import java.util.Map;

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

    Map<Long, ApplicationVo> batchGetApplicationStatus(List<ApplicationListResult> records);
}
