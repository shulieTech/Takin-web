package io.shulie.takin.web.config.sync.file.endpoint;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shiyajian
 * create: 2020-10-02
 */
@RestController("/open-api")
@RequestMapping
public class HttpConfigEndpoint {

    @GetMapping("/allow_list")
    public void getAllowList() {
        // 白名单
    }

    // todo 其他待定
}
