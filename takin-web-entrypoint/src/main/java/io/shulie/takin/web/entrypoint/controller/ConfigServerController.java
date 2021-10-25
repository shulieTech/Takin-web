package io.shulie.takin.web.entrypoint.controller;

import io.shulie.takin.web.biz.pojo.request.config.UpdateConfigServerRequest;
import io.shulie.takin.web.biz.service.config.ConfigServerService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 程序配置表控制层
 *
 * @author liuchuan
 * @since 2021-06-03 13:40:57
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL + "configServer")
@Api(tags = "接口: 服务配置")
public class ConfigServerController {

    @Autowired
    private ConfigServerService configServerService;

    @ApiOperation("|_ 修改")
    @PutMapping
    public void update(@Validated @RequestBody UpdateConfigServerRequest updateRequest) {
        configServerService.update(updateRequest);
    }

}
