package io.shulie.takin.web.entrypoint.controller.cloud.resources.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PullCloudResourcesSchedule {

    @Scheduled(cron = "0 0 0 * * ? ")
    public void pullData() {

    }
}
