package io.shulie.takin.web.biz.service.webide.impl;

import com.alibaba.fastjson.JSON;
import com.pamirs.pradar.log.parser.trace.RpcEntry;
import com.pamirs.pradar.log.parser.trace.RpcStack;
import io.shulie.takin.common.beans.page.PagingList;
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
        traceInfoQueryDTO.setQueryType(2);

        PagingList<EntryTraceInfoDTO> entryTraceInfoDtoPagingList = traceClient.listEntryTraceInfo(traceInfoQueryDTO);

        if (entryTraceInfoDtoPagingList == null ||
                CollectionUtils.isEmpty(entryTraceInfoDtoPagingList.getList()) || entryTraceInfoDtoPagingList.getList().get(0) == null) {
            return Collections.emptyList();
        }

        return getRcpDetailFromAmdb(entryTraceInfoDtoPagingList.getList().get(0).getTraceId());
    }

    private List<TraceRespDTO> getRcpDetailFromAmdb(String traceId) {
        RpcStack rpcStack = traceClient.getTraceDetailById(traceId);
        if (rpcStack == null || CollectionUtils.isEmpty(rpcStack.getRpcEntries())) {
            return Collections.emptyList();
        }

        List<TraceRespDTO> traceRespDTOS = new ArrayList<>();
        rpcStack.getRpcEntries().stream().forEach(rpcEntry -> {
            traceRespDTOS.add(rpcEntry2TraceRespDTO(rpcEntry));
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
        if (businessLinkResult != null) {
            EntranceRuleDTO dto = new EntranceRuleDTO();
            dto.setEntrance(businessLinkResult.getEntrace());
            dto.setBusinessType(businessLinkResult.getType());
            return dto;
        }
        return null;
    }

    private TraceRespDTO rpcEntry2TraceRespDTO(RpcEntry rpcEntry) {
        TraceRespDTO traceRespDTO = new TraceRespDTO();
        traceRespDTO.setTraceId(rpcEntry.getTraceId());
        traceRespDTO.setAgentId(rpcEntry.getClientAgentId());
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
        traceRespDTO.setHostIp(rpcEntry.getClientIp());
        traceRespDTO.setPort(rpcEntry.getClientPort());
        traceRespDTO.setAttributes(rpcEntry.getAttributes() != null ? JSON.toJSONString(rpcEntry.getAttributes()) : null);
        traceRespDTO.setStartTime(rpcEntry.getStartTime());
        traceRespDTO.setComment(rpcEntry.getClientCallbackMsg());
        return traceRespDTO;
    }


}
