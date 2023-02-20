package io.shulie.takin.web.biz.job;

import com.xxl.job.core.handler.annotation.XxlJob;
import io.shulie.takin.web.biz.service.VerifyTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @date 2021/6/15 5:40 下午
 */
@Component
@Slf4j
public class ShowdownVerifyJob  {

    @Autowired
    private VerifyTaskService verifyTaskService;


    @XxlJob("showdownVerifyJobExecute")
    public void execute() {
        verifyTaskService.showdownVerifyTask();
    }
}
