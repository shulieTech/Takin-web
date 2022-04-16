package io.shulie.takin.web.entrypoint.controller.open;

import java.util.List;

import io.shulie.takin.web.biz.pojo.response.ApplicationEntryResponse;
import io.shulie.takin.web.biz.service.PressureService;
import io.shulie.takin.web.common.constant.UrlConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 开放接口之压测控制层
 *
 * @author liuchuan
 * @since 2021-06-03 13:40:57
 */
@RestController
@RequestMapping(UrlConstants.OPEN_PREFIX + "pressure")
@Api(tags = "开放接口: 压测相关")
public class PressureController {

    @Autowired
    private PressureService pressureService;

    @ApiOperation("|_ 通过报告id获得应用入口")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "reportId", value = "任务id", required = true,
            dataType = "long", paramType = "query")
    })
    @GetMapping("/entries")
    public List<ApplicationEntryResponse> getApplicationEntries(@RequestParam Long reportId) {
        return pressureService.getApplicationEntriesByJobId(reportId);
    }

}
