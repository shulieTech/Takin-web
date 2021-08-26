package io.shulie.takin.web.biz.service.async;

import java.util.List;

import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageWrapperResp.SceneSlaRefResp;
import io.shulie.takin.web.data.param.perfomanceanaly.PerformanceBaseDataParam;

/**
 * 异步数据
 *
 * @author qianshui
 * @date 2020/11/9 下午8:59
 */
public interface AsyncService {

    void savePerformanceBaseData(PerformanceBaseDataParam param);

    void monitorCpuMemory(Long sceneId, Long reportId, List<String> appNames, List<SceneSlaRefResp> stopSla, List<SceneSlaRefResp> warnSla);
}
