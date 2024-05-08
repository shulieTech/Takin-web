package io.shulie.takin.web.biz.job;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.biz.service.report.ReportTaskService;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.common.pojo.bo.agent.ReportMockBO;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Description agent心跳数据清理任务
 * @Author ocean_wll
 * @Date 2021/11/18 2:30 下午
 */
@Component
@ElasticSchedulerJob(jobName = "calcReportMockFallbackJob", cron = "*/15 * * * * ? *", description = "延迟1min汇总reportMock数据")
@Slf4j
public class CalcReportMockFallbackJob implements SimpleJob {
    @Autowired
    private RedisClientUtils redisClientUtils;
    @Autowired
    private ReportTaskService reportTaskService;
    @Autowired
    private ReportService reportService;

    @Override
    public void execute(ShardingContext shardingContext) {
        Set<ZSetOperations.TypedTuple<String>> sets = redisClientUtils.zReverseRangeWithScores(WebRedisKeyConstant.REPORT_MOCK_CALC_FALLBACK, 0, -1);
        if(CollectionUtils.isEmpty(sets)) {
            return;
        }
        List<String> removeReportIds = new ArrayList<>();
        sets.forEach(typedTuple -> {
            try {
                if(typedTuple.getScore() <= System.currentTimeMillis()) {
                    removeReportIds.add(typedTuple.getValue());
                }
                ReportMockBO mockBO = JSON.parseObject(typedTuple.getValue(), ReportMockBO.class);
                TenantCommonExt commonExt = WebPluginUtils.fillTenantCommonExt(mockBO.getTenantId(), mockBO.getEnvCode());
                WebPluginUtils.setTraceTenantContext(mockBO.getTenantId(),mockBO.getTenantAppKey(),mockBO.getEnvCode(),commonExt.getTenantCode(), ContextSourceEnum.JOB.getCode());
                ReportDetailOutput detailOutput = reportService.getSimpleReportByReportId(mockBO.getReportId());
                if(detailOutput == null) {
                    return;
                }
                reportTaskService.calcMockSummary(mockBO.getReportId(), DateUtil.parseDateTime(detailOutput.getStartTime()), detailOutput.getTenantId(), detailOutput.getEnvCode());
            } catch (Exception e) {

            }
        });
        if(CollectionUtils.isNotEmpty(removeReportIds)) {
            redisClientUtils.zRemove(WebRedisKeyConstant.REPORT_MOCK_CALC_FALLBACK, removeReportIds.toArray());
        }
    }
}
