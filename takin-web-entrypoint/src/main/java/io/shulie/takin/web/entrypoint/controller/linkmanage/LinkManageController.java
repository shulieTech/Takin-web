package io.shulie.takin.web.entrypoint.controller.linkmanage;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import com.pamirs.takin.entity.domain.dto.linkmanage.BusinessActiveIdAndNameDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.BusinessFlowDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.BusinessFlowIdAndNameDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.MiddleWareNameDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.SceneDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.SystemProcessIdAndNameDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.linkstatistics.LinkHistoryInfoDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.linkstatistics.LinkRemarkDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.linkstatistics.LinkRemarkmiddleWareDto;
import com.pamirs.takin.entity.domain.entity.linkmanage.MiddleWareDistinctVo;
import com.pamirs.takin.entity.domain.entity.linkmanage.statistics.StatisticsQueryVo;
import com.pamirs.takin.entity.domain.vo.linkmanage.BusinessFlowVo;
import com.pamirs.takin.entity.domain.vo.linkmanage.MiddleWareEntity;
import com.pamirs.takin.entity.domain.vo.linkmanage.queryparam.SceneQueryVo;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageDeleteReq;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.constant.BizOpConstants.Vars;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessActivityNameResponse;
import io.shulie.takin.web.biz.service.linkmanage.LinkManageService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * ????????????
 *
 * @author vernon
 * @date 2019/11/29 14:05
 * @see io.shulie.takin.web.entrypoint.controller.activity.ActivityController
 * @see io.shulie.takin.web.entrypoint.controller.application.ApplicationEntranceController
 */
@Slf4j
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL)
@Api(tags = "linkmanage", value = "????????????")
@Deprecated
public class LinkManageController {

    @Resource
    private LinkManageService linkManageService;

    @RequestMapping(value = "/link/scene/manage", method = RequestMethod.DELETE)
    @ApiOperation("??????????????????")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.LINK_CARDING,
        subModuleName = BizOpConstants.SubModules.BUSINESS_PROCESS,
        logMsgKey = BizOpConstants.Message.MESSAGE_BUSINESS_PROCESS_DELETE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
        needAuth = ActionTypeEnum.DELETE
    )
    public Response<String> deleteScene(@RequestBody SceneManageDeleteReq req) {
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.DELETE);
        BusinessFlowDto dto = linkManageService.getBusinessFlowDetail(req.getId());
        if (null == dto) {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_VALIDATE_ERROR, "????????????????????????");
        }
        OperationLogContextHolder.addVars(Vars.BUSINESS_FLOW_ID, dto.getId());
        return Response.success(linkManageService.deleteScene(req.getId().toString()));
    }

    @GetMapping("/link/scene/manage")
    @ApiOperation("????????????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response<List<SceneDto>> getScenes
        (
            @ApiParam(name = "sceneId", value = "??????id") Long sceneId,
            @ApiParam(name = "sceneName", value = "??????????????????") String sceneName,
            @ApiParam(name = "entrance", value = "??????") String entrance,
            @ApiParam(name = "ischange", value = "????????????") String ischange,
            @ApiParam(name = "businessName", value = "???????????????") String businessName,
            @ApiParam(name = "middleWareType", value = "???????????????") String middleWareType,
            @ApiParam(name = "middleWareName", value = "???????????????") String middleWareName,
            @ApiParam(name = "middleWareVersion", value = "???????????????") String middleWareVersion,
            @ApiParam(name = "linkLevel", value = "??????????????????") String linkLevel,
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
        vo.setLinkLevel(linkLevel);
        vo.setCurrentPage(current);
        vo.setPageSize(pageSize);
        return linkManageService.getScenes(vo);
    }

    @GetMapping("/link/statistic")
    @ApiOperation("????????????")
    public Response<LinkRemarkDto> getstatisticsInfo() {
        LinkRemarkDto dto = linkManageService.getstatisticsInfo();
        return Response.success(dto);
    }

    @GetMapping("/link/statistic/middleware")
    @ApiOperation("?????????????????????????????????")
    public Response<List<LinkRemarkmiddleWareDto>> getMiddleWareInfo(StatisticsQueryVo vo) {
        // TODO: 2019/12/10
        return linkManageService.getMiddleWareInfo(vo);
    }

    @GetMapping("/link/statistic/chart")
    @ApiOperation("?????????????????????")
    public Response<LinkHistoryInfoDto> getChart() {
        LinkHistoryInfoDto dto = linkManageService.getChart();
        return Response.success(dto);
    }

    @GetMapping("/link/linkmanage/middleware")
    @ApiOperation("???????????????????????????????????????")
    public Response<List<MiddleWareEntity>> getAllMiddleWareTypeList() {

        try {
            List<MiddleWareEntity> list = linkManageService.getAllMiddleWareTypeList();
            return Response.success(list);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.LINK_QUERY_ERROR, e.getMessage());
        }
    }

    /**
     * ????????????????????????????????????id
     *
     * @return -
     */
    @GetMapping("/link/tech/linkmanage/all")
    @ApiOperation("?????????????????????id???????????????,???????????????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SYSTEM_PROCESS,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response<List<SystemProcessIdAndNameDto>> systemProcessFuzzSearch(
        @ApiParam(name = "systemProcessName", value = "??????????????????") String systemProcessName) {
        try {
            List<SystemProcessIdAndNameDto> dto = linkManageService.getAllSystemProcess(systemProcessName);
            return Response.success(dto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.LINK_QUERY_ERROR, e.getMessage());
        }
    }

    /**
     * ????????????????????????????????????id
     *
     * @return -
     */
    @GetMapping("/link/tech/linkmanage/canRelate/all")
    @ApiOperation("?????????????????????id???????????????,???????????????????????????")
    public Response<List<SystemProcessIdAndNameDto>> systemProcessFuzzSearch2(
        @ApiParam(name = "systemProcessName", value = "??????????????????") String systemProcessName) {
        try {
            List<SystemProcessIdAndNameDto> dto = linkManageService.getAllSystemProcessCanrelateBusiness(
                systemProcessName);

            return Response.success(dto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.LINK_QUERY_ERROR, e.getMessage());
        }
    }

    @GetMapping("/link/entrance")
    @ApiOperation("?????????????????????")
    public Response<List<String>> entranceFuzzSerach(@ApiParam(name = "entrance", value = "?????????") String entrance) {
        try {
            List<String> result = linkManageService.entranceFuzzSerach(entrance);
            return Response.success(result);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.LINK_QUERY_ERROR, e.getMessage());
        }
    }

    @GetMapping("/link/bussinessActive")
    @ApiOperation("????????????????????????????????????id")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_ACTIVITY,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response<List<BusinessActiveIdAndNameDto>> bussinessActiveNameFuzzSearch(
        @ApiParam(name = "bussinessActiveName", value = "??????????????????") String bussinessActiveName) {

        try {
            List<BusinessActiveIdAndNameDto> result
                = linkManageService.businessActiveNameFuzzSearch(bussinessActiveName);
            return Response.success(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.LINK_QUERY_ERROR, e.getMessage());
        }
    }

    @GetMapping("/link/business/manage/getBusinessActiveByFlowId")
    @ApiOperation("????????????id????????????????????????????????????id")
    public List<BusinessActivityNameResponse> getBusinessActiveByFlowId(Long businessFlowId) {
        return linkManageService.getBusinessActiveByFlowId(businessFlowId);
    }

    @GetMapping("/link/businessFlow")
    @ApiOperation("????????????????????????????????????id")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response<List<BusinessFlowIdAndNameDto>> businessFlowIdFuzzSearch(
        @ApiParam(name = "businessFlowName", value = "??????????????????") String businessFlowName
    ) {
        try {
            List<BusinessFlowIdAndNameDto> result =
                linkManageService.businessFlowIdFuzzSearch(businessFlowName);
            return Response.success(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.LINK_QUERY_ERROR, e.getMessage());
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @return -
     */
    @PostMapping("/link/scene/middlewares")
    @ApiOperation("????????????????????????????????????")
    public Response<List<MiddleWareEntity>> businessProcessMiddleWares(
        @ApiParam(name = "vo", value = "????????????????????????")
        @RequestBody MiddleWareDistinctVo vo) {
        try {
            List<String> ids = vo.getIds();
            List<MiddleWareEntity> lists = linkManageService.businessProcessMiddleWares(ids);
            return Response.success(lists);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.LINK_QUERY_ERROR, e.getMessage());
        }
    }

    @PostMapping("/link/scene/tree/add")
    @ApiOperation("?????????????????????")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.LINK_CARDING,
        subModuleName = BizOpConstants.SubModules.BUSINESS_PROCESS,
        logMsgKey = BizOpConstants.Message.MESSAGE_BUSINESS_PROCESS_CREATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
        needAuth = ActionTypeEnum.CREATE
    )
    public Response<?> addBusinessFlow(@RequestBody BusinessFlowVo vo) {
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.CREATE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.BUSINESS_PROCESS, vo.getSceneName());
        try {
            linkManageService.addBusinessFlow(vo);
            return Response.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.LINK_ADD_ERROR, e.getMessage());
        }

    }

    @GetMapping("/link/scene/tree/detail")
    @ApiOperation("???????????????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
        needAuth = ActionTypeEnum.QUERY
    )
    @Deprecated
    public Response<?> getBusinessFlowDetail(@NotNull Long id) {
        try {
            BusinessFlowDto dto = linkManageService.getBusinessFlowDetail(id);
            return Response.success(dto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.LINK_QUERY_ERROR, e.getMessage());
        }
    }

    @PostMapping("/link/scene/tree/modify")
    @ApiOperation("??????????????????")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.LINK_CARDING,
        subModuleName = BizOpConstants.SubModules.BUSINESS_PROCESS,
        logMsgKey = BizOpConstants.Message.MESSAGE_BUSINESS_PROCESS_UPDATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
        needAuth = ActionTypeEnum.UPDATE
    )
    public Response<?> modifyBusinessFlow(@RequestBody BusinessFlowVo vo) {
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.UPDATE);
        try {
            linkManageService.modifyBusinessFlow(vo);
            return Response.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.LINK_UPDATE_ERROR, e.getMessage());
        }
    }

    @GetMapping("/link/midlleWare/cascade")
    @ApiOperation("???????????????????????????")
    public Response<List<MiddleWareNameDto>> cascadeMiddleWareNameAndVersion(@ApiParam(name = "middleWareType"
        , value = "???????????????") String middleWareType) {

        try {
            List<MiddleWareNameDto> dtos = linkManageService.cascadeMiddleWareNameAndVersion(middleWareType);
            return Response.success(dtos);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.LINK_QUERY_ERROR, e.getMessage());
        }
    }

    @GetMapping("/link/middleWare/name")
    @ApiOperation("?????????????????????")
    public Response<List<MiddleWareNameDto>> getDistinctMiddleWareName() {
        try {
            List<MiddleWareNameDto> dtos = linkManageService.getDistinctMiddleWareName();
            return Response.success(dtos);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.LINK_QUERY_ERROR, e.getMessage());
        }
    }

}
