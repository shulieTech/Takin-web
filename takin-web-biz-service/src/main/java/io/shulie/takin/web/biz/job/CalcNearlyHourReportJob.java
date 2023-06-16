package io.shulie.takin.web.biz.job;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.shulie.takin.web.biz.common.AbstractSceneTask;
import io.shulie.takin.web.biz.service.report.ReportTaskService;
import io.shulie.takin.web.biz.threadpool.ThreadPoolUtil;
import io.shulie.takin.web.common.pojo.dto.SceneTaskDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class CalcNearlyHourReportJob extends AbstractSceneTask {

    @Autowired
    private ReportTaskService reportTaskService;

    private static Map<Long, AtomicInteger> runningTasks = new ConcurrentHashMap<>();
    private static AtomicInteger EMPTY = new AtomicInteger();

    @XxlJob("calcNearlyHourReportJob")
    public void execute() {
        try {
            this.execute_ext();
        } catch (Throwable e) {
            // 捕捉全部异常,防止任务异常，导致esjob有问题
            log.error("io.shulie.takin.web.biz.job.calcNearlyHourReportJob#execute error" + ExceptionUtils.getStackTrace(e));
        }
    }

    //取过去一个小时的report，计算水位数据
    public void execute_ext() {
        long start = System.currentTimeMillis();
        List<Long> reportIds = reportTaskService.nearlyHourReportIds(20);
        if (CollectionUtils.isEmpty(reportIds)) {
            log.warn("calcNearlyHourReportJob current not running pressure task!!!");
            return;
        }
        for (Long reportId : reportIds) {
            // 开始数据层分片
            if (reportId % XxlJobHelper.getShardTotal() == XxlJobHelper.getShardIndex()) {
                Object task = runningTasks.putIfAbsent(reportId, EMPTY);
                if (task == null) {
                    ThreadPoolUtil.getReportTpsThreadPool().execute(() -> {
                        try {
                            reportTaskService.calcMachineDate(reportId);
                        } catch (Throwable e) {
                            log.error("execute calcNearlyHourReportJob occured error. reportId={}", reportId, e);
                        } finally {
                            runningTasks.remove(reportId);
                        }
                    });
                }
            }
        }
        log.debug("calcNearlyHourReportJob 执行时间:{}", System.currentTimeMillis() - start);
    }

    @Override
    protected void runTaskInTenantIfNecessary(SceneTaskDto tenantTask, Long reportId) {

    }

    @Override
    protected Map<Long, AtomicInteger> getRunningTasks() {
        return runningTasks;
    }
}
