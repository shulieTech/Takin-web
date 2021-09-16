package io.shulie.takin.web.biz.service.perfomanceanaly.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.date.DateUtil;
import org.springframework.beans.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import io.shulie.takin.channel.bean.CommandResponse;
import io.shulie.takin.web.biz.agent.AgentCommandEnum;
import org.apache.commons.collections4.CollectionUtils;
import io.shulie.takin.web.biz.agent.AgentCommandFactory;
import io.shulie.takin.web.common.exception.ExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.data.dao.perfomanceanaly.PerformanceBaseDataDAO;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.MemoryModelVo;
import io.shulie.takin.web.biz.service.perfomanceanaly.ReportDetailService;
import io.shulie.takin.web.biz.service.perfomanceanaly.MemoryAnalysisService;
import io.shulie.takin.web.data.param.perfomanceanaly.PerformanceBaseQueryParam;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.ReportTimeResponse;
import io.shulie.takin.web.data.result.perfomanceanaly.PerformanceBaseDataResult;
import io.shulie.takin.web.biz.pojo.request.perfomanceanaly.MemoryAnalysisRequest;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.DownloadDumpResponse;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.MemoryAnalysisResponse;

/**
 * @author mubai
 * @date 2020-11-09 11:31
 */

@Service
@Slf4j
public class MemoryAnalysisServiceImpl implements MemoryAnalysisService {

    @Autowired
    private PerformanceBaseDataDAO performanceBaseDataDAO;

    @Autowired
    private ReportDetailService reportDetailService;

    @Autowired
    private AgentCommandFactory agentCommandFactory;

    @Override
    public MemoryAnalysisResponse queryMemoryDump(MemoryAnalysisRequest request) {
        PerformanceBaseQueryParam param = new PerformanceBaseQueryParam();
        BeanUtils.copyProperties(request, param);
        ReportTimeResponse timeResponse = reportDetailService.getReportTime(request.getReportId());
        param.setStartTime(timeResponse.getStartTime());
        if (timeResponse.getEndTime() != null) {
            param.setEndTime(timeResponse.getEndTime());
        } else {
            param.setEndTime(DateUtil.formatDateTime(new Date()));
        }

        param.setAppName(request.getAppName());
        String[] splits = StringUtils.split(request.getProcessName(), "|");
        param.setAppIp(splits[0]);
        param.setAgentId(splits[1]);
        List<PerformanceBaseDataResult> baseDataResultList = performanceBaseDataDAO.getPerformanceBaseDataList(param);
        //将数据转化为前端需要的格式
        return assembleData(baseDataResultList);
    }

    public MemoryAnalysisResponse assembleData(List<PerformanceBaseDataResult> list) {
        MemoryAnalysisResponse result = new MemoryAnalysisResponse();
        List<MemoryModelVo> heapMemoryList = new ArrayList<>();
        List<MemoryModelVo> gcCountList = new ArrayList<>();
        List<MemoryModelVo> gcCostList = new ArrayList<>();
        result.setHeapMemory(heapMemoryList);
        result.setGcCount(gcCountList);
        result.setGcCost(gcCostList);
        if (CollectionUtils.isNotEmpty(list)) {
            for (PerformanceBaseDataResult dataResult : list) {
                MemoryModelVo heapMemoryTotal = new MemoryModelVo();
                MemoryModelVo heapMemoryPerm = new MemoryModelVo();
                MemoryModelVo heapMemoryYoung = new MemoryModelVo();
                MemoryModelVo heapMemoryOld = new MemoryModelVo();
                MemoryModelVo heapMemoryTotalNon = new MemoryModelVo();
                MemoryModelVo heapMemoryTotalBufferPool = new MemoryModelVo();

                String time = dataResult.getTime().substring(11, 19);
                heapMemoryTotal.setTime(time);
                heapMemoryTotal.setType("总和");
                heapMemoryTotal.setValue(mb2mb(dataResult.getTotalMemory()));

                heapMemoryPerm.setTime(time);
                heapMemoryPerm.setValue(mb2mb(dataResult.getPermMemory()));
                heapMemoryPerm.setType("永久代");

                heapMemoryYoung.setTime(time);
                heapMemoryYoung.setValue(mb2mb(dataResult.getYoungMemory()));
                heapMemoryYoung.setType("年轻代");

                heapMemoryOld.setTime(time);
                heapMemoryOld.setValue(mb2mb(dataResult.getOldMemory()));
                heapMemoryOld.setType("老年代");

                // 非堆
                heapMemoryTotalNon.setTime(time);
                heapMemoryTotalNon.setValue(mb2mb(dataResult.getTotalNonHeapMemory()));
                heapMemoryTotalNon.setType("非堆内存总内存");
                //buffer
                heapMemoryTotalBufferPool.setTime(time);
                heapMemoryTotalBufferPool.setValue(mb2mb(dataResult.getTotalBufferPoolMemory()));
                heapMemoryTotalBufferPool.setType("buffer-pool总内存");

                heapMemoryList.add(heapMemoryTotal);
                heapMemoryList.add(heapMemoryPerm);
                heapMemoryList.add(heapMemoryYoung);
                heapMemoryList.add(heapMemoryOld);
                heapMemoryList.add(heapMemoryTotalNon);
                heapMemoryList.add(heapMemoryTotalBufferPool);
                //gc count
                MemoryModelVo fullGcCount = new MemoryModelVo();
                MemoryModelVo youngGcCount = new MemoryModelVo();
                fullGcCount.setTime(time);
                fullGcCount.setValue(dataResult.getFullGcCount());
                fullGcCount.setType("Full GC 次数");
                youngGcCount.setTime(time);
                youngGcCount.setValue(dataResult.getYoungGcCount());
                youngGcCount.setType("Young GC 次数");
                gcCountList.add(fullGcCount);
                gcCountList.add(youngGcCount);
                // gc cost
                MemoryModelVo fullGcCost = new MemoryModelVo();
                MemoryModelVo youngGcCost = new MemoryModelVo();
                fullGcCost.setTime(time);
                fullGcCost.setValue(dataResult.getFullGcCost());
                fullGcCost.setType("Full GC 耗时");
                youngGcCost.setTime(time);
                youngGcCost.setValue(dataResult.getYoungGcCost());
                youngGcCost.setType("Young GC 耗时");

                gcCostList.add(fullGcCost);
                gcCostList.add(youngGcCost);

            }
        }

        return result;

    }

    @Override
    public DownloadDumpResponse downloadDump(String agentId) throws Exception {
        // 上传到zk
        CommandResponse<String> commandResponse = agentCommandFactory.send(AgentCommandEnum.PULL_AGENT_DUMP_HEADDUMP_COMMAND,
            agentId, null);
        if (!commandResponse.isSuccess()) {
            throw new TakinWebException(ExceptionCode.DUMP_ERROR, commandResponse.getMessage());
        }
        return new DownloadDumpResponse(commandResponse.getResult());
    }

    public BigDecimal mb2gb(Double source) {
        if (source == null) {
            return new BigDecimal(0);
        }
        return new BigDecimal(source / 1024).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal mb2mb(Double source) {
        if (source == null) {
            return new BigDecimal(0).setScale(2, RoundingMode.HALF_UP);
        }
        return new BigDecimal(source).divide(BigDecimal.valueOf(1024 * 1024), 2, RoundingMode.HALF_UP);
    }
}
