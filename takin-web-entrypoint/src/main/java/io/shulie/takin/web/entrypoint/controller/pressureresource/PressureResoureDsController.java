package io.shulie.takin.web.entrypoint.controller.pressureresource;

import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.pressureresource.*;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceCommonService;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceDsService;
import io.shulie.takin.web.biz.service.pressureresource.common.ModuleEnum;
import io.shulie.takin.web.biz.service.pressureresource.common.SourceTypeEnum;
import io.shulie.takin.web.biz.service.pressureresource.vo.CommandTaskVo;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 链路资源配置-数据源隔离
 *
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:51 PM
 */
@RestController
@RequestMapping(value = ApiUrls.TAKIN_API_URL + "/pressureResource/ds")
@Api(tags = "接口: 数据源隔离")
@Slf4j
public class PressureResoureDsController {
    private static Logger logger = LoggerFactory.getLogger(PressureResoureDsController.class);

    @Resource
    private PressureResourceDsService pressureResourceDsService;

    @Resource
    private PressureResourceCommonService pressureResourceCommonService;

    @ApiOperation("链路压测资源-数据源-新增")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseResult create(@RequestBody PressureResourceRelateDsInput input) {
        input.setType(SourceTypeEnum.MANUAL.getCode());
        pressureResourceDsService.add(input);

        CommandTaskVo taskVo = new CommandTaskVo();
        taskVo.setResourceId(input.getResourceId());
        taskVo.setModule(ModuleEnum.DS.getCode());
        pressureResourceCommonService.pushRedisCommand(taskVo);
        return ResponseResult.success();
    }

    @ApiOperation("链路压测资源-数据源视图")
    @RequestMapping(value = "/listByDs", method = RequestMethod.GET)
    public ResponseResult listByDs(PressureResourceRelateDsRequest request) {
        return ResponseResult.success(pressureResourceDsService.listByDs(request));
    }

    @ApiOperation("链路压测资源-数据源-删除")
    @RequestMapping(value = "/del", method = RequestMethod.GET)
    public ResponseResult del(PressureResourceRelateDsInput input) {
        pressureResourceDsService.del(input.getId());
        return ResponseResult.success();
    }

    @ApiOperation("链路压测资源-应用视图")
    @RequestMapping(value = "/listByApp", method = RequestMethod.GET)
    public ResponseResult listByApp(PressureResourceRelateDsRequest request) {
        return ResponseResult.success(pressureResourceDsService.listByApp(request));
    }

    @ApiOperation("链路压测资源-导入影子资源")
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public ResponseResult importDsConfig(@RequestParam MultipartFile file, @RequestParam Long resourceId) {
        pressureResourceDsService.importDsConfig(file, resourceId);

        CommandTaskVo taskVo = new CommandTaskVo();
        taskVo.setResourceId(resourceId);
        taskVo.setModule(ModuleEnum.DS.getCode());
        pressureResourceCommonService.pushRedisCommand(taskVo);
        return ResponseResult.success();
    }

    @ApiOperation("链路压测资源-导出影子资源")
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public ResponseResult exportDsConfig(HttpServletResponse response, Long resourceId) {
        pressureResourceDsService.export(response, resourceId);
        return ResponseResult.success();
    }
}
