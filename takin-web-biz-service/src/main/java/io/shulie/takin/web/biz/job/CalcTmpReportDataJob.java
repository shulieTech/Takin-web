package io.shulie.takin.web.biz.job;

import com.xxl.job.core.handler.annotation.XxlJob;
import io.shulie.takin.web.biz.common.AbstractSceneTask;
import io.shulie.takin.web.biz.service.report.ReportTaskService;
import io.shulie.takin.web.biz.threadpool.ThreadPoolUtil;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.common.pojo.dto.SceneTaskDto;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class CalcTmpReportDataJob  extends AbstractSceneTask {
    @Autowired
    private ReportTaskService reportTaskService;

    private static Map<Long, AtomicInteger> runningTasks = new ConcurrentHashMap<>();
    private static AtomicInteger EMPTY = new AtomicInteger();

    @XxlJob("calcTmpReportDataJobExecute")
    public void execute() {
        try {
            this.execute_ext();
        } catch (Throwable e) {
            // 捕捉全部异常,防止任务异常，导致esjob有问题
            log.error("io.shulie.takin.web.biz.job.CalcTmpReportDataJob#execute error" + ExceptionUtils.getStackTrace(e));
        }
    }

    public void execute_ext() {
        long start = System.currentTimeMillis();
        final Boolean openVersion = WebPluginUtils.isOpenVersion();
        List<SceneTaskDto> taskDtoList = getTaskFromRedis();
        if (taskDtoList == null) {
            log.warn("current not running pressure task!!!");
            return;
        }
        if (openVersion) {
            for (SceneTaskDto taskDto : taskDtoList) {
                Long reportId = taskDto.getReportId();
                // 开始数据层分片
                Object task = runningTasks.putIfAbsent(reportId, EMPTY);
                if (task == null) {
                    ThreadPoolUtil.getReportTpsThreadPool().execute(() -> {
                        try {
                            reportTaskService.calcTmpReportData(reportId);
                        } catch (Throwable e) {
                            log.error("execute CalcTmpReportDataJob occured error. reportId={}", reportId, e);
                        } finally {
                            runningTasks.remove(reportId);
                        }
                    });
                }
//                if (reportId % XxlJobHelper.getShardTotal() == XxlJobHelper.getShardIndex()) {
//
//                }
            }
        } else {
            this.runTask_ext(taskDtoList);
        }
        log.debug("CalcTmpReportDataJob 执行时间:{}", System.currentTimeMillis() - start);
    }

    @Override
    protected void runTaskInTenantIfNecessary(SceneTaskDto tenantTask, Long reportId) {
        //将任务放入线程池
        ThreadPoolUtil.getReportTpsThreadPool().execute(() -> {
            try {
                tenantTask.setSource(ContextSourceEnum.JOB.getCode());
                WebPluginUtils.setTraceTenantContext(tenantTask);
                reportTaskService.calcTmpReportData(tenantTask.getReportId());
            } catch (Throwable e) {
                log.error("execute CalcTmpReportDataJob occured error. reportId={}", reportId, e);
            } finally {
                AtomicInteger currentRunningThreads = runningTasks.get(tenantTask.getTenantId());
                if (currentRunningThreads != null) {
                    currentRunningThreads.decrementAndGet();
                }

            }
        });
    }

    @Override
    protected Map<Long, AtomicInteger> getRunningTasks() {
        return runningTasks;
    }
}
