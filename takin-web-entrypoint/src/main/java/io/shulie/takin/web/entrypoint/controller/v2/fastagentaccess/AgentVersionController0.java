package io.shulie.takin.web.entrypoint.controller.v2.fastagentaccess;

import io.shulie.takin.cloud.common.constants.APIUrls;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentInfoListQueryRequest;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentInfoListResponse;
import io.shulie.takin.web.biz.service.fastagentaccess.AgentVersionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * agent版本管理(AgentVersion)controller
 * V2
 *
 * @author 南风
 * @date 2021-11-12 5:32 下午
 */
@RestController
@RequestMapping(APIUrls.TRO_API_URL + "v2/fast/agent/access")
@Api(tags = "接口-v2：agent版本管理")
public class AgentVersionController0 {


    @Autowired
    private AgentVersionService agentVersionService;


    @ApiOperation("获取探针列表数据")
    @GetMapping("/list")
    public PagingList<AgentInfoListResponse> agentBaseList(AgentInfoListQueryRequest queryRequest) {
        return agentVersionService.getList(queryRequest);
    }

//    @ApiOperation("获取应用接入状态枚举")
//    @GetMapping("/status")
//    public List<SelectVO> status() {
//        return agentVersionService.getAccessStatusList();
//    }
//




}
