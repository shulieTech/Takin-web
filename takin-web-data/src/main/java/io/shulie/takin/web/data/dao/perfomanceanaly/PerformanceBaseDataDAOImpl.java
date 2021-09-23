package io.shulie.takin.web.data.dao.perfomanceanaly;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import io.shulie.takin.utils.json.JsonHelper;
import com.pamirs.takin.common.util.DateUtils;
import org.springframework.stereotype.Service;
import com.shulie.tesla.sequence.impl.DefaultSequence;
import org.apache.commons.collections4.CollectionUtils;
import io.shulie.takin.web.data.common.InfluxDatabaseWriter;
import org.springframework.beans.factory.annotation.Autowired;
import io.shulie.takin.web.data.model.mysql.PerformanceThreadDataEntity;
import io.shulie.takin.web.data.mapper.mysql.PerformanceThreadDataMapper;
import io.shulie.takin.web.common.vo.perfomanceanaly.PerformanceThreadDataVO;
import io.shulie.takin.web.data.mapper.mysql.PerformanceThreadStackDataMapper;
import io.shulie.takin.web.data.model.mysql.PerformanceThreadStackDataEntity;
import io.shulie.takin.web.data.param.perfomanceanaly.PerformanceBaseDataParam;
import io.shulie.takin.web.data.param.perfomanceanaly.PerformanceBaseQueryParam;
import io.shulie.takin.web.data.result.perfomanceanaly.PerformanceBaseDataResult;

/**
 * @author qianshui
 * @date 2020/11/4 下午2:39
 */
@Service
@Slf4j
public class PerformanceBaseDataDAOImpl implements PerformanceBaseDataDAO {

    private final static String DEFAULT_THREAD_STATUS = "RUNNABLE";
    @Resource
    private InfluxDatabaseWriter influxDatabaseWriter;

    @Resource
    private PerformanceThreadDataMapper performanceThreadDataMapper;

    @Resource
    private PerformanceThreadStackDataMapper performanceThreadStackDataMapper;

    @Autowired
    private DefaultSequence baseOrderLineSequence;

    @Autowired
    private DefaultSequence threadOrderLineSequence;

    @Override
    public void insert(PerformanceBaseDataParam param) {
        long baseId = baseOrderLineSequence.nextValue();
        // 插入base数据
        influxWriterBase(param, baseId);
        // 插入thread数据
        influxWriterThread(param, baseId);
    }

    private void influxWriterThread(PerformanceBaseDataParam param, Long baseId) {
        long start = System.currentTimeMillis();
        // 记录关联关系 thread threadStack
        List<PerformanceThreadStackDataEntity> stackDataEntities = Lists.newArrayList();
        param.getThreadDataList().forEach(data -> {
            if (StringUtils.isBlank(data.getThreadStatus())) {
                data.setThreadStatus(DEFAULT_THREAD_STATUS);
            }
            long threadId = threadOrderLineSequence.nextValue();
            // 记录关联关系
            PerformanceThreadStackDataEntity entity = new PerformanceThreadStackDataEntity();
            entity.setThreadStackLink(threadId);
            entity.setThreadStack(data.getThreadStack());
            entity.setGmtCreate(new Date());
            stackDataEntities.add(entity);
            // 处理数据
            data.setThreadStack("");
            data.setThreadStackLink(threadId);
        });

        PerformanceThreadDataEntity threadDataEntity = new PerformanceThreadDataEntity();
        threadDataEntity.setBaseId(baseId);
        threadDataEntity.setAgentId(StringUtils.isNotBlank(param.getAgentId()) ? param.getAgentId() : "null");
        threadDataEntity.setAppIP(StringUtils.isNotBlank(param.getAppIp()) ? param.getAppIp() : "null");
        threadDataEntity.setAppName(StringUtils.isNotBlank(param.getAppName()) ? param.getAppName() : "null");
        threadDataEntity.setTimestamp(
                param.getTimestamp() != null ? DateUtils.dateToString(new Date(param.getTimestamp()), "yyyy-MM-dd HH:mm:ss")
                        : "null");
        threadDataEntity.setThreadData(JsonHelper.bean2Json(param.getThreadDataList()));
        threadDataEntity.setGmtCreate(new Date());
        performanceThreadDataMapper.insert(threadDataEntity);
        // 插入influxdb
        long mid = System.currentTimeMillis();
        // threadStack 存入mysql thread_stack_link
        performanceThreadStackDataMapper.insertBatchSomeColumn(stackDataEntities);
        log.debug("influxDBWriter运行时间：{},insertBatchSomeColumn运行时间:{},数据量:{}", mid - start,
                System.currentTimeMillis() - mid, stackDataEntities.size());
    }

    private void influxWriterBase(PerformanceBaseDataParam param, Long baseId) {
        long start = System.currentTimeMillis();
        // 计算合计cpu利用率
        double cpuUseRate = 0.00;
        for (PerformanceThreadDataVO dataParam : param.getThreadDataList()) {
            BigDecimal b1 = BigDecimal.valueOf((cpuUseRate));
            BigDecimal b2 = BigDecimal.valueOf(
                    dataParam.getThreadCpuUsage() == null ? 0.00 : dataParam.getThreadCpuUsage());
            cpuUseRate = b1.add(b2).doubleValue();
        }
        Map<String, Object> fields = Maps.newHashMap();
        fields.put("total_memory", param.getTotalMemory());
        fields.put("perm_memory", param.getPermMemory());
        fields.put("young_memory", param.getYoungMemory());
        fields.put("old_memory", param.getOldMemory());
        fields.put("young_gc_count", param.getYoungGcCount());
        fields.put("full_gc_count", param.getFullGcCount());
        fields.put("young_gc_cost", param.getYoungGcCost());
        fields.put("full_gc_cost", param.getFullGcCost());
        fields.put("cpu_use_rate", cpuUseRate);
        // 新增buffer
        fields.put("total_buffer_pool_memory", param.getTotalBufferPoolMemory());
        // 新增非堆
        fields.put("total_no_heap_memory", param.getTotalNonHeapMemory());
        fields.put("thread_count", param.getThreadDataList().size());
        // 保存原始时间戳，后续作为组装base_id的唯一值
        fields.put("timestamp", param.getTimestamp() != null ? param.getTimestamp() : "null");
        // base_id 先存进去
        fields.put("base_id", baseId);
        Map<String, String> tags = Maps.newHashMap();
        tags.put("agent_id", StringUtils.isNotBlank(param.getAgentId()) ? param.getAgentId() : "null");
        tags.put("app_name", StringUtils.isNotBlank(param.getAppName()) ? param.getAppName() : "null");
        tags.put("app_ip", StringUtils.isNotBlank(param.getAppIp()) ? param.getAppIp() : "null");
        tags.put("process_id", param.getProcessId() != null ? String.valueOf(param.getProcessId()) : "null");
        tags.put("process_name", StringUtils.isNotBlank(param.getProcessName()) ? param.getProcessName() : "null");
        try {
            influxDatabaseWriter.insert("t_performance_base_data", tags, fields, param.getTimestamp());
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("influxWriterBase运行时间：{}", System.currentTimeMillis() - start);

    }

    @Override
    public List<String> getProcessNameList(PerformanceBaseQueryParam param) {
        long start = System.currentTimeMillis();
        String influxDatabaseSql = "select" +
                " agent_id ,app_ip,young_memory,old_memory" +
                " from t_performance_base_data" +
                " where " +
                " app_name = '" + param.getAppName() + "'" +
                " and time >= '" + param.getStartTime() + "'" +
                " and time <= '" + param.getEndTime() + "'" +
                " TZ('Asia/Shanghai')";

        List<PerformanceBaseDataResult> dataList = influxDatabaseWriter.query(influxDatabaseSql,
                PerformanceBaseDataResult.class);

        log.info("getProcessNameList.query运行时间：{},数据量:{}", System.currentTimeMillis() - start, dataList.size());

        if (CollectionUtils.isEmpty(dataList)) {
            return Lists.newArrayList();
        }

        return dataList.stream()
                .map(data -> data.getAppIp() + "|" + data.getAgentId()).distinct().collect(Collectors.toList());
    }

    @Override
    public PerformanceBaseDataResult getOnePerformanceBaseData(PerformanceBaseQueryParam param) {
        long start = System.currentTimeMillis();
        String influxDatabaseSql = "select *" +
                " from t_performance_base_data" +
                " where " +
                " time >= " + "'" + param.getStartTime() + "'" +
                " and time <= " + "'" + param.getEndTime() + "'" +
                " and app_name = " + "'" + param.getAppName() + "'" +
                " and app_ip = " + "'" + param.getAppIp() + "'" +
                " and agent_id = " + "'" + param.getAgentId() + "'" +
                " limit 1" +
                " TZ('Asia/Shanghai')";
        PerformanceBaseDataResult result = influxDatabaseWriter.querySingle(influxDatabaseSql,
                PerformanceBaseDataResult.class);
        log.info("getOnePerformanceBaseData.querySingle运行时间:{}", System.currentTimeMillis() - start);
        return Optional.ofNullable(result).orElse(new PerformanceBaseDataResult());
    }

    @Override
    public List<PerformanceBaseDataResult> getPerformanceBaseDataList(PerformanceBaseQueryParam param) {
        long start = System.currentTimeMillis();
        String influxDatabaseSql = "select *" +
                " from t_performance_base_data" +
                " where " +
                " time >= " + "'" + param.getStartTime() + "'" +
                " and time <= " + "'" + param.getEndTime() + "'" +
                " and app_name = " + "'" + param.getAppName() + "'" +
                " and app_ip = " + "'" + param.getAppIp() + "'" +
                " and agent_id = " + "'" + param.getAgentId() + "'" +
                "order by time TZ('Asia/Shanghai') ";

        List<PerformanceBaseDataResult> dataList = influxDatabaseWriter.query(influxDatabaseSql,
                PerformanceBaseDataResult.class);
        log.info("getPerformanceBaseDataList.query运行时间:{},数据量:{}", System.currentTimeMillis() - start, dataList.size());
        if (CollectionUtils.isEmpty(dataList)) {
            return Lists.newArrayList();
        }
        return dataList;
    }

    @Override
    public void clearData(String time) {
        if (!StringUtils.isBlank(time)) {
            influxDatabaseWriter.query(String.format("delete from t_performance_base_data where time <= '%s'", time),
                PerformanceBaseDataResult.class);
        }
    }

}
