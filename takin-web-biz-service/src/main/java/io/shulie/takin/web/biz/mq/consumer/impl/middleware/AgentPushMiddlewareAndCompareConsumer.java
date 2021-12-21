package io.shulie.takin.web.biz.mq.consumer.impl.middleware;

import java.util.List;

import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.application.ApplicationMiddlewareService;
import io.shulie.takin.web.biz.utils.PageUtils;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.constant.LockKeyConstants;
import io.shulie.takin.web.common.constant.MqConstants;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.common.pojo.dto.mq.MqApplicationMiddlewareCompareDTO;
import io.shulie.takin.web.common.util.JsonUtil;
import io.shulie.takin.web.data.dao.application.ApplicationMiddlewareDAO;
import io.shulie.takin.web.data.param.application.UpdateApplicationMiddlewareParam;
import io.shulie.takin.web.data.result.application.ApplicationMiddlewareListResult;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * @author liuchuan
 * @date 2021/7/2 5:31 下午
 */
@Slf4j
@Service(MqConstants.MQ_REDIS_PUSH_APPLICATION_MIDDLEWARE)
public class AgentPushMiddlewareAndCompareConsumer implements MessageListener {

    @Autowired
    private DistributedLock distributedLock;

    @Autowired
    private ApplicationMiddlewareDAO applicationMiddlewareDAO;

    @Autowired
    @Lazy
    private ApplicationMiddlewareService applicationMiddlewareService;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageBody = new String(message.getBody());
        if (StringUtils.isEmpty(messageBody)) {
            return;
        }

        messageBody = messageBody.substring(1, messageBody.length() - 1).replace("\\", "");
        MqApplicationMiddlewareCompareDTO mqApplicationMiddlewareCompareDTO = JsonUtil.json2Bean(messageBody,
            MqApplicationMiddlewareCompareDTO.class);
        if (mqApplicationMiddlewareCompareDTO == null) {
            return;
        }

        log.info("应用中间件上报 --> 异步消息处理 --> 消息体: {}", messageBody);

        // 锁住 applicationId
        Long applicationId = mqApplicationMiddlewareCompareDTO.getApplicationId();
        String lockKey = String.format(LockKeyConstants.LOCK_HANDLE_PUSH_APPLICATION_MIDDLEWARE, applicationId);
        if (!distributedLock.tryLockZeroWait(lockKey)) {
            return;
        }

        try {
            TenantCommonExt tenantCommonExt = new TenantCommonExt(mqApplicationMiddlewareCompareDTO.getTenantId(),
                null, mqApplicationMiddlewareCompareDTO.getEnvCode(),
                null, ContextSourceEnum.JOB.getCode());
            WebPluginUtils.setTraceTenantContext(tenantCommonExt);

            // 根据 applicationId 查询应用中间件
            log.info("应用中间件上报 --> 异步消息处理 --> 应用中间件查询");
            PageUtils.clearPageHelper();
            List<ApplicationMiddlewareListResult> applicationMiddlewareList =
                applicationMiddlewareDAO.listByApplicationId(applicationId);
            if (applicationMiddlewareList.isEmpty()) {
                return;
            }

            // 比对
            log.info("应用中间件上报 --> 异步消息处理 --> 应用中间件比对");
            List<UpdateApplicationMiddlewareParam> updateParamList =
                applicationMiddlewareService.doCompare(applicationMiddlewareList);

            log.info("应用中间件上报 --> 异步消息处理 --> 应用中间件更新");
            applicationMiddlewareDAO.updateBatchById(updateParamList);

        } finally {
            distributedLock.unLockSafely(lockKey);
            WebPluginUtils.removeTraceContext();
        }
    }

    /**
     * 获得入参
     *
     * @param message 消息体
     * @return 相应数据
     */
    private MqApplicationMiddlewareCompareDTO getMqApplicationMiddlewareCompareDTO(
        Message message) {
        String messageBody = new String(message.getBody());
        if (StringUtils.isEmpty(messageBody)) {
            return null;
        }

        String[] messageArray = messageBody.split(AppConstants.COMMA);
        if (messageArray == null || messageArray.length != 3) {
            return null;
        }

        MqApplicationMiddlewareCompareDTO mqApplicationMiddlewareCompareDTO = new MqApplicationMiddlewareCompareDTO();
        mqApplicationMiddlewareCompareDTO.setTenantId(Long.valueOf(messageArray[0]));
        mqApplicationMiddlewareCompareDTO.setEnvCode(String.valueOf(messageArray[1]));
        mqApplicationMiddlewareCompareDTO.setApplicationId(Long.valueOf(messageArray[2]));
        return mqApplicationMiddlewareCompareDTO;
    }

}
