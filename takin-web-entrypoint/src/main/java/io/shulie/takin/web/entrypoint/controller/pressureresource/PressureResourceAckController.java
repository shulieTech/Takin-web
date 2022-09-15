package io.shulie.takin.web.entrypoint.controller.pressureresource;

import com.alibaba.fastjson.JSON;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceCommandService;
import io.shulie.takin.web.biz.service.pressureresource.vo.agent.command.TakinAck;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = ApiUrls.TAKIN_API_URL + "/pressureResource/agent")
@Api(tags = "接口: 压测资源探针检测结果上报")
@Slf4j
public class PressureResourceAckController {

    @Autowired
    private PressureResourceCommandService pressureResourceCommandService;


    @PostMapping("/ack")
    public void commandAck(@RequestBody TakinAck takinAck){
        log.info("收到压测资源响应:{}", JSON.toJSONString(takinAck));
        pressureResourceCommandService.processAck(takinAck);
    }



}
