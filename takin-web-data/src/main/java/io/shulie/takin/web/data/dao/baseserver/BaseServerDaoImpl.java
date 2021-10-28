package io.shulie.takin.web.data.dao.baseserver;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.pamirs.takin.entity.domain.entity.linkmanage.figure.RpcType;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.data.common.InfluxDatabaseManager;
import io.shulie.takin.web.data.param.baseserver.BaseServerParam;
import io.shulie.takin.web.data.param.baseserver.InfluxAvgParam;
import io.shulie.takin.web.data.param.baseserver.ProcessBaseRiskParam;
import io.shulie.takin.web.data.param.baseserver.ProcessOverRiskParam;
import io.shulie.takin.web.data.param.baseserver.TimeMetricsDetailParam;
import io.shulie.takin.web.data.param.baseserver.TimeMetricsParam;
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
    private InfluxDatabaseManager influxDatabaseManager;
    private static final String REAL_TIME_DATABASE = "pradar";

    @Override
    public Collection<BaseServerResult> queryList(BaseServerParam param) {
        String sql = "select app_ip, app_name, cpu_rate, mem_rate from app_base_data where time>" + param.getStartTime()
                + " and time <= " + param.getEndTime() + " and tag_app_name='" + param.getApplicationName() + "'" +
            // 增加租户
            " and tenant_id = '" + WebPluginUtils.traceTenantId() + "'" +
            " and env_code = '" + WebPluginUtils.traceEnvCode() + "'";
        return influxDatabaseManager.query(BaseServerResult.class, sql);
    }

    @Override
    public Collection<BaseServerResult> queryBaseServer(BaseServerParam param) {
        long startTime = System.currentTimeMillis();
        String baseSql = "select max(memory) as memory,max(disk) as disk,max(cpu_cores) as cpu_cores ," +
                "mean(net_bandwidth) as net_bandwidth from app_base_data where tag_app_name = '" + param.getApplicationName()
                + "' and time > " + param.getStartTime() + " and time <= " + param.getEndTime() +
                // 增加租户
                " and tenant_id = '" + WebPluginUtils.traceTenantId() + "'" +
                " and env_code = '" + WebPluginUtils.traceEnvCode() + "'"
                + " group by tag_agent_id, tag_app_ip";
        Collection<BaseServerResult> baseServerResults = influxDatabaseManager.query(BaseServerResult.class, baseSql);
        log.info("queryBaseServer influxdb sql :{},cost time :{}", baseSql, System.currentTimeMillis() - startTime);
        return baseServerResults;
    }

    @Override
    public Collection<InfluxAvgResult> queryTraceId(InfluxAvgParam param) {
        String command = "select traceId,rt from tro_pradar where time >= " + param.getSTime() + " and time < " + param.getETime()
                + " and appName = '" + param.getAppName() + "'  and event = '" + param.getEvent() + "' and ptFlag='true' and traceId <>'' " +
                // 增加租户
                " and tenant_id = '" + WebPluginUtils.traceTenantId() + "'" +
                " and env_code = '" + WebPluginUtils.traceEnvCode() + "'"
                + " order by time desc limit 1000";

        return influxDatabaseManager.query(InfluxAvgResult.class, command, REAL_TIME_DATABASE);
    }

    @Override
    public LinkDetailResult queryTimeMetricsDetail(TimeMetricsDetailParam param) {
        LinkDetailResult linkDetailResult = new LinkDetailResult();
        long sTime = param.getSTime();
        long eTime = param.getETime();
        String event = param.getEvent();
        String rpcType = param.getRpcType();
        String appName = param.getAppName();
        String invokeApp = param.getInvokeApp();
        try {
            if (StringUtils.isBlank(param.getEvent())) {
                return linkDetailResult;
            }

            //需要增加作为入口
            String command = "select max(rt) as maxRt,min(rt) as minRt ,sum(totalRt) as rt,MEAN(totalQps) as tps," +
                    "SUM(totalCount) as count,SUM(errorCount) as errorCount " +
                    "from  tro_pradar where ptFlag='true' and time >= " + sTime + " and time < " + eTime +
                     // 增加租户
                    " and tenant_id = '" + WebPluginUtils.traceTenantId() + "'" +
                    " and env_code = '" + WebPluginUtils.traceEnvCode() + "'";

            //TODO
            if (RpcType.TRACE.getText().equals(rpcType)) {
                command = command + " and appName = '" + appName + "'  and event = '" + event + "'";
            } else if (RpcType.DB.getText().equals(rpcType)) {
                command = command + " and appName = '" + invokeApp + "' and callEvent = '" + event
                        + "' and callType = 'call-db'";
                linkDetailResult.setAppName(invokeApp);
            } else if (RpcType.CACHE.getText().equals(rpcType)) {
                command = command + " and appName = '" + invokeApp + "' and callEvent = '" + event
                        + "' and (callType = 'call-cache-read' or callType = 'call-cache-write' or callType='call-cache')";
                linkDetailResult.setAppName(invokeApp);
            } else if (RpcType.ROCKETMQ.getText().equals(rpcType)) {
                command = command + " and appName = '" + invokeApp + "' and callEvent = '" + event
                        + "' and callType = 'call-rocketmq'";
                linkDetailResult.setAppName(invokeApp);
            } else if (RpcType.ROCKETMQ_RCV.getText().equals(rpcType)) {
                command = command + " and appName = '" + invokeApp + "' and event = '" + event + "'";
                linkDetailResult.setAppName(invokeApp);
            } else if (RpcType.RABBITMQ_CLIENT.getText().equals(rpcType)) {
                command = command + " and appName = '" + invokeApp + "' and callEvent = '" + event
                        + "' and callType = 'call-rabbitmq'";
                linkDetailResult.setAppName(invokeApp);
            } else if (RpcType.RABBITMQ_RCV.getText().equals(rpcType)) {
                command = command + " and appName = '" + invokeApp + "' and event = '" + event + "'";
                linkDetailResult.setAppName(invokeApp);
            } else if (RpcType.KAFKA_CLIENT.getText().equals(rpcType)) {
                command = command + " and appName = '" + invokeApp + "' and callType = 'call-kafka'";
                linkDetailResult.setAppName(invokeApp);
            } else if (RpcType.KAFKA_RCV.getText().equals(rpcType)) {
                command = command + " and appName = '" + invokeApp + "' and event = '" + event + "'";
                linkDetailResult.setAppName(invokeApp);
            } else if (RpcType.DUBBO.getText().equals(rpcType) || RpcType.DUBBO_SERVER.getText().equals(rpcType)) {
                command = command + " and appName = '" + invokeApp + "' and callType = 'call-dubbo' and callEvent =~ /"
                        + event + "/";
                linkDetailResult.setAppName(appName);
            } else if (RpcType.HTTP_SERVER.getText().equals(rpcType) || RpcType.HTTP
                    .getText().equals(rpcType)) {
                command = command + " and appName = '" + appName + "' and event = '" + event
                        + "' and entryFlag = 'true'";
            } else {
                logger.error("Unsupport metrics query RpcType " + rpcType);
                return linkDetailResult;
            }
            Collection<InfluxAvgResult> influxAvgVoList = influxDatabaseManager.query(InfluxAvgResult.class, command, REAL_TIME_DATABASE);
            if (CollectionUtils.isEmpty(influxAvgVoList)) {
                return linkDetailResult;
            }
            if (CollectionUtils.isNotEmpty(influxAvgVoList)) {
                InfluxAvgResult influxAvgVO = influxAvgVoList.stream().findFirst().get();
                if (influxAvgVO.getCount() == 0.0) {
                    linkDetailResult.setAvgRt(0.0);
                } else {
                    linkDetailResult.setAvgRt(formatDouble(influxAvgVO.getRt() / influxAvgVO.getCount()).doubleValue());
                }
                linkDetailResult.setTotalCount(formatDouble(influxAvgVO.getCount()).intValue());
                linkDetailResult.setTps(formatDouble(influxAvgVO.getTps()).doubleValue());
                linkDetailResult.setMaxRt(formatDouble(influxAvgVO.getMaxRt()).doubleValue());
                linkDetailResult.setMinRt(formatDouble(influxAvgVO.getMinRt()).doubleValue());
            }
        } catch (Exception e) {
            logger.error("QueryTimeMetrics Error:{}", event);
        }
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
        try {
            if (StringUtils.isBlank(event)) {
                return linkDataResult;
            }
            String command =
                    "select sum(totalRt) as rt,MEAN(totalQps) as tps,SUM(totalCount) as count,SUM(errorCount) as "
                            + "errorCount "
                            +
                            "from tro_pradar where ptFlag='true' and time >= " + sTime + " and time < " + eTime;
            if (RpcType.TRACE.getText().equals(rpcType)) {
                command = command + " and appName = '" + appName + "'  and event = '" + event + "'";
            }
            if (RpcType.DB.getText().equals(rpcType)) {
                command = command + " and appName = '" + invokeApp + "' and callType = 'call-db'";
                linkDataResult.setAppName(invokeApp);
            }
            if (RpcType.CACHE.getText().equals(rpcType)) {
                command = command + " and appName = '" + invokeApp
                        + "' and (callType = 'call-cache-read' or callType = 'call-cache-write' or callType='call-cache')";
                linkDataResult.setAppName(invokeApp);
            }
            if (RpcType.ROCKETMQ.getText().equals(rpcType)) {
                command = command + " and appName = '" + invokeApp + "' and callType = 'call-rocketmq'";
                linkDataResult.setAppName(invokeApp);
            }
            if (RpcType.DUBBO.getText().equals(rpcType) || RpcType.DUBBO_SERVER.getText().equals(rpcType)) {
                command = command + " and appName = '" + invokeApp + "' and callType = 'call-dubbo' and callEvent =~ /"
                        + event + "/";
                linkDataResult.setAppName(appName);
            }
            if (RpcType.HTTP_SERVER.getText().equals(rpcType) || RpcType.HTTP
                    .getText().equals(rpcType)) {
                command = command + " and appName = '" + appName + "' and event = '" + event
                        + "' and entryFlag = 'true'";
            }
            Collection<InfluxAvgResult> influxAvgVoList = influxDatabaseManager.query(InfluxAvgResult.class, command,
                    REAL_TIME_DATABASE);
            if (CollectionUtils.isEmpty(influxAvgVoList)) {
                return linkDataResult;
            }
            if (CollectionUtils.isNotEmpty(influxAvgVoList)) {
                InfluxAvgResult influxAvgVO = influxAvgVoList.stream().findFirst().get();
                if (influxAvgVO.getCount() == 0.0) {
                    linkDataResult.setRt(0.0);
                } else {
                    linkDataResult.setRt(formatDouble(influxAvgVO.getRt() / influxAvgVO.getCount()).doubleValue());
                }
                linkDataResult.setErrorCount(influxAvgVO.getErrorCount().intValue());
                linkDataResult.setTps(formatDouble(influxAvgVO.getTps()).doubleValue());
            }
        } catch (Exception e) {
            logger.error("QueryTimeMetrics Error:{}", event);
        }
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
            String tmpSql =
                    "select max(cpu_rate) as cpu_rate,max(cpu_load) as cpu_load,max(mem_rate)as mem_rate,max(iowait) as "
                            + "iowait ,max(net_bandwidth_rate)as net_bandwidth_rate "
                            +
                            " from app_base_data where app_name = '" + appName + "' and time >= " + startTime
                            + " and time <= " + endTime +
                            // 增加租户
                            " and tenant_id = '" + WebPluginUtils.traceTenantId() + "'" +
                            " and env_code = '" + WebPluginUtils.traceEnvCode() + "'"
                            + " group by tag_app_ip";
            Collection<BaseServerResult> voList = influxDatabaseManager.query(BaseServerResult.class, tmpSql);
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
                risk.setAppIp(vo.getTagAppIp());
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
        StringBuilder sb = new StringBuilder("select mean(cpu_rate) as cpu_rate from app_base_data where");
        sb.append(" time >= ").append(param.getStartTime());
        sb.append(" and time <= ").append(param.getEndTime());
        sb.append(" and tag_app_name = '").append(param.getApplicationName()).append("'");
        sb.append(" and tag_app_ip = '").append(param.getAppIp()).append("'");
        if (param.getAgentId() != null) {
            sb.append(" and agent_id = '").append(param.getAgentId()).append("'");
        }
        // 增加租户
        sb.append(" and tenant_id = '").append(WebPluginUtils.traceTenantId()).append("'");
        sb.append(" and env_code = '").append(WebPluginUtils.traceEnvCode()).append("'");
        sb.append(" group by time(5s) order by time");
        Collection<BaseServerResult> collection = influxDatabaseManager.query(BaseServerResult.class, sb.toString());
        log.info("queryBaseData.query<app_base_data>:{},数据量:{}", System.currentTimeMillis() - start, collection.size());
        return collection;
    }

    private BigDecimal formatDouble(Double data) {
        if (data == null) {
            return new BigDecimal("0");
        }
        BigDecimal b = BigDecimal.valueOf(data);
        return b.setScale(2, RoundingMode.HALF_UP);
    }

}
