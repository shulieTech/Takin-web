package io.shulie.takin.web.entrypoint.controller.scriptmanage;

import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.PageScriptDebugRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.PageScriptDebugRequestRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptDebugDoDebugRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptDebugStopRequest;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.*;
import io.shulie.takin.web.biz.service.datamanage.CsvManageService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptDebugService;
import io.shulie.takin.web.biz.utils.DataUtil;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.util.DataTransformUtil;
import io.shulie.takin.web.data.model.mysql.ScriptCsvDataSetEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @Package io.shulie.takin.web.entrypoint.controller.scriptmanage
* @ClassName: ScriptDataController
* @author hezhongqi
* @description:
* @date 2023/9/20 17:08
*/
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL + "script/data")
@Api(tags = "接口: 脚本数据")
public class ScriptDataController {

    @Autowired
    private CsvManageService csvManageService;


    @ApiOperation("csv列表")
    @GetMapping("csv/list")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_CSV_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public List<ScriptCsvDataSetResponse> listByBusinessFlowId(@RequestParam("businessFlowId") Long businessFlowId) {
        List<ScriptCsvDataSetEntity> csvDataSetEntityList = csvManageService.listByBusinessFlowId(businessFlowId);
        return DataTransformUtil.list2list(csvDataSetEntityList,ScriptCsvDataSetResponse.class);
    }

    @ApiOperation("csv拆分")
    @GetMapping("csv/split")
    public ScriptManageStringResponse spilt(@RequestParam("businessFlowId") Long businessFlowId) {
        // todo 操作日志记录
        return new ScriptManageStringResponse("设置成功");
    }

    @ApiOperation("csv是否按分区排序")
    @GetMapping("csv/sort")
    public ScriptManageStringResponse sort(@RequestParam("businessFlowId") Long businessFlowId) {
        // todo 操作日志记录
        return new ScriptManageStringResponse("设置成功");
    }

}
