package io.shulie.takin.web.entrypoint.controller.application;

import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsEnableInputV2;
import io.shulie.takin.web.biz.pojo.input.application.TenantDataSignInput;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.data.dao.application.TenantDataSignConfigDAO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: 南风
 * @Date: 2022/5/23 5:33 下午
 */
@Slf4j
@RestController("data.sign")
@RequestMapping(ApiUrls.TAKIN_API_URL)
public class TenantDataSignController {

    @Autowired
    private TenantDataSignConfigDAO dataSignConfigDAO;

    @ApiOperation("查看对应租户的数据隔离配置")
    @GetMapping("tenant/data/sign")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.QUERY
    )
    public Integer query(@RequestParam(value = "tenantId") Long tenantId) {
        return dataSignConfigDAO.getStatus(tenantId);
    }

    @ApiOperation("修改数据隔离配置")
    @PutMapping("tenant/data/sign")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.UPDATE
    )
    public void update(@Validated @RequestBody TenantDataSignInput input) {
         dataSignConfigDAO.updateStatus(input.getStatus(),input.getTenantId());
    }
}
