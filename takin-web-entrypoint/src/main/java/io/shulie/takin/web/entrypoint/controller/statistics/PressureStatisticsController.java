package io.shulie.takin.web.entrypoint.controller.statistics;

import java.util.List;

import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.convert.statistics.StatisticsConvert;
import io.shulie.takin.web.biz.pojo.input.statistics.PressureTotalInput;
import io.shulie.takin.web.biz.pojo.output.statistics.PressureListTotalOutput;
import io.shulie.takin.web.biz.pojo.output.statistics.PressurePieTotalOutput;
import io.shulie.takin.web.biz.pojo.output.statistics.ReportTotalOutput;
import io.shulie.takin.web.biz.pojo.output.statistics.ScriptLabelListTotalOutput;
import io.shulie.takin.web.biz.service.statistics.PressureStatisticsService;
import io.shulie.takin.web.common.constant.APIUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 无涯
 * @Package io.shulie.takin.web.app.controller.statistic
 * @date 2020/11/30 7:07 下午
 */
@RestController
@RequestMapping(APIUrls.TAKIN_API_URL + "/statistic")
@Api(tags = "统计管理")
public class PressureStatisticsController {

    @Autowired
    private PressureStatisticsService pressureStatisticsService;

    /**
     * 统计场景分类，脚本类型，返回饼状图数据
     *
     * @return
     */
    @GetMapping("/getPressurePieTotal")
    @ApiOperation(value = "统计压测场景分类以及脚本类型")
    public ResponseResult<PressurePieTotalOutput> getPressurePieTotal(
        @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime,
        @RequestParam("type") String type) {
        PressureTotalInput input = new PressureTotalInput();
        input.setStartTime(startTime);
        input.setEndTime(endTime);
        input.setType(type);
        PressurePieTotalOutput output = pressureStatisticsService.getPressurePieTotal(input);
        return ResponseResult.success(StatisticsConvert.of(output));
    }

    /**
     * 统计报告通过/未通过
     *
     * @return
     */
    @GetMapping("/getReportTotal")
    @ApiOperation(value = "统计报告通过以及未通过")
    public ResponseResult<ReportTotalOutput> getReportTotal(@RequestParam("startTime") String startTime,
        @RequestParam("endTime") String endTime) {
        PressureTotalInput input = new PressureTotalInput();
        input.setStartTime(startTime);
        input.setEndTime(endTime);
        ReportTotalOutput output = pressureStatisticsService.getReportTotal(input);
        return ResponseResult.success(StatisticsConvert.of(output));
    }

    /**
     * 压测场景次数统计 && 压测脚本次数统计
     *
     * @return
     */
    @GetMapping("/getPressureListTotal")
    @ApiOperation(value = "统计压测场景次数以及压测脚本次数")
    public ResponseResult<List<PressureListTotalOutput>> getPressureListTotal(
        @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime,
        @RequestParam("type") String type) {
        PressureTotalInput input = new PressureTotalInput();
        input.setStartTime(startTime);
        input.setEndTime(endTime);
        input.setType(type);
        List<PressureListTotalOutput> output = pressureStatisticsService.getPressureListTotal(input);
        return ResponseResult.success(StatisticsConvert.ofList(output));
    }

    /**
     * 统计脚本标签
     *
     * @return
     */
    @GetMapping("/getScriptLabelListTotal")
    @ApiOperation(value = "统计脚本标签")
    public ResponseResult<List<ScriptLabelListTotalOutput>> getScriptLabelListTotal(
        @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime) {
        PressureTotalInput input = new PressureTotalInput();
        input.setStartTime(startTime);
        input.setEndTime(endTime);
        List<ScriptLabelListTotalOutput> output = pressureStatisticsService.getScriptLabelListTotal(input);
        return ResponseResult.success(StatisticsConvert.ofListOutput(output));
    }
}
