package io.shulie.takin.web.biz.service.linkManage;

import java.util.List;
import java.util.Map;

import com.pamirs.takin.entity.domain.vo.entracemanage.ApiCreateVo;
import com.pamirs.takin.entity.domain.vo.entracemanage.ApiUpdateVo;
import com.pamirs.takin.entity.domain.vo.entracemanage.EntranceApiVo;
import io.shulie.takin.web.common.common.Response;

/**
 * @author  vernon
 * @date 2020/4/2 13:10
 */
public interface ApplicationApiService {

    Response registerApi(Map<String, List<String>> register);

    Response pullApi(String appName);

    Response delete(String id);

    Response query(EntranceApiVo vo);

    Response update(ApiUpdateVo vo);

    Response create(ApiCreateVo vo);

    Response queryDetail(String id);
}
