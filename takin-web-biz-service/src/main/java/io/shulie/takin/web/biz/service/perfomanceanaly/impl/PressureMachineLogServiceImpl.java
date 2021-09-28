package io.shulie.takin.web.biz.service.perfomanceanaly.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pamirs.takin.common.util.DateUtils;
import io.shulie.takin.web.biz.pojo.request.perfomanceanaly.PressureMachineLogQueryRequest;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.PressureMachineLogResponse;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.TypeValueDateVo;
import io.shulie.takin.web.biz.service.perfomanceanaly.PressureMachineLogService;
import io.shulie.takin.web.data.dao.perfomanceanaly.PressureMachineLogDao;
import io.shulie.takin.web.data.param.machine.PressureMachineLogQueryParam;
import io.shulie.takin.web.data.result.perfomanceanaly.PressureMachineLogResult;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author mubai
 * @date 2020-11-16 13:59
 */

@Service
public class PressureMachineLogServiceImpl implements PressureMachineLogService {
    @Autowired
    private PressureMachineLogDao pressureMachineLogDao;

    @Override
    public PressureMachineLogResponse queryByExample(PressureMachineLogQueryRequest request) {

        String dayStartTime = DateUtils.getDayStartTime(request.getQueryTime());
        String dayEndTime = DateUtils.getDayEndTime(request.getQueryTime());
        if (dayEndTime == null || dayStartTime == null) {
            throw new RuntimeException("时间的格式不正确");
        }
        PressureMachineLogQueryParam queryParam = new PressureMachineLogQueryParam();
        queryParam.setStartTime(dayStartTime);
        queryParam.setEndTime(dayEndTime);
        queryParam.setMachineId(request.getMachineId());
        queryParam.setCurrent(0);
        queryParam.setPageSize(-1);
        List<PressureMachineLogResult> dbList = pressureMachineLogDao.queryList(queryParam);
        List<PressureMachineLogResult> pressureMachineLogResults = pointSample(dbList, 200);
        return assembleData(pressureMachineLogResults);

    }

    List<PressureMachineLogResult> pointSample(List<PressureMachineLogResult> source, int pointNum) {
        List<PressureMachineLogResult> results = new ArrayList<>();
        int step = 0;
        if (source != null && source.size() > pointNum) {
            step = source.size() / pointNum;
            for (int i = 0; i + step < source.size(); i++) {
                i = i + step;
                results.add(source.get(i));
            }
            results.add(source.get(source.size() - 1));
        } else {
            results = source;
        }
        return results;
    }

    /**
     * 组装成前端需要的格式
     *
     * @return
     */
    public PressureMachineLogResponse assembleData(List<PressureMachineLogResult> resultList) {
        PressureMachineLogResponse response = new PressureMachineLogResponse();
        List<TypeValueDateVo> cpuUsageList = new ArrayList<>();
        List<TypeValueDateVo> cpuLoadList = new ArrayList<>();
        List<TypeValueDateVo> memoryUsageList = new ArrayList<>();
        List<TypeValueDateVo> ioWaitPerList = new ArrayList<>();
        List<TypeValueDateVo> transmittedUsageList = new ArrayList<>();
        response.setCpuUsageList(cpuUsageList);
        response.setCpuLoadList(cpuLoadList);
        response.setMemoryUsageList(memoryUsageList);
        response.setIoWaitPerList(ioWaitPerList);
        response.setTransmittedUsageList(transmittedUsageList);

        if (CollectionUtils.isEmpty(resultList)) {
            return response;
        }
        for (PressureMachineLogResult result : resultList) {
            String date = result.getGmtCreate();
            date = date.replace("T", " ");
            if (StringUtils.isNotBlank(date)) {
                date = date.substring(0, 16);
            }
            BigDecimal multipy = new BigDecimal(100);

            TypeValueDateVo cpuUsageVo = new TypeValueDateVo();
            cpuUsageVo.setDate(date);
            cpuUsageVo.setValue(result.getCpuUsage().multiply(multipy).setScale(2, RoundingMode.HALF_UP));
            cpuUsageVo.setType("cpu 利用率");
            cpuUsageList.add(cpuUsageVo);

            TypeValueDateVo cpuLoadVo = new TypeValueDateVo();
            cpuLoadVo.setDate(date);
            cpuLoadVo.setValue(result.getCpuLoad().setScale(2, RoundingMode.HALF_UP));
            cpuLoadVo.setType("cpu load");
            cpuLoadList.add(cpuLoadVo);

            TypeValueDateVo memoryUsageVo = new TypeValueDateVo();
            memoryUsageVo.setDate(date);
            memoryUsageVo.setValue(result.getMemoryUsed().multiply(multipy).setScale(2, RoundingMode.HALF_UP));
            memoryUsageVo.setType("内存利用率");
            memoryUsageList.add(memoryUsageVo);

            TypeValueDateVo ioWaitPerVo = new TypeValueDateVo();
            ioWaitPerVo.setDate(date);
            ioWaitPerVo.setValue(result.getDiskIoWait().multiply(multipy).setScale(2, RoundingMode.HALF_UP));
            ioWaitPerVo.setType("磁盘I/O等待率");
            ioWaitPerList.add(ioWaitPerVo);

            TypeValueDateVo transmittedUsageVo = new TypeValueDateVo();
            transmittedUsageVo.setDate(date);
            transmittedUsageVo.setValue(result.getTransmittedUsage().multiply(multipy).setScale(2, RoundingMode.HALF_UP));
            transmittedUsageVo.setType("网络带宽使用率");
            transmittedUsageList.add(transmittedUsageVo);

        }
        return response;
    }

    public static void main(String[] args) {

    }

}
