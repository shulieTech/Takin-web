package io.shulie.takin.web.biz.service.dashboard;

import io.shulie.takin.web.biz.pojo.response.dashboard.UserWorkBenchResponse;

public interface WorkBenchService {
    /**
     * 获取用户的工作台统计信息
     *
     * @return 统计信息
     */
    UserWorkBenchResponse getWorkBench();
}
