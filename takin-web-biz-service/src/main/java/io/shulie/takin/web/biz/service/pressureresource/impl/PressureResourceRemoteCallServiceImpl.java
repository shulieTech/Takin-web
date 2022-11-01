package io.shulie.takin.web.biz.service.pressureresource.impl;

import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson.JSON;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.api.TraceClient;
import io.shulie.takin.web.amdb.bean.query.trace.TraceInfoQueryDTO;
import io.shulie.takin.web.amdb.bean.result.trace.EntryTraceInfoDTO;
import io.shulie.takin.web.biz.pojo.request.pressureresource.*;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceRemoteCallService;
import io.shulie.takin.web.biz.service.pressureresource.common.CheckStatusEnum;
import io.shulie.takin.web.biz.service.pressureresource.common.PassEnum;
import io.shulie.takin.web.biz.service.pressureresource.common.RemoteCallUtil;
import io.shulie.takin.web.biz.service.pressureresource.common.dy.DynamicCompilerUtil;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceRelateRemoteCallVO;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.application.AppRemoteCallDAO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateRemoteCallDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateRemoteCallMapperV2;
import io.shulie.takin.web.data.model.mysql.AppRemoteCallEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateRemoteCallEntityV2;
import io.shulie.takin.web.data.model.mysql.pressureresource.RelateRemoteCallEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceRemoteCallQueryParam;
import io.shulie.takin.web.data.result.application.AppRemoteCallResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/16 12:06 PM
 */
@Service
public class PressureResourceRemoteCallServiceImpl implements PressureResourceRemoteCallService {
    private static Logger logger = LoggerFactory.getLogger(PressureResourceRemoteCallServiceImpl.class);

    @Resource
    private PressureResourceRelateRemoteCallDAO pressureResourceRelateRemoteCallDAO;

    @Resource
    private PressureResourceRelateRemoteCallMapperV2 pressureResourceRelateRemoteCallMapperV2;

    @Resource
    private TraceClient traceClient;

    @Resource
    private AppRemoteCallDAO appRemoteCallDAO;

    /**
     * @param request
     * @return
     */
    @Override
    public PagingList<PressureResourceRelateRemoteCallVO> pageList(PressureResourceRelateRemoteCallRequest request) {
        PressureResourceRemoteCallQueryParam param = new PressureResourceRemoteCallQueryParam();
        BeanUtils.copyProperties(request, param);
        PagingList<RelateRemoteCallEntity> pageList = pressureResourceRelateRemoteCallDAO.pageList_v2(param);

        if (pageList.isEmpty()) {
            return PagingList.of(Collections.emptyList(), pageList.getTotal());
        }
        //转换下
        List<RelateRemoteCallEntity> source = pageList.getList();
        List<PressureResourceRelateRemoteCallVO> returnList = source.stream().map(configDto -> {
            PressureResourceRelateRemoteCallVO vo = new PressureResourceRelateRemoteCallVO();
            BeanUtils.copyProperties(configDto, vo);
            vo.setId(String.valueOf(configDto.getId()));
            // 调用依赖
            if (StringUtils.isNotBlank(vo.getAppName()) && StringUtils.isNotBlank(vo.getServerAppName())) {
                vo.setInvoke(vo.getAppName() + "调用" + vo.getServerAppName());
            }
            return vo;
        }).collect(Collectors.toList());
        return PagingList.of(returnList, pageList.getTotal());
    }

    /**
     * 更新mock
     *
     * @param mockInput
     */
    @Override
    public void update_v2(PressureResourceMockInput mockInput) {
        Long id = mockInput.getId();
        if (id == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "参数Id未指定");
        }
        PressureResourceRelateRemoteCallEntityV2 entity = pressureResourceRelateRemoteCallMapperV2.selectById(id);
        if (entity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_OP_ERROR, "远程调用不存在!");
        }

        PressureResourceRelateRemoteCallEntityV2 update = new PressureResourceRelateRemoteCallEntityV2();
        update.setId(id);
        if (mockInput.getPass() != null) {
            update.setPass(mockInput.getPass());
        }
        // 假如是放行,默认不检测，检测状态直接置为成功
        if (update.getPass() != null && update.getPass() == PassEnum.PASS_YES.getCode()) {
            update.setStatus(CheckStatusEnum.CHECK_FIN.getCode());
        }
        // mockReturnValue更新到app_remote_call表中
        if (mockInput.getMockInfo() != null) {
            update.setMockReturnValue(JSON.toJSONString(mockInput.getMockInfo()));
        }
        AppRemoteCallResult callResult = appRemoteCallDAO.queryOne(entity.getAppName(), entity.getInterfaceType(), entity.getInterfaceName());
        AppRemoteCallEntity updateEntity = new AppRemoteCallEntity();
        updateEntity.setId(callResult.getId());
        updateEntity.setType(RemoteCallUtil.getType(update.getMockReturnValue(), update.getPass()));
        updateEntity.setMockReturnValue(update.getMockReturnValue());
        updateEntity.setGmtModified(update.getGmtModified());
        appRemoteCallDAO.updateById(updateEntity);

        // 更新冗余
        pressureResourceRelateRemoteCallMapperV2.updateById(update);
    }

    /**
     * 获取服务平均响应时间,id为远程调用服务Id
     *
     * @param id
     * @return
     */
    @Override
    public MockDetailVO mockDetail(Long id) {
        MockDetailVO mockDetailVO = new MockDetailVO();
        mockDetailVO.setRequest(Collections.emptyList());
        mockDetailVO.setResponseTime("0");
        // 远程调用服务Id
        PressureResourceRelateRemoteCallEntityV2 call = pressureResourceRelateRemoteCallMapperV2.selectById(id);
        if (call == null) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_QUERY_ERROR, "数据未找到");
        }
        TraceInfoQueryDTO traceInfoQueryDTO = new TraceInfoQueryDTO();
        traceInfoQueryDTO.setQueryType(1);
        traceInfoQueryDTO.setAppName(call.getAppName());
        if (call.getInterfaceName().contains("#")) {
            traceInfoQueryDTO.setServiceName(call.getInterfaceName().split("#")[0]);
            traceInfoQueryDTO.setQueryMethodName(call.getInterfaceName().split("#")[1]);
        } else {
            traceInfoQueryDTO.setServiceName(call.getInterfaceName());
        }
        traceInfoQueryDTO.setLogType("2");
        traceInfoQueryDTO.setSortField("startDate");
        traceInfoQueryDTO.setSortType("desc");
        traceInfoQueryDTO.setPageNum(0);
        traceInfoQueryDTO.setPageSize(20);
        PagingList<EntryTraceInfoDTO> pageList = null;
        try {
            pageList = traceClient.listEntryTraceInfo(traceInfoQueryDTO);
            if (pageList == null || pageList.isEmpty()) {
                return mockDetailVO;
            }
        } catch (Throwable e) {
            return mockDetailVO;
        }
        Double avg = pageList.getList().stream().mapToLong(EntryTraceInfoDTO::getCost).average().orElse(0D);
        mockDetailVO.setResponseTime(String.valueOf(Math.floor(avg)));
        List<String> requests = pageList.getList().stream().map(mock -> {
            String response = mock.getResponse();
            if (response.startsWith("{{") && response.endsWith("}}")) {
                response = response.substring(1);
                response = response.substring(0, response.length() - 1);
            }
            return response;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(requests)) {
            mockDetailVO.setRequest(ListUtil.sub(requests, 1, 2));
        }
        return mockDetailVO;
    }

    /**
     * 校验
     *
     * @param mockInfo
     * @return
     */
    @Override
    public PressureResourceCheckVO check(MockInfo mockInfo) {
        PressureResourceCheckVO checkVO = new PressureResourceCheckVO();
        checkVO.setSuccess(true);

        if (StringUtils.isBlank(mockInfo.getMockValue())) {
            checkVO.setRemark("mock数据为空!");
            checkVO.setSuccess(false);
            return checkVO;
        }
        if (mockInfo.getType().equals("0")) {
            try {
                JSON.parseObject(mockInfo.getMockValue());
            } catch (Throwable e) {
                checkVO.setSuccess(false);
                checkVO.setRemark(e.getMessage());
            }
        } else {
            try {
                String remark = DynamicCompilerUtil.check(mockInfo.getMockValue());
                if (StringUtils.isNotBlank(remark)) {
                    checkVO.setRemark(remark);
                    checkVO.setSuccess(false);
                }
            } catch (Throwable e) {
                checkVO.setRemark(e.getMessage());
                checkVO.setSuccess(false);
            }
        }
        return checkVO;
    }
}
