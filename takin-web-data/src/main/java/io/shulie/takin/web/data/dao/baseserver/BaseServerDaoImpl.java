package io.shulie.takin.web.data.dao.baseserver;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import com.google.common.collect.Lists;
import io.shulie.takin.web.amdb.bean.common.AmdbResult;
import io.shulie.takin.web.amdb.util.AmdbHelper;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.param.baseserver.*;
import io.shulie.takin.web.data.result.baseserver.BaseServerResult;
import io.shulie.takin.web.data.result.baseserver.InfluxAvgResult;
import io.shulie.takin.web.data.result.baseserver.LinkDetailResult;
import io.shulie.takin.web.data.result.risk.BaseRiskResult;
import io.shulie.takin.web.data.result.risk.LinkDataResult;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.takin.properties.AmdbClientProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

/**
 * @author mubai
 * @date 2020-10-26 16:04
 */

@Service
@Slf4j
public class BaseServerDaoImpl implements BaseServerDao {
    private static final Logger logger = LoggerFactory.getLogger(BaseServerDaoImpl.class);

    @Autowired
    private AmdbClientProperties properties;

    private static final String AMDB_ENGINE_PRESSURE_QUERY_LIST_PATH = "/amdb/db/api/appBaseData/queryListMap";

    @Override
    public Collection<BaseServerResult> queryBaseServer(BaseServerParam param) {
        long startTime = System.currentTimeMillis();
        AppBaseDataQuery query = new AppBaseDataQuery();
        Map<String, String> fieldAndAlias = new HashMap<>();
        fieldAndAlias.put("agent_id", null);
        fieldAndAlias.put("app_ip", null);
        fieldAndAlias.put("max(memory)", "memory");
        fieldAndAlias.put("max(disk)", "disk");
        fieldAndAlias.put("max(cpu_cores)", "cpu_cores");
        fieldAndAlias.put("avg(net_bandwidth)", "net_bandwidth");
        query.setFieldAndAlias(fieldAndAlias);
        query.setStartTime(param.getStartTime());
        query.setEndTime(param.getEndTime());
        query.setAppName(param.getApplicationName());
        List<String> fields = new ArrayList<>();
        fields.add("agent_id");
        fields.add("app_ip");
        query.setGroupByFields(fields);
        List<BaseServerResult> baseServerResults = this.listBaseServerResult(query);
        log.debug("queryBaseServer ,cost time :{}", System.currentTimeMillis() - startTime);
        return baseServerResults;
    }

    @Override
    public Collection<InfluxAvgResult> queryTraceId(InfluxAvgParam param) {
        //接口已经换掉，不使用当前逻辑了
        return null;
    }

    @Override
    public LinkDetailResult queryTimeMetricsDetail(TimeMetricsDetailParam param) {
        LinkDetailResult linkDetailResult = new LinkDetailResult();
        //接口已经换掉，不使用当前逻辑了
        return linkDetailResult;
    }

    @Override
    public LinkDataResult queryTimeMetrics(TimeMetricsParam param) {
        long sTime = param.getSTime();
        long eTime = param.getETime();
        String event = param.getEvent();
        String rpcType = param.getRpcType();
        String appName = param.getAppName();
        String invokeApp = param.getInvokeApp();
        LinkDataResult linkDataResult = new LinkDataResult();
        //Todo 这里不应该在查询tro_pradar表了，后续再进行修复

        return linkDataResult;
    }

    @Override
    public List<BaseRiskResult> queryProcessBaseRisk(ProcessBaseRiskParam param) {
        List<String> appNames = param.getAppNames();
        long endTime = param.getEndTime();
        long startTime = param.getStartTime();
        Long reportId = param.getReportId();

        List<BaseRiskResult> results = Lists.newArrayList();
        if (CollectionUtils.isEmpty(appNames)) {
            return results;
        }
        appNames.forEach(appName -> {
            AppBaseDataQuery query = new AppBaseDataQuery();
            Map<String, String> fieldAndAlias = new HashMap<>();
            fieldAndAlias.put("app_ip", null);
            fieldAndAlias.put("max(cpu_rate)", "cpu_rate");
            fieldAndAlias.put("max(cpu_load)", "cpu_load");
            fieldAndAlias.put("max(mem_rate)", "mem_rate");
            fieldAndAlias.put("max(iowait)", "iowait");
            fieldAndAlias.put("max(net_bandwidth_rate)", "net_bandwidth_rate");
            query.setFieldAndAlias(fieldAndAlias);
            query.setStartTime(startTime);
            query.setEndTime(endTime);
            query.setAppName(appName);
            query.setGroupByFields(Collections.singletonList("app_ip"));
            List<BaseServerResult> voList = this.listBaseServerResult(query);
            if (CollectionUtils.isEmpty(voList)) {
                return;
            }

            // 最大 cpu 使用率
            double scale = Double.parseDouble(
                ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_RISK_MAX_NORM_SCALE));

            // 最大 cpu load
            int maxLoad = Integer.parseInt(
                ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_RISK_MAX_NORM_MAX_LOAD));

            voList.forEach(vo -> {
                BaseRiskResult risk = new BaseRiskResult();
                risk.setAppIp(vo.getAppIp());
                risk.setAppName(appName);
                risk.setReportId(reportId);

                StringBuilder builder = new StringBuilder();
                if (vo.getCpuRate() >= scale) {
                    builder.append("最大CPU使用率 ").append(formatDouble(vo.getCpuRate())).append("%;");
                }
                if (vo.getCpuLoad() >= maxLoad) {
                    builder.append("最大CPU Load ").append(formatDouble(vo.getCpuLoad())).append(";");
                }
                if (vo.getMemRate() >= scale) {
                    builder.append("最大内存使用率 ").append(formatDouble(vo.getMemRate())).append("%;");
                }
                if (vo.getIoWait() >= scale) {
                    builder.append("最大IO等待率 ").append(formatDouble(vo.getIoWait())).append("%;");
                }
                if (vo.getNetBandWidthRate() >= scale) {
                    builder.append("最大网络带宽使用率 ").append(formatDouble(vo.getNetBandWidthRate())).append("%;");
                }
                //  判断是否符合规则
                String content = builder.toString();
                if (StringUtils.isNotBlank(content)) {
                    risk.setContent(content.substring(0, content.lastIndexOf(";")));
                    results.add(risk);
                }
            });
        });
        return results;
    }

    /**
     * 超过tps的时候
     *
     * @param  param-
     *
     * @return -
     */
    @Override
    public List<BaseRiskResult> queryProcessOverRisk(ProcessOverRiskParam param) {
        return null;
    }


    @Override
    public Collection<BaseServerResult> queryBaseData(BaseServerParam param) {
        long start = System.currentTimeMillis();
//        sb.append(" group by time(5s) order by time");
        AppBaseDataQuery query = new AppBaseDataQuery();
        Map<String, String> fieldAndAlias = new HashMap<>();
        fieldAndAlias.put("time", null);
        fieldAndAlias.put("avg(cpu_rate)", "cpu_rate");
        query.setFieldAndAlias(fieldAndAlias);
        query.setStartTime(param.getStartTime());
        query.setEndTime(param.getEndTime());
        query.setAppName(param.getApplicationName());
        query.setAppId(param.getAppIp());
        if (param.getAgentId() != null){
            query.setAgentId(param.getAgentId());
        }
        query.setGroupByFields(Collections.singletonList("time"));

        List<BaseServerResult> baseServerResults = this.listBaseServerResult(query);
        log.info("queryBaseData.query<app_base_data>:{},数据量:{}", System.currentTimeMillis() - start, baseServerResults.size());
        return baseServerResults;
    }

    public List<BaseServerResult> listBaseServerResult(AppBaseDataQuery query) {
        try {
            query.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
            query.setEnvCode(WebPluginUtils.traceEnvCode());

            HttpMethod httpMethod = HttpMethod.POST;
            AmdbResult<List<BaseServerResult>> amdbResponse = AmdbHelper.builder().httpMethod(httpMethod)
                    .url(properties.getUrl().getAmdb() + AMDB_ENGINE_PRESSURE_QUERY_LIST_PATH)
                    .param(query)
                    .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                    .eventName("查询enginePressure数据")
                    .list(BaseServerResult.class);
            return amdbResponse.getData();
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage(), e);
        }
    }

    private BigDecimal formatDouble(Double data) {
        if (data == null) {
            return new BigDecimal("0");
        }
        BigDecimal b = BigDecimal.valueOf(data);
        return b.setScale(2, RoundingMode.HALF_UP);
    }



}
