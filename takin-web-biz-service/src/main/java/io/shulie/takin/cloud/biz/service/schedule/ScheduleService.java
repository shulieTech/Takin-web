package io.shulie.takin.cloud.biz.service.schedule;

import io.shulie.takin.cloud.ext.content.enginecall.ScheduleInitParamExt;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleRunRequest;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStartRequestExt;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStopRequestExt;

/**
 * @author 莫问
 * @date 2020-05-12
 */
public interface ScheduleService {

    /**
     * 启动调度
     *
     * @param request 请求参数
     */
    void startSchedule(ScheduleStartRequestExt request);

    /**
     * 停止调度
     *
     * @param request 请求参数
     */
    void stopSchedule(ScheduleStopRequestExt request);

    /**
     * 调度运行
     *
     * @param request 请求参数
     */
    void runSchedule(ScheduleRunRequest request);

    /**
     * 初始化回调
     *
     * @param param 请求参数
     */
    void initScheduleCallback(ScheduleInitParamExt param);
}
