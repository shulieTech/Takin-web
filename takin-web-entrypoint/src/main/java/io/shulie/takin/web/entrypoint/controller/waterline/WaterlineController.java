package io.shulie.takin.web.entrypoint.controller.waterline;

import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.WaterlineService;
import io.shulie.takin.web.common.vo.WebOptionEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/api/waterline")
@Api(tags = "容量水位管理", value = "容量水位管理")
@RestController
public class WaterlineController {

    @Autowired
    private WaterlineService waterlineService;

    @Autowired
    private ApplicationService applicationService;

    @ApiOperation("全部业务活动")
    @RequestMapping("/getAllActivityNames")
    public ResponseResult<List<String>> getAllActivityNames() {
        List<String> allActivityNames = waterlineService.getAllActivityNames();
        return ResponseResult.success(allActivityNames);
    }

    @ApiOperation("业务活动下的应用")
    @RequestMapping("/getAllApplicationsByActivity")
    public ResponseResult<List<String>> getAllApplicationsByActivity(@RequestParam(name = "activityName") String activityName) {
        List<String> allActivityNames = waterlineService.getAllApplicationsByActivity(activityName);
        return ResponseResult.success(allActivityNames);
    }

    @ApiOperation("应用下的节点")
    @RequestMapping("/getAllApplicationsByActivity")
    public ResponseResult<List<String>> getAllNodesByApplicationName(@RequestParam(name = "applicationName") String applicationName) {
        List<String> allActivityNames = waterlineService.getAllNodesByApplicationName(applicationName);
        return ResponseResult.success(allActivityNames);
    }
}
