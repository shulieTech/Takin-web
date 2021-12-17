package io.shulie.takin.web.biz.mq.consumer.impl.middleware;

import java.util.List;

import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.application.ApplicationMiddlewareService;
import io.shulie.takin.web.biz.utils.PageUtils;
import io.shulie.takin.web.common.constant.LockKeyConstants;
import io.shulie.takin.web.common.constant.MqConstants;
import io.shulie.takin.web.data.dao.application.ApplicationMiddlewareDAO;
import io.shulie.takin.web.data.param.application.UpdateApplicationMiddlewareParam;
import io.shulie.takin.web.data.result.application.ApplicationMiddlewareListResult;
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
        String applicationIdString = new String(message.getBody());
        if (StringUtils.isEmpty(applicationIdString)) {
            return;
        }

        log.info("应用中间件上报 --> 异步消息处理 --> applicationId: {}", applicationIdString);

        // 锁住 applicationId
        Long applicationId = Long.valueOf(applicationIdString);
        String lockKey = String.format(LockKeyConstants.LOCK_HANDLE_PUSH_APPLICATION_MIDDLEWARE, applicationId);
        if (!distributedLock.tryLockZeroWait(lockKey)) {
            return;
        }

        try {
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
        }
    }

}
