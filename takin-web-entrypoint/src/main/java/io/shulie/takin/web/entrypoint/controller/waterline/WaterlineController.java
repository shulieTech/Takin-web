package io.shulie.takin.web.entrypoint.controller.waterline;

import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.response.waterline.Metrics;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.WaterlineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

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

    /**
     * 获取表格指标数据:需要当前节点的名称+上级名称
     * @param applicationName
     * @param startTime
     * @return
     * @throws ParseException
     * 如果applicationName没有值，就代表要查询所有的应用，否则查询applicationName
     */
    @ApiOperation("指标数据")
    @RequestMapping("/getAllApplicationWithMetrics")
    public ResponseResult<List<Metrics>> getAllApplicationWithMetrics(@RequestParam(name = "applicationName") String applicationName, @RequestParam(name = "startTime") String startTime,@RequestParam(name = "sceneId") Long sceneId) throws ParseException {
        List<String> names = null;
        if (StringUtils.isNotBlank(applicationName)) {
            names = Arrays.asList(applicationName);
        } else {
            ResponseResult<List<String>> result = this.getAllApplicationsWithSceneId(sceneId);
            if (CollectionUtils.isNotEmpty(result.getData())) {
                names = result.getData();
            }
        }
        List<Metrics> metrics = waterlineService.getAllApplicationWithMetrics(names, startTime);//metrics
        waterlineService.getApplicationNodesAmount(metrics);//node amount
        waterlineService.getApplicationTags(metrics);//application tags
        return ResponseResult.success(metrics);
    }

    /**
     * 获取压测场景下所有的应用
     */
    @ApiOperation("获取压测场景下所有的应用")
    @RequestMapping("/getAllApplicationsWithSceneId")
    public ResponseResult<List<String>> getAllApplicationsWithSceneId(@RequestParam(name = "sceneId") Long sceneId){
        List<String> ids = waterlineService.getAllApplicationsWithSceneId(sceneId);
        List<String> names = null;
        if (CollectionUtils.isNotEmpty(ids)) {
            names = waterlineService.getApplicationNamesWithIds(ids);
        }
        return ResponseResult.success(names);
    }
}
