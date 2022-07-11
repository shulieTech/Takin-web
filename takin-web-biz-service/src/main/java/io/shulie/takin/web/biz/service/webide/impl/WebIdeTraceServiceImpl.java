package io.shulie.takin.web.biz.service.webide.impl;

import com.alibaba.fastjson.JSON;
import com.pamirs.pradar.log.parser.trace.RpcBased;
import io.shulie.takin.web.amdb.api.TraceClient;
import io.shulie.takin.web.amdb.bean.query.trace.EntranceRuleDTO;
import io.shulie.takin.web.amdb.bean.query.trace.TraceInfoQueryDTO;
import io.shulie.takin.web.amdb.bean.result.trace.EntryTraceInfoDTO;
import io.shulie.takin.web.biz.service.webide.WebIdeTraceService;
import io.shulie.takin.web.biz.service.webide.dto.TraceRespDTO;
import io.shulie.takin.web.data.dao.linkmanage.BusinessLinkManageDAO;
import io.shulie.takin.web.data.result.linkmange.BusinessLinkResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author angju
 * @date 2022/7/8 15:55
 */
@Service
@Slf4j
public class WebIdeTraceServiceImpl implements WebIdeTraceService {

    @Resource
    private BusinessLinkManageDAO businessLinkManageDAO;

    @Resource
    private TraceClient traceClient;

    @Override
    public List<TraceRespDTO> getLastTraceDetailByBusinessActivityId(Long businessActivityId) {
        EntranceRuleDTO entranceRuleDTO = getEntryByBusinessActivityId(businessActivityId);
        if (entranceRuleDTO == null) {
            log.warn("业务活动id {} 查询不到业务活动信息", businessActivityId);
            return Collections.emptyList();
        }
        List<EntranceRuleDTO> entranceList = new ArrayList<>();
        entranceList.add(entranceRuleDTO);
        TraceInfoQueryDTO traceInfoQueryDTO = new TraceInfoQueryDTO();
        traceInfoQueryDTO.setEntranceRuleDTOS(entranceList);
        traceInfoQueryDTO.setPageNum(0);
        traceInfoQueryDTO.setQueryType(1);
        EntryTraceInfoDTO entryTraceInfo = traceClient.getEntryTraceInfo(traceInfoQueryDTO);
        if (entryTraceInfo != null) {
            return getRcpDetailFromAmdb(entryTraceInfo.getTraceId());
        }
        return Collections.emptyList();

    }

    private List<TraceRespDTO> getRcpDetailFromAmdb(String traceId) {
        List<RpcBased> rpcBasedList = traceClient.getTraceBaseById(traceId);
        if (CollectionUtils.isEmpty(rpcBasedList)) {
            return Collections.emptyList();
        }

        List<TraceRespDTO> traceRespDTOS = new ArrayList<>();
        rpcBasedList.stream().forEach(rpcBased -> {
            traceRespDTOS.add(rpcEntry2TraceRespDTO(rpcBased));
        });
        return traceRespDTOS;
    }




    @Override
    public List<TraceRespDTO> getTraceDetailByTraceId(String traceId) {
        return getRcpDetailFromAmdb(traceId);
    }


    /**
     * 根据业务ID获取业务信息
     * @param businessActivityId
     * @return
     */
    private EntranceRuleDTO getEntryByBusinessActivityId(Long businessActivityId) {
        // 查询入口集合
        BusinessLinkResult businessLinkResult = businessLinkManageDAO.selectBussinessLinkById(businessActivityId);
        if (businessLinkResult != null && businessLinkResult.getEntrace() != null) {
            EntranceRuleDTO dto = new EntranceRuleDTO();
            dto.setEntrance(businessLinkResult.getEntrace());
            dto.setBusinessType(businessLinkResult.getType());
            dto.setAppName(businessLinkResult.getApplicationName());
            return dto;
        }
        return null;
    }

    private TraceRespDTO rpcEntry2TraceRespDTO(RpcBased rpcEntry) {
        TraceRespDTO traceRespDTO = new TraceRespDTO();
        traceRespDTO.setTraceId(rpcEntry.getTraceId());
        traceRespDTO.setAgentId(rpcEntry.getAgentId());
        traceRespDTO.setAppName(rpcEntry.getAppName());
        traceRespDTO.setClusterTest(rpcEntry.isClusterTest() ? 1 : 0);
        traceRespDTO.setService(rpcEntry.getServiceName());
        traceRespDTO.setMethod(rpcEntry.getMethodName());
        traceRespDTO.setMiddlewareName(rpcEntry.getMiddlewareName());
        traceRespDTO.setRequest(rpcEntry.getRequest());
        traceRespDTO.setResponse(rpcEntry.getResponse());
        traceRespDTO.setCost(rpcEntry.getCost());
        traceRespDTO.setInvokeId(rpcEntry.getRpcId());
        traceRespDTO.setResultCode(rpcEntry.getResultCode());
        traceRespDTO.setRequestSize(rpcEntry.getRequestSize());
        traceRespDTO.setResponseSize(rpcEntry.getResponseSize());
        traceRespDTO.setHostIp(rpcEntry.getHostIp());
        traceRespDTO.setPort(rpcEntry.getPort());
        traceRespDTO.setAttributes(rpcEntry.getAttributes() != null ? JSON.toJSONString(rpcEntry.getAttributes()) : null);
        traceRespDTO.setStartTime(rpcEntry.getStartTime());
        traceRespDTO.setComment(rpcEntry.getCallbackMsg());
        return traceRespDTO;
    }


}
