package io.shulie.takin.cloud.biz.collector;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.date.DateUnit;
import com.google.common.collect.Lists;
import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.notify.processor.calibration.PressureDataCalibration;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.output.statistics.PressureOutput;
import io.shulie.takin.cloud.biz.output.statistics.RtDataOutput;
import io.shulie.takin.cloud.biz.utils.DataUtils;
import io.shulie.takin.cloud.biz.utils.Executors;
import io.shulie.takin.cloud.common.bean.collector.ResponseMetrics;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.common.bean.task.TaskResult;
import io.shulie.takin.cloud.common.constants.CollectorConstants;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.influxdb.InfluxUtil;
import io.shulie.takin.cloud.common.influxdb.InfluxWriter;
import io.shulie.takin.cloud.common.utils.CollectorUtil;
import io.shulie.takin.cloud.common.utils.CommonUtil;
import io.shulie.takin.cloud.common.utils.JmxUtil;
import io.shulie.takin.cloud.common.utils.JsonUtil;
import io.shulie.takin.cloud.common.utils.NumberUtil;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.param.report.ReportQueryParam;
import io.shulie.takin.cloud.data.param.report.ReportQueryParam.PressureTypeRelation;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @date 2020-04-20 22:13
 */
@Slf4j
@Component
public class PushWindowDataScheduled extends AbstractIndicators {

    @Resource
    private ReportDao reportDao;
    @Resource
    private InfluxWriter influxWriter;
    @Resource
    private RedisClientUtil redisClientUtil;
    @Resource
    private PressureDataCalibration pressureDataCalibration;

    @Value("${report.metric.isSaveLastPoint:true}")
    private boolean isSaveLastPoint;

    private Long getMetricsMinTimeWindow(Long jobId, Long sceneId, Long reportId, Long customerId) {
        Long timeWindow = null;
        try {
            String measurement = InfluxUtil.getMetricsMeasurement(jobId, sceneId, reportId, customerId);
            ResponseMetrics metrics = influxWriter.querySingle(
                "select * from " + measurement + " where time>0 order by time asc limit 1", ResponseMetrics.class);
            if (null != metrics) {
                timeWindow = CollectorUtil.getTimeWindowTime(metrics.getTime());
            }
        } catch (Throwable e) {
            log.error("查询失败", e);
        }
        return timeWindow;
    }

    private List<ResponseMetrics> queryMetrics(Long jobId, Long sceneId, Long reportId, Long customerId, Long timeWindow) {
        try {
            //查询引擎上报数据时，通过时间窗向前5s来查询，(0,5]
            String measurement = InfluxUtil.getMetricsMeasurement(jobId, sceneId, reportId, customerId);
            StringBuilder sql = new StringBuilder("select * from");
            sql.append(" ").append(measurement);
            if (null != timeWindow) {
                long time = TimeUnit.NANOSECONDS.convert(timeWindow, TimeUnit.MILLISECONDS);
                sql.append(" ").append("where").append(" ").append("time<=").append(time).append(" and ")
                    .append("time>").append(
                        time - TimeUnit.NANOSECONDS.convert(CollectorConstants.SEND_TIME, TimeUnit.SECONDS));
                List<ResponseMetrics> query = influxWriter.query(sql.toString(), ResponseMetrics.class);
                log.info("汇总查询日志：sceneId:{},sql:{},查询结果数量:{}", sceneId, sql, query == null ? "null" : query.size());
                return query;
            } else {
                timeWindow = getMetricsMinTimeWindow(jobId, sceneId, reportId, customerId);
                if (null != timeWindow) {
                    return queryMetrics(jobId, sceneId, reportId, customerId, timeWindow);
                }
            }
        } catch (Throwable e) {
            log.error("查询失败", e);
        }
        return null;
    }

    /**
     * 获取当前未完成统计的最小时间窗口
     */
    private Long getWorkingPressureMinTimeWindow(Long jobId, Long sceneId, Long reportId, Long customerId) {
        Long timeWindow = null;
        try {
            String measurement = InfluxUtil.getMeasurement(jobId, sceneId, reportId, customerId);
            PressureOutput pressure = influxWriter.querySingle(
                "select * from " + measurement + " where status=0 order by time asc limit 1", PressureOutput.class);
            if (null != pressure) {
                timeWindow = pressure.getTime();
            }
        } catch (Throwable e) {
            log.error("查询失败", e);
        }
        return timeWindow;
    }

    /**
     * 获取当前统计的最大时间的下一个窗口窗口
     */
    private Long getPressureMaxTimeNextTimeWindow(Long jobId, Long sceneId, Long reportId, Long customerId) {
        Long timeWindow = null;
        try {
            String measurement = InfluxUtil.getMeasurement(jobId, sceneId, reportId, customerId);
            PressureOutput pressure = influxWriter.querySingle(
                "select * from " + measurement + " where status=1 order by time desc limit 1", PressureOutput.class);
            if (null != pressure) {
                timeWindow = CollectorUtil.getNextTimeWindow(pressure.getTime());
            }
        } catch (Throwable e) {
            log.error("查询失败", e);
        }
        return timeWindow;
    }

    private Long reduceMetrics(ReportResult report, Integer podNum, long endTime, Long timeWindow,
        List<ScriptNode> nodes) {
        if (null == report) {
            return null;
        }
        Long sceneId = report.getSceneId();
        Long reportId = report.getId();
        Long customerId = report.getTenantId();
        Long jobId = report.getJobId();
        String logPre = String.format("reduceMetrics %s-%s-%s:%s",
            sceneId, reportId, customerId, showTime(timeWindow));
        log.info(logPre + " start!");
        //如果时间窗口为空
        if (null == timeWindow) {
            //则通过当前压测统计表的未完成记录时间进行统计（数据统计有缺失的为未完成）
            timeWindow = getWorkingPressureMinTimeWindow(jobId, sceneId, reportId, customerId);
            //如果不存在当前未完成记录时间
            if (null == timeWindow) {
                //则根据最新统计记录时间获取下一个时间窗口
                timeWindow = getPressureMaxTimeNextTimeWindow(jobId, sceneId, reportId, customerId);
            }
        }
        //如果当前处理的时间窗口已经大于当结束时间窗口，则退出
        if (null != timeWindow && timeWindow > endTime) {
            log.info("{} return 1!timeWindow={}, endTime={}",
                logPre, showTime(timeWindow), showTime(endTime));
            return timeWindow;
        }
        //timeWindow如果为空，则获取全部metrics数据，如果不为空则获取该时间窗口的数据
        List<ResponseMetrics> metricsList = queryMetrics(jobId, sceneId, reportId, customerId, timeWindow);
        if (CollectionUtils.isEmpty(metricsList)) {
            log.info("{}, timeWindow={} ， metrics 是空集合!", logPre, showTime(timeWindow));
            return timeWindow;
        }
        log.info("{} queryMetrics timeWindow={}, endTime={}, metricsList.size={}",
            logPre, showTime(timeWindow), showTime(endTime), metricsList.size());
        if (null == timeWindow) {
            timeWindow = metricsList.stream().filter(Objects::nonNull)
                .map(t -> CollectorUtil.getTimeWindowTime(t.getTime()))
                .filter(l -> l > 0)
                .findFirst()
                .orElse(endTime);
        }
        //如果当前处理的时间窗口已经大于结束时间窗口，则退出
        if (timeWindow > endTime) {
            log.info("{} return 3!timeWindow={}, endTime={}",
                logPre, showTime(timeWindow), showTime(endTime));
            return timeWindow;
        }

        List<String> transactions = metricsList.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getTransaction)
            .filter(StringUtils::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(transactions)) {
            log.info("{} return 4!transactions is empty!", logPre);
            return timeWindow;
        }

        String measurement = InfluxUtil.getMeasurement(jobId, sceneId, reportId, customerId);
        long time = timeWindow;

        List<PressureOutput> results = transactions.stream().filter(StringUtils::isNotBlank)
            .map(s -> this.filterByTransaction(metricsList, s))
            .filter(CollectionUtils::isNotEmpty)
            .map(l -> this.toPressureOutput(l, podNum, time))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(results)) {
            log.info("results is empty!");
            return timeWindow;
        }

        //统计没有回传的节点数据
        if (CollectionUtils.isNotEmpty(nodes)) {
            Map<String, PressureOutput> pressureMap = results.stream().filter(Objects::nonNull)
                .collect(Collectors.toMap(PressureOutput::getTransaction, o -> o, (o1, o2) -> o1));
            nodes.stream().filter(Objects::nonNull)
                .forEach(n -> countPressure(n, pressureMap, results));
        }
        results.stream().filter(Objects::nonNull)
            .map(p -> InfluxUtil.toPoint(measurement, time, p))
            .forEach(influxWriter::insert);
        log.info("{} finished!timeWindow={}, endTime={}",
            logPre, showTime(timeWindow), showTime(endTime));
        return timeWindow;
    }

    /**
     * 统计各个节点的数据
     *
     * @param node      节点
     * @param sourceMap 现有的数据和节点映射（jmeter上报的原生数据统计）
     * @param results   数据结果集合
     * @return 返回当前节点的统计结果
     */
    private PressureOutput countPressure(ScriptNode node, Map<String, PressureOutput> sourceMap,
        List<PressureOutput> results) {
        if (null == node || StringUtils.isBlank(node.getXpathMd5()) || null == sourceMap) {
            return null;
        }
        //sourceMap中的key都是jmeter上报的
        PressureOutput result = sourceMap.get(node.getXpathMd5());
        if (null != result) {
            return result;
        }
        if (CollectionUtils.isEmpty(node.getChildren())) {
            return null;
        }
        List<PressureOutput> childPressures = node.getChildren().stream().filter(Objects::nonNull)
            .map(n -> countPressure(n, sourceMap, results))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        result = countPressure(node, childPressures, sourceMap);
        if (null != result) {
            results.add(result);
        }
        return result;
    }

    /**
     * 根据子节点统计结果来统计当前节点的数据
     *
     * @param node           当前节点
     * @param childPressures 子节点统计数据结果(1级子节点)
     * @return 返回当前节点统计结果
     */
    private PressureOutput countPressure(ScriptNode node, List<PressureOutput> childPressures,
        Map<String, PressureOutput> sourceMap) {
        if (CollectionUtils.isEmpty(childPressures)) {
            return null;
        }
        long time = childPressures.stream().filter(Objects::nonNull)
            .mapToLong(PressureOutput::getTime)
            .findAny()
            .orElse(0L);
        if (0 == time) {
            return null;
        }
        /*================== childPressures只用1级子节点 统计开始 =====================================================*/
        int activeThreads;
        if (NodeTypeEnum.TEST_PLAN == node.getType()) {//TEST_PLAN节点取加总
            activeThreads = NumberUtil.sum(childPressures, PressureOutput::getActiveThreads);
        } else {//其他分组节点（控制器、线程组）：取平均
            activeThreads = NumberUtil.maxInt(childPressures, PressureOutput::getActiveThreads);
        }
        long sendBytes = NumberUtil.sumLong(childPressures, PressureOutput::getSentBytes);
        long receiveBytes = NumberUtil.sumLong(childPressures, PressureOutput::getReceivedBytes);
        int dataNum = NumberUtil.minInt(childPressures, PressureOutput::getDataNum, 1);
        double dataRate = NumberUtil.minDouble(childPressures, PressureOutput::getDataRate, 1d);
        int status = NumberUtil.minInt(childPressures, PressureOutput::getStatus, 1);
        /*=================== childPressures只用1级子节点 统计结束 =====================================================/

        /*=================== subPressures含有1级子节点和事务控制器这种子节点的子节点 计开始 ===============================*/
        /*
         * 多级子节点,满足过滤条件的多级子节点:如果该节点的子节点有自己上报数据，则继续递归取其子节点的子节点：
         * 这里事务控制器会自己上报数据，当父节点包含事务控制器时，会取事务控制器和事务控制的子节点合并计算
         */
        List<ScriptNode> childNodes = JmxUtil.getChildNodesByFilterFunc(node,
            n -> sourceMap.containsKey(n.getXpathMd5()));
        final List<PressureOutput> subPressures = Lists.newArrayList(childPressures);
        if (CollectionUtils.isNotEmpty(childNodes)) {
            childNodes.stream().filter(Objects::nonNull)
                .map(ScriptNode::getXpathMd5)
                .filter(StringUtils::isNotBlank)
                .map(sourceMap::get)
                .filter(Objects::nonNull)
                .filter(d -> !childPressures.contains(d))
                .forEach(subPressures::add);
        }
        int count = NumberUtil.sum(subPressures, PressureOutput::getCount);
        int failCount = NumberUtil.sum(subPressures, PressureOutput::getFailCount);
        int saCount = NumberUtil.sum(subPressures, PressureOutput::getSaCount);
        long sumRt = NumberUtil.sumLong(subPressures, PressureOutput::getSumRt);
        double maxRt = NumberUtil.maxDouble(subPressures, PressureOutput::getMaxRt);
        double minRt = NumberUtil.minDouble(subPressures, PressureOutput::getMinRt);
        double avgTps = NumberUtil.sumDouble(subPressures, PressureOutput::getAvgTps);
        String percentSa = calculateSaPercent(CommonUtil.getList(subPressures, PressureOutput::getSaPercent));
        /*=================== subPressures含有1级子节点和事务控制器这种子节点的子节点 计结束 ===============================*/

        double sa = NumberUtil.getPercentRate(saCount, count);
        double successRate = NumberUtil.getPercentRate(Math.max(0, count - failCount), count);
        double avgRt = NumberUtil.getRate(sumRt, count);

        PressureOutput output = new PressureOutput();
        output.setTime(time);
        output.setTransaction(node.getXpathMd5());
        output.setCount(count);
        output.setFailCount(failCount);
        output.setSaCount(saCount);
        output.setSa(sa);
        output.setSuccessRate(successRate);
        output.setSentBytes(sendBytes);
        output.setReceivedBytes(receiveBytes);
        output.setSumRt(sumRt);
        output.setAvgRt(avgRt);
        output.setMaxRt(maxRt);
        output.setMinRt(minRt);
        output.setActiveThreads(activeThreads);
        output.setAvgTps(avgTps);
        output.setSaPercent(percentSa);
        output.setDataNum(dataNum);
        output.setDataRate(dataRate);
        output.setStatus(status);
        output.setTestName(node.getTestName());
        return output;
    }

    /**
     * 单个时间窗口数据，根据transaction过滤
     */
    private List<ResponseMetrics> filterByTransaction(List<ResponseMetrics> metricsList, String transaction) {
        if (CollectionUtils.isEmpty(metricsList)) {
            return metricsList;
        }
        return metricsList.stream().filter(Objects::nonNull)
            .filter(m -> transaction.equals(m.getTransaction()))
            .collect(Collectors.toList());
    }

    /**
     * 实时数据统计
     */
    private PressureOutput toPressureOutput(List<ResponseMetrics> metricsList, Integer podNum, long time) {
        if (CollectionUtils.isEmpty(metricsList)) {
            return null;
        }
        String transaction = metricsList.get(0).getTransaction();
        String testName = metricsList.get(0).getTestName();

        int count = NumberUtil.sum(metricsList, ResponseMetrics::getCount);
        int failCount = NumberUtil.sum(metricsList, ResponseMetrics::getFailCount);
        int saCount = NumberUtil.sum(metricsList, ResponseMetrics::getSaCount);
        double sa = NumberUtil.getPercentRate(saCount, count);
        double successRate = NumberUtil.getPercentRate(count - failCount, count);
        long sendBytes = NumberUtil.sumLong(metricsList, ResponseMetrics::getSentBytes);
        long receivedBytes = NumberUtil.sumLong(metricsList, ResponseMetrics::getReceivedBytes);
        long sumRt = NumberUtil.sumLong(metricsList, ResponseMetrics::getSumRt);
        double avgRt = NumberUtil.getRate(sumRt, count);
        double maxRt = NumberUtil.maxDouble(metricsList, ResponseMetrics::getMaxRt);
        double minRt = NumberUtil.maxDouble(metricsList, ResponseMetrics::getMinRt);
        int activeThreads = NumberUtil.sum(metricsList, ResponseMetrics::getActiveThreads);
        double avgTps = NumberUtil.getRate(count, CollectorConstants.SEND_TIME);
        List<String> percentDataList = metricsList.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getPercentData)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toList());
        String percentSa = calculateSaPercent(percentDataList);
        Set<String> podNos = metricsList.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getPodNo)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toSet());

        int dataNum = CollectionUtils.isEmpty(podNos) ? 0 : podNos.size();
        double dataRate = NumberUtil.getPercentRate(dataNum, podNum, 100d);
        int status = dataNum < podNum ? 0 : 1;
        PressureOutput p = new PressureOutput();
        p.setTime(time);
        p.setTransaction(transaction);
        p.setCount(count);
        p.setFailCount(failCount);
        p.setSaCount(saCount);
        p.setSa(sa);
        p.setSuccessRate(successRate);
        p.setSentBytes(sendBytes);
        p.setReceivedBytes(receivedBytes);
        p.setSumRt(sumRt);
        p.setAvgRt(avgRt);
        p.setMaxRt(maxRt);
        p.setMinRt(minRt);
        p.setActiveThreads(activeThreads);
        p.setAvgTps(avgTps);
        p.setSaPercent(percentSa);
        p.setDataNum(dataNum);
        p.setDataRate(dataRate);
        p.setStatus(status);
        p.setTestName(testName);
        return p;
    }

    private void finishPushData(ReportResult report, Integer podNum, Long timeWindow, long endTime, List<ScriptNode> nodes) {
        if (null == report) {
            return;
        }
        Long sceneId = report.getSceneId();
        Long reportId = report.getId();
        Long customerId = report.getTenantId();
        String taskKey = getPressureTaskKey(sceneId, reportId, customerId);
        String last = String.valueOf(redisTemplate.opsForValue().get(last(taskKey)));
        long nowTimeWindow = CollectorUtil.getTimeWindowTime(System.currentTimeMillis());
        log.info("finishPushData {}-{}-{} last={}, timeWindow={}, endTime={}, now={}", sceneId, reportId, customerId,
            last, showTime(timeWindow), showTime(endTime), showTime(nowTimeWindow));

        if (null != report.getEndTime()) {
            endTime = Math.min(endTime, report.getEndTime().getTime());
        }

        if (ScheduleConstants.LAST_SIGN.equals(last) || (null != timeWindow && timeWindow > endTime)) {
            String engineName = ScheduleConstants.getEngineName(sceneId, reportId, customerId);
            // 只需触发一次即可
            String endTimeKey = engineName + ScheduleConstants.LAST_SIGN;
            Long eTime = (Long)redisTemplate.opsForValue().get(endTimeKey);
            if (null != eTime) {
                log.info("触发手动收尾操作，当前时间窗口：{},结束时间窗口：{},", showTime(timeWindow), showTime(eTime));
                endTime = Math.min(endTime, eTime);
            } else {
                eTime = endTime;
            }
            long endTimeWindow = CollectorUtil.getTimeWindowTime(endTime);
            log.info("触发收尾操作，当前时间窗口：{},结束时间窗口：{},", showTime(timeWindow), showTime(endTimeWindow));
            // 比较 endTime timeWindow
            // 如果结束时间 小于等于当前时间，数据不用补充，
            // 如果结束时间 大于 当前时间，需要补充期间每5秒的数据 延后5s
            while (isSaveLastPoint && timeWindow <= endTimeWindow && timeWindow <= nowTimeWindow) {
                timeWindow = reduceMetrics(report, podNum, eTime, timeWindow, nodes);
                timeWindow = CollectorUtil.getNextTimeWindow(timeWindow);
            }
            log.info("本次压测{}-{}-{},push data 完成", sceneId, reportId, customerId);
            // 清除 SLA配置 清除PushWindowDataScheduled 删除pod job configMap  生成报告
            Event event = new Event();
            event.setEventName("finished");
            event.setExt(new TaskResult(sceneId, reportId, customerId));
            eventCenterTemplate.doEvents(event);
            // 触发数据校准通知
            dataCalibration(report);
            redisTemplate.delete(last(taskKey));
            log.info("---> 本次压测{}-{}-{}完成，已发送finished事件！<------", sceneId, reportId, customerId);
        }
    }

    /**
     * 实时数据统计
     */
    public void pushData2() {
        ReportQueryParam param = new ReportQueryParam();
        param.setStatus(0);
        param.setIsDel(0);
        param.setPressureTypeRelation(new PressureTypeRelation(PressureSceneEnum.INSPECTION_MODE.getCode(), false));
        param.setJobIdNotNull(true);
        List<ReportResult> results = reportDao.queryReportList(param);
        if (CollectionUtils.isEmpty(results)) {
            log.debug("没有需要统计的报告！");
            return;
        }
        List<Long> reportIds = CommonUtil.getList(results, ReportResult::getId);
        log.info("找到需要统计的报告：" + JsonHelper.bean2Json(reportIds));
        results.stream().filter(Objects::nonNull).forEach(r -> combineMetricsData(r, false, null));
    }

    /**
     * 每五秒执行一次
     * 每次从redis中取10秒前的数据
     */
    @Async("collectorSchedulerPool")
    @Scheduled(cron = "0/5 * * * * ? ")
    public void pushData() {
        pushData2();
    }

    /**
     * 计算sa
     */
    private String calculateSaPercent(List<String> percentDataList) {
        if (CollectionUtils.isEmpty(percentDataList)) {
            return null;
        }
        List<Map<Integer, RtDataOutput>> percentMapList = percentDataList.stream().filter(StringUtils::isNotBlank)
            .map(DataUtils::parseToPercentMap)
            .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(percentMapList)) {
            return null;
        }
        //请求总数
        int total = percentMapList.stream().filter(Objects::nonNull)
            .map(m -> m.get(100))
            .filter(Objects::nonNull)
            .mapToInt(RtDataOutput::getHits)
            .sum();

        //所有rt按耗时排序
        List<RtDataOutput> rtDataList = percentMapList.stream().filter(Objects::nonNull)
            .peek(DataUtils::percentMapRemoveDuplicateHits)
            .map(Map::values)
            .filter(CollectionUtils::isNotEmpty)
            .flatMap(Collection::stream)
            .sorted(Comparator.comparing(RtDataOutput::getTime))
            .collect(Collectors.toList());

        Map<Integer, RtDataOutput> result = new HashMap<>(100);
        //计算逻辑
        //每个百分点的目标请求数，如果统计达标，进行下一个百分点的统计，如果tong ji
        for (int i = 1; i <= 100; i++) {
            int hits = 0;
            int time = 0;
            double need = total * i / 100d;
            for (RtDataOutput d : rtDataList) {
                if (hits < need || d.getTime() <= time) {
                    hits += d.getHits();
                    if (d.getTime() > time) {
                        time = d.getTime();
                    }
                }
            }
            result.put(i, new RtDataOutput(hits, time));
        }
        return DataUtils.percentMapToString(result);
    }

    private void scan(String pattern, Consumer<byte[]> consumer) {
        this.redisTemplate.execute((RedisConnection connection) -> {
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().count(Long.MAX_VALUE).match(pattern)
                .build())) {
                cursor.forEachRemaining(consumer);
                return null;
            } catch (Exception e) {
                throw new TakinCloudException(TakinCloudExceptionEnum.TASK_RUNNING_GET_RUNNING_JOB_KEY, "获取运行中的job失败！",
                    e);
            }
        });
    }

    /**
     * 获取符合条件的key
     *
     * @param pattern 表达式
     * @return -
     */
    public Set<String> keys(String pattern) {
        Set<String> keys = new HashSet<>();
        this.scan(pattern, item -> {
            //符合条件的key
            String key = new String(item, StandardCharsets.UTF_8);
            keys.add(key);
        });
        return keys;
    }

    private String showTime(Long timestamp) {
        if (null == timestamp) {return "";}
        // 忽略时间精度到天
        long d1 = timestamp / DateUnit.DAY.getMillis();
        long d2 = System.currentTimeMillis() / DateUnit.DAY.getMillis();
        // 转换时间
        cn.hutool.core.date.DateTime timestampDate = cn.hutool.core.date.DateUtil.date(timestamp);
        String timeString = d1 == d2 ?
            // 同一日则显示时间 HH:mm:ss
            timestampDate.toTimeStr() :
            // 不同日则显示日期时间 yyyy-MM-dd HH:mm:ss
            timestampDate.toString();
        // 返回
        return timestamp + "(" + timeString + ")";
    }

    public void combineMetricsData(ReportResult r, boolean dataCalibration, Runnable finalAction) {
        if (Objects.isNull(r)) {
            return;
        }
        Runnable runnable = () -> {
            Long sceneId = r.getSceneId();
            Long reportId = r.getId();
            Long customerId = r.getTenantId();
            String lockKey = String.format("pushData:%s:%s:%s", sceneId, reportId, customerId);
            if (!lock(lockKey, "1")) {
                return;
            }
            TenantCommonExt ext = new TenantCommonExt();
            ext.setSource(ContextSourceEnum.JOB.getCode());
            ext.setTenantId(customerId);
            ext.setEnvCode(r.getEnvCode());
            TenantInfoExt infoExt = WebPluginUtils.getTenantInfo(customerId);
            if(infoExt == null) {
                log.error("租户信息未找到【{}】", customerId);
                return;
            }
            ext.setTenantAppKey(infoExt.getTenantAppKey());
            try {
                WebPluginUtils.setTraceTenantContext(ext);
                List<ScriptNode> nodes = JsonUtil.parseArray(r.getScriptNodeTree(), ScriptNode.class);
                SceneManageWrapperOutput scene = cloudSceneManageService.getSceneManage(sceneId, null);
                if (null == scene) {
                    log.info("no such scene manager!sceneId=" + sceneId);
                    return;
                }
                //结束时间取开始压测时间-10s+总测试时间+3分钟， 3分钟富裕时间，给与pod启动和压测引擎启动延时时间
                long endTime = TimeUnit.MINUTES.toMillis(3L);
                if (null != r.getStartTime()) {
                    endTime += (r.getStartTime().getTime() - TimeUnit.SECONDS.toMillis(10));
                } else if (null != r.getGmtCreate()) {
                    endTime += r.getGmtCreate().getTime();
                }
                if (null != scene.getTotalTestTime()) {
                    endTime += TimeUnit.SECONDS.toMillis(scene.getTotalTestTime());
                } else if (null != scene.getPressureTestSecond()) {
                    endTime += TimeUnit.SECONDS.toMillis(scene.getPressureTestSecond());
                }
                int podNum = scene.getIpNum();
                long nowTimeWindow = CollectorUtil.getNowTimeWindow();
                long breakTime = Math.min(endTime, nowTimeWindow);
                Long timeWindow = null;
                do {
                    //不用递归，而是采用do...while...的方式是防止需要处理的时间段太长引起stackOverFlow错误
                    timeWindow = reduceMetrics(r, podNum, breakTime, timeWindow, nodes);
                    if (null == timeWindow) {
                        timeWindow = nowTimeWindow;
                        break;
                    }
                    timeWindow = CollectorUtil.getNextTimeWindow(timeWindow);
                } while (timeWindow <= breakTime);

                if (!dataCalibration && null != r.getEndTime() && timeWindow >= r.getEndTime().getTime()) {
                    // 更新压测场景状态  压测引擎运行中,压测引擎停止压测 ---->压测引擎停止压测
                    cloudSceneManageService.updateSceneLifeCycle(UpdateStatusBean.build(sceneId, reportId,
                            customerId)
                        .checkEnum(SceneManageStatusEnum.ENGINE_RUNNING, SceneManageStatusEnum.STOP)
                        .updateEnum(SceneManageStatusEnum.STOP)
                        .build());
                }
                if (!dataCalibration) {
                    finishPushData(r, podNum, timeWindow, endTime, nodes);
                }
            } catch (Throwable t) {
                log.error("pushData2 error!", t);
            } finally {
                if (Objects.nonNull(finalAction)) {
                    finalAction.run();
                }
                unlock(lockKey, "0");
            }
        };
        Executors.execute(runnable);
    }

    private void dataCalibration(ReportResult report) {
        Long jobId = report.getJobId();
        String reportId = String.valueOf(report.getId());
        if (redisClientUtil.lockExpire(PressureStartCache.getDataCalibrationLockKey(jobId), reportId,
            5, TimeUnit.MINUTES)) {
            TaskResult result = new TaskResult();
            result.setSceneId(report.getSceneId());
            result.setTaskId(report.getJobId());
            result.setResourceId(report.getResourceId());
            WebPluginUtils.fillCloudUserData(result);
            pressureDataCalibration.dataCalibrationAmdb(result);
            pressureDataCalibration.dataCalibrationCloud(result);
        }
    }
}
