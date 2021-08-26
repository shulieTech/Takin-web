package io.shulie.takin.web.data.dao.perfomanceanaly;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.shulie.takin.web.data.common.InfluxDatabaseWriter;
import io.shulie.takin.web.data.mapper.mysql.PressureMachineMapper;
import io.shulie.takin.web.data.param.perfomanceanaly.PressureMachineStatisticsInsertParam;
import io.shulie.takin.web.data.param.perfomanceanaly.PressureMachineStatisticsQueryParam;
import io.shulie.takin.web.data.result.perfomanceanaly.PressureMachineStatisticsDTO;
import io.shulie.takin.web.data.result.perfomanceanaly.PressureMachineStatisticsResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author mubai
 * @date 2020-11-13 11:43
 */

@Component
public class PressureMachineStatisticsDaoImpl implements PressureMachineStatisticsDao {

    @Resource
    private PressureMachineMapper pressureMachineMapper;

    @Autowired
    private InfluxDatabaseWriter influxDatabaseWriter;

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
        influxDatabaseWriter.insert("t_pressure_machine_statistics", tags, fields, System.currentTimeMillis());
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
                " TZ('Asia/Shanghai')";

        List<PressureMachineStatisticsResult> dataList = influxDatabaseWriter.query(influxDatabaseSql,
                PressureMachineStatisticsResult.class);

        if (CollectionUtils.isEmpty(dataList)) {
            return Lists.newArrayList();
        }
        return dataList;

    }

    @Override
    public PressureMachineStatisticsResult getNewlyStatistics() {
        String influxDatabaseSql = "select" +
                " machine_total ,machine_pressured,machine_free,machine_offline,time as gmtCreate  " +
                " from t_pressure_machine_statistics" +
                " order by time desc limit 1 " +
                " TZ('Asia/Shanghai')";

        List<PressureMachineStatisticsResult> dataList = influxDatabaseWriter.query(influxDatabaseSql,
                PressureMachineStatisticsResult.class);

        if (CollectionUtils.isEmpty(dataList)) {
            return new PressureMachineStatisticsResult();
        }
        return dataList.get(0);

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

    @Override
    public void clearRubbishData(String time) {
        if (StringUtils.isBlank(time)) {
            return;
        }
        String influxDatabaseSql = "delete" +
                " from t_pressure_machine_statistics" +
                " where time <= '" + time + "'";

        influxDatabaseWriter.query(influxDatabaseSql, PressureMachineStatisticsResult.class);
    }
}
