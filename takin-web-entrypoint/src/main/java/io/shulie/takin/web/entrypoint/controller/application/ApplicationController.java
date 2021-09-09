package io.shulie.takin.web.entrypoint.controller.application;

import java.util.List;
import java.util.stream.Collectors;

import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.web.biz.constant.BizOpConstants.ModuleCode;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.web.common.vo.WebOptionEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shiyajian
 * create: 2020-12-29
 */
@RestController
@RequestMapping("/api/application")
@Api(tags = "应用管理", value = "应用管理")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    /**
     * 获得所有应用的名字
     */
    @GetMapping("/names")
    @ApiOperation("获得所有的应用名称")
    @AuthVerification(
        moduleCode = ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public List<WebOptionEntity> getApplicationName() {
        return applicationService.getApplicationName().stream()
            .map(item -> {
                WebOptionEntity webOptionEntity = new WebOptionEntity();
                webOptionEntity.setLabel(item);
                webOptionEntity.setValue(item);
                return webOptionEntity;
            }).collect(Collectors.toList());
    }
}
