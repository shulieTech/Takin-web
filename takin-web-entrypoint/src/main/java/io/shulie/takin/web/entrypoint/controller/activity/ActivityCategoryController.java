package io.shulie.takin.web.entrypoint.controller.activity;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.constant.BizOpConstants.Message;
import io.shulie.takin.web.biz.constant.BizOpConstants.OpTypes;
import io.shulie.takin.web.biz.constant.BizOpConstants.Vars;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityCategoryCreateRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityCategoryDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityCategoryMoveRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityCategoryUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityCategoryTreeResponse;
import io.shulie.takin.web.biz.service.activity.ActivityCategoryService;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "业务活动分类管理", value = "业务活动分类管理")
@RestController
@RequestMapping("api/businessActivity/category")
public class ActivityCategoryController {

    @Resource
    private ActivityCategoryService activityCategoryService;

    @ApiOperation("分类列表")
    @GetMapping("list")
    public ResponseResult<List<ActivityCategoryTreeResponse>> list() {
        return ResponseResult.success(Collections.singletonList(activityCategoryService.list()));
    }

    @ApiOperation("新增分类")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "业务活动分类父级id", value = "parentId", required = true,
            dataType = "long", paramType = "body"),
        @ApiImplicitParam(name = "业务活动分类名称", value = "title", required = true,
            dataType = "string", paramType = "body")
    })
    @ModuleDef(
        moduleName = BizOpConstants.Modules.LINK_CARDING,
        subModuleName = BizOpConstants.SubModules.BUSINESS_ACTIVITY,
        logMsgKey = Message.MESSAGE_BUSINESS_ACTIVITY_CATEGORY_CREATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_ACTIVITY,
        needAuth = ActionTypeEnum.CREATE
    )
    @PostMapping("add")
    public ResponseResult<String> addCategory(@Valid @RequestBody ActivityCategoryCreateRequest createRequest) {
        Long id = activityCategoryService.addCategory(createRequest);
        OperationLogContextHolder.operationType(OpTypes.CREATE);
        OperationLogContextHolder.addVars(Vars.CATEGORY_ID, String.valueOf(id));
        OperationLogContextHolder.addVars(Vars.CATEGORY_PARENT_ID, String.valueOf(createRequest.getParentId()));
        OperationLogContextHolder.addVars(Vars.CATEGORY_NAME, createRequest.getTitle());
        return ResponseResult.success("新增成功");
    }

    @ApiOperation("修改分类")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "业务活动分类id", value = "id", required = true,
            dataType = "long", paramType = "body"),
        @ApiImplicitParam(name = "业务活动分类名称", value = "title", required = true,
            dataType = "string", paramType = "body")
    })
    @ModuleDef(
        moduleName = BizOpConstants.Modules.LINK_CARDING,
        subModuleName = BizOpConstants.SubModules.BUSINESS_ACTIVITY,
        logMsgKey = Message.MESSAGE_BUSINESS_ACTIVITY_CATEGORY_UPDATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_ACTIVITY,
        needAuth = ActionTypeEnum.UPDATE
    )
    @PutMapping("update")
    public ResponseResult<String> updateCategory(@Valid @RequestBody ActivityCategoryUpdateRequest updateRequest) {
        activityCategoryService.updateCategory(updateRequest);
        OperationLogContextHolder.operationType(OpTypes.UPDATE);
        OperationLogContextHolder.addVars(Vars.CATEGORY_ID, String.valueOf(updateRequest.getTitle()));
        OperationLogContextHolder.addVars(Vars.CATEGORY_NAME, updateRequest.getTitle());
        return ResponseResult.success("修改成功");
    }

    @ApiOperation("删除分类")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "业务活动分类id", value = "id", required = true,
            dataType = "long", paramType = "body")
    })
    @ModuleDef(
        moduleName = BizOpConstants.Modules.LINK_CARDING,
        subModuleName = BizOpConstants.SubModules.BUSINESS_ACTIVITY,
        logMsgKey = Message.MESSAGE_BUSINESS_ACTIVITY_CATEGORY_DELETE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_ACTIVITY,
        needAuth = ActionTypeEnum.DELETE
    )
    @DeleteMapping("delete")
    public ResponseResult<String> deleteCategory(@Valid @RequestBody ActivityCategoryDeleteRequest deleteRequest) {
        Long id = deleteRequest.getId();
        activityCategoryService.deleteCategory(id);
        OperationLogContextHolder.operationType(OpTypes.DELETE);
        OperationLogContextHolder.addVars(Vars.CATEGORY_ID, String.valueOf(id));
        return ResponseResult.success("删除成功");
    }

    @ApiOperation("移动分类")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "业务活动分类id", value = "from", required = true,
            dataType = "long", paramType = "body"),
        @ApiImplicitParam(name = "业务活动分类id", value = "to", required = true,
            dataType = "long", paramType = "body")
    })
    @PutMapping("move")
    public ResponseResult<String> move(@Valid @RequestBody ActivityCategoryMoveRequest moveRequest) {
        activityCategoryService.move(moveRequest.getFrom(), moveRequest.getTo());
        return ResponseResult.success("移动成功");
    }
}
