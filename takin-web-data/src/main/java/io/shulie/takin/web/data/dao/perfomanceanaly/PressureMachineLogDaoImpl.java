package io.shulie.takin.web.data.dao.perfomanceanaly;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.shulie.takin.web.data.param.machine.PressureMachineLogInsertParam;
import io.shulie.takin.web.data.param.machine.PressureMachineLogQueryParam;
import io.shulie.takin.web.data.result.perfomanceanaly.PressureMachineLogResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.stereotype.Component;

/**
 * @author mubai
 * @date 2020-11-16 11:14
 */

@Component
public class PressureMachineLogDaoImpl implements PressureMachineLogDao {

    @Override
    public void insert(PressureMachineLogInsertParam param) {

        influxDbInsert(param);

    }


    @Override
    public List<PressureMachineLogResult> queryList(PressureMachineLogQueryParam queryParam) {
        return influxDbQuery(queryParam);
    }

    void influxDbInsert(PressureMachineLogInsertParam param) {
        Map<String, Object> fields = Maps.newHashMap();
        fields.put("name", param.getName());
        fields.put("ip", param.getIp());
        fields.put("flag", param.getFlag());
        fields.put("cpu", param.getCpu());
        fields.put("memory", param.getMemory());
        fields.put("machine_usage", param.getMachineUsage());
        fields.put("disk", param.getDisk());
        fields.put("cpu_usage", param.getCpuUsage());
        fields.put("cpu_load", param.getCpuLoad());
        fields.put("memory_used", param.getMemoryUsed());
        fields.put("disk_io_wait", param.getDiskIoWait());
        fields.put("transmitted_total", param.getTransmittedTotal());
        fields.put("transmitted_in", param.getTransmittedIn());
        fields.put("transmitted_in_usage", param.getTransmittedInUsage());
        fields.put("transmitted_out", param.getTransmittedOut());
        fields.put("transmitted_out_usage", param.getTransmittedOutUsage());
        fields.put("transmitted_usage", param.getTransmittedUsage());
        fields.put("scene_names", param.getSceneNames());
        fields.put("status", param.getStatus());

        Map<String, String> tags = Maps.newHashMap();
        tags.put("machine_id", String.valueOf(param.getMachineId()));
        tags.put("tenant_id", String.valueOf(WebPluginUtils.traceTenantId()));
        tags.put("tenant_app_key", WebPluginUtils.traceTenantAppKey() + "");
        tags.put("env_code", String.valueOf(WebPluginUtils.traceEnvCode()));
//        influxDatabaseWriter.insert("t_pressure_machine_log", tags, fields, System.currentTimeMillis());


    }


    List<PressureMachineLogResult> influxDbQuery(PressureMachineLogQueryParam param) {
        String influxDaatabaseSql = "select" +
                " cpu_usage,cpu_load,memory_used,disk_io_wait,transmitted_usage,time as gmtCreate " +
                " from t_pressure_machine_log" +
                " where " +
                "  time >= " + "'" + param.getStartTime() + "'" +
                " and time <= " + "'" + param.getEndTime() + "'" +
                " and tenant_id = " + "'" + WebPluginUtils.traceTenantId() + "'" +
                " and env_code = " + "'" + WebPluginUtils.traceEnvCode() + "'" +
                " and time <= " + "'" + param.getEndTime() + "'" +
                " and machine_id =" + "'" + param.getMachineId() + "'" +
                " TZ('Asia/Shanghai')";
        return Lists.newArrayList();
//        List<PressureMachineLogResult> dataList = influxDatabaseWriter.query(influxDaatabaseSql,
//                PressureMachineLogResult.class);
//
//        if (CollectionUtils.isEmpty(dataList)) {
//            return Lists.newArrayList();
//        }
//        return dataList;
    }
}
