//package io.shulie.takin.web.entrypoint.controller.middleware;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import io.shulie.takin.common.beans.annotation.ModuleDef;
//import io.shulie.takin.common.beans.page.PagingList;
//import io.shulie.takin.common.beans.annotation.AuthVerification;
//import io.shulie.takin.web.common.constant.APIUrls;
//import io.shulie.takin.web.biz.constant.BizOpConstants;
//import io.shulie.takin.web.biz.constant.BizOpConstants.Message;
//import io.shulie.takin.web.biz.constant.BizOpConstants.ModuleCode;
//import io.shulie.takin.web.biz.constant.BizOpConstants.Modules;
//import io.shulie.takin.web.biz.constant.BizOpConstants.SubModules;
//import io.shulie.takin.web.biz.constant.BizOpConstants.Vars;
//import io.shulie.takin.web.common.context.OperationLogContextHolder;
//import io.shulie.takin.web.biz.pojo.input.fastdebug.FastDebugConfigCreateInput;
//import io.shulie.takin.web.biz.pojo.input.middleware.AppMiddlewareScanSearchInput;
//import io.shulie.takin.web.biz.pojo.output.middleware.AppMiddlewareScanOutput;
//import io.shulie.takin.web.app.request.fastdebug.FastDebugConfigCreateRequest;
//import io.shulie.takin.web.biz.pojo.request.middleware.AppMiddlewareScanSearchRequest;
//import io.shulie.takin.web.app.response.fastdebug.FastDebugConfigStringResponse;
//import io.shulie.takin.web.biz.pojo.response.middleware.AppMiddlewareScanResponse;
//import io.shulie.takin.web.app.service.middleware.AppMiddlewareScanService;
//import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @author 无涯
//// * @date 2021/2/24 4:59 下午
// */
//@RestController
//@RequestMapping(APIUrls.takin_API_URL + "middleware")
//@Api("中间件扫描")
//public class AppMiddlewareScanController {
//
//    @Autowired
//    private AppMiddlewareScanService appMiddlewareScanService;
//
//    /**
//     * 默认展示未支持、未录入的中间件，未支持的靠前
//     * @param request
//     * @return
//     */
//    @PostMapping("/scan/list")
//    @ApiOperation("中间件扫描结果列表")
//    @AuthVerification(
//        moduleCode = ModuleCode.MIDDLEWARE_SCAN_MANAGE,
//        needAuth = ActionTypeEnum.QUERY
//    )
//    public PagingList<AppMiddlewareScanResponse> getList(@RequestBody AppMiddlewareScanSearchRequest request) {
//        AppMiddlewareScanSearchInput input = new AppMiddlewareScanSearchInput();
//        BeanUtils.copyProperties(request, input);
//        PagingList<AppMiddlewareScanOutput> pagingList = appMiddlewareScanService.getPageList(input);
//        List<AppMiddlewareScanResponse> responses = pagingList.getList().stream().map(output -> {
//            AppMiddlewareScanResponse response = new AppMiddlewareScanResponse();
//            BeanUtils.copyProperties(output,response);
//            return response;
//        }).collect(Collectors.toList());
//        return PagingList.of(responses,pagingList.getTotal());
//    }
//}
