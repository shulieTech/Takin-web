package io.shulie.takin.web.ext.api.e2e;

import io.shulie.takin.web.ext.entity.e2e.E2eBaseStorageParam;
import io.shulie.takin.web.ext.entity.e2e.E2eBaseStorageRequest;
import io.shulie.takin.web.ext.entity.e2e.E2eExceptionConfigInfoExt;
import org.pf4j.ExtensionPoint;

import java.util.List;
import java.util.Map;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.ext.api.e2e
 * @ClassName: InspectionExtApi
 * @Description: e2e
 * @Date: 2021/10/11 16:05
 */
public interface InspectionExtApi extends ExtensionPoint {

    /**
     * 根据租户、环境 获取 e2e 模块的异常配置信息
     */
    List<E2eExceptionConfigInfoExt> getExceptionConfig(Long tenantId,String envCode, String...service);

    /**
     * 瓶颈计算
     */
    Map<Integer, Integer> bottleneckCompute(E2eBaseStorageRequest baseStorageRequest, List<E2eExceptionConfigInfoExt> configs);

    /**
     * 瓶颈入库
     */
    Long bottleneckStorage(E2eBaseStorageParam baseStorageParam);
}
