package io.shulie.takin.web.entrypoint.controller.cloud.sla;

import io.shulie.takin.cloud.biz.service.sla.SlaService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(tags = "sla回调接口")
@RequestMapping("/api/sla")
public class SLAController {

    @Autowired
    private SlaService slaService;

}
