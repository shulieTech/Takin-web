package io.shulie.takin.web.entrypoint.controller.scenemanage;

import java.util.List;

import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL + "engine")
@Api(tags = "压力机集群", value = "压力机集群")
public class PressureClusterController {

    @GetMapping("list")
    public List<Object> clusters() {



        return null;
    }
}
