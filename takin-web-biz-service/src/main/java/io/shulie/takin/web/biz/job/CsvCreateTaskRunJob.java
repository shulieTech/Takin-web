package io.shulie.takin.web.biz.job;

import io.shulie.takin.web.biz.service.datamanage.CsvManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
* @Package io.shulie.takin.web.biz.job
* @ClassName: CsvCreateTaskRunJob
* @author hezhongqi
* @description:
* @date 2023/10/8 11:26
*/
@Component
@Slf4j
public class CsvCreateTaskRunJob {
    @Resource
    private CsvManageService csvManageService;

    //@XxlJob("csvCreateTaskRunJobExecute")
    //@Scheduled(cron = "0/10 * *  * * ?")
    public void execute() {
        try {
            csvManageService.runJob();
        } catch (Exception e) {
            log.error("csvCreateTaskRunJobExecute 异常",e);
        }
    }
}
