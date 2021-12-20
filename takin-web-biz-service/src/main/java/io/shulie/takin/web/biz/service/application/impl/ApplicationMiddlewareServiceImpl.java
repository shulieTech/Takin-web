package io.shulie.takin.web.biz.service.application.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.mq.producer.Producer;
import io.shulie.takin.web.biz.pojo.dto.application.CompareApplicationMiddlewareDTO;
import io.shulie.takin.web.biz.pojo.dto.mq.MessageDTO;
import io.shulie.takin.web.biz.pojo.request.agent.PushMiddlewareListRequest;
import io.shulie.takin.web.biz.pojo.request.agent.PushMiddlewareRequest;
import io.shulie.takin.web.biz.pojo.request.application.ListApplicationMiddlewareRequest;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationMiddlewareCountResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationMiddlewareListResponse;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.application.ApplicationMiddlewareService;
import io.shulie.takin.web.biz.service.application.MiddlewareJarService;
import io.shulie.takin.web.biz.utils.PageUtils;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.constant.LockKeyConstants;
import io.shulie.takin.web.common.constant.MqConstants;
import io.shulie.takin.web.common.enums.application.ApplicationMiddlewareStatusEnum;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.pojo.dto.mq.MqApplicationMiddlewareCompareDTO;
import io.shulie.takin.web.common.util.DataTransformUtil;
import io.shulie.takin.web.common.util.JsonUtil;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.application.ApplicationMiddlewareDAO;
import io.shulie.takin.web.data.param.application.CreateApplicationMiddlewareParam;
import io.shulie.takin.web.data.param.application.PageApplicationMiddlewareParam;
import io.shulie.takin.web.data.param.application.QueryApplicationMiddlewareParam;
import io.shulie.takin.web.data.param.application.UpdateApplicationMiddlewareParam;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationMiddlewareListResult;
import io.shulie.takin.web.data.result.application.ApplicationMiddlewareStatusAboutCountResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 应用中间件(ApplicationMiddleware)service
 *
 * @author liuchuan
 * @date 2021-06-30 16:11:28
 */
@Slf4j
@Service
public class ApplicationMiddlewareServiceImpl implements ApplicationMiddlewareService {

    @Autowired
    private Producer producer;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private ApplicationMiddlewareDAO applicationMiddlewareDAO;

    @Autowired
    private DistributedLock distributedLock;

    @Autowired
    private MiddlewareJarService middlewareJarService;

    @Override
    public PagingList<ApplicationMiddlewareListResponse> page(
        ListApplicationMiddlewareRequest listApplicationMiddlewareRequest) {
        PageApplicationMiddlewareParam pageApplicationMiddlewareParam = new PageApplicationMiddlewareParam();
        BeanUtils.copyProperties(listApplicationMiddlewareRequest, pageApplicationMiddlewareParam);

        // 类型排序
        PageUtils.clearPageHelper();
        IPage<ApplicationMiddlewareListResult> page = applicationMiddlewareDAO.page(pageApplicationMiddlewareParam);
        List<ApplicationMiddlewareListResult> results = page.getRecords();
        if (results.isEmpty()) {
            return PagingList.of(Collections.emptyList(), page.getTotal());
        }

        List<ApplicationMiddlewareListResponse> responseList = results.stream().map(result -> {
            ApplicationMiddlewareListResponse response = new ApplicationMiddlewareListResponse();
            BeanUtils.copyProperties(result, response);

            // 状态转换
            ApplicationMiddlewareStatusEnum applicationMiddlewareStatusEnum =
                ApplicationMiddlewareStatusEnum.getByCode(result.getStatus());
            if (applicationMiddlewareStatusEnum != null) {
                response.setStatusDesc(applicationMiddlewareStatusEnum.getDesc());
            }

            return response;
        }).collect(Collectors.toList());
        return PagingList.of(responseList, page.getTotal());
    }

    @Override
    public ApplicationMiddlewareCountResponse countSome(Long applicationId) {
        PageUtils.clearPageHelper();
        List<ApplicationMiddlewareStatusAboutCountResult> statusMapCountResultList = applicationMiddlewareDAO
            .listCountByApplicationIdAndStatusAndGroupByStatus(applicationId, null);

        if (statusMapCountResultList.isEmpty()) {
            return new ApplicationMiddlewareCountResponse();
        }

        // 状态统计转为 状态 -> 统计个数 map
        Map<Integer, Integer> statusAboutCount = statusMapCountResultList.stream()
            .collect(Collectors.toMap(ApplicationMiddlewareStatusAboutCountResult::getStatus,
                ApplicationMiddlewareStatusAboutCountResult::getCount, (v1, v2) -> v2));

        ApplicationMiddlewareCountResponse response = new ApplicationMiddlewareCountResponse();
        response.setTotalCount(applicationMiddlewareDAO.countByApplicationIdAndStatus(applicationId, null));
        response.setSupportedCount(statusAboutCount.get(ApplicationMiddlewareStatusEnum.SUPPORTED.getCode()));
        response.setUnknownCount(statusAboutCount.get(ApplicationMiddlewareStatusEnum.UNKNOWN.getCode()));
        response.setNotSupportedCount(statusAboutCount.get(ApplicationMiddlewareStatusEnum.NOT_SUPPORTED.getCode()));
        response.setNoneCount(statusAboutCount.get(ApplicationMiddlewareStatusEnum.NONE.getCode()));
        response.setNoSupportRequiredCount(
            statusAboutCount.get(ApplicationMiddlewareStatusEnum.NO_SUPPORT_REQUIRED.getCode()));

        return response;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void compare(Long applicationId) {
        String lockKey = String.format(LockKeyConstants.LOCK_COMPARE_APPLICATION_MIDDLEWARE, applicationId);
        if (!distributedLock.tryLockZeroWait(lockKey)) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_DEBUG_REPEAT_ERROR, AppConstants.TOO_FREQUENTLY);
        }

        try {
            // 查询应用中间件列表
            QueryApplicationMiddlewareParam param = new QueryApplicationMiddlewareParam();
            param.setApplicationId(applicationId);
            List<ApplicationMiddlewareListResult> results = applicationMiddlewareDAO.listByApplicationId(param);
            if (results.isEmpty()) {
                return;
            }

            // 比对
            List<UpdateApplicationMiddlewareParam> updateParamList = this.doCompare(results);
            // 数据库更新
            applicationMiddlewareDAO.updateBatchById(updateParamList);

        } finally {
            distributedLock.unLockSafely(lockKey);
        }
    }

    @Override
    public List<UpdateApplicationMiddlewareParam> doCompare(List<ApplicationMiddlewareListResult> results) {
        // 转 dto
        List<CompareApplicationMiddlewareDTO> compareApplicationMiddlewareList = DataTransformUtil.list2list(results,
            CompareApplicationMiddlewareDTO.class);

        // 比对
        middlewareJarService.appCompare(compareApplicationMiddlewareList);

        // 返回
        return DataTransformUtil.list2list(compareApplicationMiddlewareList, UpdateApplicationMiddlewareParam.class);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void pushMiddlewareList(PushMiddlewareRequest pushMiddlewareRequest) {
        List<PushMiddlewareListRequest> middlewareList = pushMiddlewareRequest.getMiddlewareList();
        if (middlewareList.isEmpty()) {
            return;
        }

        String applicationName = pushMiddlewareRequest.getApplicationName();
        log.info("应用中间件上报 --> 应用名称: {}", applicationName);

        String lockKey = String.format(LockKeyConstants.LOCK_PUSH_APPLICATION_MIDDLEWARE, applicationName);
        // 60s 内, 同一个应用重复上传的, 不处理, 幂等
        // 不管程序是否执行结束, 等待 60s, 自己释放
        if (!distributedLock.tryLockSecondsTimeUnit(lockKey, 0L, 60L)) {
            return;
        }

        // 根据中间件名称查询, 获取应用id
        ApplicationDetailResult application = applicationDAO.getByName(applicationName);
        try {
            this.isPushError(application == null, "应用不存在!");

            // 根据应用id, 删除应用中间件
            log.info("应用中间件上报 --> 删除应用下原有的中间件");
            applicationMiddlewareDAO.removeByApplicationId(application.getApplicationId());

            // 新的中间件插入
            log.info("应用中间件上报 --> 插入上报中间件");
            List<CreateApplicationMiddlewareParam> createApplicationMiddlewareParamList =
                this.listCreateApplicationMiddlewareParam(middlewareList, application);
            this.isPushError(!applicationMiddlewareDAO.insertBatch(createApplicationMiddlewareParamList),
                "应用中间件报错失败!");

        } catch (Exception e) {
            // 发生错误, 解锁
            distributedLock.unLockSafely(lockKey);
            throw this.getPushError(e.getMessage());
        }

        // 将应用id加入消息队列, 慢慢消费
        log.info("应用中间件上报 --> 异步消息发送, applicationId: {}", application.getApplicationId());
        MqApplicationMiddlewareCompareDTO mqApplicationMiddlewareCompareDTO = new MqApplicationMiddlewareCompareDTO();
        mqApplicationMiddlewareCompareDTO.setApplicationId(application.getApplicationId());
        mqApplicationMiddlewareCompareDTO.setEnvCode(WebPluginUtils.traceEnvCode());
        mqApplicationMiddlewareCompareDTO.setTenantId(WebPluginUtils.traceTenantId());
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessage(JsonUtil.bean2Json(mqApplicationMiddlewareCompareDTO));
        messageDTO.setTopic(MqConstants.MQ_REDIS_PUSH_APPLICATION_MIDDLEWARE);
        producer.produce(messageDTO);

        // 这里不用解锁, 60s 等待自动释放
    }

    @Override
    public Map<String, Map<Integer, Integer>> getApplicationNameAboutStatusCountMap(
        List<Long> applicationIds) {
        List<Integer> statusList = Arrays.asList(ApplicationMiddlewareStatusEnum.NONE.getCode(),
            ApplicationMiddlewareStatusEnum.UNKNOWN.getCode(),
            ApplicationMiddlewareStatusEnum.NOT_SUPPORTED.getCode());
        List<ApplicationMiddlewareStatusAboutCountResult> results = applicationMiddlewareDAO
            .listStatusCountByAndGroupByApplicationNameListAndStatus(applicationIds, statusList);
        if (results.isEmpty()) {
            return Collections.emptyMap();
        }

        return results.stream()
            .collect(Collectors.groupingBy(ApplicationMiddlewareStatusAboutCountResult::getApplicationName,
                Collectors.toMap(ApplicationMiddlewareStatusAboutCountResult::getStatus,
                    ApplicationMiddlewareStatusAboutCountResult::getCount)));
    }

    /**
     * 获得推送异常
     *
     * @param message 错误消息
     * @return 推送一场
     */
    private TakinWebException getPushError(String message) {
        return new TakinWebException(ExceptionCode.APPLICATION_MIDDLEWARE_PUSH_ERROR, message);
    }

    /**
     * 推送异常
     *
     * @param condition 条件
     * @param message   错误信息
     */
    private void isPushError(boolean condition, String message) {
        if (condition) {
            throw this.getPushError(message);
        }
    }

    /**
     * 上报的中间件转新增的中间件对象
     *
     * @param middlewareList 上报的中间件
     * @param application    应用
     * @return 待新增的中间件对象列表
     */
    private List<CreateApplicationMiddlewareParam> listCreateApplicationMiddlewareParam(
        List<PushMiddlewareListRequest> middlewareList, ApplicationDetailResult application) {
        return middlewareList.stream().map(pushMiddlewareListRequest -> {
            CreateApplicationMiddlewareParam createParam = new CreateApplicationMiddlewareParam();
            createParam.setApplicationId(application.getApplicationId());
            createParam.setApplicationName(application.getApplicationName());

            String artifactId = pushMiddlewareListRequest.getArtifactId();
            createParam.setArtifactId(artifactId == null ? "" : artifactId);

            String groupId = pushMiddlewareListRequest.getGroupId();
            createParam.setGroupId(groupId == null ? "" : groupId);

            String version = pushMiddlewareListRequest.getVersion();
            createParam.setVersion(version == null ? "" : version);

            createParam.setEnvCode(WebPluginUtils.traceEnvCode());
            createParam.setTenantId(WebPluginUtils.traceTenantId());
            return createParam;
        }).collect(Collectors.toList());
    }

}
