package io.shulie.takin.web.entrypoint.controller.openapi;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.pamirs.takin.entity.domain.dto.linkmanage.BusinessActiveViewListDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.BusinessFlowDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.SceneDto;
import com.pamirs.takin.entity.domain.vo.linkmanage.queryparam.BusinessQueryVo;
import com.pamirs.takin.entity.domain.vo.linkmanage.queryparam.SceneQueryVo;
import io.shulie.takin.web.biz.service.linkManage.LinkManageService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.APIUrls;
import io.shulie.takin.web.biz.pojo.openapi.converter.LinkManageOpenApiConverter;
import io.shulie.takin.web.biz.pojo.openapi.response.linkmanage.BusinessActiveViewListOpenApiResp;
import io.shulie.takin.web.biz.pojo.openapi.response.linkmanage.BusinessFlowOpenApiResp;
import io.shulie.takin.web.biz.pojo.openapi.response.linkmanage.BusinessLinkOpenApiResp;
import io.shulie.takin.web.biz.pojo.openapi.response.linkmanage.SceneOpenApiResp;
import io.shulie.takin.web.biz.pojo.openapi.response.linkmanage.SystemProcessViewListOpenApiResp;
import io.shulie.takin.web.biz.pojo.openapi.response.linkmanage.TechLinkOpenApiResp;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessLinkResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 链路管理
 *
 * @author vernon
 * @date 2019/11/29 14:05
 */
@RestController
@RequestMapping(APIUrls.TAKIN_OPEN_API_URL)
@Api(tags = "linkmanage", value = "链路标注")
public class LinkManageOpenApi {

    @Autowired
    private LinkManageService linkManageService;

    @GetMapping("/link/tech/linkManage")
    @ApiOperation("系统流程列表查询接口")
    public Response<List<SystemProcessViewListOpenApiResp>> gettechLinksViwList(
        @ApiParam(name = "linkName", value = "系统流程名字") String linkName,
        @ApiParam(name = "entrance", value = "入口") String entrance,
        @ApiParam(name = "ischange", value = "是否变更") String ischange,
        @ApiParam(name = "middleWareType", value = "中间件类型") String middleWareType,
        @ApiParam(name = "middleWareName", value = "中间件名称") String middleWareName,
        @ApiParam(name = "middleWareVersion", value = "中间件版本") String middleWareVersion,
        Integer current,
        Integer pageSize
    ) {
        return Response.success();
    }

    @GetMapping("/link/tech/linkManage/detail")
    @ApiOperation("从本地数据库查询系统流程详情查询接口")
    public Response<TechLinkOpenApiResp> fetchTechLinkDetail(
        @RequestParam("id")
        @ApiParam(name = "id", value = "系统流程主键") String id) {
        return Response.success();
    }

    @GetMapping("/link/business/manage")
    @ApiOperation("业务活动列表查询")
    public Response<List<BusinessActiveViewListOpenApiResp>> getBussisnessLinks(
        @ApiParam(name = "businessLinkName", value = "业务活动名字") String businessLinkName,
        @ApiParam(name = "entrance", value = "入口") String entrance,
        @ApiParam(name = "ischange", value = "是否变更") String ischange,
        @ApiParam(name = "domain", value = "业务域") String domain,
        @ApiParam(name = "middleWareType", value = "中间件类型") String middleWareType,
        @ApiParam(name = "middleWareName", value = "中间件名称") String middleWareName,
        @ApiParam(name = "middleWareVersion", value = "中间件版本号") String middleWareVersion,
        @ApiParam(name = "systemProcessName", value = "系统流程名字") String systemProcessName,
        Integer current,
        Integer pageSize
    ) {
        BusinessQueryVo vo = new BusinessQueryVo();
        vo.setBusinessLinkName(businessLinkName);
        vo.setEntrance(entrance);
        vo.setIschange(ischange);
        vo.setDomain(domain);
        vo.setMiddleWareType(middleWareType);
        vo.setMiddleWareName(middleWareName);
        vo.setTechLinkName(systemProcessName);
        vo.setVersion(middleWareVersion);
        vo.setCurrentPage(current);
        vo.setPageSize(pageSize);
        Response<List<BusinessActiveViewListDto>> bussisnessLinks = linkManageService.getBussisnessLinks(vo);
        return Response.success(
            LinkManageOpenApiConverter.INSTANCE.ofListBusinessActiveViewListOpenApiResp(bussisnessLinks.getData()));
    }

    @GetMapping("/link/business/manage/detail")
    @ApiOperation("业务活动详情查询")
    public Response<BusinessLinkOpenApiResp> getBussisnessLinkDetail(@ApiParam(name = "id", value = "业务活动主键")
    @RequestParam("id")
        String id) {
        try {
            BusinessLinkResponse businessLinkResponse = linkManageService.getBussisnessLinkDetail(id);
            return Response.success(
                LinkManageOpenApiConverter.INSTANCE.ofBusinessLinkOpenApiResp(businessLinkResponse));
        } catch (Exception e) {
            return Response.fail("0", e.getMessage());
        }

    }

    @GetMapping("/link/scene/manage")
    @ApiOperation("业务流程列表查询")
    public Response<List<SceneOpenApiResp>> getScenes
        (
            @ApiParam(name = "sceneId", value = "场景id") Long sceneId,
            @ApiParam(name = "sceneName", value = "业务流程名字") String sceneName,
            @ApiParam(name = "entrance", value = "入口") String entrance,
            @ApiParam(name = "ischange", value = "是否变更") String ischange,
            @ApiParam(name = "businessName", value = "业务活动名") String businessName,
            @ApiParam(name = "middleWareType", value = "中间件类型") String middleWareType,
            @ApiParam(name = "middleWareName", value = "中间件名字") String middleWareName,
            @ApiParam(name = "middleWareVersion", value = "中间件版本") String middleWareVersion,
            Integer current,
            Integer pageSize
        ) {
        SceneQueryVo vo = new SceneQueryVo();
        vo.setSceneId(sceneId);
        vo.setSceneName(sceneName);
        vo.setEntrace(entrance);
        vo.setIschanged(ischange);
        vo.setBusinessName(businessName);
        vo.setMiddleWareType(middleWareType);
        vo.setMiddleWareName(middleWareName);
        vo.setMiddleWareVersion(middleWareVersion);
        vo.setCurrentPage(current);
        vo.setPageSize(pageSize);
        Response<List<SceneDto>> scenes = linkManageService.getScenes(vo);
        return Response.success(LinkManageOpenApiConverter.INSTANCE.ofListSceneOpenApiResp(scenes.getData()));
    }

    @GetMapping("/link/scene/tree/detail")
    @ApiOperation("业务流程树详情获取")
    public Response<BusinessFlowOpenApiResp> getBusinessFlowDetail(@NotNull String id) {
        try {
            BusinessFlowDto dto = linkManageService.getBusinessFlowDetail(id);
            return Response.success(LinkManageOpenApiConverter.INSTANCE.ofBusinessFlowOpenApiResp(dto));
        } catch (Exception e) {
            return Response.fail("0", e.getMessage(), null);
        }
    }

}
