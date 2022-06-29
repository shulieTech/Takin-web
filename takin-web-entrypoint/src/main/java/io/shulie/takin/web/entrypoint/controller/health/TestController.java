package io.shulie.takin.web.entrypoint.controller.health;

import io.shulie.takin.common.beans.response.ResponseResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class TestController {
    // 啥也不干,支持多种请求方式
    @RequestMapping(value = "/test")
    public ResponseResult test(Map<String, Object> paramMap) {
        return ResponseResult.success();
    }
}
