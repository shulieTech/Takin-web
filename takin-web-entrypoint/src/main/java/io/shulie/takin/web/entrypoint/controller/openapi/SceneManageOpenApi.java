package io.shulie.takin.web.entrypoint.controller.openapi;

import java.util.List;

import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageQueryVO;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageListResp;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.biz.pojo.openapi.response.scenemanage.SceneManageListOpenApiResp;
import io.shulie.takin.web.biz.pojo.openapi.response.scenemanage.SceneManageOpenApiResp;
import io.shulie.takin.web.common.domain.WebResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qianshui
 * @date 2020/4/17 下午2:31
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_OPEN_API_URL + "/scenemanage")
@Api(tags = "压测场景管理")
public class SceneManageOpenApi {

    @Autowired
    private SceneManageService sceneManageService;

    @GetMapping("/detail")
    @ApiOperation(value = "压测场景详情")
    public Response<SceneManageOpenApiResp> getDetail(@ApiParam(name = "id", value = "ID", required = true) Long id) {
        ResponseResult<SceneManageWrapperResp> responseResult = sceneManageService.detailScene(id);
        return Response.success(ofSceneManageOpenApiResp(responseResult.getData()));
    }

    @GetMapping("/list")
    @ApiOperation(value = "压测场景列表")
    public Response<List<SceneManageListOpenApiResp>> getList(@ApiParam(name = "current", value = "页码", required = true) Integer current,
        @ApiParam(name = "pageSize", value = "页大小", required = true) Integer pageSize,
        @ApiParam(name = "customName", value = "客户名称") String customerName,
        @ApiParam(name = "customId", value = "客户ID") Long customerId,
        @ApiParam(name = "sceneId", value = "压测场景ID") Long sceneId,
        @ApiParam(name = "sceneName", value = "压测场景名称") String sceneName,
        @ApiParam(name = "status", value = "压测状态") Integer status) {
        SceneManageQueryVO queryVO = new SceneManageQueryVO();
        /*
        TODO 具体实现
        queryVO.setCurrent(current);
        queryVO.setCurrentPage(current);
        queryVO.setPageSize(pageSize);
        queryVO.setCustomerName(customerName);
        queryVO.setCustomerId(customerId);
        */
        queryVO.setSceneId(sceneId);
        queryVO.setSceneName(sceneName);
        queryVO.setStatus(status);
        WebResponse<List<SceneManageListResp>> pageList = sceneManageService.getPageList(queryVO);
        return Response.success(ofListSceneManageListOpenApiResp(pageList.getData()));
    }

    private SceneManageOpenApiResp ofSceneManageOpenApiResp(Object data) {
        if (data == null) {
            return null;
        }
        String s = JsonHelper.bean2Json(data);
        return JsonHelper.json2Bean(s, SceneManageOpenApiResp.class);
    }

    private List<SceneManageListOpenApiResp> ofListSceneManageListOpenApiResp(Object data) {
        if (data == null) {
            return null;
        }
        String s = JsonHelper.bean2Json(data);
        return JsonHelper.json2List(s, SceneManageListOpenApiResp.class);
    }
}
