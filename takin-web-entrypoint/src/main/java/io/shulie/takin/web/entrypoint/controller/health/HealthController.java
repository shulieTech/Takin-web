package io.shulie.takin.web.entrypoint.controller.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qianshui
 * @date 2020/5/29 下午4:42
 */
@RestController
@RequestMapping("/api/health")
@Slf4j
public class HealthController {

    @GetMapping
    public String checkHealth() {
        log.info("OK ...");
        return "OK";
    }
}
