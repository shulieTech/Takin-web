package io.shulie.takin.web.biz.service.perfomanceanaly.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.pamirs.takin.common.util.DateUtils;
import io.shulie.takin.web.biz.convert.performace.PressureMachineStatisticsRespConvert;
import io.shulie.takin.web.biz.pojo.request.perfomanceanaly.PressureMachineStatisticsRequest;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.PressureMachineStatisticsResponse;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.TypeValueDateVo;
import io.shulie.takin.web.biz.service.perfomanceanaly.PressureMachineStatisticsService;
import io.shulie.takin.web.data.dao.perfomanceanaly.PressureMachineStatisticsDao;
import io.shulie.takin.web.data.param.perfomanceanaly.PressureMachineStatisticsInsertParam;
import io.shulie.takin.web.data.param.perfomanceanaly.PressureMachineStatisticsQueryParam;
import io.shulie.takin.web.data.result.perfomanceanaly.PressureMachineStatisticsResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author mubai
 * @date 2020-11-13 11:36
 */

@Service
public class PressureMachineStatisticsServiceImpl implements PressureMachineStatisticsService {
    @Autowired
    private PressureMachineStatisticsDao pressureMachineStatisticsDao;

    @Override
    public void statistics() {
        PressureMachineStatisticsResult statistics = getStatistics();
        PressureMachineStatisticsInsertParam insertParam = new PressureMachineStatisticsInsertParam();
        BeanUtils.copyProperties(statistics, insertParam);
        pressureMachineStatisticsDao.insert(insertParam);
    }

    @Override
    public void insert(PressureMachineStatisticsInsertParam param) {
        pressureMachineStatisticsDao.insert(param);
    }

    @Override
    public PressureMachineStatisticsResponse getNewlyStatistics() {
        PressureMachineStatisticsResult newlyStatistics = pressureMachineStatisticsDao.getNewlyStatistics();
        return PressureMachineStatisticsRespConvert.INSTANCE.of(newlyStatistics);
    }

    @Override
    public List<TypeValueDateVo> queryByExample(PressureMachineStatisticsRequest request) {
        PressureMachineStatisticsQueryParam param = new PressureMachineStatisticsQueryParam();
        BeanUtils.copyProperties(request, param);
        List<PressureMachineStatisticsResult> list = pressureMachineStatisticsDao.queryByExample(param);
        //将数据进行采样，取200个点
        list = pointSample(list);
        return assembleData(list);
    }

    List<PressureMachineStatisticsResult> pointSample(List<PressureMachineStatisticsResult> source) {
        List<PressureMachineStatisticsResult> results = new ArrayList<>();
        int step = 0;
        if (source != null && source.size() > 100) {
            step = source.size() / 100;
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

    private List<TypeValueDateVo> assembleData(List<PressureMachineStatisticsResult> list) {
        List<TypeValueDateVo> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        for (PressureMachineStatisticsResult result : list) {
            String time = result.getGmtCreate().substring(0, 19);
            time = time.replaceAll("T", " ");
            TypeValueDateVo totalVo = new TypeValueDateVo();
            totalVo.setType("总数");
            totalVo.setValue(result.getMachineTotal());
            totalVo.setDate(time);

            TypeValueDateVo pressuredVo = new TypeValueDateVo();
            pressuredVo.setDate(time);
            pressuredVo.setValue(result.getMachinePressured());
            pressuredVo.setType("压测中");

            TypeValueDateVo freeVo = new TypeValueDateVo();
            freeVo.setDate(time);
            freeVo.setValue(result.getMachineFree());
            freeVo.setType("空闲");

            TypeValueDateVo offlineVo = new TypeValueDateVo();
            offlineVo.setType("离线");
            offlineVo.setDate(time);
            offlineVo.setValue(result.getMachineOffline());

            resultList.add(totalVo);
            resultList.add(pressuredVo);
            resultList.add(freeVo);
            resultList.add(offlineVo);
        }
        return resultList;
    }

    @Override
    public PressureMachineStatisticsResult getStatistics() {
        //获取压力机
        return pressureMachineStatisticsDao.statistics();
    }


}
