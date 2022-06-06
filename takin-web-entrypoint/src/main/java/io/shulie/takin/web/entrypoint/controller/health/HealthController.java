package io.shulie.takin.web.entrypoint.controller.health;

import lombok.extern.slf4j.Slf4j;
import io.shulie.takin.web.common.common.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qianshui
 * @date 2020/5/29 下午4:42
 */
@Slf4j
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public Response<String> checkHealth() {
        return Response.success("OK");
    }
}
