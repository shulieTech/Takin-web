package io.shulie.takin.web.biz.service.linkmanage;

import java.util.Map;
import java.util.List;

import com.pamirs.takin.entity.domain.vo.entracemanage.ApiCreateVo;
import com.pamirs.takin.entity.domain.vo.entracemanage.ApiUpdateVo;
import com.pamirs.takin.entity.domain.vo.entracemanage.EntranceApiVo;

import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.vo.application.ApplicationApiManageVO;

/**
 * @author vernon
 * @date 2020/4/2 13:10
 */
public interface ApplicationApiService {

    Response registerApi(Map<String, List<String>> register);

    /**
     * 老版
     *
     * @param appName
     * @return
     */
    Response pullApi(String appName);

    /**
     * 新版
     *
     * @param appName
     * @return
     */
    Map<String, List<String>> pullApiV1(String appName);

    Response delete(String id);

    Response query(EntranceApiVo vo);

    Response update(ApiUpdateVo vo);

    Response create(ApiCreateVo vo);

    Response queryDetail(String id);

    Map<Long, List<ApplicationApiManageVO>> selectListGroupByAppId();

}
