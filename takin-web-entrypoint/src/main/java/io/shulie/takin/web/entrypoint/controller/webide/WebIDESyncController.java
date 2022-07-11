package io.shulie.takin.web.entrypoint.controller.webide;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowPageQueryRequest;
import io.shulie.takin.web.biz.pojo.request.webide.WebIDESyncScriptRequest;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessActivityInfoResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowListWebIDEResponse;
import io.shulie.takin.web.biz.service.webide.WebIDESyncService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: 南风
 * @Date: 2022/6/17 4:20 下午
 */
@RequestMapping("/api/webide")
@Api(tags = "webIDE数据同步", value = "webIDE数据同步")
@RestController
public class WebIDESyncController {

    @Resource
    private WebIDESyncService webIDESyncService;



    @ApiOperation("同步业务流程并启动调试")
    @PostMapping("/script/businessFlow/sync")
    public void syncScriptBusinessFlow(@RequestBody WebIDESyncScriptRequest request){
        if(Objects.nonNull(request)){
            webIDESyncService.syncScript(request);
        }
    }

    @ApiOperation("获取业务流程列表")
    @GetMapping("/script/businessFlow/sceneList")
    public PagingList<BusinessFlowListWebIDEResponse> sceneList(@ApiParam("业务流程名称") String businessFlowName, Integer current, Integer pageSize){
        BusinessFlowPageQueryRequest queryRequest = new BusinessFlowPageQueryRequest();
        queryRequest.setCurrentPage(current);
        queryRequest.setPageSize(pageSize);
        queryRequest.setBusinessFlowName(businessFlowName);
        return webIDESyncService.sceneList(queryRequest);
    }

    @ApiOperation("获取业务流程下的业务活动列表")
    @GetMapping("/script/businessFlow/activityList")
    public List<BusinessActivityInfoResponse> activityList(@RequestParam("businessFlowId") Long businessFlowId){
        if(Objects.isNull(businessFlowId)){
            return new ArrayList<>();
        }
        return webIDESyncService.activityList(businessFlowId);
    }
}
