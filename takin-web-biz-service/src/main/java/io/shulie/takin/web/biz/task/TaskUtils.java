package io.shulie.takin.web.biz.task;

import io.shulie.takin.web.common.job.TakinTask;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.biz.task
 * @ClassName: TaskUtils
 * @Description: TODO
 * @Date: 2021/11/29 23:03
 */
public class TaskUtils implements TaskTopicConstant {

    static TaskPool taskPool;

    @Autowired
    public void setPluginManager(TaskPool taskPool) {
        TaskUtils.taskPool = taskPool;
    }

    /**
     * 创建报告任务
     * @param reportId
     */
    public static void createReportMessage(Long reportId) {
        TakinTask takinTask = new TakinTask();
        takinTask.setId(reportId);
        takinTask.setTopic(REPORT_TOPIC);
        takinTask.setMessage("报告队列");
        takinTask.setId(reportId);
        WebPluginUtils.fillTenantCommonExt(takinTask);
        taskPool.addTask(takinTask);
    }
}
