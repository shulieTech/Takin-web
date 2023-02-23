package io.shulie.takin.web.biz.threadpool;

import java.util.concurrent.CountDownLatch;

import com.pamirs.takin.entity.domain.vo.ApplicationVo;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.common.common.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

/**
 * @author junshi
 * @ClassName ApplicationInfoSync
 * @Description
 * @createTime 2023年02月22日 11:57
 */
@Component
@EnableAsync
@Slf4j
public class ApplicationInfoSync {

    @Autowired
    @Lazy
    private ApplicationService applicationService;

    @Async(value = "foreachQueryThreadPool")
    public Integer getApplicationInfo(String id, CountDownLatch countDownLatch) {
        try {
            Response<ApplicationVo> vo = applicationService.getApplicationInfo(id);
            if (vo.getSuccess() && vo.getData() != null) {
                return vo.getData().getAccessStatus();
            }
        } catch (Exception e) {
            log.error("ApplicationInfoSync#getApplicationInfo Exception, message={}", e.getMessage());
        } finally {
            countDownLatch.countDown();
        }
        return null;
    }
}
