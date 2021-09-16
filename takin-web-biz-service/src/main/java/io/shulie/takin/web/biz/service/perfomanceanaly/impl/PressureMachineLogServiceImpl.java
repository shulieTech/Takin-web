package io.shulie.takin.web.biz.service.perfomanceanaly.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;

import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import io.shulie.takin.web.data.dao.perfomanceanaly.PressureMachineLogDao;
import io.shulie.takin.web.data.param.machine.PressureMachineLogQueryParam;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.TypeValueDateVo;
import io.shulie.takin.web.data.result.perfomanceanaly.PressureMachineLogResult;
import io.shulie.takin.web.biz.service.perfomanceanaly.PressureMachineLogService;
import io.shulie.takin.web.biz.pojo.request.perfomanceanaly.PressureMachineLogQueryRequest;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.PressureMachineLogResponse;

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
        String dayStartTime, dayEndTime;
        try {
            dayStartTime = DateUtil.parseDateTime(request.getQueryTime()).toDateStr() + "00:00:00";
            dayEndTime = DateUtil.parseDateTime(request.getQueryTime()).toDateStr() + "23:59:59";
        } catch (Exception ex) {
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

    @Override
    public void clearRubbishData() {
        pressureMachineLogDao.clearRubbishData(DateUtil.offsetDay(new Date(), -21).toString());
    }

    List<PressureMachineLogResult> pointSample(List<PressureMachineLogResult> source, int pointNum) {
        List<PressureMachineLogResult> results = new ArrayList<>();
        int step;
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
     * @return -
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
