package io.shulie.takin.web.biz.service.perfomanceanaly.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import io.shulie.takin.cloud.open.req.scenemanage.SceneManageQueryByIdsReq;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.input.PressureMachineInput;
import io.shulie.takin.web.biz.pojo.request.perfomanceanaly.PressureMachineDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.perfomanceanaly.PressureMachineUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.PressureMachineResponse;
import io.shulie.takin.web.biz.service.perfomanceanaly.PressureMachineService;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.biz.utils.FileUtils;
import io.shulie.takin.web.data.dao.perfomanceanaly.PressureMachineDao;
import io.shulie.takin.web.data.dao.perfomanceanaly.PressureMachineLogDao;
import io.shulie.takin.web.data.param.machine.PressureMachineDeleteParam;
import io.shulie.takin.web.data.param.machine.PressureMachineInsertParam;
import io.shulie.takin.web.data.param.machine.PressureMachineLogInsertParam;
import io.shulie.takin.web.data.param.machine.PressureMachineQueryParam;
import io.shulie.takin.web.data.param.machine.PressureMachineUpdateParam;
import io.shulie.takin.web.data.result.perfomanceanaly.PressureMachineResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author mubai
 * @date 2020-11-13 08:57
 */

@Component
@Slf4j
public class PressureMachineServiceImpl implements PressureMachineService {
    @Autowired
    private PressureMachineDao pressureMachineDao;

    @Resource
    private PressureMachineLogDao pressureMachineLogDao;

    @Autowired
    private SceneManageService sceneManageService;

    @Override
    public Long insert(PressureMachineInput input) {
        if (input == null) {
            return 0L;
        }
        //表中有则更新，无则新增
        PressureMachineQueryParam queryParam = new PressureMachineQueryParam();
        queryParam.setIp(input.getIp());
        PressureMachineResponse dbMachine = getByIp(input.getIp());
        Long machineId = null;

        String sceneNames = fillSceneName(input.getSceneId());
        if (dbMachine == null) {
            PressureMachineInsertParam param = new PressureMachineInsertParam();
            BeanUtils.copyProperties(input, param);
            //补充sceneNames;
            param.setSceneNames(sceneNames);
            try {
                Integer insert = pressureMachineDao.insert(param);
                machineId = Long.valueOf(insert);
            } catch (Exception e) {
                log.error("新增压力机失败 --" + e.getMessage());
            }
        } else {
            //更新
            PressureMachineUpdateParam updateParam = new PressureMachineUpdateParam();
            BeanUtils.copyProperties(input, updateParam);
            updateParam.setId(dbMachine.getId());
            //补充sceneNames
            updateParam.setSceneNames(sceneNames);
            pressureMachineDao.update(updateParam);
            machineId = dbMachine.getId();
        }
        return machineId;
    }

    @Override
    public void upload(PressureMachineInput input) {

        //网络带宽重新计算
        calculateTransmitted(input);
        //计算机器水位；
        List<BigDecimal> baseList = new ArrayList<>();
        baseList.add(input.getCpuUsage());
        baseList.add(input.getMemoryUsed());
        baseList.add(input.getCpuLoad().divide(new BigDecimal(input.getCpu()), 4, RoundingMode.HALF_UP));
        baseList.add(input.getDiskIoWait());
        baseList.add(input.getTransmittedUsage());
        BigDecimal max = Collections.max(baseList);
        //机器水位
        input.setMachineUsage(max);

        Long machineId = insert(input);
        //插入日志记录表；
        PressureMachineLogInsertParam logInsertParam = new PressureMachineLogInsertParam();
        BeanUtils.copyProperties(input, logInsertParam);
        logInsertParam.setMachineId(machineId);
        logInsertParam.setSceneNames(fillSceneName(input.getSceneId()));
        pressureMachineLogDao.insert(logInsertParam);
    }

    public Boolean calculateTransmitted(PressureMachineInput param) {
        boolean isUpdate = false;
        //表中有则更新，无则新增
        PressureMachineQueryParam queryParam = new PressureMachineQueryParam();
        queryParam.setIp(param.getIp());
        PressureMachineResponse result = getByIp(param.getIp());
        if (result != null) {
            isUpdate = true;
        }
        //组装网络带宽利用率
        if (result == null || result.getTransmittedTotal() == null || result.getTransmittedTotal().longValue() == 0) {
            param.setTransmittedInUsage(new BigDecimal(0));
            param.setTransmittedOutUsage(new BigDecimal(0));
            param.setTransmittedUsage(new BigDecimal(0));
        } else {
            Long total = result.getTransmittedTotal().longValue();
            total = total * 1024 * 1024;
            BigDecimal inPer = new BigDecimal(param.getTransmittedIn() * 8.0 / total).setScale(4, BigDecimal.ROUND_HALF_UP);
            BigDecimal outPer = new BigDecimal(param.getTransmittedOut() * 8.0 / total).setScale(4, BigDecimal.ROUND_HALF_UP);
            BigDecimal per = inPer.compareTo(outPer) == 1 ? inPer : outPer;
            param.setTransmittedOutUsage(outPer);
            param.setTransmittedInUsage(inPer);
            param.setTransmittedUsage(per);
        }

        return isUpdate;
    }

    @Override
    public PagingList<PressureMachineResponse> queryByExample(PressureMachineQueryParam param) {
        PagingList<PressureMachineResult> pressureMachineResultPage = pressureMachineDao.queryByExample(param);
        List<PressureMachineResponse> outputList = pressureMachineResultPage.getList().stream().map(pressureMachineResult -> {
            PressureMachineResponse response = new PressureMachineResponse();
            BeanUtils.copyProperties(pressureMachineResult, response);
            response.setDisk(FileUtils.getByteSize(pressureMachineResult.getDisk()));
            response.setMemory(FileUtils.getByteSize(pressureMachineResult.getMemory()));
            response.setMachineUsage(response.getMachineUsage().multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_DOWN));
            //非压测中的机器，压测场景不展示；
            if (response.getStatus() != 1) {
                response.setSceneNames(null);
            }
            return response;
        }).collect(Collectors.toList());
        return PagingList.of(outputList, pressureMachineResultPage.getTotal());
    }

    @Override
    public void update(PressureMachineUpdateRequest request) {
        PressureMachineUpdateParam param = new PressureMachineUpdateParam();
        BeanUtils.copyProperties(request, param);
        //更新的时候重新计算网络带宽的入、出的使用率
        PressureMachineResult dbData = pressureMachineDao.getById(request.getId());
        if (request.getTransmittedTotal() != null) {
            if (request.getTransmittedTotal() == 0) {
                param.setTransmittedInUsage(new BigDecimal(0));
                param.setTransmittedOutUsage(new BigDecimal(0));
                param.setTransmittedUsage(new BigDecimal(0));
            } else {
                Long total = request.getTransmittedTotal();
                total = total * 1024 * 1024;
                BigDecimal inPer = new BigDecimal(dbData.getTransmittedIn() * 8.0 / total).setScale(2, BigDecimal.ROUND_HALF_UP);
                BigDecimal outPer = new BigDecimal(dbData.getTransmittedOut() * 8.0 / total).setScale(2, BigDecimal.ROUND_HALF_UP);
                BigDecimal per = inPer.doubleValue() >= outPer.doubleValue() ? inPer : outPer;
                param.setTransmittedOutUsage(outPer);
                param.setTransmittedInUsage(inPer);
                param.setTransmittedUsage(per);
            }
        }
        pressureMachineDao.update(param);
    }

    @Override
    public void delete(PressureMachineDeleteRequest request) {
        PressureMachineResult dbData = pressureMachineDao.getById(request.getId());
        if (dbData != null && dbData.getStatus() != -1) {
            throw new RuntimeException("非离线机器不能删除！");
        }
        PressureMachineDeleteParam param = new PressureMachineDeleteParam();
        BeanUtils.copyProperties(request, param);
        pressureMachineDao.delete(param);

    }

    @Override
    public PressureMachineResponse getByIp(String ip) {

        PressureMachineResult byIp = pressureMachineDao.getByIp(ip);
        if (byIp == null) {
            return null;
        }
        PressureMachineResponse response = new PressureMachineResponse();
        BeanUtils.copyProperties(byIp, response);
        return response;
    }

    @Override
    public void updatePressureMachineStatus(Long id, Integer status) {
        PressureMachineUpdateParam param = new PressureMachineUpdateParam();
        param.setId(id);
        param.setStatus(status);
        pressureMachineDao.update(param);
    }

    public String fillSceneName(List<Long> sceneIds) {
        if (CollectionUtils.isEmpty(sceneIds)) {
            return "";
        }
        SceneManageQueryByIdsReq req = new SceneManageQueryByIdsReq();
        req.setSceneIds(sceneIds);
        ResponseResult<List<SceneManageWrapperResp>> byIds = sceneManageService.getByIds(req);
        List<SceneManageWrapperResp> data = byIds.getData();
        List<String> names = new ArrayList<>();
        for (Object wrapperResp : data) {
            String s = JSON.toJSONString(wrapperResp);
            SceneManageWrapperResp scene = JSON.parseObject(s, SceneManageWrapperResp.class);
            names.add(scene.getPressureTestSceneName());
        }
        return StringUtils.join(names, "|");
    }

}
