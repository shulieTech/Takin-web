package io.shulie.takin.web.data.dao.perfomanceanaly;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.shulie.takin.web.data.mapper.mysql.PressureMachineMapper;
import io.shulie.takin.web.data.param.perfomanceanaly.PressureMachineStatisticsInsertParam;
import io.shulie.takin.web.data.param.perfomanceanaly.PressureMachineStatisticsQueryParam;
import io.shulie.takin.web.data.result.perfomanceanaly.PressureMachineStatisticsDTO;
import io.shulie.takin.web.data.result.perfomanceanaly.PressureMachineStatisticsResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.stereotype.Component;

/**
 * @author mubai
 * @date 2020-11-13 11:43
 */

@Component
public class PressureMachineStatisticsDaoImpl implements PressureMachineStatisticsDao {

    @Resource
    private PressureMachineMapper pressureMachineMapper;

    @Override
    public void insert(PressureMachineStatisticsInsertParam param) {
        influxInsert(param);
    }


    private void influxInsert(PressureMachineStatisticsInsertParam baseEntity) {
        Map<String, Object> fields = Maps.newHashMap();
        fields.put("machine_total", baseEntity.getMachineTotal());
        fields.put("machine_pressured", baseEntity.getMachinePressured());
        fields.put("machine_free", baseEntity.getMachineFree());
        fields.put("machine_offline", baseEntity.getMachineOffline());
        Map<String, String> tags = Maps.newHashMap();
        tags.put("tenant_id", String.valueOf(WebPluginUtils.traceTenantId()));
        tags.put("tenant_app_key", WebPluginUtils.traceTenantAppKey() + "");
        tags.put("env_code", String.valueOf(WebPluginUtils.traceEnvCode()));
//        influxDatabaseWriter.insert("t_pressure_machine_statistics", tags, fields, System.currentTimeMillis());
    }


    @Override
    public List<PressureMachineStatisticsResult> queryByExample(PressureMachineStatisticsQueryParam param) {
        // 采用influxdb 进行查询
        return influxdbQuery(param);
    }

    List<PressureMachineStatisticsResult> influxdbQuery(PressureMachineStatisticsQueryParam param) {
        String influxDatabaseSql = "select" +
                " machine_total ,machine_pressured,machine_free,machine_offline,time as gmtCreate  " +
                " from t_pressure_machine_statistics" +
                " where " +
                "  time >= " + "'" + param.getStartTime() + "'" +
                " and time <= " + "'" + param.getEndTime() + "'" +
                " and tenant_id = " + "'" + WebPluginUtils.traceTenantId() + "'" +
                " and env_code = " + "'" + WebPluginUtils.traceEnvCode() + "'" +
                " TZ('Asia/Shanghai')";
        return Lists.newArrayList();
//        List<PressureMachineStatisticsResult> dataList = influxDatabaseWriter.query(influxDatabaseSql,
//                PressureMachineStatisticsResult.class);
//
//        if (CollectionUtils.isEmpty(dataList)) {
//            return Lists.newArrayList();
//        }
//        return dataList;

    }

    @Override
    public PressureMachineStatisticsResult getNewlyStatistics() {
        String influxDatabaseSql = "select" +
                " machine_total ,machine_pressured,machine_free,machine_offline,time as gmtCreate  " +
                " from t_pressure_machine_statistics" +
                " where  tenant_id = " + "'" + WebPluginUtils.traceTenantId() + "'" +
                " and env_code = " + "'" + WebPluginUtils.traceEnvCode() + "'" +
                " order by time desc limit 1 " +
                " TZ('Asia/Shanghai')";

        return new PressureMachineStatisticsResult();
//        List<PressureMachineStatisticsResult> dataList = influxDatabaseWriter.query(influxDatabaseSql,
//                PressureMachineStatisticsResult.class);
//
//        if (CollectionUtils.isEmpty(dataList)) {
//            return new PressureMachineStatisticsResult();
//        }
//        return dataList.get(0);

    }

    @Override
    public PressureMachineStatisticsResult statistics() {

        PressureMachineStatisticsResult result = new PressureMachineStatisticsResult();
        List<PressureMachineStatisticsDTO> statistics = pressureMachineMapper.statistics();
        Long totalNum = 0L;
        for (PressureMachineStatisticsDTO dto : statistics) {
            if (dto.getStatus() == -1) {
                result.setMachineOffline(dto.getCount().intValue());
            } else if (dto.getStatus() == 0) {
                result.setMachineFree(dto.getCount().intValue());
            } else if (dto.getStatus() == 1) {
                result.setMachinePressured(dto.getCount().intValue());
            }
            totalNum += dto.getCount();
        }
        result.setMachineTotal(totalNum.intValue());
        return result;
    }


}
