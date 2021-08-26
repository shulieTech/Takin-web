package io.shulie.takin.web.entrypoint.controller.opsscript;

import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.web.common.constant.APIUrls;
import io.shulie.takin.web.biz.constant.BizOpConstants;

/**
 * 运维脚本文件(OpsScriptFile)表控制层
 *
 * @author caijy
 * @since 2021-06-16 10:47:11
 */
@RestController
@RequestMapping(APIUrls.TAKIN_API_URL + "opsScriptFile")
@Api(tags = "")
public class OpsScriptFileController {

    @ApiOperation("")
    @GetMapping
    @ModuleDef(
            moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
            subModuleName = BizOpConstants.SubModules.SHADOW_DATABASE_TABLE,
            logMsgKey = BizOpConstants.Message.MESSAGE_SHADOW_DATABASE_TABLE_CREATE
    )
    public void index() {

    }

}
