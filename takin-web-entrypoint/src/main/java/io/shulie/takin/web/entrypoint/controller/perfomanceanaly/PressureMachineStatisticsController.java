package io.shulie.takin.web.entrypoint.controller.perfomanceanaly;

import io.shulie.takin.web.biz.pojo.request.perfomanceanaly.PressureMachineStatisticsRequest;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.PressureMachineStatisticsResponse;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.TypeValueDateVo;
import io.shulie.takin.web.biz.service.perfomanceanaly.PressureMachineStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author mubai
 * @date 2020-11-13 17:44
 */

@RestController
@RequestMapping("/api/pressure/machine/statistics")
@Api(tags = "压力机统计")
public class PressureMachineStatisticsController {

    @Autowired
    private PressureMachineStatisticsService pressureMachineStatisticsService;

    @GetMapping
    @ApiOperation(value = "压力机最新统计")
    public PressureMachineStatisticsResponse statistics() {
        return pressureMachineStatisticsService.getNewlyStatistics();
    }

    @GetMapping(value = "/trend/chart")
    @ApiOperation(value = "压力机趋势图")
    public List<TypeValueDateVo> statisticsTrendChart(
            @RequestParam String startTime, @RequestParam String endTime) {
        PressureMachineStatisticsRequest request = new PressureMachineStatisticsRequest();
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setCurrent(0);
        request.setPageSize(-1);
        return pressureMachineStatisticsService.queryByExample(request);
    }

}
