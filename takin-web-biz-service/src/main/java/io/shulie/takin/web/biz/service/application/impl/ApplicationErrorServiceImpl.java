package io.shulie.takin.web.biz.service.application.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.excel.util.CollectionUtils;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.text.CharSequenceUtil;
import com.google.common.collect.Lists;
import com.pamirs.takin.common.util.DateUtils;
import com.pamirs.takin.entity.domain.dto.NodeUploadDataDTO;
import com.pamirs.takin.entity.domain.entity.ExceptionInfo;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationErrorQueryInput;
import io.shulie.takin.web.biz.pojo.output.application.ApplicationErrorOutput;
import io.shulie.takin.web.biz.pojo.output.application.ApplicationExceptionOutput;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.application.ApplicationErrorService;
import io.shulie.takin.web.biz.service.impl.ApplicationServiceImpl;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.common.Separator;
import io.shulie.takin.web.common.enums.application.AppExceptionCodeEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationResult;
import io.shulie.takin.web.data.result.application.InstanceInfoResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
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

    @Resource
    private ApplicationService applicationService;

    @Resource
    private ApplicationDAO applicationDAO;

    @Resource
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    @Override
    public List<ApplicationErrorOutput> list(ApplicationErrorQueryInput queryRequest) {
        List<ApplicationErrorOutput> responseList = Lists.newArrayList();
        ApplicationDetailResult tApplicationMnt = ensureApplicationExist(queryRequest);

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

        return this.processErrorList(responseList);
    }

    private ApplicationDetailResult ensureApplicationExist(ApplicationErrorQueryInput queryRequest) {
        Response<ApplicationDetailResult> applicationMntResponse = applicationService.getApplicationInfoForError(
                String.valueOf(queryRequest.getApplicationId()));
        ApplicationDetailResult tApplicationMnt = applicationMntResponse.getData();
        if (Objects.isNull(tApplicationMnt)) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_VALIDATE_ERROR, "应用不存在");
        }
        return tApplicationMnt;
    }

    private void putNodeExceptionIfNeeded(List<ApplicationErrorOutput> responseList,
                                          ApplicationDetailResult tApplicationMnt) {
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
        nodeUploadDataDTOList.parallelStream().forEach(n -> {
            NodeUploadDataDTO nodeUploadDataDTO = JSONObject.parseObject(n, NodeUploadDataDTO.class);
            Map<String, Object> exceptionMap = nodeUploadDataDTO.getSwitchErrorMap();
            if (exceptionMap != null && exceptionMap.size() > 0) {
                for (Map.Entry<String, Object> entry : exceptionMap.entrySet()) {
                    String message = String.valueOf(entry.getValue());
                    if (message.contains("errorCode")) {
                        ExceptionInfo exceptionInfo = null;
                        try {
                            exceptionInfo = JSONObject.parseObject(message, ExceptionInfo.class);
                        } catch (Exception e) {
                            log.error("异常转换失败：错误信息: {},异常内容{}", message, e.getMessage());
                        }
                        ApplicationErrorOutput applicationErrorResponse
                                = new ApplicationErrorOutput()
                                .setExceptionId(exceptionInfo != null ? exceptionInfo.getErrorCode() : "web-异常原文显示")
                                .setAgentIdList(Collections.singletonList(nodeUploadDataDTO.getAgentId()))
                                .setDescription(exceptionInfo != null ? exceptionInfo.getMessage() : message)
                                .setDetail(exceptionInfo != null ? exceptionInfo.getDetail() : message)
                                .setTime(nodeUploadDataDTO.getExceptionTime());
                        if (!StringUtil.equals("探针接入异常", applicationErrorResponse.getDetail())
                                || !StringUtil.equals("探针接入异常", applicationErrorResponse.getDescription())) {
                            responseList.add(applicationErrorResponse);
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
     * @param totalNodeCount  节点数量
     * @return 节点错误
     */
    private ApplicationErrorOutput getNodeErrorResponse(String applicationName, Integer totalNodeCount) {
        List<ApplicationResult> applicationResultList = applicationDAO.getApplicationByName(
                Collections.singletonList(applicationName));

        ApplicationErrorOutput applicationErrorResponse = null;
        if (CollectionUtils.isEmpty(applicationResultList)
                || !totalNodeCount.equals(applicationResultList.get(0).getInstanceInfo().getInstanceOnlineAmount())) {
            applicationErrorResponse = new ApplicationErrorOutput();
            applicationErrorResponse.setExceptionId("-");
            applicationErrorResponse.setAgentIdList(Collections.singletonList("-"));
            applicationErrorResponse.setDescription("已安装探针节点数 与 配置的节点总数 不一致");
            applicationErrorResponse.setTime(DateUtils.getNowDateStr());

            Integer onlineNodeNum = 0;
            if (!CollectionUtils.isEmpty(applicationResultList)) {
                onlineNodeNum = applicationResultList.get(0).getInstanceInfo().getInstanceOnlineAmount();
            }
            applicationErrorResponse.setDetail("设置节点数：" + totalNodeCount + "，上报的已安装探针节点数：" + onlineNodeNum);
        }

        return applicationErrorResponse;
    }

    /**
     * 错误列表排序处理
     *
     * @param responseList 错误列表
     * @return 排序好的错误列表
     */
    private List<ApplicationErrorOutput> processErrorList(List<ApplicationErrorOutput> responseList) {
        // 按照时间倒序输出
        List<ApplicationErrorOutput> sortedList = responseList.parallelStream()
                .filter(t -> t != null && CharSequenceUtil.isNotBlank(t.getTime()))
                .sorted((a1, a2) -> a2.getTime().compareTo(a1.getTime()))
                .collect(Collectors.toList());

        List<ApplicationErrorOutput> noTimeList = responseList.parallelStream()
                // 无时间的
                .filter(response -> response != null && CharSequenceUtil.isBlank(response.getTime()))
                .collect(Collectors.toList());

        if (sortedList.isEmpty()) {
            return noTimeList;
        }

        sortedList.addAll(noTimeList);
        return sortedList;
    }

}
