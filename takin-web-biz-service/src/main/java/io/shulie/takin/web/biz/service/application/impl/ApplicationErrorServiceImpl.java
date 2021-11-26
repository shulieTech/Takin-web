package io.shulie.takin.web.biz.service.application.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.alibaba.excel.util.CollectionUtils;
import com.alibaba.fastjson.JSONObject;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.collect.Lists;
import com.pamirs.takin.common.util.DateUtils;
import com.pamirs.takin.entity.domain.dto.NodeUploadDataDTO;
import com.pamirs.takin.entity.domain.entity.ExceptionInfo;
import com.pamirs.takin.entity.domain.entity.TApplicationMnt;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationErrorQueryInput;
import io.shulie.takin.web.biz.pojo.output.application.ApplicationErrorOutput;
import io.shulie.takin.web.biz.pojo.output.application.ApplicationExceptionOutput;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationNodeDashBoardResponse;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.application.ApplicationErrorService;
import io.shulie.takin.web.biz.service.application.ApplicationNodeService;
import io.shulie.takin.web.biz.service.impl.ApplicationServiceImpl;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.common.Separator;
import io.shulie.takin.web.common.enums.application.AppExceptionCodeEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.result.application.ApplicationResult;
import io.shulie.takin.web.data.result.application.InstanceInfoResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author fanxx
 * @date 2020/10/16 11:41 上午
 */
@Component
@Slf4j
public class ApplicationErrorServiceImpl implements ApplicationErrorService {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private ApplicationNodeService applicationNodeService;

    @Override
    public List<ApplicationErrorOutput> list(ApplicationErrorQueryInput queryRequest) {
        List<ApplicationErrorOutput> responseList = Lists.newArrayList();
        TApplicationMnt tApplicationMnt = ensureApplicationExist(queryRequest);

        // 应用节点相关错误信息
        ApplicationErrorOutput nodeErrorResponse =
            this.getNodeErrorResponse(tApplicationMnt.getApplicationName(), tApplicationMnt.getNodeNum());
        if (nodeErrorResponse != null) {
            responseList.add(nodeErrorResponse);
        }
        //redisKey改造
        String appUniqueKey = CommonUtil.generateRedisKeyWithSeparator(Separator.Separator3,
            WebPluginUtils.traceTenantAppKey(), WebPluginUtils.traceEnvCode(),
            queryRequest.getApplicationId() + ApplicationServiceImpl.PRADARNODE_KEYSET);
        Set<String> keys = redisTemplate.opsForSet().members(appUniqueKey);
        if (keys == null || keys.size() == 0) {
            return responseList;
        }
        for (String nodeKey : keys) {
            if (!redisTemplate.hasKey(nodeKey)) {
                continue;
            }
            List<String> nodeUploadDataDTOList = redisTemplate.opsForList().range(nodeKey, 0, -1);
            if (CollectionUtils.isEmpty(nodeUploadDataDTOList)) {
                continue;
            }
            convertNodeUploadDataList(responseList, nodeUploadDataDTOList);
        }
        // 按照时间倒序输出
        responseList.sort((a1, a2) -> a2.getTime().compareTo(a1.getTime()));
        return responseList;
    }

    private TApplicationMnt ensureApplicationExist(ApplicationErrorQueryInput queryRequest) {
        Response<TApplicationMnt> applicationMntResponse = applicationService.getApplicationInfoForError(
            String.valueOf(queryRequest.getApplicationId()));
        TApplicationMnt tApplicationMnt = applicationMntResponse.getData();
        if (Objects.isNull(tApplicationMnt)) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_VALIDATE_ERROR, "应用不存在");
        }
        return tApplicationMnt;
    }

    private void putNodeExceptionIfNeeded(List<ApplicationErrorOutput> responseList, TApplicationMnt tApplicationMnt) {
        Integer totalNodeCount = tApplicationMnt.getNodeNum();
        Integer onlineNodeCount = 0;
        List<ApplicationResult> applicationResultList = applicationDAO.getApplicationByName(
            Collections.singletonList(tApplicationMnt.getApplicationName()));
        if (CollectionUtils.isEmpty(applicationResultList)) {
            log.error("AMDB中应用信息查询结果为空");
        } else {
            onlineNodeCount = applicationResultList.get(0).getInstanceInfo().getInstanceOnlineAmount();
        }
        if (applicationResultList.size() > 1) {
            // TODO
            log.error("AMDB存在多个名称重复的应用！");
        }
        if (!totalNodeCount.equals(onlineNodeCount)) {
            responseList.add(new ApplicationErrorOutput()
                .setExceptionId("-")
                .setAgentIdList(Collections.singletonList("-"))
                .setDescription("在线节点数 与 配置的节点总数 不一致")
                .setTime(DateUtils.getNowDateStr())
                .setDetail("设置节点数：" + totalNodeCount + "，在线节点数：" + onlineNodeCount));
        }
    }

    private void convertNodeUploadDataList(List<ApplicationErrorOutput> responseList,
        List<String> nodeUploadDataDTOList) {
        nodeUploadDataDTOList.forEach(n -> {
            NodeUploadDataDTO nodeUploadDataDTO = JSONObject.parseObject(n, NodeUploadDataDTO.class);
            Map<String, Object> exceptionMap = nodeUploadDataDTO.getSwitchErrorMap();
            if (exceptionMap != null && exceptionMap.size() > 0) {
                for (Map.Entry<String, Object> entry : exceptionMap.entrySet()) {
                    String message = String.valueOf(entry.getValue());
                    if (message.contains("errorCode")) {
                        try {
                            ExceptionInfo exceptionInfo = JSONObject.parseObject(message,
                                ExceptionInfo.class);
                            ApplicationErrorOutput applicationErrorResponse
                                = new ApplicationErrorOutput()
                                .setExceptionId(exceptionInfo.getErrorCode())
                                .setAgentIdList(Collections.singletonList(nodeUploadDataDTO.getAgentId()))
                                .setDescription(exceptionInfo.getMessage())
                                .setDetail(exceptionInfo.getDetail())
                                .setTime(nodeUploadDataDTO.getExceptionTime());
                            responseList.add(applicationErrorResponse);
                        } catch (Exception e) {
                            log.error("异常转换失败：错误信息: {}", message, e);
                        }
                    }
                }
            }
        });
    }

    @Override
    public List<ApplicationExceptionOutput> getAppException(List<String> appNames) {
        List<ApplicationExceptionOutput> outputs = Lists.newArrayList();
        List<ApplicationResult> applicationResultList = applicationDAO.getApplicationByName(appNames);
        applicationResultList.forEach(app -> {
            InstanceInfoResult result = app.getInstanceInfo();
            if (!result.getInstanceAmount().equals(result.getInstanceOnlineAmount())) {
                ApplicationExceptionOutput output = new ApplicationExceptionOutput();
                output.setApplicationName(app.getAppName());
                output.setAgentIds(Arrays.asList("-"));
                output.setCode(AppExceptionCodeEnum.EPC0001.getCode());
                output.setDescription(AppExceptionCodeEnum.EPC0001.getDesc());
                output.setTime(DateUtils.getNowDateStr());
                outputs.add(output);
            }
            //redisKey改造
            String appUniqueKey = CommonUtil.generateRedisKeyWithSeparator(Separator.Separator3,
                WebPluginUtils.traceTenantAppKey(), WebPluginUtils.traceTenantCode(),
                app.getAppId() + ApplicationServiceImpl.PRADAR_SEPERATE_FLAG);
            Set<String> keys = redisTemplate.keys(appUniqueKey + "*");
            if (keys != null) {
                for (String nodeKey : keys) {
                    List<String> nodeUploadDataDTOList = redisTemplate.opsForList().range(nodeKey, 0, -1);
                    if (CollectionUtils.isEmpty(nodeUploadDataDTOList)) {
                        continue;
                    } else {
                        nodeUploadDataDTOList.forEach(n -> {
                            NodeUploadDataDTO nodeUploadDataDTO = JSONObject.parseObject(n, NodeUploadDataDTO.class);
                            Map<String, Object> exceptionMap = nodeUploadDataDTO.getSwitchErrorMap();
                            if (exceptionMap != null && exceptionMap.size() > 0) {
                                for (Map.Entry<String, Object> entry : exceptionMap.entrySet()) {
                                    String message = String.valueOf(entry.getValue());
                                    if (message.contains("errorCode")) {
                                        try {
                                            ExceptionInfo exceptionInfo = JSONObject.parseObject(message,
                                                ExceptionInfo.class);
                                            ApplicationExceptionOutput output = new ApplicationExceptionOutput();
                                            output.setApplicationName(app.getAppName());
                                            output.setAgentIds(Arrays.asList(nodeUploadDataDTO.getAgentId()));
                                            output.setCode(exceptionInfo.getErrorCode());
                                            output.setDescription(exceptionInfo.getMessage());
                                            // todo 时间需要修改
                                            output.setTime(nodeUploadDataDTO.getExceptionTime());
                                            // todo 明细不全不传 exceptionInfo.getDetail()
                                            outputs.add(output);
                                        } catch (Exception e) {
                                            log.error(message);
                                            log.error("异常转换失败：", e);
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
        return outputs;
    }

    /**
     * 关于节点错误的信息
     *
     * @param applicationName 应用名称
     * @param nodeNum 节点数量
     * @return 节点错误
     */
    private ApplicationErrorOutput getNodeErrorResponse(String applicationName, Integer nodeNum) {
        ApplicationNodeDashBoardResponse applicationNodeDashBoardResponse =
            applicationNodeService.getApplicationNodeDashBoardResponse(applicationName, nodeNum);
        String errorMsg = applicationNodeDashBoardResponse.getErrorMsg();
        if (StringUtils.isBlank(errorMsg)) {
            return null;
        }

        ApplicationErrorOutput applicationErrorResponse = new ApplicationErrorOutput();
        applicationErrorResponse.setExceptionId("-");
        applicationErrorResponse.setAgentIdList(Collections.singletonList("-"));
        applicationErrorResponse.setDescription(errorMsg);
        applicationErrorResponse.setTime(DateUtils.getNowDateStr());
        applicationErrorResponse.setDetail(String.format("设置节点数：%d，上报的已安装探针节点数：%d", nodeNum, applicationNodeDashBoardResponse.getProbeInstalledNodeNum()));
        return applicationErrorResponse;
    }

}
