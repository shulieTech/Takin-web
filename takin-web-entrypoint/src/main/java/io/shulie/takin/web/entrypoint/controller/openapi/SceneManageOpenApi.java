package io.shulie.takin.web.entrypoint.controller.openapi;

import java.util.List;

import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageQueryVO;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.biz.pojo.input.scenemanage.SceneManageListOutput;
import io.shulie.takin.web.biz.pojo.openapi.response.scenemanage.SceneManageOpenApiResp;
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
    public Response<List<SceneManageListOutput>> getList(@ApiParam(name = "current", value = "页码", required = true) Integer current,
        @ApiParam(name = "pageSize", value = "页大小", required = true) Integer pageSize,
        @ApiParam(name = "sceneId", value = "压测场景ID") Long sceneId,
        @ApiParam(name = "sceneName", value = "压测场景名称") String sceneName,
        @ApiParam(name = "status", value = "压测状态") Integer status) {
        SceneManageQueryVO queryVO = new SceneManageQueryVO();
        queryVO.setSceneId(sceneId);
        queryVO.setSceneName(sceneName);
        queryVO.setStatus(status);
        queryVO.setPageSize(pageSize);
        queryVO.setPageNumber(current + 1);
        ResponseResult<List<SceneManageListOutput>> responseResult = sceneManageService.getPageList(queryVO);
        return Response.success(responseResult.getData(), responseResult.getTotalNum());
    }

    private SceneManageOpenApiResp ofSceneManageOpenApiResp(Object data) {
        if (data == null) {
            return null;
        }
        String s = JsonHelper.bean2Json(data);
        return JsonHelper.json2Bean(s, SceneManageOpenApiResp.class);
    }
}
