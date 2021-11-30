package io.shulie.takin.web.biz.task;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.common.TaskHelper;
import io.shulie.takin.web.biz.service.report.ReportTaskService;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt.TenantEnv;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 汇总应用 机器数 风险机器数
 * 10
 */
@Component
@Slf4j
public class CalcApplicationSummaryTask implements SimpleTask{

    @Autowired
    private ReportTaskService reportTaskService;

    @Autowired
    @Qualifier("calcApplicationSummaryJobThreadPool")
    private ThreadPoolExecutor calcApplicationSummaryJobThreadPool;

    @Autowired
    @Qualifier("fastDebugThreadPool")
    private ThreadPoolExecutor fastDebugThreadPool;

    @Override
    public void runTask() {
        long start = System.currentTimeMillis();
        if (WebPluginUtils.isOpenVersion()) {
            if (!ConfigServerHelper.getBooleanValueByKey(ConfigServerKeyEnum.TAKIN_REPORT_OPEN_TASK)) {
                return;
            }
            //final TenantCommonExt commonExt = WebPluginUtils.setTraceTenantContext(
            //    WebPluginUtils.traceTenantId(), WebPluginUtils.traceTenantAppKey(), WebPluginUtils.traceEnvCode(),
            //    WebPluginUtils.traceTenantCode(),
            //    ContextSourceEnum.JOB.getCode());
            // 私有化 + 开源 根据 报告id进行分片
            List<Long> reportIds = reportTaskService.getRunningReport();
            log.info("获取正在压测中的报告:{}", JsonHelper.bean2Json(reportIds));
            for (Long reportId : reportIds) {
                // 开始数据层分片
                if (reportId % getPartTotal() == getMachieId()) {
                    //fastDebugThreadPool.execute(() -> {
                    //    WebPluginUtils.setTraceTenantContext(commonExt);
                    //    reportTaskService.calcApplicationSummary(reportId);
                    //});
                    reportTaskService.calcApplicationSummary(reportId);
                }
            }
        }else {
            // saas 根据租户进行分片
            List<TenantInfoExt> tenantInfoExts = WebPluginUtils.getTenantInfoList();
            for (TenantInfoExt ext : tenantInfoExts) {
                // 开始数据层分片
                if (ext.getTenantId() % getPartTotal() == getMachieId()) {
                    // 根据环境 分线程
                    for (TenantEnv e : ext.getEnvs()) {
                        final TenantCommonExt commonExt = WebPluginUtils.setTraceTenantContext(
                            ext.getTenantId(), ext.getTenantAppKey(), e.getEnvCode(), ext.getTenantCode(),
                            ContextSourceEnum.JOB.getCode());
                        if (!ConfigServerHelper.getBooleanValueByKey(ConfigServerKeyEnum.TAKIN_REPORT_OPEN_TASK)) {
                            continue;
                        }
                        this.calcApplicationSummary(commonExt);
                    }
                }
            }
        }
        log.info("CalcApplicationSummaryTask 执行时间:{}", System.currentTimeMillis() - start);
    }


    private void calcApplicationSummary(TenantCommonExt commonExt) {
        WebPluginUtils.setTraceTenantContext(commonExt);
        List<Long> reportIds = reportTaskService.getRunningReport();
        if (CollectionUtils.isEmpty(reportIds)){
            log.debug("暂无压测中的报告！");
            return;
        }
        log.info("获取租户【{}】【{}】正在压测中的报告:{}", commonExt.getTenantId(), commonExt.getEnvCode(),
            JsonHelper.bean2Json(reportIds));
        for (Long reportId : reportIds) {
            // 开始数据层分片
            fastDebugThreadPool.execute(() -> {
                WebPluginUtils.setTraceTenantContext(commonExt);
                reportTaskService.calcApplicationSummary(reportId);
            });
        }
    }


}