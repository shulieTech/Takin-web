package io.shulie.takin.web.entrypoint.controller.webide;

import io.shulie.takin.web.biz.service.webide.WebIdeTraceService;
import io.shulie.takin.web.biz.service.webide.dto.TraceRespDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author angju
 * @date 2022/7/8 16:36
 */
@RequestMapping("/api/webide/trace")
@Api(tags = "webIDE查询 trace 相关接口", value = "webIDE查询 trace 相关接口")
@RestController
public class WebIdeTraceServiceController {
    @Resource
    private WebIdeTraceService webIdeTraceService;


    @ApiOperation("根据业务id查询trace详情")
    @GetMapping("/business")
    public List<TraceRespDTO> getTraceDetailByBusinessActivityId(@RequestParam("businessActivityId") Long businessActivityId){
        return webIdeTraceService.getLastTraceDetailByBusinessActivityId(businessActivityId);
    }


    @ApiOperation("根据traceId查询trace详情")
    @GetMapping("")
    public List<TraceRespDTO> getTraceDetailByTraceId(@RequestParam("traceId") String traceId){
        return webIdeTraceService.getTraceDetailByTraceId(traceId);
    }
}
