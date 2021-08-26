package io.shulie.takin.web.biz.service.perfomanceanaly;

import java.util.List;

import io.shulie.takin.web.biz.pojo.input.PerformanceBaseDataCreateInput;

/**
 * @author qianshui
 * @date 2020/11/4 下午2:44
 */
public interface PerformanceBaseDataService {

    /**
     * 缓存接收到的Agent数据
     *
     * @param input Agent数据
     */
    void cache(PerformanceBaseDataCreateInput input);

    /**
     * 获取进程名称列表
     *
     * @param reportId 报告主键
     * @param appName  应用名称
     * @return 进程名称列表
     */
    List<String> getProcessName(Long reportId, String appName);

    void clearData(Integer time);
}
