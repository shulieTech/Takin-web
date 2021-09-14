package io.shulie.takin.web.biz.service.async;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.entity.domain.dto.report.ReportDetailDTO;
import io.shulie.takin.cloud.common.bean.RuleBean;
import io.shulie.takin.cloud.common.bean.sla.SlaBean;
import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import io.shulie.takin.cloud.open.req.report.WarnCreateReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageIdReq;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageWrapperResp.SceneSlaRefResp;
import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;
import io.shulie.takin.web.biz.service.report.impl.ReportApplicationService;
import io.shulie.takin.web.biz.service.risk.util.DateUtil;
import io.shulie.takin.web.common.util.RedisHelper;
import io.shulie.takin.web.data.dao.baseserver.BaseServerDao;
import io.shulie.takin.web.data.dao.perfomanceanaly.PerformanceBaseDataDAO;
import io.shulie.takin.web.data.param.baseserver.BaseServerParam;
import io.shulie.takin.web.data.param.perfomanceanaly.PerformanceBaseDataParam;
import io.shulie.takin.web.data.result.baseserver.BaseServerResult;
import io.shulie.takin.web.diff.api.scenetask.SceneTaskApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author qianshui
 * @date 2020/11/9 下午9:01
 */
@Service
@Slf4j
public class AsyncServiceImpl implements AsyncService {

    @Autowired
    private PerformanceBaseDataDAO performanceBaseDataDAO;

    @Autowired
    private ReportApplicationService reportApplicationService;

    @Autowired
    private BaseServerDao baseServerDao;

    @Autowired
    private SceneTaskApi sceneTaskApi;

    @Autowired
    private RedisClientUtils redisClientUtils;

    @Async("agentDataThreadPool")
    @Override
    public void savePerformanceBaseData(PerformanceBaseDataParam param) {
        Set<String> keys = RedisHelper.keys(String.format(WebRedisKeyConstant.PTING_APPLICATION_KEY, "*"));
        if(keys.size() == 0) {
            return;
        }
        // 应用是否属于压测中
        List<String> applications = Lists.newArrayList();
        keys.forEach(key -> {
            applications.addAll((List<String>) RedisHelper.getValueByKey(key));
        });
        if(applications.contains(param.getAppName())) {
            performanceBaseDataDAO.insert(param);
        }
    }

    @Async("backgroundMonitorThreadPool")
    @Override
    public void monitorCpuMemory(Long sceneId, Long reportId, List<String> appNames, List<SceneSlaRefResp> stopSla,
        List<SceneSlaRefResp> warnSla) {
        try {
            ReportDetailDTO reportDetail = reportApplicationService.getDetail(reportId);
            long startTime = DateUtil.parseSecondFormatter(reportDetail.getStartTime()).getTime();
            // cloud 会把0 状态强制成"1"状态
            while (reportDetail.getEndTime() == null && reportDetail.getTaskStatus() == 1) {
                //查询应用基础数据 cpu | memory
                List<BaseServerResult> baseList = getAllBaseData(appNames, startTime, System.currentTimeMillis());
                Long stopMax = groupCompare(sceneId, reportId, baseList, stopSla, true);
                if (stopMax != null) {
                    startTime = startTime < stopMax ? stopMax : startTime;
                }
                Long warnMax = groupCompare(sceneId, reportId, baseList, warnSla, false);
                if (warnMax != null) {
                    startTime = startTime < warnMax ? warnMax : startTime;
                }
                Thread.sleep(5 * 1000);
                reportDetail = reportApplicationService.getDetail(reportId);
            }
        } catch (Exception e) {
            log.error("backgroundMonitorThreadPool error, sceneId={}, reportId={}, errmsg={}", sceneId, reportId, e.getMessage(), e);
        }
    }

    private List<BaseServerResult> getAllBaseData(List<String> appNames, Long startTime, Long endTime) {
        List<BaseServerResult> dataList = Lists.newArrayList();
        BaseServerParam param = new BaseServerParam();
        param.setStartTime(formatTimestamp(startTime));
        param.setEndTime(formatTimestamp(endTime));
        appNames.forEach(data -> {
            param.setApplicationName(data);
            Collection<BaseServerResult> temp = baseServerDao.queryList(param);
            if (CollectionUtils.isNotEmpty(temp)) {
                dataList.addAll(temp);
            }
        });
        return dataList;
    }

    /**
     * 返回最后一条没达标的数据
     */
    private Long groupCompare(Long sceneId, Long reportId, List<BaseServerResult> baseList, List<SceneSlaRefResp> slaList, Boolean stop) {
        if (CollectionUtils.isEmpty(baseList) || CollectionUtils.isEmpty(slaList)) {
            return null;
        }
        Map<String, List<BaseServerResult>> dataMap = baseList.stream().collect(
            Collectors.groupingBy(data -> data.getAppName() + "|" + data.getAppIp()));
        List<Long> timeList = Lists.newArrayList();
        dataMap.forEach((key, value) -> slaList.forEach(data -> timeList.add(getMatchList(sceneId, reportId, key, value, data, stop))));
        Collections.sort(timeList);
        return timeList.get(0);
    }

    /**
     * 返回最大无触发时间
     *
     * @return -
     */
    private Long getMatchList(Long sceneId, Long reportId, String prefix, List<BaseServerResult> baseList, SceneSlaRefResp sla, Boolean stop) {
        RuleBean ruleBean = sla.getRule();
        int limit = ruleBean.getTimes();
        int current = 0;
        long maxNormalTime = 0L;
        Map<String, Object> resultMap = Maps.newHashMap();
        if (ruleBean.getIndexInfo() == 4) {
            for (int i = 0; i < baseList.size(); i++) {
                matchCompare(resultMap, baseList.get(i).getCpuRate(), ruleBean.getDuring(), ruleBean.getCondition());
                if (Boolean.TRUE.equals(resultMap.get("result"))) {
                    current++;
                    if (current >= limit) {
                        if (stop) {
                            //发起stop
                            stop(baseList.get(i), sla, "cpu利用率", sceneId, reportId, resultMap);
                            break;
                        } else {
                            String value = prefix + "|" + baseList.get(i).getExtTime();
                            if (redisClientUtils.zsetAdd(WebRedisKeyConstant.REPORT_WARN_PREFIX + reportId, value) == 0) {
                                continue;
                            }
                            //发起warn
                            warn(baseList.get(i), sla, "cpu利用率", reportId, resultMap, baseList.get(i).getExtTime());
                        }
                    }
                } else {
                    //断开连续，重记
                    current = 0;
                    maxNormalTime = baseList.get(i).getExtTime();
                }
            }
        } else if (ruleBean.getIndexInfo() == 5) {
            for (int i = 0; i < baseList.size(); i++) {
                matchCompare(resultMap, baseList.get(i).getMemRate(), ruleBean.getDuring(), ruleBean.getCondition());
                if (Boolean.TRUE.equals(resultMap.get("result"))) {
                    current++;
                    if (current >= limit) {
                        if (stop) {
                            //发起stop
                            stop(baseList.get(i), sla, "内存使用率", sceneId, reportId, resultMap);
                            break;
                        } else {
                            String value = prefix + "|" + baseList.get(i).getExtTime();
                            if (redisClientUtils.zsetAdd(WebRedisKeyConstant.REPORT_WARN_PREFIX + reportId, value) == 0) {
                                continue;
                            }
                            //发起warn
                            warn(baseList.get(i), sla, "内存使用率", reportId, resultMap, baseList.get(i).getExtTime());
                        }
                    }
                } else {
                    //断开连续，重记
                    current = 0;
                    maxNormalTime = baseList.get(i).getExtTime();
                }
            }
        }
        return maxNormalTime;
    }

    /**
     * 比较真实值和目标值 是否满足条件
     */
    private void matchCompare(Map<String, Object> resultMap, Double realValue, BigDecimal goalValue, Integer compareType) {
        if (realValue == null) {
            realValue = 0d;
        }
        resultMap.clear();
        switch (compareType) {
            case 0:
                resultMap.put("compare", ">=");
                resultMap.put("real", realValue);
                resultMap.put("result", new BigDecimal(realValue).compareTo(goalValue) >= 0);
                break;
            case 1:
                resultMap.put("compare", ">");
                resultMap.put("real", realValue);
                resultMap.put("result", new BigDecimal(realValue).compareTo(goalValue) > 0);
                break;
            case 2:
                resultMap.put("compare", "=");
                resultMap.put("real", realValue);
                resultMap.put("result", new BigDecimal(realValue).compareTo(goalValue) == 0);
                break;
            case 3:
                resultMap.put("compare", "<=");
                resultMap.put("real", realValue);
                resultMap.put("result", new BigDecimal(realValue).compareTo(goalValue) <= 0);
                break;
            case 4:
                resultMap.put("compare", "<");
                resultMap.put("real", realValue);
                resultMap.put("result", new BigDecimal(realValue).compareTo(goalValue) < 0);
                break;
            default:
                resultMap.put("result", false);
        }
    }

    private void stop(BaseServerResult baseData, SceneSlaRefResp sla, String type, Long sceneId, Long reportId, Map<String, Object> resultMap) {
        // 保存中断信息
        WarnCreateReq createReq = warn(baseData, sla, type, reportId, resultMap, baseData.getExtTime());

        SceneManageIdReq req = new SceneManageIdReq();
        req.setId(sceneId);
        // 触发sla终止压测
        SlaBean slaBean = new SlaBean();
        slaBean.setRuleName(createReq.getSlaName());
        // 对象数据源
        slaBean.setBusinessActivity(createReq.getBusinessActivityName());
        slaBean.setRule(createReq.getWarnContent());
        req.setSlaBean(slaBean);
        req.setReportId(reportId);
        sceneTaskApi.stopTask(req);
        log.warn("触发SLA终止压测，sceneId={}, appName={}, appIp={}", sceneId, baseData.getAppName(), baseData.getAppIp());
    }

    private WarnCreateReq warn(BaseServerResult baseData, SceneSlaRefResp sla, String type, Long reportId, Map<String, Object> resultMap,
        Long timestamp) {
        WarnCreateReq warnDetail = new WarnCreateReq();
        warnDetail.setPtId(reportId);
        warnDetail.setSlaId(sla.getId());
        warnDetail.setSlaName(sla.getRuleName());
        warnDetail.setBusinessActivityId(0L);
        warnDetail.setBusinessActivityName(baseData.getAppName() + "|" + baseData.getAppIp());
        StringBuffer sb = new StringBuffer();
        sb.append(type);
        sb.append(resultMap.get("compare"));
        sb.append(sla.getRule().getDuring());
        sb.append("%");
        sb.append(", 连续");
        sb.append(sla.getRule().getTimes());
        sb.append("次");
        warnDetail.setWarnContent(sb.toString());
        warnDetail.setWarnTime(com.pamirs.takin.common.util.http.DateUtil.formatTime(timestamp));
        warnDetail.setRealValue((Double)resultMap.get("real"));
        sceneTaskApi.addWarn(warnDetail);
        return warnDetail;
    }

    private long formatTimestamp(long timestamp) {
        String temp = timestamp + "000000";
        return Long.parseLong(temp);
    }
}
