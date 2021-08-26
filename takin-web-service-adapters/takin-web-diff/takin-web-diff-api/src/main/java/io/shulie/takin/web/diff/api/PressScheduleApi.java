package io.shulie.takin.web.diff.api;

/**
 * @author shiyajian
 * create: 2020-10-19
 */
public interface PressScheduleApi {

    /**
     * 开启压测调度任务
     * 私有化版本：本地调用k8s模块的启动；
     * 云版本：通过http调用远端
     */
    void startSchedule();
}
