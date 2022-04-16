package io.shulie.takin.web.biz.service;

import java.util.List;

import io.shulie.takin.web.biz.pojo.response.ApplicationEntryResponse;

/**
 * 压测相关服务层
 *
 * @author liuchuan
 * @date 2021/10/25 2:40 下午
 */
public interface PressureService {

    /**
     * 根据报告id, 获得应用入口相关列表
     *
     * @param jobId 任务id
     * @return 应用入口相关列表
     */
    List<ApplicationEntryResponse> getApplicationEntriesByJobId(Long jobId);

}
