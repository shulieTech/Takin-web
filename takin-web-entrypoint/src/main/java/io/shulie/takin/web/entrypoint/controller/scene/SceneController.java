package io.shulie.takin.web.entrypoint.controller.scene;

import java.util.List;

import io.shulie.takin.web.biz.pojo.output.scene.SceneListForSelectOutput;
import io.shulie.takin.web.biz.pojo.output.scene.SceneReportListOutput;
import io.shulie.takin.web.biz.pojo.request.scene.ListSceneForSelectRequest;
import io.shulie.takin.web.biz.pojo.request.scene.ListSceneReportRequest;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.common.constant.APIUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 场景(Probe)表控制层
 *
 * @author liuchuan
 * @since 2021-06-03 13:40:57
 */
@RestController("v1.scene")
@RequestMapping(APIUrls.TAKIN_API_URL + "scene/")
@Api(tags = "接口: 场景")
public class SceneController {

    @Autowired
    private SceneManageService sceneManageService;

    @ApiOperation("|_ 列表")
    @GetMapping("list")
    public List<SceneListForSelectOutput> index(@Validated ListSceneForSelectRequest request) {
        return sceneManageService.listForSelect(request);
    }

    @ApiOperation("|_ 报告列表")
    @GetMapping("report/list")
    public List<SceneReportListOutput> reportList(@Validated ListSceneReportRequest request) {
        return sceneManageService.listReportBySceneIds(request);
    }

    @ApiOperation("|_ 报告排名")
    @GetMapping("report/rank")
    public List<SceneReportListOutput> reportRank(@Validated ListSceneReportRequest request) {
        return sceneManageService.rankReport(request);
    }

}
