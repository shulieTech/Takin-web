package io.shulie.takin.web.biz.service.scriptmanage;

import io.shulie.takin.cloud.common.enums.PressureSceneEnum;

import java.util.List;

public interface ScriptDeployService {
    /**
     * 调试启动前，压测启动前
     * 进行文件缺失校验：csv文件、jar包
     * csv文件根据文件名一个个进行匹配
     * jar包，只要有一个JavaRequest的请求，就必须有jar包，jar包对不对不管
     * @param masterId
     * @return
     */
    List<String> checkLeakFile(Long masterId, PressureSceneEnum pressureSceneEnum);
}
