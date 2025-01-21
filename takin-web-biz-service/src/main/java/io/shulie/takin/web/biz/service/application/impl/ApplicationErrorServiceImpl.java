package io.shulie.takin.web.biz.service.application.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.excel.util.CollectionUtils;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.pamirs.takin.common.util.DateUtils;
import com.pamirs.takin.entity.domain.dto.NodeUploadDataDTO;
import com.pamirs.takin.entity.domain.entity.ExceptionInfo;
import com.pamirs.takin.entity.domain.vo.ApplicationVo;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationErrorQueryInput;
import io.shulie.takin.web.biz.pojo.output.application.ApplicationErrorOutput;
import io.shulie.takin.web.biz.pojo.output.application.ApplicationExceptionOutput;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.application.ApplicationErrorService;
import io.shulie.takin.web.biz.service.impl.ApplicationServiceImpl;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.common.Separator;
import io.shulie.takin.web.common.enums.application.AppAccessStatusEnum;
import io.shulie.takin.web.common.enums.application.AppExceptionCodeEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationListResult;
import io.shulie.takin.web.data.result.application.ApplicationResult;
import io.shulie.takin.web.data.result.application.InstanceInfoResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    private void convertNodeUploadDataList(List<ApplicationErrorOutput> responseList, List<String> nodeUploadDataDTOList) {
        nodeUploadDataDTOList.parallelStream().forEach(n -> {
            NodeUploadDataDTO nodeUploadDataDTO = JSONObject.parseObject(n, NodeUploadDataDTO.class);
            Map<String, Object> exceptionMap = nodeUploadDataDTO.getSwitchErrorMap();
            if (MapUtil.isEmpty(exceptionMap)) {
                return;
            }
            for (Map.Entry<String, Object> entry : exceptionMap.entrySet()) {
                String message = String.valueOf(entry.getValue());
                if (!message.contains("errorCode")) {
                    continue;
                }
                ExceptionInfo exceptionInfo = null;
                try {
                    exceptionInfo = JSONObject.parseObject(message, ExceptionInfo.class);
                    ApplicationErrorOutput applicationErrorResponse = new ApplicationErrorOutput().setExceptionId(exceptionInfo != null ? exceptionInfo.getErrorCode() : "web-异常原文显示").setAgentIdList(Collections.singletonList(nodeUploadDataDTO.getAgentId())).setDescription(exceptionInfo != null ? exceptionInfo.getMessage() : message).setDetail(exceptionInfo != null ? exceptionInfo.getDetail() : message).setTime(nodeUploadDataDTO.getExceptionTime());
                    if (!StringUtil.equals("探针接入异常", applicationErrorResponse.getDetail()) || !StringUtil.equals("探针接入异常", applicationErrorResponse.getDescription())) {
                        responseList.add(applicationErrorResponse);
                    }
                } catch (Exception e) {
                    if (e.getMessage().contains("unexpect token error")) {
                        ApplicationErrorOutput applicationErrorResponse = new ApplicationErrorOutput().setExceptionId("web-异常原文显示").setAgentIdList(Collections.singletonList(nodeUploadDataDTO.getAgentId())).setDescription(message).setDetail(message).setTime(nodeUploadDataDTO.getExceptionTime());
                        if (!StringUtil.equals("探针接入异常", applicationErrorResponse.getDetail()) || !StringUtil.equals("探针接入异常", applicationErrorResponse.getDescription())) {
                            responseList.add(applicationErrorResponse);
                        }
                    } else {
                        log.error("异常转换失败：错误信息: {},异常内容{}", message, e.getMessage());
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
            if (result.getInstanceAmount().equals(result.getInstanceOnlineAmount())) {
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
            if (keys == null) {
                return;
            }
            for (String nodeKey : keys) {
                List<String> nodeUploadDataDTOList = redisTemplate.opsForList().range(nodeKey, 0, -1);
                if (CollectionUtils.isEmpty(nodeUploadDataDTOList)) {
                    continue;
                }
                nodeUploadDataDTOList.forEach(n -> {
                    NodeUploadDataDTO nodeUploadDataDTO = JSONObject.parseObject(n, NodeUploadDataDTO.class);
                    Map<String, Object> exceptionMap = nodeUploadDataDTO.getSwitchErrorMap();
                    if (MapUtil.isEmpty(exceptionMap)) {
                        return;
                    }
                    for (Map.Entry<String, Object> entry : exceptionMap.entrySet()) {
                        String message = String.valueOf(entry.getValue());
                        if (!message.contains("errorCode")) {
                            continue;
                        }
                        try {
                            ExceptionInfo exceptionInfo = JSONObject.parseObject(message, ExceptionInfo.class);
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
                });
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

    @Override
    public Map<Long, ApplicationVo> batchGetApplicationStatus(List<ApplicationListResult> records) {
        // 结果Map，key=应用ID，value=应用视图信息
        Map<Long, ApplicationVo> applicationStatusMap = new HashMap<>();
        if (CollectionUtils.isEmpty(records)) {
            return applicationStatusMap;
        }

        // ===========================
        // 1. 查询数据库中的应用信息
        // ===========================
        List<String> appNames = records.stream().map(ApplicationListResult::getApplicationName).collect(Collectors.toList());

        List<ApplicationResult> applicationResultList = applicationDAO.getApplicationByName(appNames);
        if (CollectionUtils.isEmpty(applicationResultList)) {
            return applicationStatusMap;
        }

        Map<String, ApplicationListResult> applicationListResultMap = records.stream().collect(Collectors.toMap(ApplicationListResult::getApplicationName, Function.identity(), (a, b) -> a));
        for (ApplicationResult applicationResult : applicationResultList) {
            ApplicationListResult applicationListResult = applicationListResultMap.get(applicationResult.getAppName());
            applicationResult.getInstanceInfo().setInstanceAmount(applicationListResult.getNodeNum());
        }
        // key: 应用名称, value: 应用数据库实体
        Map<String, ApplicationResult> applicationDetailResultMap = applicationResultList.stream().collect(Collectors.toMap(ApplicationResult::getAppName, Function.identity(), (a, b) -> a));

        // ===========================
        // 2. 一次性收集所有 nodeKey
        // ===========================
        // key: 应用ID, value: 该应用下的所有 nodeKey
        Map<Long, Set<String>> appNodeKeysMap = new HashMap<>(records.size());
        for (ApplicationListResult application : records) {
            Long appId = application.getApplicationId();
            String appUniqueKey = CommonUtil.generateRedisKeyWithSeparator(Separator.Separator3, WebPluginUtils.traceTenantAppKey(), // 租户
                    WebPluginUtils.traceEnvCode(),      // 环境
                    appId + ApplicationServiceImpl.PRADARNODE_KEYSET);

            Set<String> nodeKeys = redisTemplate.opsForSet().members(appUniqueKey);
            if (CollectionUtils.isEmpty(nodeKeys)) {
                nodeKeys = Collections.emptySet();
            }
            appNodeKeysMap.put(appId, nodeKeys);
        }

        // ===========================
        // 3. 用 Pipeline 一次性获取所有 nodeKey 的最新异常JSON
        // ===========================
        // 收集“所有应用的所有 nodeKey”放入同一个 list，确保顺序一致
        List<String> allNodeKeys = new ArrayList<>();
        for (Set<String> nodeSet : appNodeKeysMap.values()) {
            allNodeKeys.addAll(nodeSet);
        }

        // 如果压根没有任何 nodeKey，就直接判定是否节点数异常即可
        if (CollectionUtils.isEmpty(allNodeKeys)) {
            for (ApplicationListResult application : records) {
                ApplicationResult appResult = applicationDetailResultMap.get(application.getApplicationName());
                // 获取节点数是否不一致
                boolean nodeCountMismatch = isNodeCountMismatch(appResult);

                ApplicationVo vo = new ApplicationVo();
                vo.setPrimaryKeyId(application.getApplicationId());
                // 如果节点数不一致，就标记为3，否则1
                vo.setAccessStatus(nodeCountMismatch ? AppAccessStatusEnum.EXCEPTION.getCode() : AppAccessStatusEnum.NORMAL.getCode());
                applicationStatusMap.put(application.getApplicationId(), vo);
            }
            return applicationStatusMap;
        }

        // 执行Pipeline: 每个 nodeKey 都去 lIndex(key, 0) 拿最新一条异常数据
        List<Object> pipelineResults = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (String nodeKey : allNodeKeys) {
                connection.listCommands().lIndex(nodeKey.getBytes(StandardCharsets.UTF_8), 0);
            }
            return null;
        });

        // 将 pipeline 结果按顺序映射到 nodeKey => JSON
        Map<String, String> nodeKeyDataMap = new HashMap<>(allNodeKeys.size());
        for (int i = 0; i < allNodeKeys.size(); i++) {
            Object resultObj = pipelineResults.get(i);
            String dataStr = null;
            if (resultObj instanceof byte[]) {
                dataStr = (String) redisTemplate.getValueSerializer().deserialize((byte[]) resultObj);
            } else if (resultObj instanceof String) {
                dataStr = (String) resultObj;
            }
            // 可能为空，也可能是一个JSON字符串
            nodeKeyDataMap.put(allNodeKeys.get(i), dataStr);
        }

        // ===========================
        // 4. 每个应用再去判断是否有异常
        // ===========================
        for (ApplicationListResult application : records) {
            Long appId = application.getApplicationId();
            ApplicationResult appResult = applicationDetailResultMap.get(application.getApplicationName());

            ApplicationVo applicationVo = new ApplicationVo();
            applicationVo.setPrimaryKeyId(appId);

            // 判断“节点数是否不匹配”
            boolean nodeCountMismatch = isNodeCountMismatch(appResult);

            // 拿该应用对应的所有 nodeKey，再去取 pipeline 的数据
            Set<String> nodeKeys = appNodeKeysMap.getOrDefault(appId, Collections.emptySet());
            if (CollectionUtils.isEmpty(nodeKeys)) {
                // 没有任何节点上报，就只能依赖节点数是否匹配来判断
                applicationVo.setAccessStatus(nodeCountMismatch ? AppAccessStatusEnum.EXCEPTION.getCode() : AppAccessStatusEnum.NORMAL.getCode());
                applicationStatusMap.put(appId, applicationVo);
                continue;
            }

            // 收集当前应用所有“nodeKey最新数据”
            List<String> nodeDataList = nodeKeys.stream().map(nodeKeyDataMap::get).filter(StringUtils::isNotBlank).collect(Collectors.toList());

            // 判断是否存在有效的开关异常
            boolean hasSwitchError = processApplicationErrors(nodeDataList, /* 你们的过期时间(秒) */ 300);

            // 最终逻辑：只要节点数不匹配或者存在Agent开关异常 => 标记3，否则1
            applicationVo.setAccessStatus(nodeCountMismatch || hasSwitchError ? AppAccessStatusEnum.EXCEPTION.getCode() : AppAccessStatusEnum.NORMAL.getCode());

            applicationStatusMap.put(appId, applicationVo);
        }

        return applicationStatusMap;
    }

    /**
     * 判断节点数是否不匹配
     * 根据自己的业务逻辑：当 totalNodeCount != onlineNodeCount 时，视为不匹配
     */
    private boolean isNodeCountMismatch(ApplicationResult appResult) {
        if (appResult == null || appResult.getInstanceInfo() == null) {
            return false; // 或者看需求，也可以默认返回 true
        }
        Integer totalNodeCount = appResult.getInstanceInfo().getInstanceAmount();
        Integer onlineNodeCount = appResult.getInstanceInfo().getInstanceOnlineAmount();
        return totalNodeCount != null && !totalNodeCount.equals(onlineNodeCount);
    }

    public boolean processApplicationErrors(List<String> nodeDataList, int errorExpireTimeInSeconds) {
        if (CollectionUtils.isEmpty(nodeDataList)) {
            return false;
        }

        List<Map<String, String>> allExceptions = new ArrayList<>();
        for (String jsonData : nodeDataList) {
            if (StringUtils.isBlank(jsonData)) {
                continue;
            }
            try {
                // 解析成 DTO
                NodeUploadDataDTO dto = JSONObject.parseObject(jsonData, NodeUploadDataDTO.class);
                if (dto == null || MapUtils.isEmpty(dto.getSwitchErrorMap())) {
                    continue;
                }
                // 收集 switchErrorMap 里边的每条异常
                for (Object errorJson : dto.getSwitchErrorMap().values()) {
                    Map<String, String> errMap = JSONObject.parseObject((String) errorJson, Map.class);
                    if (errMap != null && errMap.containsKey("occurTime")) {
                        allExceptions.add(errMap);
                    }
                }
            } catch (Exception e) {
                log.warn("解析nodeData时出错, data={}", jsonData, e);
            }
        }

        if (allExceptions.isEmpty()) {
            return false;
        }

        // 按 occurTime 降序
        allExceptions.sort((m1, m2) -> compareOccurTime(m2.get("occurTime"), m1.get("occurTime")));
        // 拿最新的一条
        Map<String, String> latestException = allExceptions.get(0);

        // 是否有 errorCode
        String errorCode = latestException.get("errorCode");
        // occurTime 是否在“errorExpireTimeInSeconds”秒范围内
        String occurTime = latestException.get("occurTime");
        boolean recentEnough = isWithinErrorExpireTime(occurTime, errorExpireTimeInSeconds);

        return (StringUtils.isNotBlank(errorCode) && recentEnough);
    }

    /**
     * 时间比较工具, 返回 (time1 - time2)
     */
    private int compareOccurTime(String time1, String time2) {
        // 注意：Hutool 的 parse 支持可变参模式，可以传多个格式
        try {
            DateTime dateTime1 = DateUtil.parse(time1, "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss");
            DateTime dateTime2 = DateUtil.parse(time2, "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss");
            return dateTime1.compareTo(dateTime2);
        } catch (Exception e) {
            log.warn("compareOccurTime失败, time1={}, time2={}", time1, time2, e);
            return 0;
        }
    }

    /**
     * 判断时间是否在误差范围内
     */
    private boolean isWithinErrorExpireTime(String occurTimeStr, int expireSeconds) {
        try {
            DateTime occurTime = DateUtil.parse(occurTimeStr, "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss");
            DateTime now = DateTime.now();
            long diff = Math.abs(DateUtil.between(occurTime, now, DateUnit.SECOND));
            // 只要在正负 expireSeconds 之内，就视为有效
            return diff <= expireSeconds;
        } catch (Exception e) {
            log.warn("异常时间解析失败: {}", occurTimeStr, e);
            return false;
        }
    }


}
