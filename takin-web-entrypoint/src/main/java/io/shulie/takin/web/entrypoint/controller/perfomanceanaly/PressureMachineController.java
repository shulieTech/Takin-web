package io.shulie.takin.web.entrypoint.controller.perfomanceanaly;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.input.PressureMachineInput;
import io.shulie.takin.web.biz.service.perfomanceanaly.PressureMachineLogService;
import io.shulie.takin.web.biz.service.perfomanceanaly.PressureMachineService;
import io.shulie.takin.web.biz.pojo.request.perfomanceanaly.PressureMachineDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.perfomanceanaly.PressureMachineInsertRequest;
import io.shulie.takin.web.biz.pojo.request.perfomanceanaly.PressureMachineLogQueryRequest;
import io.shulie.takin.web.biz.pojo.request.perfomanceanaly.PressureMachineUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.PressureMachineLogResponse;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.PressureMachineResponse;
import io.shulie.takin.web.data.param.machine.PressureMachineQueryParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author mubai
 * @date 2020-11-13 09:17
 */

@RestController
@RequestMapping(value = "/api/pressure/machine")
@Api(tags = "压力机接口")
public class PressureMachineController {

    @Autowired
    private PressureMachineService pressureMachineService;

    @Autowired
    private PressureMachineLogService machineLogService;

    @PostMapping("/upload")
    @ApiOperation(value = "上报压力机信息")
    public void uploadMachineInfo(@RequestBody PressureMachineInsertRequest request) {
        PressureMachineInput input = new PressureMachineInput();
        BeanUtils.copyProperties(request, input);
        pressureMachineService.upload(input);
    }

    @GetMapping(value = "/log/info")
    @ApiOperation(value = "压力机日志趋势图")
    public PressureMachineLogResponse getPressureMachineLogChart(
        @RequestParam(value = "id", required = true) Long id,
        @RequestParam(value = "queryTime", required = true) String queryTime) {
        PressureMachineLogQueryRequest request = new PressureMachineLogQueryRequest();
        request.setMachineId(id);
        request.setQueryTime(queryTime);
        return machineLogService.queryByExample(request);
    }

    @PutMapping
    @ApiOperation(value = "修改压力机")
    public void update(@RequestBody PressureMachineUpdateRequest request) {
        pressureMachineService.update(request);
    }

    @DeleteMapping
    @ApiOperation(value = "删除压力机")
    public void delete(@RequestBody PressureMachineDeleteRequest request) {
        pressureMachineService.delete(request);
    }

    @GetMapping(value = "/list")
    @ApiOperation(value = "压力机列表")
    public PagingList<PressureMachineResponse> getPressureMachineList(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "ip", required = false) String ip,
        @RequestParam(value = "flag", required = false) String flag,
        @RequestParam(value = "status", required = false) Integer status,
        @RequestParam(value = "order", required = false) Integer order,
        @RequestParam(value = "current", required = false, defaultValue = "0") Integer current,
        @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {
        PressureMachineQueryParam request = new PressureMachineQueryParam();
        request.setName(name);
        request.setIp(ip);
        request.setFlag(flag);
        request.setStatus(status);
        request.setMachineUsageOrder(order);
        request.setCurrent(current + 1);
        request.setPageSize(pageSize);
        return pressureMachineService.queryByExample(request);

    }

}
