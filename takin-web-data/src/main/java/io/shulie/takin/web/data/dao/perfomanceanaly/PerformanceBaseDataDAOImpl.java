package io.shulie.takin.web.data.dao.perfomanceanaly;

import cn.hutool.core.collection.ListUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.common.util.DateUtils;
import com.pamirs.takin.common.util.http.DateUtil;
import com.shulie.tesla.sequence.impl.DefaultSequence;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.amdb.bean.common.AmdbResult;
import io.shulie.takin.web.amdb.util.AmdbHelper;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.vo.perfomanceanaly.PerformanceThreadDataVO;
import io.shulie.takin.web.data.mapper.mysql.PerformanceThreadDataMapper;
import io.shulie.takin.web.data.mapper.mysql.PerformanceThreadStackDataMapper;
import io.shulie.takin.web.data.model.mysql.PerformanceThreadDataEntity;
import io.shulie.takin.web.data.model.mysql.PerformanceThreadStackDataEntity;
import io.shulie.takin.web.data.param.perfomanceanaly.PerformanceBaseDataParam;
import io.shulie.takin.web.data.param.perfomanceanaly.PerformanceBaseQueryParam;
import io.shulie.takin.web.data.result.perfomanceanaly.PerformanceBaseDataAll;
import io.shulie.takin.web.data.result.perfomanceanaly.PerformanceBaseDataQuery;
import io.shulie.takin.web.data.result.perfomanceanaly.PerformanceBaseDataResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.takin.properties.AmdbClientProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author qianshui
 * @date 2020/11/4 下午2:39
 */
@Service
@Slf4j
public class PerformanceBaseDataDAOImpl implements PerformanceBaseDataDAO {

    private final static String DEFAULT_THREAD_STATUS = "RUNNABLE";
    private static final String AMDB_PERFORMANCE_QUERY_LIST_PATH = "/amdb/db/api/performance/queryList";
    @Resource
    private PerformanceThreadDataMapper performanceThreadDataMapper;
    @Autowired
    private AmdbClientProperties properties;
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
        threadDataEntity.setTimestamp(param.getTimestamp() != null ? DateUtils.dateToString(new Date(param.getTimestamp()), "yyyy-MM-dd HH:mm:ss") : "null");
        threadDataEntity.setThreadData(JsonHelper.bean2Json(param.getThreadDataList()));
        threadDataEntity.setGmtCreate(new Date());
        performanceThreadDataMapper.insert(threadDataEntity);
        // 插入influxdb
        long mid = System.currentTimeMillis();
        // threadStack 存入mysql thread_stack_link
        if (CollectionUtils.isEmpty(stackDataEntities)) {
            return;
        }
        if (stackDataEntities.size() > 40) {
            for (List<PerformanceThreadStackDataEntity> entityList : ListUtil.split(stackDataEntities, 40)) {
                performanceThreadStackDataMapper.insertBatchSomeColumn(entityList);
            }
        } else {
            performanceThreadStackDataMapper.insertBatchSomeColumn(stackDataEntities);
        }
        log.debug("influxDBWriter运行时间：{},insertBatchSomeColumn运行时间:{},数据量:{}", mid - start, System.currentTimeMillis() - mid, stackDataEntities.size());
    }

    private void influxWriterBase(PerformanceBaseDataParam param, Long baseId) {
        long start = System.currentTimeMillis();
        // 计算合计cpu利用率
        double cpuUseRate = 0.00;
        for (PerformanceThreadDataVO dataParam : param.getThreadDataList()) {
            BigDecimal b1 = BigDecimal.valueOf((cpuUseRate));
            BigDecimal b2 = BigDecimal.valueOf(dataParam.getThreadCpuUsage() == null ? 0.00 : dataParam.getThreadCpuUsage());
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
        // 租户信息tag
        tags.put("tenant_id", WebPluginUtils.traceTenantId() + "");
        tags.put("tenant_app_key", WebPluginUtils.traceTenantAppKey() + "");
        tags.put("env_code", WebPluginUtils.traceEnvCode() + "");
        try {
            //功能已迁移到surge-receive中
//            influxDatabaseWriter.insert("t_performance_base_data", tags, fields, param.getTimestamp());
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("influxWriterBase运行时间：{}", System.currentTimeMillis() - start);

    }

    @Override
    public List<String> getProcessNameList(PerformanceBaseQueryParam param) {
        long start = System.currentTimeMillis();
        Date startTime = DateUtil.getDate(param.getStartTime());
        Date endTime = DateUtil.getDate(param.getEndTime());
        PerformanceBaseDataQuery query = new PerformanceBaseDataQuery();
        query.setAppName(param.getAppName());
        if (startTime != null) {
            query.setStartTime(startTime.getTime());
        }
        if (endTime != null) {
            query.setEndTime(endTime.getTime());
        }
        query.setTenantId(WebPluginUtils.traceTenantId());

        List<PerformanceBaseDataResult> dataList = this.listPerformanceBaseData(query);
        log.info("getProcessNameList.query运行时间：{},数据量:{}", System.currentTimeMillis() - start, dataList.size());
        if (CollectionUtils.isEmpty(dataList)) {
            return Lists.newArrayList();
        }
        return dataList.stream().map(data -> data.getAppIp() + "|" + data.getAgentId()).distinct().collect(Collectors.toList());
    }

    @Override
    public PerformanceBaseDataResult getOnePerformanceBaseData(PerformanceBaseQueryParam param) {
        long start = System.currentTimeMillis();
        Date startTime = DateUtil.getDate(param.getStartTime());
        Date endTime = DateUtil.getDate(param.getEndTime());
        PerformanceBaseDataQuery query = new PerformanceBaseDataQuery();
        query.setAppName(param.getAppName());
        query.setAppIp(param.getAppIp());
        if (startTime != null) {
            query.setStartTime(startTime.getTime());
        }
        if (endTime != null) {
            query.setEndTime(endTime.getTime());
        }
        query.setTenantId(WebPluginUtils.traceTenantId());
        query.setLimit(1);
        List<PerformanceBaseDataResult> dataList = this.listPerformanceBaseData(query);

        log.info("getOnePerformanceBaseData.querySingle运行时间:{}", System.currentTimeMillis() - start);
        if (CollectionUtils.isNotEmpty(dataList)){
            return dataList.get(0);
        }
        return new PerformanceBaseDataResult();
    }

    @Override
    public List<PerformanceBaseDataResult> getPerformanceBaseDataList(PerformanceBaseQueryParam param) {
        long start = System.currentTimeMillis();
        Date startTime = DateUtil.getDate(param.getStartTime());
        Date endTime = DateUtil.getDate(param.getEndTime());
        PerformanceBaseDataQuery query = new PerformanceBaseDataQuery();
        query.setAppName(param.getAppName());
        query.setAppIp(param.getAppIp());
        query.setAgentId(param.getAgentId());
        if (startTime != null) {
            query.setStartTime(startTime.getTime());
        }
        if (endTime != null) {
            query.setEndTime(endTime.getTime());
        }
        query.setTenantId(WebPluginUtils.traceTenantId());
        List<PerformanceBaseDataResult> dataList = this.listPerformanceBaseData(query);
        log.info("getPerformanceBaseDataList.query运行时间:{},数据量:{}", System.currentTimeMillis() - start, dataList.size());
        if (CollectionUtils.isEmpty(dataList)) {
            return Lists.newArrayList();
        }
        return dataList;
    }


    private List<PerformanceBaseDataResult> listPerformanceBaseData(PerformanceBaseDataQuery query) {
        try {
            query.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
            query.setEnvCode(WebPluginUtils.traceEnvCode());

            HttpMethod httpMethod = HttpMethod.POST;
            AmdbResult<List<PerformanceBaseDataAll>> amdbResponse = AmdbHelper.builder().httpMethod(httpMethod)
                    .url(properties.getUrl().getAmdb() + AMDB_PERFORMANCE_QUERY_LIST_PATH)
                    .param(query)
                    .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                    .eventName("查询性能基础数据失败")
                    .list(PerformanceBaseDataAll.class);
            return this.convert(amdbResponse.getData());
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage(), e);
        }
    }

    private List<PerformanceBaseDataResult> convert(List<PerformanceBaseDataAll> performanceBaseDataAlls) {
        List<PerformanceBaseDataResult> results = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(performanceBaseDataAlls)) {
            performanceBaseDataAlls.forEach(performanceBaseDataAll -> {
                PerformanceBaseDataResult performanceBaseDataResult = new PerformanceBaseDataResult();
                performanceBaseDataResult.setAgentId(performanceBaseDataAll.getAgentId());
                performanceBaseDataResult.setAppName(performanceBaseDataAll.getAppName());
                performanceBaseDataResult.setAppIp(performanceBaseDataAll.getAppIp());
                performanceBaseDataResult.setProcessId(StringUtils.isNotBlank(performanceBaseDataAll.getProcessId()) ? Long.parseLong(performanceBaseDataAll.getProcessId()) : null);
                performanceBaseDataResult.setProcessName(performanceBaseDataAll.getProcessName());
                performanceBaseDataResult.setTimestamp(performanceBaseDataAll.getTimestamp());
                performanceBaseDataResult.setTime(DateUtil.formatTime(performanceBaseDataAll.getTime()));
                performanceBaseDataResult.setTotalMemory(performanceBaseDataAll.getTotalMemory() != null ? performanceBaseDataAll.getTotalMemory() * 1.00D : 0.0D);
                performanceBaseDataResult.setPermMemory(performanceBaseDataAll.getPermMemory() != null ? performanceBaseDataAll.getPermMemory() * 1.00D : 0.0D);
                performanceBaseDataResult.setYoungMemory(performanceBaseDataAll.getYoungMemory() != null ? performanceBaseDataAll.getYoungMemory() * 1.00D : 0.0D);
                performanceBaseDataResult.setOldMemory(performanceBaseDataAll.getOldMemory() != null ? performanceBaseDataAll.getOldMemory() * 1.00D : 0.0D);
                performanceBaseDataResult.setYoungGcCount(performanceBaseDataAll.getYoungGcCount());
                performanceBaseDataResult.setFullGcCount(performanceBaseDataAll.getFullGcCount());
                performanceBaseDataResult.setYoungGcCost(performanceBaseDataAll.getYoungGcCost());
                performanceBaseDataResult.setFullGcCost(performanceBaseDataAll.getFullGcCost());
                performanceBaseDataResult.setBaseId(performanceBaseDataAll.getBaseId() + "");
                performanceBaseDataResult.setCpuUseRate(performanceBaseDataAll.getCpuUseRate() != null ? performanceBaseDataAll.getCpuUseRate().doubleValue() : 0.00D);
                performanceBaseDataResult.setThreadCount(performanceBaseDataAll.getThreadCount());
                performanceBaseDataResult.setTotalNonHeapMemory(performanceBaseDataAll.getTotalNoHeapMemory() != null ? performanceBaseDataAll.getTotalNoHeapMemory() * 1.00D : 0.0D);
                performanceBaseDataResult.setTotalBufferPoolMemory(performanceBaseDataAll.getTotalBufferPoolMemory() != null ? performanceBaseDataAll.getTotalBufferPoolMemory() * 1.00D : 0.0D);
                results.add(performanceBaseDataResult);
            });
        }
        return results;
    }

}
